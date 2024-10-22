package com.example.employ_events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.provider.Settings;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEventFragment extends Fragment {

    private AddEventBinding binding;
    private Date eventDate, registrationDeadline;
    private Time eventStartTime, eventEndTime;
    private String android_id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = AddEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get references to input fields and buttons
        EditText eventTitleInput = binding.eventTitle;
        EditText descriptionInput = binding.description;
        Button eventDateButton = binding.eventDate;
        Button registrationDeadlineButton = binding.registrationDateDeadline;
        Button startTimeButton = binding.eventStartTime;
        Button endTimeButton = binding.eventEndTime;
        Button saveButton = binding.saveEventButton;

        android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);


        // Date picker dialogs
        eventDateButton.setOnClickListener(view -> showDatePicker(eventDateButton, true));
        registrationDeadlineButton.setOnClickListener(view -> showDatePicker(registrationDeadlineButton, false));

        // Time picker dialogs
        startTimeButton.setOnClickListener(view -> showTimePicker(startTimeButton, true));
        endTimeButton.setOnClickListener(view -> showTimePicker(endTimeButton, false));

        // Save event button click listener
        saveButton.setOnClickListener(view -> {
            try {
                String eventTitle = eventTitleInput.getText().toString();
                String description = descriptionInput.getText().toString();

                if (eventDate == null || registrationDeadline == null || eventStartTime == null || eventEndTime == null) {
                    Toast.makeText(getContext(), "Please select all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new Event object (assuming Event constructor exists)
                Event newEvent = new Event(
                        eventTitle, eventDate, registrationDeadline, new Date(), false, description,
                        eventStartTime, eventEndTime
                );

                Toast.makeText(getContext(), "Event Created: " + eventTitle, Toast.LENGTH_SHORT).show();

                // TODO: Save the event in a list or database

            } catch (Exception e) {
                Toast.makeText(getContext(), "Error creating event!", Toast.LENGTH_SHORT).show();
            }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
