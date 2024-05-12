package com.example.edermacarelatestt;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
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

public class SignUpDermatologistActivity2 extends AppCompatActivity {

    EditText editTextPasswordD, editTextExperience, editTextMobileNo, editTextCity, editTextDistrict, editTextState;
    Spinner spinnerState, spinnerDistrict;

    Button buttonSignUpD;
    private FirebaseFirestore db;
    private String imageUri;
    private  String imageUrl;
    private String verified;
    private String qualification;
    private String qualificationYear;
    private String universityName;
    private String permanent_address;
    private StorageReference storageReference;
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
            imageUri = intent.getStringExtra("imageUri");
            System.out.println("i got imageuriii");
        }

        storageReference = FirebaseStorage.getInstance().getReference();

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

//        uploadImageToStorage(imageUri, name, registrationNo);

        // Check if the record exists in Dermatologist_IADVL
        checkIfRecordExists(name, registrationNo, new RecordExistsCallback() {
            @Override
            public void onRecordChecked(boolean exists) {
                if (exists) {
                    // Check if the record exists in dermatologist
                    checkIfRecordExistsInDermatologist(name, registrationNo, new RecordExistsCallback() {
                        @Override
                        public void onRecordChecked(boolean existsInDermatologist) {
                            if (existsInDermatologist) {
                                showAlert("Record already exists in dermatologist");
                            } else {
                                // Post data to Firebase
                                uploadImageToStorage(imageUri,name, registrationNo);
                            }
                        }
                    });
                } else {
                    showAlert("Record doesn't exist in Dermatologist_IADVL");
                }
            }
        });
    }

    private void uploadImageToStorage(String imageUri, String name, String registrationNo) {
        if (imageUri != null) {
            Uri file = Uri.parse(imageUri);
            StorageReference imageRef = storageReference.child("Dermaimages/" + name + "_" + registrationNo);
            imageRef.putFile(file)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, now get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Get the download URL
                            imageUrl = uri.toString();
                            System.out.println(imageUrl);
                            // Now proceed to save the data with the image URL
                            postDataToFirebase();
                            System.out.println("posted data");

                        });
                    })
                    .addOnFailureListener(e -> {
                        showAlert("Failed to upload image");
                    });
        }
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

    private void checkIfRecordExistsInDermatologist(String name, String registrationNo, RecordExistsCallback callback) {
        // Create a query to check for matching registration number
        CollectionReference dermatologistRef = db.collection("dermatologist");
        Query query = dermatologistRef.whereEqualTo("RegID", registrationNo);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean exists = !task.getResult().isEmpty();
                callback.onRecordChecked(exists);
            } else {
                Log.w(TAG, "Error getting documents: ", task.getException());
                callback.onRecordChecked(false);
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
            dermatologist.put("ImageURL", imageUrl);
            dermatologist.put("permanent_address",permanent_address);

            String emailDerma= getIntent().getStringExtra("email");
            String nameDerma= getIntent().getStringExtra("name");

            db.collection("dermatologist")
                    .add(dermatologist)
                    .addOnSuccessListener(documentReference -> {
                        showVerificationAlertDialog();
                        sendEmail(emailDerma, nameDerma);
                        redirectToLogin();
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

    public void sendEmail(String receiverEmail,String dermatologistName) {
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
            mimeMessage.setSubject("eDermaCare: Welcome to eDermaCare - Doctor Verification Complete!");
            String emailBody = "Dr. " + dermatologistName + ",\n\n" +
                    "We are thrilled to inform you that your verification process for eDermaCare has been successfully completed! \n" +
                    "Welcome aboard to our esteemed network of healthcare professionals dedicated to providing top-notch dermatological care.\n"+
                    "\n" +
                    "We look forward to a fruitful collaboration and making a positive impact on the lives of our patients together.\n"+
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

    private void redirectToLogin() {
        Intent intent = new Intent(SignUpDermatologistActivity2.this, LoginDermatologistActivity.class);
        startActivity(intent);
        finish();
    }
}
