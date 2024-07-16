package com.example.campuscourier.supplier;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campuscourier.R;

import com.example.campuscourier.shared.RequestAdapter;
import com.example.campuscourier.shared.Requests;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class CheckboxAdapter extends RecyclerView.Adapter<CheckboxAdapter.ViewHolder> {
    // creating variables for our ArrayList and context
    Requests r;
    private ArrayList<Requests> requestsArrayList;
    private Context context;
    private SharedPreferences sharedPref;
    private OnClickListener onClickListener;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    // creating constructor for our adapter class
    public CheckboxAdapter(ArrayList<Requests> requestsArrayList, Context context) {
        this.requestsArrayList = requestsArrayList;
        this.context = context;
        this.sharedPref = context.getSharedPreferences("your_shared_pref_name", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public CheckboxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_checkbox_card, parent,false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        if (viewHolder.requestDate == null) {
            Log.e("ViewHolder", "requestDate is null");
        }
        if (viewHolder.requestTime == null) {
            Log.e("ViewHolder", "requestTime is null");
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CheckboxAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Requests requests = requestsArrayList.get(position);
        holder.requestedItem.setText(requests.getItem());
        holder.itemLocation.setText(requests.getLocation());
        holder.itemStatus.setText(requests.getStatus());
        holder.requestDate.setText(requests.getDate());
        holder.requestTime.setText(requests.getTime());

        holder.r = requests;

        boolean isChecked = sharedPref.getBoolean(requests.getDocId(), false);
        holder.checkBox.setChecked(isChecked);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state of the checkbox
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(requests.getDocId(), isChecked);
                editor.apply();
                if (isChecked) {
                    db.collection("users").document(requests.getUserId()).collection("posts").document(requests.getDocId()).update("status", "Delivering");
                    db.collection("users").document(requests.getSupplierId()).collection("todo").document(requests.getDocId()).update("status", "Delivering");
                    db.collection("posts").document(requests.getDocId()).update("status", "Delivering");
                    Log.d("checked", "isStatusDone: " + requests.getStatus());
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(position, requests);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestsArrayList.size();
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Requests r);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public Requests r;
        public Object id;
        // creating variables for our text views.
        private ImageView postImage;
        private CheckBox checkBox;
        private TextView requestedItem, itemLocation, itemStatus, requestDate, requestTime, uid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            postImage = itemView.findViewById(R.id.postImage);
            requestedItem = itemView.findViewById(R.id.requestedItem);
            itemLocation = itemView.findViewById(R.id.itemLocation);
            itemStatus = itemView.findViewById(R.id.itemStatus);
            requestDate = itemView.findViewById(R.id.requestDate);
            requestTime = itemView.findViewById(R.id.requestTime);
            checkBox = itemView.findViewById(R.id.checkbox);


        }
    }
}