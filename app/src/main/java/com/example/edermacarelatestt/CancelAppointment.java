package com.example.edermacarelatestt;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class CancelAppointment extends AppCompatActivity {

    private TextView nameTextView, genderTextView, emailTextView, mobileTextView, locationTextView;
    private String userId, Name, Date, Time, receiverEmail, Dname, appointmentId;
    private String location;
    private FirebaseFirestore db;
    Button yesAppointment,noAppointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancelled_appointmentdialogue);
        Bundle extras = getIntent().getExtras();
        userId = extras.getString("user_id");
        Name = extras.getString("Patientname");
        Dname = extras.getString("Dname");
        Date = extras.getString("date");
        Time = extras.getString("time");
        receiverEmail = extras.getString("receivermail");
        appointmentId = extras.getString("appointmentId");

        db = FirebaseFirestore.getInstance();
        // Initialize TextViews
        nameTextView = findViewById(R.id.fbname);
        genderTextView = findViewById(R.id.fbgender);
        emailTextView = findViewById(R.id.fbnemail);
        mobileTextView = findViewById(R.id.fbmobile);
        locationTextView = findViewById(R.id.fblocation);
        yesAppointment = findViewById(R.id.yesAppointment);
        noAppointment= findViewById(R.id.noAppointment);
        DocumentReference appointmentRef = db.collection("dermatologist")
                .document(userId)
                .collection("appointments")
                .document(appointmentId);

        appointmentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, retrieve appointment details
                        String name = document.getString("name");
                        String gender = document.getString("gender");
                        String email = document.getString("email");
                        String mobile = document.getString("mobile");
                        location = document.getString("clinicAddress");

                        // Set retrieved details to TextViews
                        nameTextView.setText(name);
                        genderTextView.setText(gender);
                        emailTextView.setText(email);
                        mobileTextView.setText(mobile);
                        locationTextView.setText(location);
                    } else {
                        Toast.makeText(CancelAppointment.this, "Error fetching data!", Toast.LENGTH_SHORT).show();
                    }} else {
                    Toast.makeText(CancelAppointment.this, "Error fetching data!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        yesAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAppointmentee(userId, appointmentId);
                fetchDermatologistNameAndSendEmail();

                Toast.makeText(CancelAppointment.this, "Appointment has been booked successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CancelAppointment.this, CancelledAppointments.class);
                intent.putExtra("user_email", userId);
                startActivity(intent);

            }
        });

        noAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CancelAppointment.this, "Appointment has not been booked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CancelAppointment.this, IncomingAppointments.class);
                intent.putExtra("user_email", userId);
                startActivity(intent);
            }
        });
    }
    public void sendEmail(String receiverEmail, String patientName, String dermatologistName, String date, String time, String location) {
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
            mimeMessage.setSubject("eDermaCare: Appointment with dermatologist has been cancelled");
            String emailBody = "Dear " + patientName + ",\n\n" +
                    "Your appointment with dermatologist Dr. " + dermatologistName + " has been cancelled.\n" +
                    "Appointment details:\n" +
                    "Date: " + date + "\n" +
                    "Time: " + time + "\n" +
                    "Address: " + location + "\n\n" +
                    "Thank you.\n" +
                    "\n" +
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
    private void cancelAppointmentee(String userId, String appointmentId) {
        if (userId == null || appointmentId == null) {
            System.out.println("userId or appointmentId is null");
            return;
        }

        // Get the appointment document reference
        DocumentReference appointmentRef = db.collection("dermatologist")
                .document(userId)
                .collection("appointments")
                .document(appointmentId);

        // Update fields including confirmed and pending
        appointmentRef.update("cancelled", true,
                        "pending", false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Print confirmation message to console
                        Log.d(TAG, "Appointment cancelled successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating appointment: ", e);
                    }
                });
    }

    private void fetchDermatologistNameAndSendEmail() {
        // Access Firestore to get dermatologistName
        db.collection("dermatologist")
                .document(userId) // Assuming userId corresponds to the dermatologist document
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String dermatologistName = documentSnapshot.getString("Name");
                            // Use the retrieved dermatologistName
                            sendEmail(receiverEmail, Name, dermatologistName, Date, Time, location);
                        } else {
                            Log.d(TAG, "Dermatologist document does not exist");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error retrieving dermatologist document: ", e);
                    }
                });
    }

}
