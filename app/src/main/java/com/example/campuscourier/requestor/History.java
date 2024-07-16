package com.example.campuscourier.requestor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campuscourier.R;
import com.example.campuscourier.shared.FirebaseHelper;
import com.example.campuscourier.shared.RequestAdapter;
import com.example.campuscourier.shared.Requests;
import com.example.campuscourier.shared.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    Button buttonHome;

    RecyclerView rvRequests;
    ArrayList<Requests> requestsArrayList;
    RequestAdapter requestAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "ReqAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvRequests = findViewById(R.id.rvRequests);

        // creating our new array list
        requestsArrayList = new ArrayList<>();
        rvRequests.setHasFixedSize(true);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        // adding our array list to our recycler view adapter class.
        requestAdapter = new RequestAdapter(requestsArrayList, this);
        // setting adapter to our recycler view.
        rvRequests.setAdapter(requestAdapter);
        requestAdapter.setOnClickListener(new RequestAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Requests r) {
                Intent intent = new Intent(getApplicationContext(), RequestDetails.class);
                // Passing the data to the
                // EmployeeDetails Activity
                intent.putExtra(Home.NEXT_SCREEN, r);
                startActivity(intent);
                finish();
            }
        });

        FirebaseHelper.getRHistoryPosts(requestsArrayList, requestAdapter);

        buttonHome = findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
