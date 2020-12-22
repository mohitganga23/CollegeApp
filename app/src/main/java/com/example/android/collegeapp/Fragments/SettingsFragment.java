package com.example.android.collegeapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.android.collegeapp.Activities.ProfileActivity;
import com.example.android.collegeapp.R;
import com.example.android.collegeapp.Activities.RegistrationActivity;
import com.example.android.collegeapp.Activities.SettingsMenuList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private String[] menuTexts = {
            "My Profile",
            "Notifications",
            "Account Settings",
            "App Settings",
            "Log out",
            "Delete Account"};

    private Integer[] menuImages = {
            R.drawable.ic_account_circle_black_24dp,
            R.drawable.ic_notifications_active_black_24dp,
            R.drawable.ic_settings_black_24dp,
            R.drawable.branch,
            R.drawable.ic_close_black_24dp,
            R.drawable.ic_delete_forever_black_24dp};

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ListView listView = view.findViewById(R.id.list);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        SettingsMenuList settingsMenuList = new SettingsMenuList(getActivity(), menuTexts, menuImages);
        listView.setAdapter(settingsMenuList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                } else if (position == 1) {
                    Toast.makeText(getActivity(), "Notifications", Toast.LENGTH_SHORT).show();
                } else if (position == 2) {
                    Toast.makeText(getActivity(), "Account Settings", Toast.LENGTH_SHORT).show();
                } else if (position == 3) {
                    Toast.makeText(getActivity(), "App Settings", Toast.LENGTH_SHORT).show();
                } else if (position == 4) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Do you want to log out ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), RegistrationActivity.class));
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (position == 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Delete Account ?");
                    builder.setMessage("All your data will be lost");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getContext(), RegistrationActivity.class));
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });
        return view;
    }
}
