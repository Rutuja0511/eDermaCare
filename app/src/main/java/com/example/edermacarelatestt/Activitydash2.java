package com.example.edermacarelatestt;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Activitydash2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dermatologist);

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open,
                R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new fragment_home2()).commit();
            navigationView.setCheckedItem(R.id.navhome2);
        }
        String fragmentToLoad = getIntent().getStringExtra("fragment");

        // Load the appropriate fragment based on the extra
        if (fragmentToLoad != null && fragmentToLoad.equals("profileFragment2")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new profileFragment2())
                    .commit();
        }
        if (fragmentToLoad != null && fragmentToLoad.equals("clinicProfile")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new clinicProfile())
                    .commit();
        }
    }
    @Override

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navhs) {
            String userEmail = getIntent().getStringExtra("user_email");

            if (userEmail != null) {
                // Pass user email to profileFragment
                Bundle bundle = new Bundle();
                bundle.putString("user_email", userEmail);
                profileFragment2 fragment = new profileFragment2();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            } else {
                // Handle the case where user email is not found
                Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
            }

        }else if (itemId == R.id.navhs2) {
            // Redirect to Dermatologist Profile Activity
            startActivity(new Intent(Activitydash2.this,DermatologistProfile.class));
        }else if(itemId==R.id.navh6){
            String userId = getIntent().getStringExtra("user_id");
            Intent intent = new Intent(Activitydash2.this, DExtraDetails.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
//            startActivity(new Intent(Activitydash2.this,DExtraDetails.class));
        } else if(itemId==R.id.navh3){
            startActivity(new Intent(Activitydash2.this, incomingAppointment.class));
        } else  if (item.getItemId() == R.id.logout) {
            logout();
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void logout() {
        // Navigate back to the login screen
        Intent intent = new Intent(Activitydash2.this, LoginDermatologistActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }


}
