package com.example.campuscourier.supplier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campuscourier.R;
import com.example.campuscourier.requestor.RequestDetails;
import com.example.campuscourier.shared.FirebaseHelper;
import com.example.campuscourier.shared.RequestAdapter;
import com.example.campuscourier.shared.Requests;
import com.example.campuscourier.shared.ThemeManager;
import com.example.campuscourier.supplier.HomeSupplier;
import com.example.campuscourier.supplier.RequestDetailsForSupplierCancel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class HistorySupplier extends AppCompatActivity {

    Button buttonSupplierHome;

    RecyclerView rvRequests;
    ArrayList<Requests> requestsArrayList;
    RequestAdapter requestAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "SupAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_supplier);

        rvRequests = findViewById(R.id.rvSupplierPosts);

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
                Intent intent = new Intent(getApplicationContext(), RequestDetailsForSupplierCancel.class);
                // Passing the data to the
                // EmployeeDetails Activity
                intent.putExtra(HomeSupplier.NEXT_SCREEN, r);
                startActivity(intent);
                finish();
            }
        });
//local changes
        FirebaseHelper.getSHistoryPosts(requestsArrayList, requestAdapter);
//end of local changes
        buttonSupplierHome = findViewById(R.id.buttonSupplierHome);
        buttonSupplierHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeSupplier.class);
                startActivity(intent);
                finish();
            }
        });
    }
}