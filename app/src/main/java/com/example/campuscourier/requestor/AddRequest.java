package com.example.campuscourier.requestor;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.campuscourier.shared.FirebaseHelper;
import com.example.campuscourier.shared.MyApplication;
import com.example.campuscourier.shared.ThemeManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class AddRequest extends AppCompatActivity {

    TextInputEditText inputItem, inputDescription;
    DatePicker datePicker;
    TimePicker timePicker;
    Spinner inputLocation, inputUrgency, inputCategory;
    Button buttonAddImage, buttonRemoveImage, buttonPost, buttonBackToHome;
    ImageView imageView;
    int SELECT_PICTURE = 200;
    Uri selectedImageUri;
    FirebaseAuth mAuth;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "ReqAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);

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
        buttonPost = findViewById(R.id.buttonPost);
        buttonBackToHome = findViewById(R.id.buttonBackToHome);

        buttonBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                finish();
            }
        });

        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }});

        buttonRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != selectedImageUri) {
                    imageView.setImageResource(R.drawable.image);
                }
            }
        });

        ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_item);
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_item);
        inputLocation.setAdapter(adapterLocation);

        ArrayAdapter<CharSequence> adapterUrgency = ArrayAdapter.createFromResource(this, R.array.urgency, android.R.layout.simple_spinner_item);
        adapterUrgency.setDropDownViewResource(android.R.layout.simple_spinner_item);
        inputUrgency.setAdapter(adapterUrgency);

        ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_item);
        inputCategory.setAdapter(adapterCategory);

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item, date, time, location, urgency, category;
                String description = "";
                String imageUri = "";
                String imageStorageUri = "";
                String status = "Not Accepted";
                String supplierId = "";
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

                if (null != selectedImageUri) {
                    // Get the data from an ImageView as bytes
                    imageUri = String.valueOf(selectedImageUri);
                    StorageReference imageRef = storageRef.child(userId).child(imageUri);
                    imageView.setDrawingCacheEnabled(true);
                    imageView.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = imageRef.putBytes(data);
                    imageStorageUri = "gs://campus-courier-3b8be.appspot.com/"+userId+"/"+imageUri;
                    Toast.makeText(MyApplication.getAppContext(), "Posting...", Toast.LENGTH_SHORT).show();
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
                    FirebaseHelper.addDataToFirestore(item, description, imageUri, imageStorageUri, date, time, location, urgency, status, userId, supplierId, category);
                } else{
                    FirebaseHelper.addDataToFirestore(item, description, imageUri, imageStorageUri, date, time, location, urgency, status, userId, supplierId, category);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AddRequest.this);
                builder.setTitle("Notice");
                builder.setMessage("Normal requests require an additional $1.50 fee to be paid to the supplier. Urgent requests require an additional $3.00 fee to be paid to the supplier.");
                builder.setPositiveButton("I will pay the fee.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                        Toast.makeText(MyApplication.getAppContext(), "Request posted.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
            });
                builder.show();
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