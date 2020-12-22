package com.example.android.collegeapp.Models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class EventId {
    @Exclude
    public String EventId;

    public <T extends EventId> T withID(@NonNull final String id) {
        this.EventId = id;
        return (T) this;
    }
}
