package com.example.edermacarelatestt;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.res.AssetManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class detectFragment1 extends Fragment {

    // UI elements
    Button BSelectImage;
    Button Process;
    ImageView IVPreviewImage;
    TextView textView2;

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
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                                        IVPreviewImage.setImageBitmap(bitmap);
                                        processImage(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detect1, container, false);

        // Register UI elements
        BSelectImage = view.findViewById(R.id.BSelectImage);
        IVPreviewImage = view.findViewById(R.id.IVPreviewImage);
        textView2 = view.findViewById(R.id.textView2);
        Process = view.findViewById(R.id.Process);

        // Handle button click to select image
        BSelectImage.setOnClickListener(v -> selectImage());
        Process.setOnClickListener(v -> processImage(((BitmapDrawable) IVPreviewImage.getDrawable()).getBitmap()));


        return view;
    }

    // Method to launch image selection activity
    void selectImage() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        selectImageLauncher.launch(Intent.createChooser(i, "Select Picture"));
    }

    // Method to process the selected image
    void processImage(Bitmap bitmap) {

        Bitmap imageBitmap = ((BitmapDrawable) IVPreviewImage.getDrawable()).getBitmap();
        // Load the TFLite model (assuming you have 'model.tflite' in the assets folder)
        try {
            Interpreter.Options options = new Interpreter.Options();
// Add any options if needed, e.g., setNumThreads, setUseNNAPI, etc.
            Interpreter tflite = new Interpreter(loadModelFile(), options);
            int inputSize = 224; // Change this to match your model's input size

            // Preprocess the input image to match the model input requirements
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false);
            ByteBuffer inputBuffer = convertBitmapToByteBuffer(resizedBitmap);

            float[][] outputArray = new float[1][tflite.getOutputTensor(0).shape()[1]];

            // Run inference on the preprocessed image
            tflite.run(inputBuffer, outputArray);

            // Call the findResultClassIndex method after running inference
            int resultClassIndex = findResultClassIndex(outputArray);
            List<String> classList = loadClassLabels();

            // Get the class name corresponding to the predicted class index
            String predictedClassName = classList.get(resultClassIndex);

            // Update the TextView with the processed result
            String resultText = "Result: " + predictedClassName;
            textView2.setText(resultText);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the TFLite model from the assets folder
    private MappedByteBuffer loadModelFile() throws IOException {
        // Load model file here
        AssetManager assetManager = requireContext().getAssets();
        AssetFileDescriptor fileDescriptor = assetManager.openFd("converted_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
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
}
