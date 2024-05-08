package com.example.edermacarelatestt;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SignUpDermatologistActivity2 extends AppCompatActivity {

    EditText editTextPasswordD, editTextExperience, editTextMobileNo, editTextCity, editTextDistrict, editTextState;
    Spinner spinnerState, spinnerDistrict;

    Button buttonSignUpD;
    private FirebaseFirestore db;
    private String imageUri;
    private String verified;
    private String qualification;
    private String qualificationYear;
    private String universityName;
    private String permanent_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity2_signup_dermatologist);
        editTextPasswordD = findViewById(R.id.dermatologist_signup_password);

        spinnerState = findViewById(R.id.spinnerState);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
//        spinnerCity = findViewById(R.id.spinnerCity);
        editTextCity= findViewById(R.id.dermatologist_city);

        editTextMobileNo = findViewById(R.id.dermatologist_mobileNo);
        editTextExperience = findViewById(R.id.dermatologist_experience);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("imageUri")) {
            imageUri = intent.getStringExtra("imageUri"); // Get the image URI
        }

        buttonSignUpD = findViewById(R.id.dermatologist_signup_button);

        buttonSignUpD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpDermatologistActivity2.this, "We will Verify your details and contact you within 24 hours", Toast.LENGTH_SHORT).show();
                signUpDermatologist();
            }
        });

        initializeDropdowns();

    }



    private void showVerificationAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpDermatologistActivity2.this);
        builder.setMessage("Thank you for signing up. We will verify your details and contact you within 24 hours.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void signUpDermatologist() {
        String password = editTextPasswordD.getText().toString();
        String experience = editTextExperience.getText().toString();
        String mobileNo = editTextMobileNo.getText().toString();
//        String city = spinnerCity.getSelectedItem().toString();
        String city = editTextCity.getText().toString();
        String district = spinnerDistrict.getSelectedItem().toString();
        String state = spinnerState.getSelectedItem().toString();


        if (TextUtils.isEmpty(experience) || TextUtils.isEmpty(mobileNo)) {
            Toast.makeText(SignUpDermatologistActivity2.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            showAlert("Please enter a password with at least 6 characters");
            return;
        }

        String hashedPassword = hashPassword(password);

        String registrationNo = getIntent().getStringExtra("registrationNo");
        String yearOfRegistration = getIntent().getStringExtra("registrationYear");
        String stateMedicalCouncil = getIntent().getStringExtra("stateMedicalCouncil");
        String name = getIntent().getStringExtra("name");

//        DermatologistVerification.performVerification(registrationNo, yearOfRegistration, stateMedicalCouncil, new DermatologistVerification.VerificationCallback() {
//            @Override
//            public void onVerificationComplete(JSONObject result) {
//                try {
//                    JSONObject resultObj = result.getJSONObject("result");
//                    System.out.println(resultObj);
//                    JSONObject sourceOutput = resultObj.getJSONObject("source_output");
//                    String status = sourceOutput.getString("status");
//                    verified = status.equals("id_found") ? "true" : "false";
//                    if (status.equals("id_found")) {
//                        JSONObject imrDetails = sourceOutput.getJSONObject("imr_details");
//                        qualification = imrDetails.getString("qualification");
//                        qualificationYear = imrDetails.getString("qualification_year");
//                        universityName = imrDetails.getString("university_name");
//                        permanent_address=imrDetails.getString("permanent_address");
//                    }
//                    Log.d(TAG, "Verification Status: " + verified);
//                    System.out.println(verified);
//                    Log.d(TAG, "Qualification: " + qualification);
//                    Log.d(TAG, "Qualification Year: " + qualificationYear);
//                    Log.d(TAG, "University Name: " + universityName);
//                    Log.d(TAG, "add: " + permanent_address);
//
//                    // Post data to Firebase only after verification is complete
//                    postDataToFirebase();
//                } catch (JSONException e) {
//                    Log.e(TAG, "JSONException occurred: " + e.getMessage());
//                }
//            }
//
//            @Override
//            public void onVerificationFailed(String errorMessage) {
//                Log.e(TAG, "Verification failed: " + errorMessage);
//            }
//        });

        checkIfRecordExists(name, registrationNo, new RecordExistsCallback() {
            @Override
            public void onRecordChecked(boolean exists) {
                if (exists) {
                    postDataToFirebase();
                } else {
                    showAlert("Record doesn't exist");
                }
            }
        });

    }

    private void checkIfRecordExists(String name, String registrationNo, RecordExistsCallback callback) {
        // Create a query to check for matching name and registration number
        CollectionReference dermatologistRef = db.collection("Dermatologist_IADVL");
        Query query = dermatologistRef.whereEqualTo("Membership Number", registrationNo);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = !task.getResult().isEmpty();
                        callback.onRecordChecked(exists);
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                        callback.onRecordChecked(false); // Consider it doesn't exist on error
                    }
                });
    }


    private void postDataToFirebase() {
        // Check if necessary values are available and post data to Firebase
        if (!TextUtils.isEmpty(imageUri)){
            Map<String, Object> dermatologist = new HashMap<>();
            dermatologist.put("Name", getIntent().getStringExtra("name"));
            dermatologist.put("Email", getIntent().getStringExtra("email"));
            dermatologist.put("RegID", getIntent().getStringExtra("registrationNo"));
            dermatologist.put("RegYear", getIntent().getStringExtra("registrationYear"));
            dermatologist.put("StateMedicalCouncil", getIntent().getStringExtra("stateMedicalCouncil"));
            dermatologist.put("Exp", editTextExperience.getText().toString());
            dermatologist.put("Mobile", editTextMobileNo.getText().toString());
            dermatologist.put("City", editTextCity.getText().toString());
            dermatologist.put("District", spinnerDistrict.getSelectedItem().toString());
            dermatologist.put("State", spinnerState.getSelectedItem().toString());
            dermatologist.put("Password", hashPassword(editTextPasswordD.getText().toString()));
//            dermatologist.put("verified", verified);
            dermatologist.put("qualification", qualification);
            dermatologist.put("qualificationYear", qualificationYear);
            dermatologist.put("universityName", universityName);
            dermatologist.put("ImageURL", imageUri);
            dermatologist.put("permanent_address",permanent_address);

            db.collection("dermatologist")
                    .add(dermatologist)
                    .addOnSuccessListener(documentReference -> {
                        showVerificationAlertDialog();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUpDermatologistActivity2.this, "Error adding dermatologist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(SignUpDermatologistActivity2.this, "Image URI  is empty or verification failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    interface RecordExistsCallback {
        void onRecordChecked(boolean exists);
    }

    // Initialize and populate dropdowns
    private void initializeDropdowns() {
        spinnerState = findViewById(R.id.spinnerState);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
//        spinnerCity = findViewById(R.id.spinnerCity);

        // Populate state dropdown
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(
                this, R.array.states_array, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateAdapter);

        // Set listeners for state and district selection changes
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Based on state selection, populate district dropdown
                String selectedState = parent.getItemAtPosition(position).toString();
                populateDistrictDropdown(selectedState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Based on district selection, populate city dropdown
                String selectedDistrict = parent.getItemAtPosition(position).toString();
//                populateCityDropdown(selectedDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });
    }

    // Populate district dropdown based on selected state
    private void populateDistrictDropdown(String selectedState) {
        int districtsArrayId = getResources().getIdentifier(
                "districts_" + selectedState.toLowerCase().replace(" ", "_"),
                "array",
                getPackageName());
        if (districtsArrayId != 0) {
            ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(
                    this, districtsArrayId, android.R.layout.simple_spinner_item);
            districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDistrict.setAdapter(districtAdapter);
        } else {
//            Toast.makeText(this, "No districts found for selected state", Toast.LENGTH_SHORT).show();
        }
    }

    // Populate city dropdown based on selected district
//    private void populateCityDropdown(String selectedDistrict) {
//        int citiesArrayId = getResources().getIdentifier(
//                "cities_" + selectedDistrict.toLowerCase().replace(" ", "_"),
//                "array",
//                getPackageName());
//        if (citiesArrayId != 0) {
//            ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(
//                    this, citiesArrayId, android.R.layout.simple_spinner_item);
//            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerCity.setAdapter(cityAdapter);
//        } else {
////            Toast.makeText(this, "No cities found for selected district", Toast.LENGTH_SHORT).show();
//        }
//    }


}
