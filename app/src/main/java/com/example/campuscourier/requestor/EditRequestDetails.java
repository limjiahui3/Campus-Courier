package com.example.campuscourier.requestor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.campuscourier.R;
import com.example.campuscourier.shared.MyApplication;
import com.example.campuscourier.shared.Requests;
import com.example.campuscourier.shared.FirebaseHelper;
import com.example.campuscourier.shared.ThemeManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class EditRequestDetails extends AppCompatActivity {

    Requests r;
    TextInputEditText inputItem, inputDescription;
    DatePicker datePicker;
    TimePicker timePicker;
    Spinner inputLocation, inputUrgency, inputCategory;
    Button buttonAddImage, buttonRemoveImage, buttonSave, buttonBackToRequestDetails;
    ImageView imageView;
    int SELECT_PICTURE = 200;
    Uri selectedImageUri;
    FirebaseAuth mAuth;
    @SuppressLint("StaticFieldLeak")
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    public static final String NEXT_SCREEN = "details_screen";
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "ReqAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_request_details);

        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        inputItem = findViewById(R.id.inputItem);
        inputDescription = findViewById(R.id.inputDescription);
        buttonAddImage = findViewById(R.id.buttonAddImage);
        buttonRemoveImage = findViewById(R.id.buttonRemoveImage);
        imageView = findViewById(R.id.image);
        imageView.setImageResource(R.drawable.image);
        datePicker = findViewById(R.id.date);
        timePicker = findViewById(R.id.time);
        timePicker.setIs24HourView(true);
        inputLocation = findViewById(R.id.inputLocation);
        inputUrgency = findViewById(R.id.inputUrgency);
        inputCategory = findViewById(R.id.inputCategories);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBackToRequestDetails = findViewById(R.id.buttonBackToRequestDetails);

        ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_item);
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_item);
        inputLocation.setAdapter(adapterLocation);

        ArrayAdapter<CharSequence> adapterUrgency = ArrayAdapter.createFromResource(this, R.array.urgency, android.R.layout.simple_spinner_item);
        adapterUrgency.setDropDownViewResource(android.R.layout.simple_spinner_item);
        inputUrgency.setAdapter(adapterUrgency);

        ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_item);
        inputCategory.setAdapter(adapterCategory);

        if (getIntent().hasExtra(RequestDetails.NEXT_SCREEN)) {
            // get the Serializable data model class with the details in it
            r = (Requests) getIntent().getSerializableExtra(RequestDetails.NEXT_SCREEN);
        }
        if (r != null) {
            inputItem.setText(r.getItem());
            inputCategory.setSelection(adapterCategory.getPosition(r.getCategory()));
            inputDescription.setText(r.getDescription());
            if(!Objects.equals(r.getImageStorageUri(), "")){
                StorageReference ref = storage.getReferenceFromUrl(r.getImageStorageUri());
                final long MAX_BYTE = 2100 * 1600;
                ref.getBytes(MAX_BYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                        Log.d("IMAGE", "image shown");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.d("IMAGE", "image not shown");
                    }});}
            datePicker.updateDate(r.getYear(), r.getMonth(), r.getDay());
            timePicker.setHour(r.getHour());
            timePicker.setMinute(r.getMinute());
            inputLocation.setSelection(adapterLocation.getPosition(r.getLocation()));
            inputUrgency.setSelection(adapterUrgency.getPosition(r.getUrgency()));
        }

        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }});

        buttonRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageResource(R.drawable.image);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String item, description, date, time, location, urgency, imageUri, imageStorageUri, category;

                item = String.valueOf(inputItem.getText());
                description = String.valueOf(inputDescription.getText());
                date = datePicker.getYear()+format((datePicker.getMonth()+1))+format(datePicker.getDayOfMonth());
                time = format(timePicker.getHour())+format(timePicker.getMinute());
                location = String.valueOf(inputLocation.getSelectedItem());
                urgency = String.valueOf(inputUrgency.getSelectedItem());
                category = String.valueOf(inputCategory.getSelectedItem());

                if (TextUtils.isEmpty(item)){
                    Toast.makeText(getApplicationContext(), "Enter item", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("item", item);
                db.collection("posts").document(r.getDocId()).update("item", item);

                db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("description", description);
                db.collection("posts").document(r.getDocId()).update("description", description);

                db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("date", date);
                db.collection("posts").document(r.getDocId()).update("date", date);

                db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("time", time);
                db.collection("posts").document(r.getDocId()).update("time", time);

                db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("location", location);
                db.collection("posts").document(r.getDocId()).update("location", location);

                db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("urgency", urgency);
                db.collection("posts").document(r.getDocId()).update("urgency", urgency);

                db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("category", category);
                db.collection("posts").document(r.getDocId()).update("category", category);

                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                if (null != selectedImageUri) {
                    // Get the data from an ImageView as bytes
                    FirebaseHelper.deleteImageFromStorage(r);
                    imageUri = String.valueOf(selectedImageUri);
                    StorageReference imageRef = storageRef.child(userId).child(imageUri);
                    bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = imageRef.putBytes(data);
                    imageStorageUri = "gs://campus-courier-3b8be.appspot.com/"+userId+"/"+imageUri;
                    Toast.makeText(MyApplication.getAppContext(), "Editing...", Toast.LENGTH_SHORT).show();
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully, now load it
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle unsuccessful upload
                            Log.d("IMAGE", "Image upload failed: " + e.getMessage());
                        }
                    });

                    db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("imageUri", imageUri);
                    db.collection("posts").document(r.getDocId()).update("imageUri", imageUri);

                    db.collection("users").document(r.getUserId()).collection("posts").document(r.getDocId()).update("imageStorageUri", imageStorageUri);
                    db.collection("posts").document(r.getDocId()).update("imageStorageUri", imageStorageUri);
                }

                else{
                    if(imageView.getDrawable()==null){
                        FirebaseHelper.deleteImageFromStorage(r);
                    }
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        buttonBackToRequestDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RequestDetails.class);
                intent.putExtra(NEXT_SCREEN, r);
                startActivity(intent);
                finish();
            }
        });
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        // pass the constant to compare it with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // compare the resultCode with the SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, true));
                }
            }
        }
    }

    public String format(int x){
        if (String.valueOf(x).length()==1){
            return "0"+x;
        }
        return String.valueOf(x);
    }
}