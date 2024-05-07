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
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class IncomingAppointments extends AppCompatActivity {

    private FirebaseFirestore db;
    private View rootView;

    Button rescheduler, confirmer;
    String userId; // User ID obtained from Intent

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming_appointment);

        Log.d(TAG, "onCreate: Activity created");
        System.out.println("oncreate incoming");
        db = FirebaseFirestore.getInstance();

        rootView = findViewById(android.R.id.content);

        // Get the user ID from Intent
        userId = getIntent().getStringExtra("user_email");
        if (userId != null) {
            loadPendingAppointments();
        } else {
            System.out.println("user id is null");
        }
    }

    private void loadPendingAppointments() {
        db.collection("dermatologist")
                .document(userId)
                .collection("appointments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Map<String, Object>> appointments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Check the "pending" field within each document
                                Boolean pendingValue = document.getBoolean("pending");
                                if (pendingValue != null && pendingValue.booleanValue()) {
                                    Map<String, Object> appointmentData = document.getData();
                                    // Store the document ID (appointment ID) directly in the appointment data map
                                    appointmentData.put("documentId", document.getId());
                                    appointments.add(appointmentData);
                                }
                            }
                            System.out.println("no of incoming appointments" + appointments.size());
                            System.out.println(userId);
                            Log.d(TAG, "Number of appointments: " + appointments.size());
                            populateAppointments(appointments, IncomingAppointments.this);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void confirmAppointment(String userId, String appointmentId) {
        if (userId == null || appointmentId == null) {
            System.out.println("userId or appointmentId is null");
            return;
        }else{
            System.out.println("thike"+ userId +" "+appointmentId);
        }

        // Get the appointment document reference
        DocumentReference appointmentRef = db.collection("dermatologist")
                .document(userId)
                .collection("appointments")
                .document(appointmentId);

        // Update fields
        appointmentRef.update("confirmed", true,
                        "pending", false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Print confirmation message to console
                        Log.d(TAG, "Appointment confirmed successfully.");
                        // Reload pending appointments
                        loadPendingAppointments();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating appointment: ", e);
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
            rescheduler = cardView.findViewById(R.id.rescheduler);
            confirmer = cardView.findViewById(R.id.confirmer);
            String location = String.valueOf(appointment.get("clinicAddress"));

            System.out.println("address"+location);
            // Populate data from the appointment map
            patient_nameTextView.setText(" " + appointment.get("name"));
            timeTextView.setText(" " + appointment.get("time"));
            dateTextView.setText(" " + appointment.get("date"));
            diseaseTextView.setText(" " + appointment.get("disease"));
            genderTextView.setText(" " + appointment.get("gender"));

            // Load image from URL using a library like Picasso or Glide
            // Example with Glide:
            String imageUrl = (String) appointment.get("imageURL");
            System.out.println(imageUrl);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context).load(imageUrl).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.upload_icon);
            }

            // Set onClickListener for the rescheduler button
            rescheduler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IncomingAppointments.this, RescheduleAppointment.class);
                    startActivity(intent);
                }
            });



            // Retrieve appointment ID (document ID) from appointment data
            String appointmentId = (String) appointment.get("documentId"); // Assuming the field name is "documentId"
            System.out.println(appointmentId+"yp");
            String userId = getIntent().getStringExtra("user_email");

            // Get the dermatologist's name from Firestore
            db.collection("dermatologist")
                    .document(userId) // Assuming userId is the ID of the dermatologist
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String dermatologistName = documentSnapshot.getString("Name");
                                // Set onClickListener for the confirm button
                                confirmer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Confirm the appointment using obtained user ID and appointment ID
                                        confirmAppointment(userId, appointmentId);
                                        // Get the receiver's email from the appointment data
                                        String receiverEmail = (String) appointment.get("email");

                                        // Pass the retrieved dermatologist's name to sendEmail method
                                        sendEmail(receiverEmail, (String) appointment.get("name"), dermatologistName, (String) appointment.get("date"), (String) appointment.get("time"));
                                    }
                                });
                            } else {
                                Log.d(TAG, "Dermatologist document not found");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error retrieving dermatologist document", e);
                        }
                    });

            // Set onClickListener for the confirm button
            confirmer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("yaha hu "+appointmentId+" "+userId);
                    // Confirm the appointment using obtained user ID and appointment ID
                    confirmAppointment(userId, appointmentId);
                    // Get the receiver's email from the appointment data
                    String receiverEmail = (String) appointment.get("email");

                    sendEmail(receiverEmail, (String) appointment.get("name"), "Dermatologist Name", (String) appointment.get("date"), (String) appointment.get("time"));

                }
            });
        }
    }

    public void sendEmail(String receiverEmail, String patientName, String dermatologistName, String date, String time) {
        try {
            String stringSenderEmail = "edermacare01@gmail.com";
            String passwordSenderEmail = "eeal uvjd crje rvxs";

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, passwordSenderEmail);
                }
            });
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
            mimeMessage.setSubject("eDermaCare: Appointment with dermatologist has been confirmed");
            String emailBody = "Dear " + patientName + ",\n\n" +
                    "Your appointment with dermatologist Dr. " + dermatologistName + " has been confirmed.\n" +
                    "Appointment details:\n" +
                    "Date: " + date + "\n" +
                    "Time: " + time + "\n"+
//                    "Address: " + address + "\n\n" +
                    "Thank you.\n" +
                    "\n"+
                    "Best regards,\neDermaCare Team";
            mimeMessage.setText(emailBody);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
