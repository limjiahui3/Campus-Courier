package com.example.campuscourier.supplier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.campuscourier.R;
import com.example.campuscourier.requestor.Home;
import com.example.campuscourier.shared.FirebaseHelper;
import com.example.campuscourier.shared.Requests;
import com.example.campuscourier.shared.ThemeManager;
import com.example.campuscourier.shared.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class RequestDetailsForSupplierAccept extends AppCompatActivity {

    Requests r;
    TextView requestorTelegram, itemName, itemDescription, expiryDate, expiryTime, location, urgency;
    ImageView image;
    Button buttonAcceptRequest, buttonBackToHome;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    @SuppressLint("StaticFieldLeak")
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "SupAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details_for_supplier_accept);
        requestorTelegram = findViewById(R.id.requestorTelegram);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        image = findViewById(R.id.postImage);
        expiryDate = findViewById(R.id.expiryDate);
        expiryTime = findViewById(R.id.expiryTime);
        location = findViewById(R.id.location);
        urgency = findViewById(R.id.urgency);
        buttonAcceptRequest = findViewById(R.id.buttonAcceptRequest);
        buttonBackToHome = findViewById(R.id.buttonBackToHome);

        if (getIntent().hasExtra(FindRequests.NEXT_SCREEN)) {
            // get the Serializable data model class with the details in it
            r = (Requests) getIntent().getSerializableExtra(FindRequests.NEXT_SCREEN);
        }
        if (r != null) {

            db.collection("users").document(r.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Users u = documentSnapshot.toObject(Users.class);
                    requestorTelegram.setText(u.getTelegram());
                    SpannableString spannableSupplier = new SpannableString(requestorTelegram.getText());
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            // Define the action to take when the Telegram username is clicked
                            String username = requestorTelegram.getText().toString();
                            openTelegram(username);
                        }
                    };
                    spannableSupplier.setSpan(clickableSpan, 0, spannableSupplier.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    requestorTelegram.setText(spannableSupplier);
                    requestorTelegram.setMovementMethod(LinkMovementMethod.getInstance()); // Enable clickable links in TextView
                }
            });

            itemName.setText(r.getItem());
            itemDescription.setText(r.getDescription());
            expiryDate.setText(r.getDate());
            expiryTime.setText(r.getTime());
            location.setText(r.getLocation());
            urgency.setText(r.getUrgency());
            if(!Objects.equals(r.getImageStorageUri(), "")){
                StorageReference ref = storage.getReferenceFromUrl(r.getImageStorageUri());
                final long MAX_BYTE = 2100 * 1600;
                ref.getBytes(MAX_BYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        image.setImageBitmap(bitmap);
                        Log.d("IMAGE", "image shown");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.d("IMAGE", "image not shown");
                        image.setImageResource(R.drawable.image);
                    }});}
        }

        buttonAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("status", "Accepted");
                db.collection("posts").document(r.getDocId()).update("status", "Accepted");

                db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("supplierId", userId);
                db.collection("posts").document(r.getDocId()).update("supplierId", userId);

                r.setStatus("Accepted");
                r.setSupplierId(userId);

                FirebaseHelper.addPostToTodoCollection(r);
                Intent intent = new Intent(getApplicationContext(), HomeSupplier.class);
                startActivity(intent);
                finish();
            }
        });

        buttonBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindRequests.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void openTelegram(String username) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://t.me/" + username));
        startActivity(intent);
    }
}