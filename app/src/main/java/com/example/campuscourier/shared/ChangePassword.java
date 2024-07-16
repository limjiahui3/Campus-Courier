package com.example.campuscourier.shared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.campuscourier.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button changePasswordButton, buttonBackToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "NeutralAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        buttonBackToProfile = findViewById(R.id.buttonBackToProfile);
        buttonBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });

        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (validateInput(oldPassword, newPassword, confirmPassword)) {
                    changePassword(oldPassword, newPassword);
                }
            }
        });
    }

    private boolean validateInput(String oldPassword, String newPassword, String confirmPassword) {
        // Implement your validation logic here (e.g., check for empty fields, password criteria)

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "New password and confirm password must match.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Add more validation as needed

        return true;
    }

    private void changePassword(String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            user.reauthenticate(credential)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            user.updatePassword(newPassword)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ChangePassword.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                            finish();
                                            // Handle success (e.g., navigate to another activity)
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ChangePassword.this, "Failed to update password.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangePassword.this, "Authentication failed. Please check your old password.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}