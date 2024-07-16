package com.example.campuscourier.admin;

import static com.example.campuscourier.shared.FirebaseHelper.getReportAdmin;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campuscourier.R;
import com.example.campuscourier.shared.Requests_2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Objects;

public class AdminAdaptor extends RecyclerView.Adapter<AdminAdaptor.ViewHolder> {
    private ArrayList<Requests_2> reportItems;
    private Context context;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdminAdaptor(ArrayList<Requests_2> reportItems, Context context) {
        this.reportItems = reportItems;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_card_admin_report, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAdaptor.ViewHolder holder, int position) {
        Requests_2 reportItem = reportItems.get(position);
        holder.bind(reportItem);

        if (reportItem.getStatus().equals("Pending")) {
            holder.bind(reportItem);
        } else {
            // If the status is not "Pending", hide the view
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ReportId, Description, ReasonDetail, OtherReason, Other_Description;
        private EditText Points_deducted;
        private Button buttonAccept, buttonDecline;
        private String docId, userId; // Document ID


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ReportId = itemView.findViewById(R.id.telegram_handle);
            Description = itemView.findViewById(R.id.Reason_detail);
            ReasonDetail = itemView.findViewById(R.id.Reason_report);
            OtherReason = itemView.findViewById(R.id.Other_detail);
            Other_Description = itemView.findViewById(R.id.Other_Description);
            buttonAccept = itemView.findViewById(R.id.buttonAccept);


            buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Points_deducted.getText().toString().isEmpty()) {
                        Toast.makeText(context, "Please fill up the points you want to deduct", Toast.LENGTH_SHORT).show();
                        ;
                    } else {
                        int pointsToDeduct = Integer.parseInt(Points_deducted.getText().toString());
                        db.collection("report").document(docId).update("status", "Accepted");
                        db.collection("users").document(userId).collection("report").document(docId).update("status", "Accepted");

                        db.collection("users")
                                .whereEqualTo("telegram", ReportId.getText().toString().trim())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                        Object pointsObj = document.get("points");
                                        if (pointsObj instanceof Long) {
                                            long currentPoints = (Long) pointsObj;
                                            int updatedPoints = (int) (currentPoints - pointsToDeduct);

                                            // Update the points field in Firestore
                                            db.collection("users").document(document.getId())
                                                    .update("points", updatedPoints)
                                                    .addOnSuccessListener(aVoid -> {
                                                        if(updatedPoints <0){
                                                            Toast.makeText(context, "Following user has messed up too many times, Please disable account", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            Toast.makeText(context, "Points deducted successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(context, "Failed to deduct points", Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(context, "User points field not found or invalid type", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                                });


                        int position = getAdapterPosition();
                        reportItems.remove(position);
                        notifyItemRemoved(position);
                    }
                }
            });


            buttonDecline = itemView.findViewById(R.id.buttonDecline);
            Points_deducted = itemView.findViewById(R.id.Points_deducted);

            buttonDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("report").document(docId).update("status", "Decline");
                    db.collection("users").document(userId).collection("report").document(docId).update("status", "Declined");
                    int position = getAdapterPosition();
                    reportItems.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }

        public void bind(Requests_2 reportItem) {
            // Bind data to views
            ReportId.setText(reportItem.getReportId());
            Description.setText(reportItem.getDescription());
            ReasonDetail.setText(reportItem.getSelectedDescription().toString());
            OtherReason.setText(reportItem.getOtherReason());
            Points_deducted.setFilters(new InputFilter[]{new InputFilterMinMax(1, 100)});
            if (reportItem.getSelectedDescription().contains("Others")) {
                OtherReason.setVisibility(View.VISIBLE);
                OtherReason.setText(reportItem.getOtherReason());
                Other_Description.setVisibility(View.VISIBLE);
            } else {
                OtherReason.setVisibility(View.GONE);
                Other_Description.setVisibility(View.GONE);
            }

            // Set the document ID
            docId = reportItem.getDocId();
            userId = reportItem.getUserId();


        }

        public class InputFilterMinMax implements InputFilter {
            private int min, max;

            public InputFilterMinMax(int min, int max) {
                this.min = min;
                this.max = max;
            }

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                try {
                    int input = Integer.parseInt(dest.toString() + source.toString());
                    if (isInRange(min, max, input)) {
                        return null;
                    }
                } catch (NumberFormatException ignored) {
                }
                return "";
            }

            private boolean isInRange(int a, int b, int c) {
                return b > a ? c >= a && c <= b : c >= b && c <= a;
            }
        }
    }


}