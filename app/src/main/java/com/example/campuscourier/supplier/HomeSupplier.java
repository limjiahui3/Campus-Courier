package com.example.campuscourier.supplier;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.campuscourier.R;
import com.example.campuscourier.requestor.AddRequest;
import com.example.campuscourier.requestor.History;
import com.example.campuscourier.requestor.Home;
import com.example.campuscourier.shared.FirebaseHelper;
import com.example.campuscourier.shared.Login;
import com.example.campuscourier.shared.Profile;
import com.example.campuscourier.shared.Requests;
import com.example.campuscourier.shared.ThemeManager;
import com.example.campuscourier.shared.Users;
import com.example.campuscourier.supplier.CheckboxAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.Objects;

public class HomeSupplier extends AppCompatActivity {

    Button buttonToRequestor, buttonSupplierLogout, buttonHistory;
    Requests r;
    RecyclerView rvRequests;
    TextView name, number;
    ArrayList<Requests> requestsArrayList;
    CheckboxAdapter checkboxAdapter;
    public static final String NEXT_SCREEN = "details_for_supplier_cancel_screen";
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("StaticFieldLeak")
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "SupAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_supplier);

        name = findViewById(R.id.name);
        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users u = documentSnapshot.toObject(Users.class);
                name.setText(u.getTelegram());
            }
        });
        number = findViewById(R.id.number);
        Query query = db.collection("users").document(userId).collection("todo").whereNotEqualTo("status", "Delivered");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    int n = (int)snapshot.getCount();
                    String s = Integer.toString(n);
                    number.setText(s);
                    Log.d(TAG, "Count: " + snapshot.getCount());
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });

        rvRequests = findViewById(R.id.rvRequests);
        requestsArrayList = new ArrayList<>();
        rvRequests.setHasFixedSize(true);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        checkboxAdapter = new CheckboxAdapter(requestsArrayList, this);
        rvRequests.setAdapter(checkboxAdapter);
        FirebaseHelper.getToDoPosts(requestsArrayList, checkboxAdapter);
        checkboxAdapter.setOnClickListener(new CheckboxAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Requests r) {
                Intent intent = new Intent(getApplicationContext(), RequestDetailsForSupplierCancel.class);
                intent.putExtra(NEXT_SCREEN, r);
                startActivity(intent);
                finish();
            }
        });

        buttonToRequestor = findViewById(R.id.buttonToRequestor);
        buttonToRequestor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                finish();
            }
        });

        buttonHistory = findViewById(R.id.buttonHistory);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistorySupplier.class);
                startActivity(intent);
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewSupplier );
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int ItemId = item.getItemId();
                if (ItemId == R.id.nav_searchRequest){
                    startActivity(new Intent(HomeSupplier.this, FindRequests.class));
                    return true;
                }
                else if (ItemId == R.id.nav_supplier_profile) {
                    startActivity(new Intent(HomeSupplier.this, SupplierProfile.class));
                    return true;
                }
                return false;
            }
        });
    }


}