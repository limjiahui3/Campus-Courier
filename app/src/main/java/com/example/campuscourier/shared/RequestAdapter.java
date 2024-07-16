package com.example.campuscourier.shared;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campuscourier.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    // creating variables for our ArrayList and context
    private ArrayList<Requests> requestsArrayList;
    private Context context;
    private OnClickListener onClickListener;
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    // creating constructor for our adapter class
    public RequestAdapter(ArrayList<Requests> requestsArrayList, Context context) {
        this.requestsArrayList = requestsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_card_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Requests requests = requestsArrayList.get(position);
        holder.requestedItem.setText(requests.getItem());
        holder.itemLocation.setText(requests.getLocation());
        holder.itemStatus.setText(requests.getStatus());
        holder.requestDate.setText(requests.getDate());
        holder.requestTime.setText(requests.getTime());

        if(requests.getUrgency().equals("Urgent")){
            holder.urgency.setText("!!");
            Log.d("URGENT", "set urgent");
        }
        else{
            holder.urgency.setText("");
            Log.d("URGENT", "set not urgent");
        }

        if(!Objects.equals(requests.getImageStorageUri(), "")) {
            StorageReference ref = storage.getReferenceFromUrl(requests.getImageStorageUri());
            Log.d("IMAGE", "image found");
            final long MAX_BYTE = 2100 * 1600;
            ref.getBytes(MAX_BYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.postImage.setImageBitmap(bitmap);
                    Log.d("IMAGE", "image shown");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("IMAGE", "image not shown");
                }});}
        else{
            holder.postImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.image));
        }

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

    public void searchRequestsArrayList(ArrayList<Requests> searchList){
        requestsArrayList = searchList;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Requests r);
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private ImageView postImage;
        private TextView requestedItem, itemLocation, itemStatus, requestDate, requestTime, urgency;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            postImage = itemView.findViewById(R.id.postImage);
            requestedItem = itemView.findViewById(R.id.requestedItem);
            itemLocation = itemView.findViewById(R.id.itemLocation);
            itemStatus = itemView.findViewById(R.id.itemStatus);
            requestDate = itemView.findViewById(R.id.requestDate);
            requestTime = itemView.findViewById(R.id.requestTime);
            urgency = itemView.findViewById(R.id.urgency);
        }
    }
}