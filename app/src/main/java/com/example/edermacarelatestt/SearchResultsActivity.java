package com.example.edermacarelatestt;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    ArrayList<Dermatologist> dermatologistArrayList;
    MyAdapter adapter;
    private String name, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        dermatologistArrayList = new ArrayList<>();

        // Get the name and city from the intent
        name = getIntent().getStringExtra("name");
        city = getIntent().getStringExtra("city");

        // Call method to search dermatologists based on name and city
        searchDermatologists();
    }

    private void searchDermatologists() {
        // Construct the query dynamically based on the provided name and/or city
        Query query = db.collection("dermatologist");

        if (!TextUtils.isEmpty(name)) {
            query = query.whereEqualTo("Name", name);
        }

        if (!TextUtils.isEmpty(city)) {
            query = query.whereEqualTo("City", city);
        }

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        dermatologistArrayList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Dermatologist dermatologist = document.toObject(Dermatologist.class);
                            dermatologistArrayList.add(dermatologist);
                        }

                        if (dermatologistArrayList.isEmpty()) {
                            // Display a toast indicating no results were found
                            Toast.makeText(SearchResultsActivity.this, "No dermatologists found", Toast.LENGTH_SHORT).show();
                        } else {
                            // Display the search results
                            adapter = new MyAdapter(SearchResultsActivity.this, dermatologistArrayList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("SearchResultsActivity", "Error getting documents: ", e);
                    }
                });
    }
}
