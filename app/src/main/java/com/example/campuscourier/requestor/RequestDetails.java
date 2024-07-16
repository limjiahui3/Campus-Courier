package com.example.campuscourier.requestor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.campuscourier.R;
import com.example.campuscourier.requestor.EditRequestDetails;
import com.example.campuscourier.requestor.Home;
import com.example.campuscourier.shared.CheckCompleted;
import com.example.campuscourier.shared.FirebaseHelper;
import com.example.campuscourier.shared.Report;
import com.example.campuscourier.shared.Requests;
import com.example.campuscourier.shared.ThemeManager;
import com.example.campuscourier.shared.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class RequestDetails extends AppCompatActivity {

    Requests r;
    TextView supplierTelegram, itemName, itemDescription, expiryDate, expiryTime, location, urgency, category;
    ImageView image;
    Button buttonEditRequest, buttonBackToHome, buttonDeleteRequest, buttonReport, buttonSupplierCompleted;
    public static final String NEXT_SCREEN = "edit_details_screen";
    public static final String REPORT_SCREEN = "report_screen";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    CheckCompleted checkCompleted = new CheckCompleted();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "ReqAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        supplierTelegram = findViewById(R.id.supplierTelegram);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        image = findViewById(R.id.postImage);
        image.setImageResource(R.drawable.image);
        expiryDate = findViewById(R.id.expiryDate);
        expiryTime = findViewById(R.id.expiryTime);
        location = findViewById(R.id.location);
        urgency = findViewById(R.id.urgency);
        category = findViewById(R.id.category);
        buttonEditRequest = findViewById(R.id.buttonEditRequest);
        buttonSupplierCompleted = findViewById(R.id.buttonSupplierCompleted);
        buttonBackToHome = findViewById(R.id.buttonBackToHome);
        buttonDeleteRequest = findViewById(R.id.buttonDeleteRequest);
        buttonReport = findViewById(R.id.buttonReport);
        if (getIntent().hasExtra(Home.NEXT_SCREEN)) {
            // get the Serializable data model class with the details in it
            r = (Requests) getIntent().getSerializableExtra(Home.NEXT_SCREEN);
            Log.d("INFO TRANSFERRED", "FROM HOME");
        }
        if (getIntent().hasExtra(Report.NEXT_SCREEN)) {
            // get the Serializable data model class with the details in it
            r = (Requests) getIntent().getSerializableExtra(Report.NEXT_SCREEN);
            Log.d("INFO TRANSFERRED", "FROM REPORT");
        }
        if (getIntent().hasExtra(EditRequestDetails.NEXT_SCREEN)) {
            // get the Serializable data model class with the details in it
            r = (Requests) getIntent().getSerializableExtra(EditRequestDetails.NEXT_SCREEN);
        }
        if (r != null) {

            supplierTelegram.setText("-");
            if(!Objects.equals(r.getSupplierId(), "")){
                db.collection("users").document(r.getSupplierId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Users u = documentSnapshot.toObject(Users.class);
                        supplierTelegram.setText(u.getTelegram());
                        SpannableString spannableSupplier = new SpannableString(supplierTelegram.getText());
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                // Define the action to take when the Telegram username is clicked
                                String username = supplierTelegram.getText().toString();
                                openTelegram(username);
                            }
                        };
                        spannableSupplier.setSpan(clickableSpan, 0, spannableSupplier.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        supplierTelegram.setText(spannableSupplier);
                        supplierTelegram.setMovementMethod(LinkMovementMethod.getInstance()); // Enable clickable links in TextView
                    }
                });
            }
            itemDescription.setText("-");
            if(!Objects.equals(r.getDescription(), "")){
                itemDescription.setText(r.getDescription());
            }

            itemName.setText(r.getItem());
            expiryDate.setText(r.getDate());
            expiryTime.setText(r.getTime());
            location.setText(r.getLocation());
            urgency.setText(r.getUrgency());
            category.setText(r.getCategory());
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

        buttonEditRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditRequestDetails.class);
                intent.putExtra(NEXT_SCREEN, r);
                startActivity(intent);
                finish();
            }
        });

        buttonDeleteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper.deleteDataFromFirestore(r);
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                finish();
            }
        });

        buttonBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Report.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSupplierCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("Delivering".equals(r.getStatus())) {
                    new AlertDialog.Builder(RequestDetails.this)
                            .setTitle("Confirmation")
                            .setMessage("You have marked the request as delivered. Please wait for the supplier to confirm the delivery.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    r.setRequestorDone();
                                    db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("RequestorDone", true);
                                    db.collection("users").document(r.getSupplierId()).collection("todo").document(r.getDocId()).update("RequestorDone", true);
                                    db.collection("posts").document(r.getDocId()).update("RequestorDone", true);
                                    checkCompleted.checkAndUpdatePostStatus(r);
                                    Log.d("CheckCompleted", "isRequestorDone: " + String.valueOf(r.isRequestorDone()));
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setCancelable(false)
                            .show();
                } else {
                    Toast.makeText(RequestDetails.this, "Please wait for supplier to deliver item first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openTelegram(String username) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://t.me/" + username));
        startActivity(intent);
    }
}
