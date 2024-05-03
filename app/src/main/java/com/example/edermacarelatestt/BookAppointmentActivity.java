package com.example.edermacarelatestt;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentReference;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get TextViews for displaying date and time
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

        // Get the name and city from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("user_id");
            String name = extras.getString("Name");
            String city = extras.getString("City");

            // Set name and city TextViews
            fbname.setText(name);
            fbcity.setText(city);

            // Query the Firestore collection to find the dermatologist with the given name and city
            db.collection("dermatologist")
                    .whereEqualTo("Name", name)
                    .whereEqualTo("City", city)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Extract the image URL from the document
                                    String imageURL = document.getString("ImageURL");
                                    if (imageURL != null && !imageURL.isEmpty()) {
                                        Glide.with(BookAppointmentActivity.this).load(imageURL).into(dermatologistImageView);
                                    }


                                    // Extract other details and set TextViews
                                    String district = document.getString("District");
                                    String email = document.getString("Email");
                                    String exp = document.getString("Exp");
                                    String mobile = document.getString("Mobile");
                                    String state = document.getString("State");

                                    // Set the details to TextViews
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
                        }
                    });
        }

        // Button to select date
        Button selectDateButton = findViewById(R.id.selectDateButton);
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Button to select time
        Button selectTimeButton = findViewById(R.id.selectTimeButton);
        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        // Button to book appointment
        Button bookAppointmentButton = findViewById(R.id.bookAppointment);
        bookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointmentToFirestore();
            }
        });
    }

    private void showDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(BookAppointmentActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate = Calendar.getInstance();
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, monthOfYear);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Update TextView with selected date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(selectedDate.getTime());
                dateTextView.setText(formattedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(BookAppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Update TextView with selected time
                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDate.set(Calendar.MINUTE, minute);

                DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
                String formattedTime = timeFormat.format(selectedDate.getTime());
                timeTextView.setText(formattedTime);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveAppointmentToFirestore() {
        if (selectedDate != null && userId != null) {
            // Get the appointment details
            String name = fbname.getText().toString();
            String city = fbcity.getText().toString();
            String district = fbdistrict.getText().toString();
            String state = fbstate.getText().toString();
            String email = fbnemail.getText().toString();
            String mobile = fbmobile.getText().toString();
            String experience = fbexperience.getText().toString();

            // Format the selected date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String selectedDateString = dateFormat.format(selectedDate.getTime());
            String selectedTimeString = timeFormat.format(selectedDate.getTime());

            // Create a Map to store the appointment data
            Map<String, Object> appointment = new HashMap<>();
            appointment.put("name", name);
            appointment.put("city", city);
            appointment.put("district", district);
            appointment.put("state", state);
            appointment.put("email", email);
            appointment.put("mobile", mobile);
            appointment.put("experience", experience);
            appointment.put("date", selectedDateString); // Add selected date
            appointment.put("time", selectedTimeString); // Add selected time

            // Add the appointment to Firestore
            db.collection("patients")
                    .document(userId)
                    .collection("appointment")
                    .add(appointment)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(BookAppointmentActivity.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BookAppointmentActivity.this, "Failed to book appointment. Please try again.", Toast.LENGTH_SHORT).show();
                                Log.e("BookAppointmentActivity", "Error booking appointment", task.getException());
                            }
                        }
                    });

            // Query the dermatologist collection
            db.collection("dermatologist")
                    .whereEqualTo("Name", name)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Get the document reference of the dermatologist
                                    DocumentReference dermatologistRef = document.getReference();

                                    // Add the appointment to the subcollection "appointment"
                                    dermatologistRef.collection("appointment")
                                            .add(appointment)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(BookAppointmentActivity.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(BookAppointmentActivity.this, "Failed to book appointment. Please try again.", Toast.LENGTH_SHORT).show();
                                                        Log.e("BookAppointmentActivity", "Error booking appointment", task.getException());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                // Handle errors
                                Log.e("BookAppointmentActivity", "Error fetching dermatologist details", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(BookAppointmentActivity.this, "Please select date and time", Toast.LENGTH_SHORT).show();
        }
    }
}
