package com.example.edermacarelatestt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
public class Activitydash extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityfiledashboard);


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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new detectFragment1()).commit();
            navigationView.setCheckedItem(R.id.navhome);
        }
    }
    @Override

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navhome) {
            String userId = getIntent().getStringExtra("user_id");
            if (userId != null) {
                // Pass userId to profileFragment
                Bundle bundle = new Bundle();
                bundle.putString("user_id", userId);
                detectFragment1 fragment = new detectFragment1();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            } else {
                // Handle the case where userId is not found
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.navhs) {
            String userEmail = getIntent().getStringExtra("user_email");

            if (userEmail != null) {
                // Pass user email to profileFragment
                Bundle bundle = new Bundle();
                bundle.putString("user_email", userEmail);
                profileFragment fragment = new profileFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            } else {
                // Handle the case where user email is not found
                Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.navh) {
            String userId = getIntent().getStringExtra("user_id");
            if (userId != null) {
                // Pass userId to profileFragment
                Bundle bundle = new Bundle();
                bundle.putString("user_id", userId);
                detectFragment1 fragment = new detectFragment1();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            } else {
                // Handle the case where userId is not found
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            }
             }
        else if (itemId == R.id.navh2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HistoryFragment()).commit();
        }
        else if (itemId == R.id.navh3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConsultationFragment()).commit();
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
}
