package com.example.campuscourier.shared;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import androidx.fragment.app.Fragment;

import com.example.campuscourier.R;
import com.example.campuscourier.requestor.RequestDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateTelegram extends AppCompatActivity {
    InputFilter blockAtSymbol = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source != null && source.length() > 0 && source.charAt(0) == '@') {
                // Block the '@' character
                return "";
            }
            return null; // Accept all other characters
        }
    };
    private EditText newTelegramHandle;
    private Button saveHandleButton, buttonBackToProfile;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "NeutralAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_telegram);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        newTelegramHandle = findViewById(R.id.newTelegramHandle);
        newTelegramHandle.setFilters(new InputFilter[]{blockAtSymbol});
        saveHandleButton = findViewById(R.id.buttonSaveHandle);


        buttonBackToProfile = findViewById(R.id.buttonBackToProfile);
        buttonBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });

        saveHandleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newHandle = newTelegramHandle.getText().toString().trim();
                if (!newHandle.isEmpty()) {
                    updateTelegramHandle(newHandle);
                } else {
                    Toast.makeText(UpdateTelegram.this, "Please enter a new Telegram handle", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTelegramHandle(String newHandle) {
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        userRef.update("telegram", newHandle)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UpdateTelegram.this, "Telegram handle updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after successful update
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UpdateTelegram.this, "Failed to update Telegram handle", Toast.LENGTH_SHORT).show();
                });
    }
}


