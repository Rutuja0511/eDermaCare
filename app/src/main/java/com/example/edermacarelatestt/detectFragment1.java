package com.example.edermacarelatestt;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class detectFragment1 extends Fragment {
    Button BSelectImage;
    Button Process;
    Button Reset;

    Button descriptionButton, consultDoctorButton;
    ImageView IVPreviewImage;
    TextView textView2;

    // Firebase
    private FirebaseFirestore mFirestore;
    private StorageReference mStorageRef;

    // User ID
    private String userId;

    // Activity result launcher for selecting an image
    private final ActivityResultLauncher<Intent> selectImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == FragmentActivity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri selectedImageUri = data.getData();
                                if (selectedImageUri != null) {
                                    try {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                                        IVPreviewImage.setImageBitmap(bitmap);
                                        IVPreviewImage.setVisibility(View.VISIBLE); // Show image
                                        textView2.setVisibility(View.GONE); // Hide result initially
                                    } catch (IOException e) {
                                        Log.e(TAG, "Error loading image", e);
                                    }
                                }
                            }
                        }
                    });

    private static final String TAG = "detectFragment1";
    private static final int OUTPUT_SIZE = 6;// specify the size of the output array

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detect1, container, false);


        // Initialize Firebase
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Get user ID from arguments
        if (getArguments() != null) {
            userId = getArguments().getString("user_id");
        }

        // Register UI elements
        BSelectImage = rootView.findViewById(R.id.BSelectImage);
        IVPreviewImage = rootView.findViewById(R.id.IVPreviewImage);
        textView2 = rootView.findViewById(R.id.textView2);
        Process = rootView.findViewById(R.id.Process);
        Reset = rootView.findViewById(R.id.Reset);
        descriptionButton = rootView.findViewById(R.id.descriptionButton);
        consultDoctorButton = rootView.findViewById(R.id.consultDoctorButton);

        // Handle button click to select image
        BSelectImage.setOnClickListener(v -> selectImage());
        Process.setOnClickListener(v -> processImage());
        Reset.setOnClickListener(v -> resetPage());

        return rootView;
    }

    // Method to launch image selection activity
    void selectImage() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        selectImageLauncher.launch(Intent.createChooser(i, "Select Picture"));
        IVPreviewImage.setVisibility(View.GONE);
    }

    // Method to process the selected image
    void processImage() {
        Bitmap imageBitmap = ((BitmapDrawable) IVPreviewImage.getDrawable()).getBitmap();

        try {
            // Load the TFLite model (assuming you have 'model.tflite' in the assets folder)
            Interpreter.Options options = new Interpreter.Options();
            Interpreter tflite = new Interpreter(loadModelFile(), options);
            int inputSize = 224; // Change this to match your model's input size

            // Preprocess the input image to match the model input requirements
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, inputSize, inputSize, false);
            ByteBuffer inputBuffer = convertBitmapToByteBuffer(resizedBitmap);
            float[][] outputArray = new float[1][OUTPUT_SIZE]; // Adjust OUTPUT_SIZE according to your model

            // Run inference on the preprocessed image
            tflite.run(inputBuffer, outputArray);

            // Call the findResultClassIndex method after running inference
            int resultClassIndex = findResultClassIndex(outputArray);
            List<String> classList = loadClassLabels();

            // Get the class name corresponding to the predicted class index
            String predictedClassName = classList.get(resultClassIndex);

            // Update the TextView with the processed result
            String resultText = "Detected Disease: " + predictedClassName;
            textView2.setText(resultText);
            textView2.setVisibility(View.VISIBLE); // Show result

            // Store data in Firebase
            storeDataInFirebase(imageBitmap, resultText);

            descriptionButton.setEnabled(true);
            descriptionButton.setVisibility(View.VISIBLE);
            consultDoctorButton.setEnabled(true);
            consultDoctorButton.setVisibility(View.VISIBLE);

            // Set click listeners for the buttons
            descriptionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle description button click
                    // Redirect to the respective detected disease page (acne.xml or psoriasis.xml)
                    if (predictedClassName.equals("Acne & Related Diseases")) {
                        navigateToAcnePage();
                        return;
                    }
                    if (predictedClassName.equals("Psoriasis")) {
                        navigateToPsoriasisPage();
                        return;
                    }
                    if (predictedClassName.equals("TineaRingWorm")) {
                        navigateToRingwormPage();
                        return;
                    }
                    if (predictedClassName.equals("InsectBites")) {
                        navigateToInsectPage();
                        return;
                    }
                    if (predictedClassName.equals("Nail Infections")) {
                        navigateToNailPage();
                        return;
                    }
                    if(predictedClassName.equals("Non Skin")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Unknown Disease");
                        builder.setMessage("The uploaded image does not match any known diseases. Please upload a different image.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                // Handle the dialog button click if needed
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });

            consultDoctorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle consult doctor button click
                    // You can add your logic here to consult a doctor
                    Intent intent = new Intent(getActivity(), BookConsultation.class);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                }
            });


        } catch (IOException e) {
            Log.e(TAG, "Error processing image", e);
        }
    }

    private void navigateToAcnePage() {
        // Using Navigation Component to navigate to a fragment
        Intent intent = new Intent(requireActivity(), acneActivity.class);
        startActivity(intent);
    }
    private void navigateToInsectPage() {
        // Using Navigation Component to navigate to a fragment
        Intent intent = new Intent(requireActivity(), insectBiteActivity.class);
        startActivity(intent);
    }

    private void navigateToPsoriasisPage() {
        // Using Navigation Component to navigate to a fragment
        Intent intent = new Intent(requireActivity(), psoriasisActivity.class);
        startActivity(intent);
    }

    private void navigateToRingwormPage() {
        // Using Navigation Component to navigate to a fragment
        Intent intent = new Intent(requireActivity(), ringwormActivity.class);
        startActivity(intent);
    }

    private void navigateToNailPage() {
        // Using Navigation Component to navigate to a fragment
        Intent intent = new Intent(requireActivity(), nailActivity.class);
        startActivity(intent);
    }

    void resetPage() {
        IVPreviewImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_image_24, null)); // Reset image
        IVPreviewImage.setVisibility(View.GONE); // Hide image
        textView2.setText(""); // Clear result
        textView2.setVisibility(View.GONE); // Hide result
    }

    // Method to store data in Firebase including the image URL
    void storeDataInFirebase(Bitmap imageBitmap, String resultText) {
        // Generate current timestamp
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime()); // Format the date as "12/4/2023"

        // Convert bitmap to byte array
        byte[] imageData = convertBitmapToByteArray(imageBitmap);

        // Upload image to Firebase Storage
        String imagePath = "diseaseImage/" + date + ".png"; // Storage path for the image using date as filename
        StorageReference imageRef = mStorageRef.child(imagePath);
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully, now get the download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageURL = uri.toString();
                // Store data in Firestore along with the image URL
                try {
                    mFirestore.collection("patients")
                            .document(userId)
                            .collection("history")
                            .add(new HashMap<String, Object>() {{
                                put("result", resultText);
                                put("date", date); // Store date in Firestore
                                put("imageURL", imageURL); // Store image URL in Firestore
                            }})
                            .addOnSuccessListener(documentReference -> {
                                // Data successfully added to Firestore
                                Toast.makeText(requireContext(), "Data stored in Firestore", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Error occurred while adding data to Firestore
                                String errorMessage = "Error storing data in Firestore: " + e.getMessage();
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            });
                } catch (Exception e) {
                    // Error occurred
                    String errorMessage = "Error storing data in Firestore: " + e.getMessage();
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                // Error occurred while getting image download URL
                String errorMessage = "Error getting image URL: " + e.getMessage();
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Error occurred while uploading image
            String errorMessage = "Error uploading image: " + e.getMessage();
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
        });
    }

    // Load model file from assets
    private MappedByteBuffer loadModelFile() throws IOException {
        // Load model file here
        AssetManager assetManager = requireContext().getAssets();
        AssetFileDescriptor fileDescriptor = assetManager.openFd("modelling_Universe.tflite");
        FileInputStream inputStream = null;
        try {
            inputStream = fileDescriptor.createInputStream();
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    // Preprocess the input image into a ByteBuffer
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        // Convert bitmap to byte buffer here
        int inputSize = 224; // Change this to match your model's input size
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[inputSize * inputSize];
        bitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize);
        int pixel = 0;
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                byteBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }
        return byteBuffer;
    }

    // Find the index of the result class
    private int findResultClassIndex(float[][] outputArray) {
        // Find result class index here
        int maxIndex = 0;
        float maxProb = outputArray[0][0];
        for (int i = 1; i < outputArray[0].length; i++) {
            if (outputArray[0][i] > maxProb) {
                maxProb = outputArray[0][i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    // Load class labels from assets
    private List<String> loadClassLabels() throws IOException {
        List<String> classList = new ArrayList<>();
        BufferedReader reader = null;
        try {
            AssetManager assetManager = requireContext().getAssets();
            InputStream inputStream = assetManager.open("classes.txt");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                classList.add(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return classList;
    }

    // Convert Bitmap to byte array
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
