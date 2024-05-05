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
            // Create a new instance of the RescheduleAppointment fragment
            RescheduleAppointment fragment = new RescheduleAppointment();

            // Replace the current fragment with RescheduleAppointment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)  // Optional: allows the user to navigate back to the previous Fragment by pressing the back button
                    .commit();
        });

        return rootView;
    }

}

