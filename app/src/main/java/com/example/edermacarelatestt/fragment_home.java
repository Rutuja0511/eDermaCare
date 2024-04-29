package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class fragment_home extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Button GetStarted;
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        GetStarted = rootView.findViewById(R.id.getStarted);

        // Set OnClickListener for the button
        GetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activitydash2.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}