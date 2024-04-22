package com.example.edermacarelatestt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Calendar selectedDate;
    private TextView dateTextView, timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get TextViews for displaying date and time
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);

        // Get the name and city from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("Name");
            String city = extras.getString("City");

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
                                    // Extract the details from the document
                                    String district = document.getString("District");
                                    String email = document.getString("Email");
                                    String exp = document.getString("Exp");
                                    String licenseURL = document.getString("LicenseURL");
                                    String mobile = document.getString("Mobile");
                                    String password = document.getString("Password");
                                    String regID = document.getString("RegID");
                                    String regYear = document.getString("RegYear");
                                    String state = document.getString("State");
                                    String stateMedicalCouncil = document.getString("StateMedicalCouncil");

                                    // Set the details to TextViews
                                    TextView nameTextView = findViewById(R.id.fbname);
                                    TextView cityTextView = findViewById(R.id.fbcity);
                                    TextView districtTextView = findViewById(R.id.fbdistrict);
                                    TextView emailTextView = findViewById(R.id.fbnemail);
                                    TextView expTextView = findViewById(R.id.fbexperience);
                                    TextView mobileTextView = findViewById(R.id.fbmobile);
//                                    TextView regIDTextView = findViewById(R.id.regIDTextView);
//                                    TextView regYearTextView = findViewById(R.id.regYearTextView);
                                    TextView stateTextView = findViewById(R.id.fbstate);
//                                    TextView stateMedicalCouncilTextView = findViewById(R.id.stateMedicalCouncilTextView);

                                    nameTextView.setText(name);
                                    cityTextView.setText(city);
                                    districtTextView.setText(district);
                                    emailTextView.setText(email);
                                    expTextView.setText(exp);
                                    mobileTextView.setText(mobile);
//                                    regIDTextView.setText(regID);
//                                    regYearTextView.setText(regYear);
                                    stateTextView.setText(state);
//                                    stateMedicalCouncilTextView.setText(stateMedicalCouncil);
                                    // You can set more details similarly
                                }
                            } else {
                                // Handle errors
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
}
