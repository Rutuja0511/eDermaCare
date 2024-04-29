package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class fragment_home2 extends Fragment {
    Button GetStarted ;
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
                Intent intent = new Intent(getActivity(), Activitydash2.class);
                startActivity(intent);
            }
        });

        return rootView;

    }


}