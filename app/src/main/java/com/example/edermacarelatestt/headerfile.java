package com.example.edermacarelatestt;

import android.util.Log;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class headerfile {
    private String userId;
    private TextView textView;

    public headerfile(String userId, TextView textView) {
        this.userId = userId;
        this.textView = textView;
        setNameForTextView();
    }

    private void setNameForTextView() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("patients").child(userId).child("Name");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.getValue(String.class);
                    textView.setText(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e("FirebaseError", "Error fetching data from Firebase: " + databaseError.getMessage());
                // You can also display a toast message to inform the user about the error
            }
        });
    }
}
