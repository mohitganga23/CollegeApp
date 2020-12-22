package com.example.android.collegeapp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.android.collegeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        final ImageView eventLike = findViewById(R.id.eventLike);
        ImageView eventImage = findViewById(R.id.eventRImage);

        TextView eventTitle = findViewById(R.id.eventRTitle);
        TextView eventDesc = findViewById(R.id.eventAbout);
        TextView eventAddress = findViewById(R.id.eventRAddress);
        TextView eventOrg = findViewById(R.id.eventROrganiser);
        TextView contact = findViewById(R.id.eventROrgMob);
        TextView orgEmail = findViewById(R.id.eventROrgEmail);
        TextView eventDate = findViewById(R.id.eventRDate);
        TextView eventTime = findViewById(R.id.eventRTime);

        final Button register = findViewById(R.id.register);

        Toolbar t = findViewById(R.id.eventToolbar);
        t.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        final String id = getIntent().getStringExtra("eventID");
        final String url = getIntent().getStringExtra("url");
        final String title = getIntent().getStringExtra("title");
        final String desc = getIntent().getStringExtra("desc");
        final String addr = getIntent().getStringExtra("addr");
        final String org = getIntent().getStringExtra("eventOrg");
        final String c1 = getIntent().getStringExtra("c1");
        final String c2 = getIntent().getStringExtra("c2");
        final String email = getIntent().getStringExtra("orgemail");
        final String date = getIntent().getStringExtra("date");
        final String time = getIntent().getStringExtra("time");

        eventTitle.setText(title);
        eventDesc.setText(desc);
        eventAddress.setText(addr);
        eventOrg.setText(org);
        orgEmail.setText(email);
        eventDate.setText(date);
        eventTime.setText(time);

        contact.setText(c1);
        if (!c2.isEmpty()) {
            contact.append(", " + c2);
        }

        Glide.with(this).load(url).into(eventImage);

        firebaseFirestore.collection("Events/" + id + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int count = queryDocumentSnapshots.size();
                        updateInterestedCount(count);
                    } else {
                        updateInterestedCount(0);
                    }
                }
            }
        });

        firebaseFirestore.collection("Events/" + id + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (documentSnapshot.exists()) {
                        eventLike.setImageDrawable(getDrawable(R.drawable.ic_favorite_red_24dp));
                    } else {
                        eventLike.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
                    }
                }
            }
        });

        eventLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Events/" + id + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            HashMap<String, Object> likeMap = new HashMap<>();
                            likeMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Events/" + id + "/Likes").document(currentUserId).set(likeMap);
                        } else {
                            firebaseFirestore.collection("Events/" + id + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked) {
                    register.setText("Register");
                    clicked = false;
                } else {
                    register.setText("Registered");
                    clicked = true;
                }
            }
        });
    }

    public void updateInterestedCount(int count) {
        TextView ic = findViewById(R.id.interested_count);
        if (count == 0) {
            ic.setText(String.format(Locale.getDefault(), "%d people are interested", count));
        } else if (count == 1) {
            ic.setText(String.format(Locale.getDefault(), "%d people is interested", count));
        } else {
            ic.setText(String.format(Locale.getDefault(), "%d people are interested", count));
        }
    }
}
