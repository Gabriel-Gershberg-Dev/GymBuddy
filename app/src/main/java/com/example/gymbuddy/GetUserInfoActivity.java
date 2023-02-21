package com.example.gymbuddy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GetUserInfoActivity extends AppCompatActivity {

    EditText sportsEt, ageEt,heightEt;
    ProgressBar progress_bar;
    RadioGroup genderRg;
    RadioButton maleRb, femaleRb, otherRb;
    String uid;
    String gender = "Male";
    FirebaseFirestore rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_info);
        getSupportActionBar().setTitle("Registration");


        heightEt = findViewById(R.id.heightEt);
        sportsEt = findViewById(R.id.sportsEt);
        ageEt = findViewById(R.id.ageEt);
        genderRg = findViewById(R.id.genderRg);
        progress_bar = findViewById(R.id.progress_bar);

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

        saveData();

    }

    private void saveData() {
        progress_bar.setVisibility(View.VISIBLE);

        Map userMapDetailed = new HashMap<>();

        userMapDetailed.put("uid", uid);
        userMapDetailed.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        userMapDetailed.put("sports", sportsEt.getText().toString());
        userMapDetailed.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        userMapDetailed.put("height", heightEt.getText().toString());
        userMapDetailed.put("age", ageEt.getText().toString());
        userMapDetailed.put("gender", gender);



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
}