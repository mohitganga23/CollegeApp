package com.example.android.collegeapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.collegeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final CircleImageView profileImage = findViewById(R.id.profileImage);
        final TextView profileName = findViewById(R.id.profileName);
        final TextView profileBranch = findViewById(R.id.profileBranch);
        final TextView profileSem = findViewById(R.id.profileSem);
        final TextView profileEmail = findViewById(R.id.profileEmail);
        Button edit = findViewById(R.id.btn_edit);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        String userId = firebaseAuth.getCurrentUser().getUid();
        final String userEmail = firebaseAuth.getCurrentUser().getEmail();

        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String currentName = task.getResult().getString("name");
                        String currentImage = task.getResult().getString("image");
                        String currentBranch = task.getResult().getString("branch");
                        String currentSem = task.getResult().getString("semester");

                        imageUri = Uri.parse(currentImage);

                        profileName.setText(currentName);
                        profileBranch.setText(currentBranch);
                        profileSem.setText(currentSem);
                        profileEmail.setText(userEmail);
                        profileImage.setImageURI(imageUri);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.ic_account_circle_gray_24dp);

                        Glide.with(ProfileActivity.this).setDefaultRequestOptions(placeholderRequest)
                                .load(currentImage).into(profileImage);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, "Firestore Retrieve ERROR" + error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SetupActivity.class));
            }
        });
    }
}
