package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class DermatologistProfile extends AppCompatActivity {

    Button mybutton, mybutton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dermatologist_profile);

        mybutton = findViewById(R.id.myButton);
        mybutton1 = findViewById(R.id.clinicbutton);
        mybutton.setOnClickListener(v -> {
            // Navigate to Activitydash2 activity with profileFragment2 fragment specified
            Intent intent = new Intent(DermatologistProfile.this, Activitydash2.class);
            intent.putExtra("fragment", "profileFragment2");
            startActivity(intent);
        });

        mybutton1.setOnClickListener(v -> {
            // Navigate to clinicProfile activity
            Intent intent = new Intent(DermatologistProfile.this, Activitydash2.class);
            intent.putExtra("fragment", "clinicProfile");
            startActivity(intent);
        });
    }
}
