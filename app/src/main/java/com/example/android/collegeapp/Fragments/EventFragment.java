package com.example.android.collegeapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.collegeapp.Adapters.EventRecyclerAdapter;
import com.example.android.collegeapp.Models.Event;
import com.example.android.collegeapp.Activities.NewEventActivity;
import com.example.android.collegeapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private RecyclerView eventRecyclerView;
    private EventRecyclerAdapter eventRecyclerAdapter;
    private List<Event> event_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;


    public EventFragment() {}


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event, container, false);

        FloatingActionButton addEventBtn = view.findViewById(R.id.addEventBtn);
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewEventActivity.class));
            }
        });

        eventRecyclerView = view.findViewById(R.id.eventRecyclerView);

        event_list = new ArrayList<>();
        eventRecyclerAdapter = new EventRecyclerAdapter(event_list);

        eventRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        eventRecyclerView.setAdapter(eventRecyclerAdapter);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            eventRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {
                        loadMoreEvents();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Events").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e == null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            if (isFirstPageFirstLoad) {
                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            }

                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String eventId = doc.getDocument().getId();
                                    Event event = doc.getDocument().toObject(Event.class).withID(eventId);

                                    if (isFirstPageFirstLoad) {
                                        event_list.add(event);
                                    } else {
                                        event_list.add(0, event);
                                    }
                                    eventRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                            isFirstPageFirstLoad = false;
                        }
                    }
                }
            });
        }
        return view;
    }

    public void loadMoreEvents() {
        Query nextQuery = firebaseFirestore.collection("Events")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String eventId = doc.getDocument().getId();
                                Event event = doc.getDocument().toObject(Event.class).withID(eventId);
                                event_list.add(event);
                                eventRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }
}
