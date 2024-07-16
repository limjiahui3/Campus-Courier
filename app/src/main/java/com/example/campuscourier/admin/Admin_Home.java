package com.example.campuscourier.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.example.campuscourier.R;
import com.example.campuscourier.shared.Login;
import com.example.campuscourier.shared.Requests_2;
import com.example.campuscourier.shared.FirebaseHelper;
import com.example.campuscourier.shared.ReportAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Admin_Home extends AppCompatActivity {

    Button buttonLogout,buttonAccept,buttonDecline;
    RecyclerView rvAdmin;
    ArrayList<Requests_2> AdminArrayList;
    AdminAdaptor AdminAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        rvAdmin = findViewById(R.id.rvAdmin);
        AdminArrayList = new ArrayList<>();
        rvAdmin.setHasFixedSize(true);
        rvAdmin.setLayoutManager(new LinearLayoutManager(this));
        AdminAdapter = new AdminAdaptor(AdminArrayList, this );
        rvAdmin.setAdapter(AdminAdapter);
        FirebaseHelper.getReportAdmin(AdminArrayList, AdminAdapter);
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
    }
}