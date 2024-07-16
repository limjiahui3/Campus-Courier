package com.example.campuscourier.shared;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.campuscourier.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DemeritPoints extends AppCompatActivity {

    private int demeritPoints;
    private String UserId;
    private ProgressBar progressBar;
    private TextView statusTextView;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    Button buttonBackToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demerit_points);
        String UserId = currentUser.getUid();

        buttonBackToProfile = findViewById(R.id.buttonBackToProfile);
        buttonBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });

        progressBar = findViewById(R.id.progressBar);
        statusTextView = findViewById(R.id.statusTextView);
        TextView numberTextView = findViewById(R.id.number);

        // Fetch demerit points from Firestore
        db.collection("users").document(UserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long points = documentSnapshot.getLong("points");
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
    }

    private void updateStatus() {
        if (demeritPoints >= 100) {
            statusTextView.setText("Good job! Keep it up!");
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else if (demeritPoints >= 20) {
            statusTextView.setText("Please try your best to not cause trouble.");
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        } else if (demeritPoints > 0) {
            statusTextView.setText("Someone has been naughty. If you reach 0, your account will be disabled. Don't be toxic!");
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            // Account disabled
            statusTextView.setText("Your account has been disabled due to toxic behavior.");
            statusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }
}
