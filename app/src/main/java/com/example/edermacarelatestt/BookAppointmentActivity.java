package com.example.edermacarelatestt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookAppointmentActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Calendar selectedDate;
    private TextView dateTextView, timeTextView, fbname, fbcity, fbdistrict, fbstate, fbnemail, fbmobile, fbexperience;
    private ImageView dermatologistImageView;
    private String userId;
    private boolean isAppointmentSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        fbname = findViewById(R.id.fbname);
        fbcity = findViewById(R.id.fbcity);
        fbdistrict = findViewById(R.id.fbdistrict);
        fbstate = findViewById(R.id.fbstate);
        fbnemail = findViewById(R.id.fbnemail);
        fbmobile = findViewById(R.id.fbmobile);
        fbexperience = findViewById(R.id.fbexperience);
        dermatologistImageView = findViewById(R.id.dermatologistImageView);

        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("user_id");
            String name = extras.getString("Name");
            String city = extras.getString("City");

            // Set TextViews
            fbname.setText(name);
            fbcity.setText(city);

            // Query Firestore to get dermatologist details
            db.collection("dermatologist")
                    .whereEqualTo("Name", name)
                    .whereEqualTo("City", city)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Load image URL
                                String imageURL = document.getString("ImageURL");
                                if (imageURL != null && !imageURL.isEmpty()) {
                                    Glide.with(BookAppointmentActivity.this).load(imageURL).into(dermatologistImageView);
                                }

                                // Set TextViews with details
                                String district = document.getString("District");
                                String email = document.getString("Email");
                                String exp = document.getString("Exp");
                                String mobile = document.getString("Mobile");
                                String state = document.getString("State");

                                fbdistrict.setText(district);
                                fbnemail.setText(email);
                                fbexperience.setText(exp);
                                fbmobile.setText(mobile);
                                fbstate.setText(state);
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(BookAppointmentActivity.this, "Failed to fetch dermatologist details", Toast.LENGTH_SHORT).show();
                            Log.e("BookAppointmentActivity", "Error fetching dermatologist details", task.getException());
                        }
                    });
        }

        // Button click listeners
        Button selectDateButton = findViewById(R.id.selectDateButton);
        selectDateButton.setOnClickListener(v -> showDatePicker());

        Button selectTimeButton = findViewById(R.id.selectTimeButton);
        selectTimeButton.setOnClickListener(v -> showTimePicker());

        Button bookAppointmentButton = findViewById(R.id.bookAppointment);
        bookAppointmentButton.setOnClickListener(v -> saveAppointmentToFirestore());
    }

    private void showDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(BookAppointmentActivity.this, (view, year1, monthOfYear, dayOfMonth) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(Calendar.YEAR, year1);
            selectedDate.set(Calendar.MONTH, monthOfYear);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Update TextView with selected date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(selectedDate.getTime());
            dateTextView.setText(formattedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(BookAppointmentActivity.this, (view, hourOfDay, minute1) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDate.set(Calendar.MINUTE, minute1);

            // Update TextView with selected time
            DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
            String formattedTime = timeFormat.format(selectedDate.getTime());
            timeTextView.setText(formattedTime);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveAppointmentToFirestore() {
        if (!isAppointmentSaved && selectedDate != null && userId != null) {
            isAppointmentSaved = true; // Set flag to prevent multiple saves
            // Get appointment details
            String name = fbname.getText().toString();
            String city = fbcity.getText().toString();
            String district = fbdistrict.getText().toString();
            String state = fbstate.getText().toString();
            String email = fbnemail.getText().toString();
            String mobile = fbmobile.getText().toString();
            String experience = fbexperience.getText().toString();

            // Validate appointment details
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(city) || TextUtils.isEmpty(district) || TextUtils.isEmpty(state) ||
                    TextUtils.isEmpty(email) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(experience)) {
                Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                return;
            }

            // Format selected date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String selectedDateString = dateFormat.format(selectedDate.getTime());
            String selectedTimeString = timeFormat.format(selectedDate.getTime());

            // Create appointment data
            Map<String, Object> appointment = new HashMap<>();
            appointment.put("name", name);
            appointment.put("city", city);
            appointment.put("district", district);
            appointment.put("state", state);
            appointment.put("email", email);
            appointment.put("mobile", mobile);
            appointment.put("experience", experience);
            appointment.put("date", selectedDateString);
            appointment.put("time", selectedTimeString);

            // Save appointment to Firestore
            db.collection("patients")
                    .document(userId)
                    .collection("appointment")
                    .add(appointment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(BookAppointmentActivity.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BookAppointmentActivity.this, "Failed to book appointment. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.e("BookAppointmentActivity", "Error booking appointment", task.getException());
                        }
                    });

            // Fetch patient details
            db.collection("patients").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String patientName = documentSnapshot.getString("Name");
                            String patientEmail = documentSnapshot.getString("Email");
                            String patientGender = documentSnapshot.getString("Gender");
                            String patientMobile = documentSnapshot.getString("Mobile");
                            String patientDOB = documentSnapshot.getString("DOB");

                            // Query the patient's history to get the imageURL
                            db.collection("patients")
                                    .document(userId)
                                    .collection("history")
                                    .get()
                                    .addOnCompleteListener(historyTask -> {
                                        if (historyTask.isSuccessful()) {
                                            for (DocumentSnapshot historyDocument : historyTask.getResult()) {
                                                String imageURL = historyDocument.getString("imageURL");
                                                String result = historyDocument.getString("result");

                                                // Create a Map to store the appointment data for the dermatologist
                                                Map<String, Object> dermatologistAppointment = new HashMap<>();
                                                dermatologistAppointment.put("name", patientName);
                                                dermatologistAppointment.put("email", patientEmail);
                                                dermatologistAppointment.put("gender", patientGender);
                                                dermatologistAppointment.put("date", selectedDateString); // Add selected date
                                                dermatologistAppointment.put("time", selectedTimeString); // Add selected time
                                                dermatologistAppointment.put("imageURL", imageURL); // Add imageURL
                                                dermatologistAppointment.put("disease", result);
                                                dermatologistAppointment.put("dob", patientDOB);
                                                dermatologistAppointment.put("mobile", patientMobile);
                                                dermatologistAppointment.put("pending", true); // Set pending to true
                                                dermatologistAppointment.put("confirmed", false); // Set confirm to false
                                                dermatologistAppointment.put("reschedule", false); // Set rescheduled to false
                                                dermatologistAppointment.put("cancelled", false); // Set cancelled to false


                                                // Query the dermatologist collection
                                                db.collection("dermatologist")
                                                        .whereEqualTo("Name", name)
                                                        .get()
                                                        .addOnCompleteListener(dermaTask -> {
                                                            if (dermaTask.isSuccessful()) {
                                                                for (DocumentSnapshot document : dermaTask.getResult()) {
                                                                    // Get the document reference of the dermatologist
                                                                    DocumentReference dermatologistRef = document.getReference();

                                                                    // Add the appointment to the subcollection "appointment"
                                                                    dermatologistRef.collection("appointments")
                                                                            .add(dermatologistAppointment)
                                                                            .addOnCompleteListener(dermaAppointmentTask -> {
                                                                                if (dermaAppointmentTask.isSuccessful()) {
                                                                                    Toast.makeText(BookAppointmentActivity.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                                                                                } else {
                                                                                    Toast.makeText(BookAppointmentActivity.this, "Failed to book appointment. Please try again.", Toast.LENGTH_SHORT).show();
                                                                                    Log.e("BookAppointmentActivity", "Error booking appointment", dermaAppointmentTask.getException());
                                                                                }
                                                                            });
                                                                }
                                                            } else {
                                                                // Handle errors
                                                                Log.e("BookAppointmentActivity", "Error fetching dermatologist details", dermaTask.getException());
                                                            }
                                                        });
                                            }
                                        } else {
                                            Log.d("BookAppointmentActivity", "No such document");
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("BookAppointmentActivity", "Error fetching patient's history", e));
                        } else {
                            Log.d("BookAppointmentActivity", "No such document");
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("BookAppointmentActivity", "Error fetching patient details", e);
                        }
                    });
        }
        else if (isAppointmentSaved) {
            // Show message indicating appointment is already saved
            Toast.makeText(BookAppointmentActivity.this, "Appointment already booked!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(BookAppointmentActivity.this, "Please select date and time", Toast.LENGTH_SHORT).show();
        }
    }

}


