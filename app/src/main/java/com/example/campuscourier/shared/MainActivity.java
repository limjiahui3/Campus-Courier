package com.example.campuscourier.shared;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campuscourier.R;
import com.example.campuscourier.shared.Observer;
import com.example.campuscourier.shared.Requests;

public class MainActivity extends AppCompatActivity implements Observer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void update(Requests requests) {
    }
}