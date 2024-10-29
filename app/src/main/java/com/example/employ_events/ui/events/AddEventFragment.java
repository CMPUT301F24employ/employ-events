package com.example.employ_events.ui.events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.employ_events.R;
import com.example.employ_events.databinding.AddEventBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEventFragment extends Fragment {

    private AddEventBinding binding;
    private Date eventDate, registrationDeadline, registrationStartDeadline;
    private Time eventStartTime, eventEndTime;
    private FirebaseFirestore db;

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

        // Unique ID
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;  // Represents the FACILITY ID
        uniqueID = sharedPreferences.getString("uniqueID", null);

        // FACILITY ID !!!!!!!!!!!
        String facilityID = getFacilityID(uniqueID);

        // Date picker dialogs
        eventDateButton.setOnClickListener(view -> showDatePicker(eventDateButton, true));
        registrationDeadlineButton.setOnClickListener(view -> showDatePicker(registrationDeadlineButton, false));
        registrationStartDeadlineButton.setOnClickListener(view-> showDatePicker(registrationStartDeadlineButton, false));
        // Time picker dialogs
        startTimeButton.setOnClickListener(view -> showTimePicker(startTimeButton, true));
        endTimeButton.setOnClickListener(view -> showTimePicker(endTimeButton, false));

        // Save event button click listener
        saveButton.setOnClickListener(view -> {
            try {
                String eventTitle = eventTitleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String limitString = limitInput.getText().toString();
                String feeString = feeInput.getText().toString();

                if (eventDate == null || registrationDeadline == null) {
                    Toast.makeText(getContext(), "Please select all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new Event object (assuming Event constructor exists)
                Event newEvent = new Event(
                        eventTitle, eventDate, registrationDeadline, registrationStartDeadline, false, description, facilityID
                );

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("events").add(newEvent)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Event Created Successfully", Toast.LENGTH_SHORT).show();
                            // Navigate back to FacilityFragment
                            Navigation.findNavController(view).navigate(R.id.action_addEventFragment_to_nav_facility);
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Error saving event!", Toast.LENGTH_SHORT).show());

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error creating event!", Toast.LENGTH_SHORT).show();
                }

                // TODO: Save the event in a list or database

        });

        return root;
    }

    private void showDatePicker(Button button, boolean isEventDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format and display the selected date
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

    private void showTimePicker(Button button, boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, selectedHour, selectedMinute) -> {
                    // Format and display the selected time
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

    private String getFacilityID(String uniqueID) {
        final String[] facilityID = new String[1];
        Query facility = db.collection("facilities").whereEqualTo("owner_id", uniqueID);
        facility.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        facilityID[0] = document.getId();
                    }
                }
            }
        });
        return facilityID[0];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
