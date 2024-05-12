package com.example.edermacarelatestt;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.provider.OpenableColumns;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpDermatologistActivity1 extends AppCompatActivity {

    EditText editTextNameD, editTextEmailD, editTextRegistrationNo, editTextRegistrationYear;
    Spinner spinnerStateMedicalCouncil, spinnerGender;
    TextView loginRedirectD, ImageName;
    Button buttonNextD, buttonUpload;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String imageNameStr;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
//    private ActivityResultLauncher<String> pdfLauncher;
    private ActivityResultLauncher<String> imageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1_signup_dermatologist);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        editTextNameD = findViewById(R.id.dermatologist_signup_name);
        editTextEmailD = findViewById(R.id.dermatologist_signup_email);
        editTextRegistrationNo = findViewById(R.id.dermatologist_signup_RegNumber);
        editTextRegistrationYear = findViewById(R.id.dermatologist_signup_year);
        spinnerGender=findViewById(R.id.spinner_gender_dermatologist);
//        editTextStateMedicalCouncil = findViewById(R.id.dermatologist_signup_state_council);
        spinnerStateMedicalCouncil = findViewById(R.id.spinner_state_medical_council);
        buttonUpload = findViewById(R.id.dermatologist_upload_button);
        buttonNextD = findViewById(R.id.dermatologist_next_button);
        ImageName=findViewById(R.id.dermatologistFilename);

        // Set up spinner for State Medical Council selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.statesMC_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStateMedicalCouncil.setAdapter(adapter); // Set adapter to the spinner


        editTextRegistrationYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYearPickerDialog(editTextRegistrationYear);
            }
        });

        editTextRegistrationYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYearPickerDialog(editTextRegistrationYear);
            }
        });

        Spinner spinnerGender = findViewById(R.id.spinner_gender_dermatologist);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        buttonNextD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                String name = editTextNameD.getText().toString();
                String email = editTextEmailD.getText().toString();
                String registrationNo = editTextRegistrationNo.getText().toString();
                String registrationYear = editTextRegistrationYear.getText().toString();
                String gender = spinnerGender.getSelectedItem().toString();
                String stateMedicalCouncil = spinnerStateMedicalCouncil.getSelectedItem().toString();

                // Check if any field is empty, if Maharyes, display an alert
                if (name.isEmpty()) {
                    showAlert("Please enter your name");
                    return;
                }

                // Validate email format
                if (!isValidEmail(email)) {
                    editTextEmailD.setError("Invalid email format");
                    return;
                }

                // Validate registration year format
                if (!isValidYear(registrationYear)) {
                    editTextRegistrationYear.setError("Invalid year format");
                    return;
                }

                if (registrationNo.isEmpty()) {
                    showAlert("Please enter Registration Number");
                    return;
                }

                if (stateMedicalCouncil.isEmpty()) {
                    showAlert("Please enter State Medical Council");
                    return;
                }

                // Pass data to next activity
                if (imageUri != null) {
                    Intent intent = new Intent(SignUpDermatologistActivity1.this, SignUpDermatologistActivity2.class);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("registrationNo", registrationNo);
                    intent.putExtra("registrationYear", registrationYear);
                    intent.putExtra("stateMedicalCouncil", stateMedicalCouncil);
                    intent.putExtra("gender", gender);
                    intent.putExtra("imageUri", imageUri.toString());
                    System.out.println("i passed image uri");
                    startActivity(intent);
                } else {
                    showAlert("Please upload an image");
                }
            }
        });


//        setupSpinner();

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        imageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                imageUri = uri;
                imageNameStr = getFileNameFromUri(imageUri);
                ImageName.setText(imageNameStr);
//                uploadImage(); // Upload image and handle URL in uploadImage method
            }
        });

    }

    private void showAlert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidYear(String year) {
        try {
            int yearValue = Integer.parseInt(year);
            return yearValue >= 1900 && yearValue <= Calendar.getInstance().get(Calendar.YEAR);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void showYearPickerDialog(final EditText editText) {
        final NumberPicker yearPicker = new NumberPicker(this);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(currentYear - 100); // Minimum year
        yearPicker.setMaxValue(currentYear);       // Maximum year
        yearPicker.setValue(currentYear);           // Default to current year

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Year");
        builder.setView(yearPicker);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedYear = yearPicker.getValue();
                editText.setText(String.valueOf(selectedYear));
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void openFileChooser() {
        imageLauncher.launch("image/*");
    }


    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri == null) {
            return null; // Return null if URI is null
        }

        Cursor cursor = null;
        try {
            String[] projection = {OpenableColumns.DISPLAY_NAME};
            cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex != -1) {
                    result = cursor.getString(columnIndex);
                } else {
                    // Handle case where column index is not found
                    result = uri.getLastPathSegment(); // Fallback to URI segment
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // Close the cursor when done
            }
        }
        return result;
    }
}
