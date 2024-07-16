package com.example.campuscourier.shared;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.example.campuscourier.R;
import com.example.campuscourier.shared.FAQ_n_Rules;
import com.example.campuscourier.shared.ReportStatus;
import com.example.campuscourier.shared.UpdateTelegram;
import com.example.campuscourier.requestor.AddRequest;
import com.example.campuscourier.requestor.Home;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView telehandleTextView = findViewById(R.id.telehandle);
        TextView changePasswordTextView = findViewById(R.id.changepassword);

        TextView reportStatusTextView = findViewById(R.id.reportstatus);
        TextView faqTextView = findViewById(R.id.faq);


        String UserId = currentUser.getUid();

        progressBar = findViewById(R.id.progressBar);
        statusTextView = findViewById(R.id.statusTextView);
        TextView numberTextView = findViewById(R.id.number);

        // Fetch demerit points from Firestore
        db.collection("users").document(UserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long points = documentSnapshot.getLong("points");
                        Log.d("did you fetch", "points");
                        if (points != null) {
                            demeritPoints = points.intValue();
                        } else {
                            demeritPoints = 0; // Default value if points field is null
                        }
                        numberTextView.setText(String.valueOf(demeritPoints));

                        // Set progress and update status
                        progressBar.setProgress(demeritPoints);
                        updateStatus(); // Call updateStatus method here
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "no points is inside the users field", Toast.LENGTH_SHORT).show();
                });


        telehandleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, UpdateTelegram.class));
            }
        });

        changePasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for changePasswordTextView
                startActivity(new Intent(Profile.this, ChangePassword.class));
            }
        });



        reportStatusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for reportStatusTextView
                startActivity(new Intent(Profile.this, ReportStatus.class));
            }
        });

        faqTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for faqTextView
                startActivity(new Intent(Profile.this, FAQ_n_Rules.class));
            }
        });


        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int ItemId = item.getItemId();
                if(ItemId == R.id.nav_home){
                    startActivity(new Intent(Profile.this, Home.class));
                    finish();
                    return true;
                }
                else if (ItemId == R.id.nav_addRequest) {
                    startActivity(new Intent(Profile.this, AddRequest.class));
                    return true;
                }
                return false;
            }
        });
    }
    private int demeritPoints;
    private String UserId;
    private ProgressBar progressBar;
    private TextView statusTextView;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    Button buttonBackToProfile;



    private void updateStatus() {
        if (demeritPoints >= 100) {
            statusTextView.setText("Good job! Keep it up!");
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_purple));
        } else if (demeritPoints >= 20) {
            statusTextView.setText("Please try your best to not cause trouble.");
            statusTextView.setTextColor(ContextCompat.getColor(this, R.color.purple_500));
        } else if (demeritPoints > 0) {
            statusTextView.setText("Someone has been naughty. If you reach 0, your account will be disabled. Don't be toxic!");
            statusTextView.setTextColor(ContextCompat.getColor(this, R.color.purple_700));
        } else {
            // Account disabled
            statusTextView.setText("Your account has been disabled due to toxic behavior.");
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }
}




