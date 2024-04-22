package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class BookConsultation extends AppCompatActivity {

    EditText nameEditText, cityEditText;
    Button searchButton, listAllButton;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    ArrayList<Dermatologist> dermatologistArrayList;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_consultation);

        nameEditText = findViewById(R.id.dermatologist_book_name);
        cityEditText = findViewById(R.id.dermatologist_book_city);
        searchButton = findViewById(R.id.search);
        listAllButton = findViewById(R.id.listAll);
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        dermatologistArrayList = new ArrayList<>();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDermatologist();
            }
        });

//        listAllButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listAllDermatologists();
//            }
//        });
        listAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open ListAllDermatologists activity
                startActivity(new Intent(BookConsultation.this, ListAllDermatologists.class));
            }
        });
    }

    private void searchDermatologist() {
        String name = nameEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();

        db.collection("dermatologist")
                .whereEqualTo("Name", name)
                .whereEqualTo("City", city)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dermatologistArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Dermatologist dermatologist = document.toObject(Dermatologist.class);
                            dermatologistArrayList.add(dermatologist);
                        }
                        adapter = new MyAdapter(BookConsultation.this, dermatologistArrayList);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

//    private void listAllDermatologists() {
//        db.collection("dermatologist")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        dermatologistArrayList.clear();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Dermatologist dermatologist = document.toObject(Dermatologist.class);
//                            dermatologistArrayList.add(dermatologist);
//                        }
//                        adapter = new MyAdapter(BookConsultation.this, dermatologistArrayList);
//                        recyclerView.setAdapter(adapter);
//                    }
//                });
//    }
}
