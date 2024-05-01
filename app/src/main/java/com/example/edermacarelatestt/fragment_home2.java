package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class fragment_home2 extends Fragment {
    Button GetStarted;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home2, container, false);

        // Initialize the button
        GetStarted = rootView.findViewById(R.id.getStarted);

        // Set OnClickListener for the button
        GetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get a reference to the parent activity
                Activitydash2 parentActivity = (Activitydash2) getActivity();
                if (parentActivity != null) {
                    // Get the DrawerLayout from the parent activity
                    DrawerLayout drawerLayout = parentActivity.findViewById(R.id.drawer_layout);
                    // Open the drawer
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        return rootView;
    }
}
