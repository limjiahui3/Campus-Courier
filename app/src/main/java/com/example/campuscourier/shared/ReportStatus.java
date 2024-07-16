package com.example.campuscourier.shared;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.campuscourier.R;
import com.example.campuscourier.shared.Requests_2;
import com.example.campuscourier.requestor.RequestDetails;

import java.util.ArrayList;

public class ReportStatus extends AppCompatActivity {

    Button buttonBackToProfile;
    RecyclerView rvReport;
    ArrayList<Requests_2> reportArrayList;
    ReportAdapter reportAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "NeutralAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_status);


        rvReport = findViewById(R.id.rvReport);
        reportArrayList = new ArrayList<>();
        rvReport.setHasFixedSize(true);
        rvReport.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(reportArrayList, this);
        rvReport.setAdapter(reportAdapter);
        FirebaseHelper.getReport(reportArrayList, reportAdapter);
        buttonBackToProfile = findViewById(R.id.buttonBackToProfile);

        buttonBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });


    }

}