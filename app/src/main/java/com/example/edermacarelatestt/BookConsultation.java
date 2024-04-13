package com.example.edermacarelatestt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BookConsultation extends AppCompatActivity {

    private ArrayList<Dermatologist> dermatologists;
    private ConstraintLayout dermatologistListLayout;

    private EditText nameEditText, cityEditText;
    private Button searchButton, listAllButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_consultation);

        dermatologistListLayout = findViewById(R.id.dermatologist_book_layout);
        nameEditText = findViewById(R.id.dermatologist_book_name);
        cityEditText = findViewById(R.id.dermatologist_book_city);
        searchButton = findViewById(R.id.search);
        listAllButton = findViewById(R.id.listAll);
        db = FirebaseFirestore.getInstance();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String city = cityEditText.getText().toString().trim();
                searchDermatologists(name, city);
            }
        });

        listAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllDermatologists();
            }
        });
    }

    private void searchDermatologists(String name, String city) {
        dermatologists = new ArrayList<>();
        db.collection("dermatologist")
                .whereEqualTo("Name", name)
                .whereEqualTo("City", city)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(BookConsultation.this, "No dermatologists found.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Dermatologist dermatologist = documentSnapshot.toObject(Dermatologist.class);
                                dermatologists.add(dermatologist);
                            }
                            displayResults(dermatologists);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("BookConsultation", "Error searching dermatologists: " + e.getMessage());
                        Toast.makeText(BookConsultation.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAllDermatologists() {
        dermatologists = new ArrayList<>();
        db.collection("dermatologist")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(BookConsultation.this, "No dermatologists found.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Dermatologist dermatologist = documentSnapshot.toObject(Dermatologist.class);
                                dermatologists.add(dermatologist);
                            }
                            displayResults(dermatologists);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("BookConsultation", "Error fetching all dermatologists: " + e.getMessage());
                        Toast.makeText(BookConsultation.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayResults(ArrayList<Dermatologist> results) {
        dermatologistListLayout.removeAllViews();
        for (Dermatologist dermatologist : results) {
            View view = getLayoutInflater().inflate(R.layout.dermatologist_book_layout, null);
            TextView nameTextView = view.findViewById(R.id.name);
            TextView cityTextView = view.findViewById(R.id.city);
            nameTextView.setText(dermatologist.getName());
            cityTextView.setText(dermatologist.getCity());
            dermatologistListLayout.addView(view);
        }
    }
}
