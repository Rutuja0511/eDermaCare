package com.example.edermacarelatestt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class acneActivity extends AppCompatActivity {

    private Button goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acne);

        // Initialize the goBackButton
        goBackButton = findViewById(R.id.gobackButton);

        // Set click listener for the goBackButton
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to navigate back
                finish();
            }
        });
    }
}
