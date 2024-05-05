package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class IncomingAppointments extends Fragment {

    Button reschuler;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.incoming_appointment, container, false);

        reschuler = rootView.findViewById(R.id.button_reschedule);
        reschuler.setOnClickListener(v -> {
            Intent intent  = new Intent(requireContext(), RescheduleAppointment.class);
            startActivity(intent);
        });

        return rootView;
    }

}

