package com.example.android.collegeapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.collegeapp.Activities.EventDetailsActivity;
import com.example.android.collegeapp.Models.Event;
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
import java.util.List;
import java.util.Locale;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder> {

    public List<Event> event_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public EventRecyclerAdapter(List<Event> event_list) {
        this.event_list = event_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_view, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new EventRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String eventPostID = event_list.get(position).EventId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        final String userId = event_list.get(position).getUser_id();

        final String imageUrl = event_list.get(position).getImage_url();
        holder.setEventImage(imageUrl);

        final String title = event_list.get(position).getTitle();
        holder.setEventTitle(title);

        final String eventOrg = event_list.get(position).getEventOrg();
        holder.setOrgName(eventOrg);

        final String desc = event_list.get(position).getDesc();
        final String addr = event_list.get(position).getAddress();
        final String c1 = event_list.get(position).getContact_one();
        final String c2 = event_list.get(position).getContact_two();
        final String orgemail = event_list.get(position).getOrgEmail();

        final String date = event_list.get(position).getDate();
        final String time = event_list.get(position).getTime();
        holder.setDateTime(date, time);

        holder.eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EventDetailsActivity.class);
                i.putExtra("eventID", eventPostID);
                i.putExtra("url", imageUrl);
                i.putExtra("title", title);
                i.putExtra("desc", desc);
                i.putExtra("addr", addr);
                i.putExtra("eventOrg", eventOrg);
                i.putExtra("c1", c1);
                i.putExtra("c2", c2);
                i.putExtra("orgemail", orgemail);
                i.putExtra("date", date);
                i.putExtra("time", time);
                context.startActivity(i);
            }
        });

        firebaseFirestore.collection("Events/" + eventPostID + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int count = queryDocumentSnapshots.size();
                        holder.updateInterestedCount(count);
                    } else {
                        holder.updateInterestedCount(0);
                    }
                }
            }
        });

        firebaseFirestore.collection("Events/" + eventPostID + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (documentSnapshot.exists()) {
                        holder.eventLike.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_red_24dp));
                    } else {
                        holder.eventLike.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_white_24dp));
                    }
                }
            }
        });

        holder.eventLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Events/" + eventPostID + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            HashMap<String, Object> likeMap = new HashMap<>();
                            likeMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Events/" + eventPostID + "/Likes").document(currentUserId).set(likeMap);
                        } else {
                            firebaseFirestore.collection("Events/" + eventPostID + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (event_list != null) {
            return event_list.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView eventTitleView;
        private TextView eventOrgName;
        private TextView interestedCount;
        private TextView eventTime;
        private TextView eventDate;
        private ImageView eventImageView;
        private ImageView eventLike;

        private CardView eventCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            eventCard = mView.findViewById(R.id.eventCard);
            eventLike = mView.findViewById(R.id.eventLike);
        }

        public void setEventImage(String imageUri) {
            eventImageView = mView.findViewById(R.id.eventImage);
            Glide.with(context).load(imageUri).into(eventImageView);
        }

        public void setEventTitle(String title) {
            eventTitleView = mView.findViewById(R.id.eventTitle);
            eventTitleView.setText(title);
        }

        public void setDateTime(String date, String time) {
            eventTime = mView.findViewById(R.id.eventTime);
            eventDate = mView.findViewById(R.id.eventDate);

            eventTime.setText(time);
            eventDate.setText(date);
        }

        public void setOrgName(String orgName) {
            eventOrgName = mView.findViewById(R.id.organiserName);
            eventOrgName.setText(String.format("Organised by : %s", orgName));
        }

        public void updateInterestedCount(int count) {
            interestedCount = mView.findViewById(R.id.interestedCount);
            if (count == 0) {
                interestedCount.setText(String.format(Locale.getDefault(), "%d people are interested", count));
            } else if (count == 1) {
                interestedCount.setText(String.format(Locale.getDefault(), "%d people is interested", count));
            } else {
                interestedCount.setText(String.format(Locale.getDefault(), "%d people are interested", count));
            }
        }
    }
}
