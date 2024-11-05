package com.example.employ_events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.employ_events.ui.facility.CreateFacilityFragment;
import com.example.employ_events.ui.facility.Facility;
import com.example.employ_events.ui.facility.FacilityFragment;
import com.example.employ_events.ui.profile.NewProfileFragment;
import com.example.employ_events.ui.profile.Profile;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


import com.example.employ_events.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NewProfileFragment.NewProfileDialogListener{

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseFirestore db;



    @Override
    public void provideInfo(String name, String email, String uniqueID) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        db.collection("userProfiles").document(uniqueID).set(data, SetOptions.merge());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.employ_events.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_facility, R.id.nav_profile, R.id.nav_list)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;

        if (sharedPreferences.contains("uniqueID")) {
            // Retrieve the existing UUID
            uniqueID = sharedPreferences.getString("uniqueID", null);
        } else {
            // Generate a new UUID and save it
            uniqueID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString("uniqueID", uniqueID).apply();
        }

        // Create an empty profile using their Unique ID (will not need to sign in).
        DocumentReference docRef = db.collection("userProfiles").document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && !document.exists()) {
                    db.collection("userProfiles").document(uniqueID).set(new Profile(uniqueID));
                }
            } else {
                // Handle the error, e.g., log it
                Log.e("MainActivity", "Error getting documents: ", task.getException());
            }
        });

        // Use this to check if the required INFO exists - move to waiting list.

        /*
        DocumentReference docRef = db.collection("userProfiles").document(uniqueID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && !document.exists()) {
                        new NewProfileFragment().show(getSupportFragmentManager(), "Create Profile");
                    }
                } else {
                    // Handle the error, e.g., log it
                    Log.e("MainActivity", "Error getting documents: ", task.getException());
                }
            }
        });

         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



}
