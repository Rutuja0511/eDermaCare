package com.example.edermacarelatestt;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

public class IncomingAppointments extends Fragment {

    Button reschuler;
    private FirebaseFirestore db;
    private View rootView;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Fragment created");
        System.out.println("oncereate incoming");
        db = FirebaseFirestore.getInstance();

        rootView = inflater.inflate(R.layout.incoming_appointment, container, false);
        String userId = getArguments() != null ? getArguments().getString("user_email") : null;
        if (userId != null) {
            loadPendingAppointments(userId, requireContext());
        }
        return rootView;


//        reschuler = rootView.findViewById(R.id.button_reschedule);
//        reschuler.setOnClickListener(v -> {
//            Intent intent  = new Intent(requireContext(), RescheduleAppointment.class);
//            startActivity(intent);
//        });

//        return rootView;
    }

    private void loadPendingAppointments(String userId, Context context) {
        db.collection("dermatologist")
                .document(userId)
                .collection("appointment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Map<String, Object>> appointments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Check the "pending" field within each document
                                boolean isPending = document.getBoolean("pending");
                                if (isPending) {
                                    Map<String, Object> appointmentData = document.getData();
                                    appointments.add(appointmentData);
                                }
                            }
                            System.out.println("no of a"+appointments.size());
                            Log.d(TAG, "Number of appointments: " + appointments.size());
                            populateAppointments(appointments, context);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void populateAppointments(ArrayList<Map<String, Object>> appointments, Context context) {

        LinearLayout parentLayout = rootView.findViewById(R.id.parent_incoming_layout); // assuming parent layout id is parent_layout
        LayoutInflater inflater = LayoutInflater.from(context);

        for (Map<String, Object> appointment : appointments) {
            CardView cardView = (CardView) inflater.inflate(R.layout.cardappointment, parentLayout, false);
            parentLayout.addView(cardView);

            TextView patient_nameTextView = cardView.findViewById(R.id.patient_name);
            TextView timeTextView = cardView.findViewById(R.id.time);
            TextView dateTextView = cardView.findViewById(R.id.date);
            TextView diseaseTextView = cardView.findViewById(R.id.result);
            TextView genderTextView = cardView.findViewById(R.id.gender);
            ImageView imageView = cardView.findViewById(R.id.imageView);

            // Populate data from the appointment map
            patient_nameTextView.setText("Name: " + appointment.get("name"));
            timeTextView.setText("Time: " + appointment.get("time"));
            dateTextView.setText("Date: " + appointment.get("date"));
            diseaseTextView.setText("Disease: " + appointment.get("disease"));
            genderTextView.setText("Gender: " + appointment.get("gender"));

            // Load image from URL using a library like Picasso or Glide
            // Example with Glide:
            String imageUrl = (String) appointment.get("imageURL");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context).load(imageUrl).into(imageView);
            }
        }
    }
    private CardView createHistoryItemLayout(Context context, DocumentSnapshot document) {
        String result = document.getString("result");
        String date = document.getString("date");
        String time = document.getString("time");
        String imageURL = document.getString("imageURL");

        LayoutInflater inflater = LayoutInflater.from(context);
//        LinearLayout historyItemLayout = (LinearLayout) inflater.inflate(R.layout.history_item_layout, null);
        CardView historyItemLayout = (CardView) inflater.inflate(R.layout.history_item_layout, null);

        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 2, 0, 22); // Add bottom margin for spacing
        historyItemLayout.setLayoutParams(layoutParams);

        ImageView imageView = historyItemLayout.findViewById(R.id.imageView);
        TextView dateTextView = historyItemLayout.findViewById(R.id.date);
        TextView timeTextView = historyItemLayout.findViewById(R.id.time);
        TextView resultTextView = historyItemLayout.findViewById(R.id.result);

        dateTextView.setText(date);
        timeTextView.setText(time);
        resultTextView.setText(result);

        if (imageURL != null && !imageURL.isEmpty()) {
            Glide.with(context).load(imageURL).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.upload_icon);
        }

        return historyItemLayout;
    }
}

