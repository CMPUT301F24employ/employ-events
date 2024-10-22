package com.example.employ_events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.employ_events.databinding.AddEventBinding;

import java.sql.Time;
import java.util.Date;

public class AddEventFragment extends Fragment {

    private AddEventBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = AddEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get references to input fields
        EditText eventTitleInput = binding.eventTitle;
        EditText eventDateInput = binding.eventDate; // Assume date in yyyy-MM-dd format
        EditText registrationDeadlineInput = binding.registrationDateDeadline;
        EditText descriptionInput = binding.description;
        EditText startTimeInput = binding.eventStartTime;
        EditText endTimeInput = binding.eventEndTime;
        Button saveButton = binding.saveEventButton;

        // Set button click listener to create and save event
        saveButton.setOnClickListener(view -> {
            try {
                String eventTitle = eventTitleInput.getText().toString();
                Date eventDate = new Date(eventDateInput.getText().toString());
                Date registrationDeadline = new Date(registrationDeadlineInput.getText().toString());
                String description = descriptionInput.getText().toString();
                String endTime = endTimeInput.getText().toString();
                String startTime = startTimeInput.getText().toString();

                // Create a new Event object
                Event newEvent = new Event(
                        eventTitle, eventDate, registrationDeadline, new Date(), false, description, startTime, endTime
                );

                Toast.makeText(getContext(), "Event Created: " + eventTitle, Toast.LENGTH_SHORT).show();

                // TODO: Add code to save the event in a list or database

            } catch (Exception e) {
                Toast.makeText(getContext(), "Error creating event!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}