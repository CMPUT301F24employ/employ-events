package com.example.employ_events.ui.events;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.employ_events.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/*
Authors: Tina

US 02.02.02 As an organizer I want to see on a map where entrants joined my event waiting list from.

https://github.com/osmdroid/osmdroid
 */

/**
 * A fragment that displays a map with markers showing the join locations of entrants for the event.
 * The map is centered based on the average location of all the entrants, and each entrant's location is marked with a pin.
 */
public class EventEntrantsMap extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventID;
    private float totalLongitude, totalLatitude;
    private int count = 0;

    // Empty constructor required for Fragment
    public EventEntrantsMap() {}

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @param eventID The unique identifier for the event whose entrants are to be displayed.
     * @return A new instance of EventEntrantsMap fragment.
     */
    public static EventEntrantsMap newInstance(String eventID) {
        EventEntrantsMap fragment = new EventEntrantsMap();
        Bundle args = new Bundle();
        args.putString("EVENT_ID", eventID);  // Pass the eventID as an argument
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes the eventID from the arguments and configures OSMDroid.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = getArguments().getString("EVENT_ID"); // Retrieve eventID
        }
        // Initialize OSMDroid Configuration
        Configuration.getInstance().setUserAgentValue("com.example.employ_events");
    }

    /**
     * Sets up the map and fetches entrants' locations.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.entrants_map, container, false);

        // Initialize MapView
        MapView mapView = rootView.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setTilesScaledToDpi(true);
        mapView.setUseDataConnection(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(10f);
        mapController.setCenter(new GeoPoint(53.5461, -113.4938)); // Center the map on Edmonton
        // Fetch entrants' locations
        fetchEntrantLocations(mapView);

        return rootView;
    }


    /**
     * Fetches the locations of entrants in the event and displays them on the map.
     * It also calculates the average location to center the map.
     *
     * @param mapView The MapView object where markers will be placed.
     */
    private void fetchEntrantLocations(MapView mapView) {
        db.collection("events").document(eventID).collection("entrantsList")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot entrantsSnapshot = task.getResult();
                        for (DocumentSnapshot document : entrantsSnapshot) {
                            Double latitude = document.getDouble("latitude");
                            Double longitude = document.getDouble("longitude");

                            if (latitude != null && longitude != null) {
                                // Add a marker for each entrant's location
                                GeoPoint location = new GeoPoint(latitude, longitude);
                                Marker marker = new Marker(mapView);
                                marker.setPosition(location);
                                marker.setTitle(document.getString("name"));
                                marker.setSnippet(document.getString("email"));
                                mapView.getOverlays().add(marker);

                                totalLatitude += latitude;
                                totalLongitude += longitude;
                                count++;
                            }
                        }

                        if (count > 0) {
                            // Calculate the average latitude and longitude
                            double avgLatitude = totalLatitude / count;
                            double avgLongitude = totalLongitude / count;

                            // Set the map center to the average location
                            IMapController mapController = mapView.getController();
                            mapController.setZoom(10f);
                            mapController.setCenter(new GeoPoint(avgLatitude, avgLongitude));  // Center the map
                        }

                    } else {
                        Log.e("EventEntrantsMap", "Error fetching entrants: ", task.getException());
                    }
                });
    }
}
