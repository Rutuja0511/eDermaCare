package com.example.edermacarelatestt;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class IncomingAppointments extends AppCompatActivity {

    private FirebaseFirestore db;
    private View rootView;

    Button rescheduler, confirmer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming_appointment);

        Log.d(TAG, "onCreate: Activity created");
        System.out.println("oncereate incoming");
        db = FirebaseFirestore.getInstance();

        rootView = findViewById(android.R.id.content);


        //confirmer = rootView.findViewById(R.id.confirmer);

        String userId = getIntent().getStringExtra("user_email");
        if (userId != null) {
            loadPendingAppointments(userId);
        } else {
            System.out.println("user id is null");
        }

    }

    private void loadPendingAppointments(String userId) {
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
                            System.out.println("no of a" + appointments.size());
                            Log.d(TAG, "Number of appointments: " + appointments.size());
                            populateAppointments(appointments, IncomingAppointments.this);
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
            rescheduler = rootView.findViewById(R.id.rescheduler);

            // Populate data from the appointment map
            patient_nameTextView.setText(" "+appointment.get("name"));
            timeTextView.setText(" "+appointment.get("time"));
            dateTextView.setText(" "+appointment.get("date"));
            diseaseTextView.setText(" "+appointment.get("disease"));
            genderTextView.setText(" "+appointment.get("gender"));

            // Load image from URL using a library like Picasso or Glide
            // Example with Glide:
            String imageUrl = (String) appointment.get("ImageURL");
            System.out.println(imageUrl);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context).load(imageUrl).into(imageView);
            }else {
                imageView.setImageResource(R.drawable.upload_icon);
            }

            rescheduler.setOnClickListener(v -> {
                Intent intent = new Intent(IncomingAppointments.this, RescheduleAppointment.class);
                startActivity(intent);
            });
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
