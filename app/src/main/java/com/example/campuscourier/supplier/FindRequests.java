package com.example.campuscourier.supplier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;

import com.example.campuscourier.R;
import com.example.campuscourier.requestor.Home;
import com.example.campuscourier.shared.FirebaseHelper;
import com.example.campuscourier.shared.RequestAdapter;
import com.example.campuscourier.shared.Requests;
import com.example.campuscourier.shared.ThemeManager;

import java.util.ArrayList;

public class FindRequests extends AppCompatActivity {

    SearchView searchView;
    Button backToHomeSupplier;
    RecyclerView rvRequests;
    ArrayList<Requests> requestsArrayList;
    RequestAdapter requestAdapter;
    String selectedFilterLocation = "All Location";
    String selectedFilterCategory = "All Posts";
    public static final String NEXT_SCREEN = "details_for_supplier_accept_screen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "SupAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_requests);

        searchView = findViewById(R.id.search_view);
        rvRequests = findViewById(R.id.rvRequests);
        searchView.clearFocus();

        requestsArrayList = new ArrayList<>();
        rvRequests.setHasFixedSize(true);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new RequestAdapter(requestsArrayList, this);
        rvRequests.setAdapter(requestAdapter);
        FirebaseHelper.getNotAcceptedPosts(requestsArrayList, requestAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        requestAdapter.setOnClickListener(new RequestAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Requests r) {
                Intent intent = new Intent(getApplicationContext(), RequestDetailsForSupplierAccept.class);
                intent.putExtra(NEXT_SCREEN, r);
                startActivity(intent);
                finish();
            }
        });

        backToHomeSupplier = findViewById(R.id.buttonBackToHomeSupplier);
        backToHomeSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeSupplier.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void searchList(String text) {
        ArrayList<Requests> searchList = new ArrayList<>();
        for (Requests request : requestsArrayList) {
            if (request.getItem().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(request);
            }

        }
        requestAdapter.searchRequestsArrayList(searchList);

    }
    public void filterLocation(String loc){
        selectedFilterLocation = loc;
        ArrayList<Requests> locationList = new ArrayList<>();
        if (selectedFilterCategory.equals("All Posts")){
            for (Requests request : requestsArrayList) {
                if (request.getLocation().contains(loc)) {
                    locationList.add(request);
                }
            }
        }else{
            ArrayList<Requests> CategoryList = new ArrayList<>();
            for (Requests request : requestsArrayList) {
                if (request.getCategory().contains(selectedFilterCategory)) {
                    CategoryList.add(request);
                }
            }
            for (Requests request : CategoryList) {
                if (request.getLocation().contains(loc)) {
                    locationList.add(request);
                }
            }
        }
        requestAdapter.searchRequestsArrayList(locationList);
    }

    public void filterCategory(String cat){
        selectedFilterCategory = cat;
        ArrayList<Requests> CategoryList = new ArrayList<>();
        if (selectedFilterLocation.equals("All Locations")) {
            for (Requests request : requestsArrayList) {
                if (request.getCategory().contains(cat)) {
                    CategoryList.add(request);
                }
            }
        }else{
            ArrayList<Requests> locationList = new ArrayList<>();
            for (Requests request : requestsArrayList) {
                if (request.getLocation().contains(selectedFilterLocation)) {
                    locationList.add(request);
                }
            }
            for (Requests request : locationList) {
                if (request.getCategory().contains(cat)) {
                    CategoryList.add(request);
                }
            }
        }
        requestAdapter.searchRequestsArrayList(CategoryList);
    }

    public void allpostsTapped(View view){
        selectedFilterLocation = "All Locations";
        ArrayList<Requests> locationList = new ArrayList<>(requestsArrayList);
        requestAdapter.searchRequestsArrayList(locationList);

    }
    public void anywhereTapped(View view){
        selectedFilterLocation = "Anywhere";
        filterLocation("Anywhere");
    }
    public void CCPTapped(View view){
        selectedFilterLocation = "Changi City Point";
        filterLocation("Changi City Point");
    }
    public void tampinesTapped(View view){
        selectedFilterLocation = "Tampines Mall";
        filterLocation("Tampines Mall");

    }
    public void eastpointTapped(View view){
        selectedFilterLocation = "Eastpoint Mall";
        filterLocation("Eastpoint Mall");

    }

    public void allcatTapped(View view){
        selectedFilterCategory ="All Posts";
        ArrayList<Requests> CategoryList = new ArrayList<>(requestsArrayList);
        requestAdapter.searchRequestsArrayList(CategoryList);

    }
    public void othersTapped(View view){
        selectedFilterCategory ="Others";
        filterCategory("Others");

    }
    public void foodTapped(View view){
        selectedFilterCategory ="Food";
        filterCategory("Food");

    }
    public void beveragesTapped(View view){
        selectedFilterCategory ="Beverages";
        filterCategory("Beverages");

    }
    public void stationeryTapped(View view){
        selectedFilterCategory ="Stationery and Books";
        filterCategory("Stationery and Books");

    }
    public void electronicsTapped(View view){
        selectedFilterCategory = "Electronics";
        filterCategory("Electronics");

    }
    public void toiletriesTapped(View view){
        selectedFilterCategory = "Toiletries";
        filterCategory("Toiletries");

    }
    public void healthcareTapped(View view){
        selectedFilterCategory = "Healthcare";
        filterCategory("Healthcare");

    }
}