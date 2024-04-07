package com.example.edermacarelatestt;
import java.util.*;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;



public class PatientSignUpManager {

    private DatabaseReference databaseReference;

    public PatientSignUpManager() {
        // Initialize Firebase

    }

    public  void showDatePickerDialog(final Context context, final EditText editTextDOB) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        editTextDOB.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    public boolean signUp(Context context, String name, String email, String dob, String mobile, String hashedpassword, String gender) {
        if (!name.isEmpty() && !email.isEmpty() && !hashedpassword.isEmpty() && !dob.isEmpty() && !mobile.isEmpty()) {
            // Create a Firestore instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a Map to store patient data
            Map<String, Object> patientData = new HashMap<>();
            patientData.put("Name", name);
            patientData.put("Email", email);
            patientData.put("DOB", dob);
            patientData.put("Mobile", mobile);
            patientData.put("Password", hashedpassword);
            patientData.put("Gender", gender);

            // Add patient data to Firestore
            db.collection("patients")
                    .add(patientData)
                    .addOnSuccessListener(documentReference -> {
                        // Display success message
                        Toast.makeText(context, "Sign up successful!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Display error message
                        Toast.makeText(context, "Failed to sign up: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            return true;
        } else {
            // Handle case when any field is empty
            // Display an error message
            Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private void displayField(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}