package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    RecyclerView recyclerViewSearchResults;
    FirebaseFirestore db;
    ArrayList<Dermatologist> searchResultsList;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerViewSearchResults = findViewById(R.id.recyclerView);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        searchResultsList = new ArrayList<>();

        // Set an empty adapter initially
        adapter = new MyAdapter(SearchResultsActivity.this, searchResultsList);
        adapter.setOnItemClickListener(SearchResultsActivity.this);
        recyclerViewSearchResults.setAdapter(adapter);

        // Get search parameters from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("Name");
        String city = intent.getStringExtra("City");

        // Call method to fetch and display search results
        searchDermatologists(name, city);
    }


    private void searchDermatologists(String name, String city) {
        // Build Firestore query based on search parameters
        Query query;

        if (name != null && !name.isEmpty() && (city == null || city.isEmpty())) {
            query = db.collection("dermatologist").whereEqualTo("Name", name);
        } else if (city != null && !city.isEmpty() && (name == null || name.isEmpty())) {
            query = db.collection("dermatologist").whereEqualTo("City", city);
        } else if (name != null && !name.isEmpty() && city != null && !city.isEmpty()) {
            query = db.collection("dermatologist").whereEqualTo("Name", name).whereEqualTo("City", city);
        } else {
            return;
        }

        // Execute the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                searchResultsList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Dermatologist dermatologist = document.toObject(Dermatologist.class);
                    // Check if the dermatologist's name or city matches the search criteria
                    if (name != null && !name.isEmpty() && dermatologist.getName().equalsIgnoreCase(name)) {
                        searchResultsList.add(dermatologist);
                    } else if (city != null && !city.isEmpty() && dermatologist.getCity().equalsIgnoreCase(city)) {
                        searchResultsList.add(dermatologist);
                    }
                }

                if (searchResultsList.isEmpty()) {
                    // If searchResultsList is empty, show a Toast message
                    Toast.makeText(SearchResultsActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                } else {
                    // Update RecyclerView with search results
                    adapter.notifyDataSetChanged(); // Notify adapter of data changes
                }
            } else {
                Log.e("SearchResultsActivity", "Error getting documents: ", task.getException());
            }
        });
    }



    @Override
    public void onItemClick(int position) {
        // Handle item click, e.g., start BookAppointmentActivity with selected dermatologist's details
        Dermatologist selectedDermatologist = searchResultsList.get(position);
        // Start BookAppointmentActivity and pass necessary data
        Intent intent = new Intent(SearchResultsActivity.this, BookAppointmentActivity.class);
        intent.putExtra("Name", selectedDermatologist.getName());
        intent.putExtra("City", selectedDermatologist.getCity());
        startActivity(intent);
    }

}

