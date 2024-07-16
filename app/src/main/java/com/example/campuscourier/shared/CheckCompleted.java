package com.example.campuscourier.shared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.example.campuscourier.shared.Requests;
import com.example.campuscourier.supplier.CheckboxAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CheckCompleted {

    private FirebaseFirestore db;

    public CheckCompleted() {
        db = FirebaseFirestore.getInstance();
    }

    public void checkAndUpdatePostStatus(Requests request) {
//        Log.d("CheckCompleted", "isRequestorDone: " + request.isRequestorDone());
//        Log.d("CheckCompleted", "isSupplierDone: " + request.isSupplierDone());

        if (request.isRequestorDone() && request.isSupplierDone()) {
            // Both supplier and requestor are done, update post status
            updatePostStatus(request);
        }
    }

    private void updatePostStatus(Requests request) {
        // Perform the Firestore updates here
        // Do not attach listeners here, they will be handled in the calling code
        db.collection("users").document(request.getUserId()).collection("posts").document(request.getDocId()).update("status", "Delivered");
        db.collection("users").document(request.getSupplierId()).collection("todo").document(request.getDocId()).update("status", "Delivered");
        db.collection("posts").document(request.getDocId()).update("status", "Delivered");
//        FirebaseHelper.CompletedPosts(request);
    }
}