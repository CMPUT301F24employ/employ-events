package com.example.employ_events.ui.events;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.employ_events.R;
import com.example.employ_events.databinding.AddEventBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * AddEventFragment is a Fragment that allows organizers to create an new event
 * by providing details such as title, description, dates, time, fee, capacity and location.
 */
public class AddEventFragment extends Fragment {

    private AddEventBinding binding;
    private Date eventDate, registrationDeadline, registrationStartDeadline;
    private Time eventStartTime, eventEndTime;
    private FirebaseFirestore db;
    private String facilityID; // Variable to hold the facility ID
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private Uri bannerUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = AddEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();

        // Get references to input fields and buttons
        EditText eventTitleInput = binding.eventTitle;
        EditText descriptionInput = binding.description;
        EditText limitInput = binding.limit;
        EditText feeInput = binding.fee;
        Button eventDateButton = binding.eventDate;
        Button registrationDeadlineButton = binding.registrationDateDeadline;
        Button registrationStartDeadlineButton = binding.registrationStartDeadline;
        Button startTimeButton = binding.eventStartTime;
        Button endTimeButton = binding.eventEndTime;
        Button saveButton = binding.saveEventButton;
        CheckBox geoLocation = binding.geolocationStatus;
        Button uploadBannerButton = binding.uploadBannerButton;
        ImageView bannerImageView = binding.bannerImage;
        Button removeBannerButton = binding.removeBannerButton;

        // Unique ID
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);

        // Fetch the facility ID
        fetchFacilityID(uniqueID);

        // Date picker dialogs
        eventDateButton.setOnClickListener(view -> showDatePicker(eventDateButton, true));
        registrationDeadlineButton.setOnClickListener(view -> showDatePicker(registrationDeadlineButton, false));
        registrationStartDeadlineButton.setOnClickListener(view -> showDatePicker(registrationStartDeadlineButton, false));

        // Time picker dialogs
        startTimeButton.setOnClickListener(view -> showTimePicker(startTimeButton, true));
        endTimeButton.setOnClickListener(view -> showTimePicker(endTimeButton, false));

        // Check for storage permission
        checkStoragePermission();

        // Initially hide the remove button
        removeBannerButton.setVisibility(View.GONE);

        // Set click listener for the upload banner button
        uploadBannerButton.setOnClickListener(v -> openImageChooser());

        // Set click listener for the remove banner button
        removeBannerButton.setOnClickListener(v -> {
            bannerUri = null; // Clear the banner URI
            bannerImageView.setImageDrawable(null); // Clear the displayed image
            removeBannerButton.setVisibility(View.GONE); // Hide the remove button
        });


        // Save event button click listener
        saveButton.setOnClickListener(view -> {
            if (facilityID == null) {
                Toast.makeText(getContext(), "Facility ID not fetched yet.", Toast.LENGTH_SHORT).show();
                return; // Prevent saving if facility ID is not available
            }

            try {
                String eventTitle = eventTitleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String limitString = limitInput.getText().toString();
                String feeString = feeInput.getText().toString();

                if (eventDate == null || registrationDeadline == null) {
                    Toast.makeText(getContext(), "Please select all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Event newEvent;
                if (registrationStartDeadline != null) {
                    newEvent = new Event(
                            eventTitle, eventDate, registrationDeadline, registrationStartDeadline, false, facilityID
                    );
                } else {
                    newEvent = new Event(eventTitle, eventDate, registrationDeadline, new Date(), false, facilityID);
                }
                if (!limitString.isEmpty()) {
                    Integer limit = Integer.parseInt(limitString);
                    newEvent.setLimited(limit);
                }
                if (!feeString.isEmpty()) {
                    Integer fee = Integer.parseInt(feeString);
                    newEvent.setFee(fee);
                }
                if (eventStartTime != null) {
                    newEvent.setStartTime(eventStartTime);
                }
                if (eventEndTime != null) {
                    newEvent.setEndTime(eventEndTime);
                }
                if (geoLocation.isChecked()) {
                    newEvent.setGeoLocation(true);
                }
                if (!description.isEmpty()) {
                    newEvent.setDescription(description);
                }

                newEvent.setFacilityID(facilityID);

                if (bannerUri != null) {
                    uploadBannerAndSaveEvent(newEvent, view);
                } else {
                    saveEvent(newEvent, view);
                }


            } catch (Exception e) {
                Toast.makeText(getContext(), "Error creating event!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    /**
     * Displays a date picker dialog and updates the associated button and date variables.
     *
     * @param button      The button to update with the selected date.
     * @param isEventDate Indicates if the date is for the event date or registration deadline.
     */
    private void showDatePicker(Button button, boolean isEventDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    button.setText(sdf.format(selectedDate.getTime()));
                    if (isEventDate) {
                        eventDate = selectedDate.getTime();
                    } else {
                        registrationDeadline = selectedDate.getTime();
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    /**
     * Displays a time picker dialog and updates the associated button and time variables.
     *
     * @param button     The button to update with the selected time.
     * @param isStartTime Indicates if the time is for the event start time or end time.
     */
    private void showTimePicker(Button button, boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, selectedHour, selectedMinute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    button.setText(formattedTime);

                    Time time = new Time(selectedHour, selectedMinute, 0);
                    if (isStartTime) {
                        eventStartTime = time;
                    } else {
                        eventEndTime = time;
                    }
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }

    /**
     * Retrieves the facility ID associated with the given unique ID.
     *
     * @param uniqueID The unique ID of the user.
     * @param listener Callback to return the facility ID.
     */
    private void getFacilityID(String uniqueID, OnFacilityIDFetchedListener listener) {
        Query facility = db.collection("facilities").whereEqualTo("organizer_id", uniqueID);
        facility.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    listener.onFacilityIDFetched(document.getId());
                    return; // Stop after finding the first match
                }
            }
            listener.onFacilityIDFetched(null); // No match found
        });
    }

    /**
     * Fetches the facility ID for the current user and stores it in the facilityID variable.
     *
     * @param uniqueID The unique ID of the user.
     */
    public void fetchFacilityID(String uniqueID) {
        getFacilityID(uniqueID, id -> {
            facilityID = id; // Store the facility ID
        });
    }

    /**
     * Callback interface for fetching facility ID.
     */
    public interface OnFacilityIDFetchedListener {
        void onFacilityIDFetched(String facilityID);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Banner"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            bannerUri = data.getData();
            binding.bannerImage.setImageURI(bannerUri);
            binding.bannerImage.setVisibility(View.VISIBLE);
            binding.removeBannerButton.setVisibility(View.VISIBLE);
        }
    }

    private void uploadBannerAndSaveEvent(Event newEvent, View view) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("banners/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(bannerUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    newEvent.setBannerUrl(uri.toString());
                    saveEvent(newEvent, view);
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error uploading banner!", Toast.LENGTH_SHORT).show());
        Navigation.findNavController(view).navigate(R.id.action_addEventFragment_to_eventListFragment);
    }

    private void saveEvent(Event newEvent, View view) {
        db.collection("events").add(newEvent)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Event Created Successfully", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.action_addEventFragment_to_eventListFragment);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving event!", Toast.LENGTH_SHORT).show());

    }

    private void checkStoragePermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
