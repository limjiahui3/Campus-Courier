package com.example.campuscourier.requestor;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.campuscourier.shared.DemeritPoints;
import com.example.campuscourier.shared.Profile;
import com.example.campuscourier.shared.Report;
import com.example.campuscourier.shared.ReportStatus;
import com.example.campuscourier.shared.ThemeManager;
import com.example.campuscourier.shared.Users;
import com.example.campuscourier.supplier.HomeSupplier;
import com.example.campuscourier.shared.Login;
import com.example.campuscourier.R;
import com.example.campuscourier.shared.RequestAdapter;
import com.example.campuscourier.shared.Requests;
import com.example.campuscourier.shared.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

public class Home extends AppCompatActivity {

    Button buttonLogout, buttonToSupplier, buttonHistory;
    RecyclerView rvRequests;
    ArrayList<Requests> requestsArrayList;
    RequestAdapter requestAdapter;
    TextView name, number;
    private int previousDemeritPoints;
    public static final String NEXT_SCREEN = "details_screen";
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("StaticFieldLeak")
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    private static final String TAG = "Home";
    String docId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "ReqAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        name = findViewById(R.id.name);
        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users u = documentSnapshot.toObject(Users.class);
                name.setText(u.getTelegram());
            }
        });
        number = findViewById(R.id.number);
        Query query = db.collection("users").document(userId).collection("posts").whereNotEqualTo("status", "Delivered");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    int n = (int) snapshot.getCount();
                    String s = Integer.toString(n);
                    number.setText(s);
                    Log.d(TAG, "Count: " + snapshot.getCount());
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });

        db.collection("users").document(userId).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Long currentPoints = snapshot.getLong("points");
                if (currentPoints != null) {
                    int currentDemeritPoints = currentPoints.intValue();

                    // Compare current points with previous points
                    if (currentDemeritPoints < previousDemeritPoints) {
                        showNotification("Demerit Points Given", "Someone was naughty your demerit points have been deducted!! Stop trolling >:(");
                    }

                    // Update previous points
                    previousDemeritPoints = currentDemeritPoints;
                }
            } else {
                Log.d(TAG, "Current data: null");
            }
        });


        rvRequests = findViewById(R.id.rvRequests);
        requestsArrayList = new ArrayList<>();
        rvRequests.setHasFixedSize(true);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new RequestAdapter(requestsArrayList, this);
        rvRequests.setAdapter(requestAdapter);
        FirebaseHelper.getPosts(requestsArrayList, requestAdapter);
        requestAdapter.setOnClickListener(new RequestAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Requests r) {
                Intent intent = new Intent(getApplicationContext(), RequestDetails.class);
                intent.putExtra(NEXT_SCREEN, r);
                startActivity(intent);
                finish();
            }
        });


        buttonToSupplier = findViewById(R.id.buttonToSupplier);
        buttonToSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeSupplier.class);
                startActivity(intent);
                finish();
            }
        });

        buttonHistory = findViewById(R.id.buttonHistory);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), History.class);
                startActivity(intent);
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int ItemId = item.getItemId();
                if (ItemId == R.id.nav_addRequest) {
                    startActivity(new Intent(Home.this, AddRequest.class));
                    return true;
                } else if (ItemId == R.id.nav_profile) {
                    startActivity(new Intent(Home.this, Profile.class));
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Query query = db.collection("users").document(userId).collection("posts");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    int n = (int) snapshot.getCount();
                    String s = Integer.toString(n);
                    number.setText(s);
                    Log.d(TAG, "Count: " + snapshot.getCount());
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android Oreo and higher versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default_channel", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, DemeritPoints.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Optional flag to clear the activity stack
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_channel")
                .setSmallIcon(R.drawable.courier)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // Set the PendingIntent for the notification
                .setAutoCancel(true); // Automatically dismiss the notification when clicked

        // Show the notification
        notificationManager.notify(1, builder.build());
    }
//    private void showNotification_report(String title, String message) {
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Create a notification channel for Android Oreo and higher versions
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("default_channel", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        Intent intent = new Intent(this, ReportStatus.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Optional flag to clear the activity stack
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
//        // Build the notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_channel")
//                .setSmallIcon(R.drawable.courier)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent) // Set the PendingIntent for the notification
//                .setAutoCancel(true); // Automatically dismiss the notification when clicked
//
//        // Show the notification
//        notificationManager.notify(1, builder.build());
//    }
}