package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class BookConsultation extends AppCompatActivity {

    EditText nameEditText, cityEditText;
    Button searchButton, listAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_consultation);

        nameEditText = findViewById(R.id.dermatologist_book_name);
        cityEditText = findViewById(R.id.dermatologist_book_city);
        searchButton = findViewById(R.id.search);
        listAllButton = findViewById(R.id.listAll);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get the name and city entered by the user
                String name = nameEditText.getText().toString().trim();
                String city = cityEditText.getText().toString().trim();

                // Open SearchResultsActivity and pass the name and city as extras
                Intent intent = new Intent(BookConsultation.this, SearchResultsActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });

        listAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user ID from Intent extras
                String userId = getIntent().getStringExtra("user_id");

                // Create the Intent to start ListAllDermatologists activity
                Intent intent = new Intent(BookConsultation.this, ListAllDermatologists.class);

                // Add the user ID as an extra to the Intent
                intent.putExtra("user_id", userId);

                // Start the ListAllDermatologists activity
                startActivity(intent);
            }
        });
    }
}
