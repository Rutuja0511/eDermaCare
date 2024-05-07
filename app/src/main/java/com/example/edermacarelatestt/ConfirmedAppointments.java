package com.example.edermacarelatestt;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ConfirmedAppointments  extends AppCompatActivity {
    private FirebaseFirestore db;
    private View rootView;


    String userId;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmed_appointment);

        Log.d(TAG, "onCreate: Activity created");
        System.out.println("oncreate confirm");
        db = FirebaseFirestore.getInstance();

        rootView = findViewById(android.R.id.content);

        // Get the user ID from Intent
        userId = getIntent().getStringExtra("user_email");
        if (userId != null) {
            loadConfirmedAppointments();
        } else {
            System.out.println("user id is null");
        }
    }
    private void loadConfirmedAppointments() {
        System.out.println("in load");
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
                                Boolean pendingValue = document.getBoolean("confirmed");
                                if (pendingValue != null && pendingValue.booleanValue()) {
                                    Map<String, Object> appointmentData = document.getData();
                                    // Store the document ID (appointment ID) directly in the appointment data map
                                    appointmentData.put("documentId", document.getId());
                                    appointments.add(appointmentData);
                                }
                            }
                            System.out.println("no of a" + appointments.size());
                            Log.d(TAG, "Number of appointments: " + appointments.size());
                            populateAppointments(appointments, ConfirmedAppointments.this);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void populateAppointments(ArrayList<Map<String, Object>> appointments, Context context) {
        LinearLayout parentLayout = rootView.findViewById(R.id.parent_confirmed_layout); // assuming parent layout id is parent_layout
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
            patient_nameTextView.setText(" " + appointment.get("name"));
            timeTextView.setText(" " + appointment.get("time"));
            dateTextView.setText(" " + appointment.get("date"));
            diseaseTextView.setText(" " + appointment.get("disease"));
            genderTextView.setText(" " + appointment.get("gender"));

            // Load image from URL using a library like Picasso or Glide
            // Example with Glide:
            String imageUrl = (String) appointment.get("ImageURL");
            System.out.println(imageUrl);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context).load(imageUrl).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.upload_icon);
            }

            // Set onClickListener for the rescheduler button



//            // Retrieve appointment ID (document ID) from appointment data
//            String appointmentId = (String) appointment.get("documentId"); // Assuming the field name is "documentId"
//            System.out.println(appointmentId+"yp");
//            String userId = getIntent().getStringExtra("user_email");
            // Set onClickListener for the confirm button

        }
    }
}
