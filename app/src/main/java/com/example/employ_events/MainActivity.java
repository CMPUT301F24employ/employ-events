package com.example.employ_events;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.employ_events.ui.notifications.Notification;
import com.example.employ_events.ui.profile.Profile;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.firebase.firestore.QueryDocumentSnapshot;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseFirestore db;
    private boolean isAdmin;
    private String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.employ_events.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannels();
        requestNotificationPermission();
        setSupportActionBar(binding.appBarMain.toolbar);
        monitorNotifications();

        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("uniqueID")) {
            // Retrieve the existing UUID
            uniqueID = sharedPreferences.getString("uniqueID", null);
        }
        else {
            // Generate a new UUID and save it
            uniqueID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("uniqueID", uniqueID);
            editor.apply();  // Commit changes asynchronously
        }

        // Create an empty profile using their Unique ID (will not need to sign in).
        DocumentReference docRef = db.collection("userProfiles").document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    docRef.set(new Profile(uniqueID));
                }
            } else {
                // Handle the error, e.g., log it
                Log.e("MainActivity", "Error getting documents: ", task.getException());
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Show admin menu buttons for admins and hide for non admins.
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            isAdmin = Boolean.TRUE.equals(documentSnapshot.getBoolean("admin"));
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_browse_event).setVisible(isAdmin);
            menu.findItem(R.id.nav_image).setVisible(isAdmin);
            menu.findItem(R.id.adminBrowseProfilesFragment).setVisible(isAdmin);
            menu.findItem(R.id.adminBrowseFacilitiesFragment).setVisible(isAdmin);
        }).addOnFailureListener(e -> Log.e("MainActivity", "Error fetching admin status: ", e));

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.scan_qr_code, R.id.nav_facility, R.id.nav_profile, R.id.nav_registered_events,
                R.id.entrantNotifications, R.id.nav_invitations)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

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

    /**
     * @author Sahara, Tina
     * Initializes the notifications channels for: organizers, admins, app notifications.
     */
    private void createNotificationChannels() {
        // Create the NotificationManager instance
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (notificationManager != null) {
            // Organizer Notification Channel
            CharSequence organizerName = getString(R.string.organizer_channel_name);
            String organizerDescription = getString(R.string.organizer_channel_description);
            String ORGANIZER_CHANNEL_ID = "organizer_notification_channel";
            int organizerImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel organizerChannel = new NotificationChannel(
                    ORGANIZER_CHANNEL_ID, organizerName, organizerImportance);
            organizerChannel.setDescription(organizerDescription);
            notificationManager.createNotificationChannel(organizerChannel);

            // App Notification Channel
            CharSequence appName = getString(R.string.app_channel_name);
            String appDescription = getString(R.string.app_channel_description);
            String APP_CHANNEL_ID = "app_notification_channel";
            int appImportance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel appChannel = new NotificationChannel(
                    APP_CHANNEL_ID, appName, appImportance);
            appChannel.setDescription(appDescription);
            notificationManager.createNotificationChannel(appChannel);

            // Admin Notification Channel
            CharSequence adminName = getString(R.string.admin_channel_name);
            String adminDescription = getString(R.string.admin_channel_description);
            String ADMIN_CHANNEL_ID = "admin_notification_channel";
            int adminImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel adminChannel = new NotificationChannel(
                    ADMIN_CHANNEL_ID, adminName, adminImportance);
            adminChannel.setDescription(adminDescription);
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    /**
     * @author Aasvi
     * Requests permission from user for notifications.
     */
    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1);
        }
    }

    /**
     * @author Sahara
     * Monitors user notifications from firebase.
     */
    private void monitorNotifications() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);

        if (uniqueID == null) {
            Log.e("MonitorNotifications", "Unique ID is null. Cannot monitor notifications.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("userProfiles")
                .document(uniqueID)
                .collection("Notifications")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        // List to store unread notifications
                        List<Notification> unreadNotifications = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : snapshots) {
                            // Convert Firestore data to Notification object

                            Map<String, Object> notificationMap = (Map<String, Object>) doc.get("Notification");
                            if (notificationMap != null) {
                                String eventID = (String) notificationMap.get("eventID");
                                String message = (String) notificationMap.get("message");
                                Boolean read = (Boolean) notificationMap.get("read");
                                String CHANNEL_ID = (String) notificationMap.get("CHANNEL_ID");


                                // Create Notification object
                                Notification notification = new Notification(eventID, message, Boolean.TRUE.equals(read), CHANNEL_ID);
                                // Check if notification is unread
                                if (!notification.isRead()) {
                                    unreadNotifications.add(notification);

                                    // Optionally mark the notification as read in Firebase
                                    doc.getReference().update("Notification.read", true)
                                            .addOnSuccessListener(aVoid ->
                                                    Log.d("Firestore", "Notification marked as read"))
                                            .addOnFailureListener(error ->
                                                    Log.e("Firestore", "Error marking notification as read", error));
                                }
                            }

                            // Send all unread notifications
                            for (Notification notification : unreadNotifications) {
                                notification.sendNotification(getApplicationContext());
                            }
                        }
                    }
                });
    }
}
