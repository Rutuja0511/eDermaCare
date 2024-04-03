package com.example.edermacarelatestt;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.firestore.FirebaseFirestore;

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

    // UI elements
    Button BSelectImage;
    Button Process;
    Button Reset;
    ImageView IVPreviewImage;
    TextView textView2;

    // Firebase
    private FirebaseFirestore mFirestore;

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
    private static final int OUTPUT_SIZE = 5;// specify the size of the output array

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detect1, container, false);

        // Initialize Firebase
        mFirestore = FirebaseFirestore.getInstance();

        // Get user ID from arguments
        if (getArguments() != null) {
            userId = getArguments().getString("user_id");
        }

        // Register UI elements
        BSelectImage = view.findViewById(R.id.BSelectImage);
        IVPreviewImage = view.findViewById(R.id.IVPreviewImage);
        textView2 = view.findViewById(R.id.textView2);
        Process = view.findViewById(R.id.Process);
        Reset = view.findViewById(R.id.Reset);

        // Handle button click to select image
        BSelectImage.setOnClickListener(v -> selectImage());
        Process.setOnClickListener(v -> processImage());
        Reset.setOnClickListener(v -> resetPage());

        return view;
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
            String resultText = "Class: " + predictedClassName;
            textView2.setText(resultText);
            textView2.setVisibility(View.VISIBLE); // Show result

            // Store data in Firebase
            storeDataInFirebase(imageBitmap, resultText);

        } catch (IOException e) {
            Log.e(TAG, "Error processing image", e);
        }
    }

    // Method to reset the page to its initial state
    void resetPage() {
        IVPreviewImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_image_24, null)); // Reset image
        IVPreviewImage.setVisibility(View.GONE); // Hide image
        textView2.setText(""); // Clear result
        textView2.setVisibility(View.GONE); // Hide result
    }

    // Method to store data in Firebase
    void storeDataInFirebase(Bitmap imageBitmap, String resultText) {
        // Generate current timestamp
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = dateFormat.format(calendar.getTime());

        // Convert bitmap to byte array
        byte[] imageData = convertBitmapToByteArray(imageBitmap);

        // Convert byte array to list of integers
        List<Integer> imageList = new ArrayList<>();
        for (byte b : imageData) {
            imageList.add((int) b);
        }

        // Store data in Firestore
        try {
            mFirestore.collection("patients")
                    .document(userId) // Assuming 'userId' is the unique identifier of the current user
                    .collection("history")
                    .add(new HashMap<String, Object>() {{

                        put("result", resultText);
                        put("time", timestamp);
                        put("date", calendar.getTimeInMillis()); // Add date as timestamp
                    }})
                    .addOnSuccessListener(documentReference -> {
                        // Data successfully added
                        Toast.makeText(requireContext(), "Data stored in Firestore", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Error occurred
                        String errorMessage = "Error storing data in Firestore: " + e.getMessage();

                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            String errorMessage = "Error storing data in Firestore: " + e.getMessage();

            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    // Load model file from assets
    private MappedByteBuffer loadModelFile() throws IOException {
        // Load model file here
        AssetManager assetManager = requireContext().getAssets();
        AssetFileDescriptor fileDescriptor = assetManager.openFd("modelling1.tflite");
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