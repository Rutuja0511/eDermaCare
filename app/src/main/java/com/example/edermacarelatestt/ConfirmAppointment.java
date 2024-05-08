package com.example.edermacarelatestt;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConfirmAppointment extends AppCompatActivity {

    private TextView nameTextView, genderTextView, emailTextView, mobileTextView, locationTextView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reschedule_appointment);
        Bundle extras = getIntent().getExtras();
        userId = extras.getString("user_id");
        // Initialize TextViews
        nameTextView = findViewById(R.id.fbname);
        genderTextView = findViewById(R.id.fbgender);
        emailTextView = findViewById(R.id.fbnemail);
        mobileTextView = findViewById(R.id.fbmobile);
        locationTextView = findViewById(R.id.fblocation);

        // Access Firestore and retrieve appointment details
        FirebaseFirestore.getInstance().collection("dermatologist")
                .document(userId)
                .collection("appointments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Retrieve appointment details
                                String name = document.getString("name");
                                String gender = document.getString("gender");
                                String email = document.getString("email");
                                String mobile = document.getString("mobile");
                                String location = document.getString("clinicAddress");

                                // Set retrieved details to TextViews
                                nameTextView.setText(name);
                                genderTextView.setText(gender);
                                emailTextView.setText(email);
                                mobileTextView.setText(mobile);
                                locationTextView.setText(location);
                            }
                        } else {
                            Toast.makeText(ConfirmAppointment.this, "Error fetching data!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
