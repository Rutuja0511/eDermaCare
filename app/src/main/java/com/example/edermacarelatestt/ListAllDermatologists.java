package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ListAllDermatologists extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    RecyclerView recyclerViewListAll;
    FirebaseFirestore db;
    ArrayList<Dermatologist> dermatologistArrayList;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_dermatologist);

        recyclerViewListAll = findViewById(R.id.recyclerView);
        recyclerViewListAll.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        dermatologistArrayList = new ArrayList<>();

        // Call method to fetch and display all dermatologists
        listAllDermatologists();
    }

    private void listAllDermatologists() {
        db.collection("dermatologist")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dermatologistArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Dermatologist dermatologist = document.toObject(Dermatologist.class);
                            dermatologistArrayList.add(dermatologist);
                        }
                        adapter = new MyAdapter(ListAllDermatologists.this, dermatologistArrayList);
                        adapter.setOnItemClickListener(ListAllDermatologists.this);
                        recyclerViewListAll.setAdapter(adapter);
                    } else {
                        Log.d("ListAllDermatologists", "Error getting dermatologists: ", task.getException());
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Dermatologist selectedDermatologist = dermatologistArrayList.get(position);
        String userId = getIntent().getStringExtra("user_id");
        Intent intent = new Intent(ListAllDermatologists.this, BookAppointmentActivity.class);
        intent.putExtra("Name", selectedDermatologist.getName());
        intent.putExtra("City", selectedDermatologist.getCity());
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }
}
