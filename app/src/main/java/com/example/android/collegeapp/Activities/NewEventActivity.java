package com.example.android.collegeapp.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.collegeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NewEventActivity extends AppCompatActivity {

    private ImageView eventImage, cancelImage;

    private EditText eventTitle, eventDescription, eventAddress, eventOrganiser,
            contactOne, contactTwo, eventOrganiserEmail;

    private TextView date, time;

    private LinearLayout ll1;

    private Button createEvent, addImagePoster, datePicker, timePicker;

    private String downloadURL = "";

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private Calendar c;
    private Context ctx = this;

    private String dateString;
    private String timeString;

    private Uri postImageUri = null;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);

        ll1 = findViewById(R.id.linearLayoutEvent);

        eventImage = findViewById(R.id.newEventImage);
        cancelImage = findViewById(R.id.cancelPoster);

        date = findViewById(R.id.selectedDate);
        time = findViewById(R.id.selectedTime);

        date.setText(String.format(Locale.getDefault(), "%d/%d/%d", mDay, mMonth, mYear));
        dateString = mDay + "/" + mMonth + "/" + mYear;

        time.setText(String.format(Locale.getDefault(), "%d:%d", mHour, mMinute));
        timeString = mHour + ":" + mMinute;

        eventTitle = findViewById(R.id.newEventTitle);
        eventDescription = findViewById(R.id.newEventDesc);
        eventAddress = findViewById(R.id.newEventAddress);
        eventOrganiser = findViewById(R.id.eventOrganiser);
        eventOrganiserEmail = findViewById(R.id.eventOrganiserEmail);
        contactOne = findViewById(R.id.contactNumberOne);
        contactTwo = findViewById(R.id.contactNumberTwo);

        addImagePoster = findViewById(R.id.addImage);
        createEvent = findViewById(R.id.createEventBtn);
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        current_user_id = mAuth.getCurrentUser().getUid();

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_date_picker();
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_time_picker();
            }
        });

        addImagePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setMinCropWindowSize(512, 512)
                        .start(NewEventActivity.this);
            }
        });

        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.GONE);
                postImageUri = null;
            }
        });


        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String title = eventTitle.getText().toString().trim();
                final String desc = eventDescription.getText().toString().trim();
                final String address = eventAddress.getText().toString().trim();
                final String eventOrg = eventOrganiser.getText().toString().trim();
                final String contact_one = contactOne.getText().toString().trim();

                final String contact_two;
                if (!(contactTwo.getText().toString().isEmpty())) {
                    contact_two = contactTwo.getText().toString().trim();
                } else {
                    contact_two = "";
                }

                final String orgEmail = eventOrganiserEmail.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) &&
                        !TextUtils.isEmpty(address) && !TextUtils.isEmpty(eventOrg) &&
                        !TextUtils.isEmpty(contact_one) && !TextUtils.isEmpty(orgEmail)
                        && postImageUri != null) {

                    final String randomName = UUID.randomUUID().toString();
                    final StorageReference filePath = storageReference.child("event_images").child(randomName + ".jpg");

                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadURL = uri.toString();

                                        Map<String, Object> eventMap = new HashMap<>();
                                        eventMap.put("image_url", downloadURL);
                                        eventMap.put("title", title);
                                        eventMap.put("desc", desc);
                                        eventMap.put("address", address);
                                        eventMap.put("date", dateString);
                                        eventMap.put("time", timeString);
                                        eventMap.put("eventOrg", eventOrg);
                                        eventMap.put("contact_one", contact_one);
                                        eventMap.put("contact_two", contact_two);
                                        eventMap.put("orgEmail", orgEmail);
                                        eventMap.put("user_id", current_user_id);
                                        eventMap.put("timestamp", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Events").add(eventMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {
                                                    startActivity(new Intent(NewEventActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void show_date_picker() {
        c = Calendar.getInstance();
        int mYearParam = mYear;
        int mMonthParam = mMonth - 1;
        int mDayParam = mDay;

        DatePickerDialog datePickerDialog = new DatePickerDialog(ctx,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mMonth = monthOfYear + 1;
                        mYear = year;
                        mDay = dayOfMonth;
                        date.setText(String.format(Locale.getDefault(), "%d/%d/%d", mDay, mMonth, mYear));
                        dateString = mDay + "/" + mMonth + "/" + mYear;
                    }
                }, mYearParam, mMonthParam, mDayParam);
        datePickerDialog.show();
    }

    private void show_time_picker() {

        TimePickerDialog timePickerDialog = new TimePickerDialog(ctx,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int pHour,
                                          int pMinute) {
                        mHour = pHour;
                        mMinute = pMinute;
                        time.setText(String.format(Locale.getDefault(), "%d:%d", mHour, mMinute));
                        timeString = mHour + ":" + mMinute;
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                eventImage.setImageURI(postImageUri);
                ll1.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
