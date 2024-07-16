package com.example.campuscourier.shared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.campuscourier.R;
import com.example.campuscourier.requestor.Home;
import com.example.campuscourier.requestor.RequestDetails;
import com.example.campuscourier.shared.Requests_2;
import com.example.campuscourier.supplier.RequestDetailsForSupplierCancel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import java.util.List;

public class Report extends AppCompatActivity {
    private InputFilter blockAtSymbol = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source != null && source.length() > 0 && source.charAt(0) == '@') {
                // Block the '@' character
                return "";
            }
            return null; // Accept all other characters
        }
    };

    Button buttonSubmit, buttonBackToRequestDetails;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Requests r;

    private EditText InputotherReason, InputId, InputDescription;

    List<Chip> chips_list;

    List<String> selectedDescriptions;
    public static final String NEXT_SCREEN = "details_screen";
    public static final String SUPPPLIERCANCEL_SCREEN = "SUPPPLIERCANCEL_SCREEN";
    public static final String SUPPLIERACCEPT_SCREEN = "SUPPLIERACCEPT_SCREEN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "NeutralAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonBackToRequestDetails = findViewById(R.id.buttonBackToRequestDetails);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        selectedDescriptions = new ArrayList<>();
        chips_list = new ArrayList<>();
        InputotherReason = findViewById(R.id.Other_Reason);
        InputId = findViewById(R.id.Reporting_ID);
        InputId.setFilters(new InputFilter[]{blockAtSymbol});
        InputDescription = findViewById(R.id.description_text);
        if (getIntent().hasExtra(RequestDetails.REPORT_SCREEN)) {
            // get the Serializable data model class with the details in it
            r = (Requests) getIntent().getSerializableExtra(RequestDetails.REPORT_SCREEN);
        }


        for (int chipId : new int[]{R.id.chip_late, R.id.chip_wrong_item, R.id.chip_no_show, R.id.chip_payment_amount, R.id.chip_others}) {
            Chip chip = findViewById(chipId);
            if(chip != null) {
                chips_list.add(chip);
            }
        }

        for (Chip chip : chips_list) {
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String description = ((Chip) v).getText().toString();
                    int chipId = v.getId(); // Get the ID of the clicked chip

                    if (chipId == R.id.chip_others) {
                        if (((Chip) v).isChecked()) {
                            InputotherReason.setVisibility(View.VISIBLE);
                            selectedDescriptions.add(description);
                        } else {
                            InputotherReason.setVisibility(View.GONE);
                            selectedDescriptions.remove(description); // Remove if unchecked
                        }
                    } else if (((Chip) v).isChecked()) {
                        selectedDescriptions.add(description);
                    } else {
                        selectedDescriptions.remove(description); // Remove if unchecked
                    }
                }
            });
        }

        buttonBackToRequestDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(getIntent().getStringExtra("activity"), "RequestDetails")){
                    Intent intent = new Intent(getApplicationContext(), RequestDetails.class);
                    intent.putExtra(NEXT_SCREEN, r);
                    startActivity(intent);
                    finish();
                }
                else if(Objects.equals(getIntent().getStringExtra("activity"), "RequestDetailsForSupplierCancel")){
                    Intent intent = new Intent(getApplicationContext(), RequestDetailsForSupplierCancel.class);
                    intent.putExtra(SUPPPLIERCANCEL_SCREEN, r);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), RequestDetails.class);
                    intent.putExtra(NEXT_SCREEN, r);
                    startActivity(intent);
                    finish();
                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Report_ID = InputId.getText().toString().trim();
                String Description = InputDescription.getText().toString().trim();
                String OtherReason = InputotherReason.getText().toString().trim();

                if(TextUtils.isEmpty(Report_ID) || TextUtils.isEmpty(Description)){
                    Toast.makeText(Report.this,"Please fill up all fields",Toast.LENGTH_SHORT).show();
                }
                else if (!selectedDescriptions.contains("Others")){
//                    Intent intent = new Intent(getApplicationContext(), RequestDetails.class);
//                    startActivity(intent);
//                    finish();
                    addReportToFirestore(userId,Report_ID, Description,OtherReason, selectedDescriptions);
                }
                else if (TextUtils.isEmpty(OtherReason.toString().trim())) {
                    Toast.makeText(Report.this,"Please fill up other reason for reporting",Toast.LENGTH_SHORT).show();
                }
                else{
//                    Intent intent = new Intent(getApplicationContext(), RequestDetails.class);
//                    startActivity(intent);
//                    finish();
                    addReportToFirestore(userId, Report_ID, Description,OtherReason, selectedDescriptions);
                }

            }
        });
    }


    private void addReportToFirestore(String userId,String reportId, String description,String OtherReason,List<String> selectedDescriptions) {
        String docId = userId + reportId+ description+ OtherReason;
        Requests_2 r = new Requests_2(userId, reportId, description, OtherReason, selectedDescriptions,docId);
        // below method is use to add data to Firebase Firestore.
        db.collection("users").document(userId).collection("report").document(docId).set(r).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Report created.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Report not created", Toast.LENGTH_SHORT).show();
            }
        });
        db.collection("report").document(docId).set(r).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Report created.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Report not created", Toast.LENGTH_SHORT).show();
            }
        });

//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                // after the data addition is successful
//                // we are displaying a success toast message.
//                Toast.makeText(getApplicationContext(), "Report created.", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // this method is called when the data addition process is failed.
//                // displaying a toast message when data addition is failed.
//                Toast.makeText(getApplicationContext(), "Report not created", Toast.LENGTH_SHORT).show();
//            }
//        });
//        db.collection("report").document(docId).set(r).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Log.d("NEW REPORT", "report stored");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w("NEW REPORT", "report not stored", e);
//            }
//        });


    }


}












