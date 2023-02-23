package com.example.gymbuddy;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GetUserInfoActivity extends AppCompatActivity {

    private EditText sportsEt, ageEt, heightEt;
    private ProgressBar progress_bar;
    private ImageView profilePic;
    public Uri imageUri;
    private RadioGroup genderRg;
    private RadioButton maleRb, femaleRb, otherRb;
    private String uid;
    private String gender = "Male";
    private FirebaseFirestore rootRef;
    public Map userMapDetailed = new HashMap<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");
    private String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_info);
        getSupportActionBar().setTitle("Registration");
        findViews();


        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootRef = FirebaseFirestore.getInstance();

        genderRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.maleRb) {
                    gender = "Male";
                } else if (i == R.id.femaleRb) {
                    gender = "FeMale";
                } else if (i == R.id.otherRb) {
                    gender = "Other";
                }
            }
        });


    }


    public void continueBtn(View view) {
        if (sportsEt.getText().toString().length() < 1) {
            sportsEt.setError("Sports field must be filled");
            return;
        }
        if (ageEt.getText().toString().length() < 1) {
            ageEt.setError("Age field must be filled");
            return;
        }
        if (heightEt.getText().toString().length() < 1) {
            heightEt.setError("Height field must be filled");
            return;
        }
        setDefaultPicture();
        uploadUriToStorage(imageUri);

    }

    private void setDefaultPicture() {
        if (imageUri == null) {
            int drawable = 0;
            if (gender.equals("Male"))
                drawable = R.drawable.malegym;
            if (gender.equals("FeMale"))
                drawable = R.drawable.femalegym;
            if (gender.equals("Other"))
                drawable = R.drawable.othergym;
            imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(drawable)
                    + '/' + getResources().getResourceTypeName(drawable) + '/' + getResources().getResourceEntryName(drawable));
        }
    }

    public void choosePicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Glide.with(this)
                    .load(imageUri.toString())
                    .into(profilePic);

        }
    }

    public void uploadUriToStorage(Uri uri) {
        progress_bar.setVisibility(View.VISIBLE);
        // Upload the image to Firebase Storage
        UploadTask uploadTask = imagesRef.putFile(uri);

        // Add a listener for when the upload is complete
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString(); // Store the download URL in a local variable
                        saveData();


                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error
            }
        });
    }


    private void saveData() {


        userMapDetailed.put("uid", uid);
        userMapDetailed.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        userMapDetailed.put("sports", sportsEt.getText().toString());
        userMapDetailed.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        userMapDetailed.put("height", heightEt.getText().toString());
        userMapDetailed.put("age", ageEt.getText().toString());
        userMapDetailed.put("gender", gender);
        userMapDetailed.put("image", imageUrl);



        rootRef.collection("Users").document(uid).set(userMapDetailed).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progress_bar.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress_bar.setVisibility(View.GONE);
                Toast.makeText(GetUserInfoActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void findViews() {
        heightEt = findViewById(R.id.heightEt);
        sportsEt = findViewById(R.id.sportsEt);
        ageEt = findViewById(R.id.ageEt);
        genderRg = findViewById(R.id.genderRg);
        progress_bar = findViewById(R.id.progress_bar);
        profilePic = findViewById(R.id.profilePic);

    }
}