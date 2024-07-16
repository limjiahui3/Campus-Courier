package com.example.campuscourier.shared;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.campuscourier.shared.Requests_2;
import com.example.campuscourier.admin.AdminAdaptor;
import com.example.campuscourier.supplier.CheckboxAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirebaseHelper {

    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("StaticFieldLeak")
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    static String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


    public static void getPosts(ArrayList<Requests> requestsArrayList, RequestAdapter requestAdapter){
        db.collection("users").document(userId).collection("posts").whereNotEqualTo("status","Delivered").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are
                            // hiding our progress bar and adding
                            // our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Requests r = d.toObject(Requests.class);
                                // and we will pass this object class
                                // inside our arraylist which we have
                                // created for recycler view.
                                requestsArrayList.add(r);
                            }
                            // after adding the data to recycler view.
                            // we are calling recycler view notifyDataSetChanged
                            // method to notify that data has been changed in recycler view.
                            requestAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public static void getNotAcceptedPosts(ArrayList<Requests> requestsArrayList, RequestAdapter requestAdapter){
        db.collection("posts").whereEqualTo("status", "Not Accepted").orderBy("urgency", Query.Direction.DESCENDING).orderBy("date", Query.Direction.ASCENDING).orderBy("time", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Requests r = d.toObject(Requests.class);
                                requestsArrayList.add(r);
                            }
                            requestAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    public static void getToDoPosts(ArrayList<Requests> requestsArrayList, CheckboxAdapter requestAdapter){
        db.collection("users").document(userId).collection("todo").whereNotEqualTo("status","Delivered").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Requests r = d.toObject(Requests.class);
                                requestsArrayList.add(r);
                            }
                            requestAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public static void getRHistoryPosts(ArrayList<Requests> requestsArrayList, RequestAdapter requestAdapter){
        db.collection("users").document(userId).collection("posts").whereEqualTo("status", "Delivered").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Requests r = d.toObject(Requests.class);
                                requestsArrayList.add(r);
                            }
                            requestAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    public static void getSHistoryPosts(ArrayList<Requests> requestsArrayList, RequestAdapter requestAdapter){
        db.collection("users").document(userId).collection("todo").whereEqualTo("status", "Delivered").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Requests r = d.toObject(Requests.class);
                                requestsArrayList.add(r);
                            }
                            requestAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public static void CompletedPosts(Requests r) {

        String documentId = r.getDocId();

        db.collection("users").document(userId).collection("todo").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MyApplication.getAppContext(), "Request completed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyApplication.getAppContext(), "Request not completed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getReport(ArrayList<Requests_2> requestsArrayList, ReportAdapter reportAdapter){
        db.collection("users").document(userId).collection("report").whereEqualTo("userId",userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // after getting the data we are calling on success method                    // and inside this method we are checking if the received
                // query snapshot is empty or not.
                if (!queryDocumentSnapshots.isEmpty()) {
                // if the snapshot is not empty we are                        // hiding our progress bar and adding
                // our data in a list.
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {                            // after getting this list we are passing
                    // that list to our object class.
                        Requests_2 r = d.toObject(Requests_2.class);
                    // and we will pass this object class                            // inside our arraylist which we have
                    // created for recycler view.
                        requestsArrayList.add(r);
                }                        // after adding the data to recycler view.
                // we are calling recycler view notifyDataSetChanged                        // method to notify that data has been changed in recycler view.
                reportAdapter.notifyDataSetChanged();
            }}});
}
    public static void getReportAdmin(ArrayList<Requests_2> AdminArrayList, AdminAdaptor AdminAdapter){
        db.collection("report").get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {                @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {                    // after getting the data we are calling on success method
                // and inside this method we are checking if the received                    // query snapshot is empty or not.
                if (!queryDocumentSnapshots.isEmpty()) {                        // if the snapshot is not empty we are
                    // hiding our progress bar and adding                        // our data in a list.
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        // after getting this list we are passing                            // that list to our object class.
                        Requests_2 r = d.toObject(Requests_2.class);                            // and we will pass this object class
                        // inside our arraylist which we have                            // created for recycler view.
                        AdminArrayList.add(r);                        }
                    // after adding the data to recycler view.                        // we are calling recycler view notifyDataSetChanged
                    // method to notify that data has been changed in recycler view.
                    AdminAdapter.notifyDataSetChanged();
                }                }
            });}

    public static void addUserToFirestore(String email, String telegram, int points){
        Users u = new Users(email, telegram, points);

        db.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .set(u)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("NEW USER", "user stored");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("NEW USER", "user not stored", e);
                    }
                });
    }

    public static void addDataToFirestore(String item, String description, String imageUri, String imageStorageUri, String date, String time, String location, String urgency, String status, String userId, String supplierId, String category) {
        String docId = userId+item+description+date+time+location+urgency;
        Requests r = new Requests(item, description, imageUri, imageStorageUri, date, time, location, urgency, status, userId, docId, supplierId, category);

        db.collection("users").document(userId).collection("posts").document(docId).set(r).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                Toast.makeText(MyApplication.getAppContext(), "Request posted.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyApplication.getAppContext(), "Request not posted.", Toast.LENGTH_SHORT).show();
            }
        });

        db.collection("posts").document(docId).set(r).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("NEW REQUEST", "request stored");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("NEW REQUEST", "request not stored", e);
            }
        });
    }

    public static void deleteImageFromStorage(Requests r) {
        if (!Objects.equals(r.getImageStorageUri(), "")) {
            StorageReference ref = storage.getReferenceFromUrl(r.getImageStorageUri());
            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("DELETE IMAGE", "image deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("DELETE IMAGE", "image not deleted");
                }
            });
        }
    }

    public static void deleteDataFromFirestore(Requests r) {

        if(!Objects.equals(r.getImageStorageUri(), "")){
            StorageReference ref = storage.getReferenceFromUrl(r.getImageStorageUri());
            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("DELETE IMAGE", "image deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("DELETE IMAGE", "image not deleted");
                }
            });
        }

        String documentId = r.getDocId();

        db.collection("users").document(userId).collection("posts").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MyApplication.getAppContext(), "Request deleted.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyApplication.getAppContext(), "Request not deleted", Toast.LENGTH_SHORT).show();
            }
        });

        db.collection("posts").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DELETE REQUEST", "request deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("DELETE REQUEST", "request not deleted", e);
            }
        });
    }

    public static void addPostToTodoCollection(Requests r) {

        db.collection("users").document(userId).collection("todo").document(r.getDocId()).set(r).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MyApplication.getAppContext(), "Request accepted.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyApplication.getAppContext(), "Request not accepted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void removePostFromTodoCollection(Requests r) {

        String documentId = r.getDocId();

        db.collection("users").document(userId).collection("todo").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MyApplication.getAppContext(), "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyApplication.getAppContext(), "Request not cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
