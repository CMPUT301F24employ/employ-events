package com.example.employ_events.ui.fragment.facility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.model.Facility;

/*
Authors: Tina

The purpose of this fragment is to prompt the user to create a facility if they click on facility but do not have one.
When dismissed or cancelled, it will send the user back to the home screen as they are not allowed to manage their facility
if it does not exist.

US 02.01.03 As an organizer, I want to create and manage my facility profile

 */

/**
 * A DialogFragment for creating a new facility. It collects information from the user,
 * such as facility name, address, email, and phone number, and communicates this information
 * back to the hosting activity through the CreateFacilityDialogListener interface.
 * @author Tina
 */
public class CreateFacilityFragment extends DialogFragment {
    private CreateFacilityDialogListener listener;
    private boolean isCreateButtonClicked = false;


    /**
     * Interface for communicating facility creation events to the hosting activity.
     */
    public interface CreateFacilityDialogListener {
        /**
         * Called when a new facility is created.
         *
         * @param facility  The created Facility object.
         * @param uniqueID  A unique identifier for the facility.
         */
        void createFacility(Facility facility, String uniqueID);
        void onFacilityCreated();
    }

    /**
     * Sets the listener for facility creation events.
     * @param listener The listener to notify when a facility is created.
     */
    public void setListener(CreateFacilityDialogListener listener) {
        this.listener = listener;
    }
    /**
     * Called when the fragment is attached to its context. This is where we ensure that the
     * listener is an instance of CreateFacilityDialogListener.
     * @param context The context to which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateFacilityDialogListener) {
            listener = (CreateFacilityDialogListener) context;
        }
    }

    /**
     * Creates and returns the dialog for creating a new facility. It inflates the dialog layout,
     * sets up the positive and negative buttons, and handles the input validation and facility
     * creation process.
     * @return The created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Retrieve the uniqueID from shared preferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("uniqueID", null);

        // Inflate the dialog layout
        View view = getLayoutInflater().inflate(R.layout.create_facility, null);
        EditText editFacilityName = view.findViewById(R.id.editFacilityName);
        EditText editFacilityAddress = view.findViewById(R.id.editFacilityAddress);
        EditText editFacilityEmail = view.findViewById(R.id.editFacilityEmail);
        EditText editFacilityPhone = view.findViewById(R.id.editFacilityPhone);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle("Create Facility")
                .setPositiveButton("Create", null)
                .setNegativeButton("Not Now", null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                // Validate inputs before creating the facility
                if (validateInputs(editFacilityName, editFacilityEmail, editFacilityAddress)) {
                    String name = editFacilityName.getText().toString().trim();
                    String address = editFacilityAddress.getText().toString().trim();
                    String email = editFacilityEmail.getText().toString().trim();
                    String phone = editFacilityPhone.getText().toString().trim();
                    phone = phone.isEmpty() ? null : phone;

                    // Create a new Facility object and notify the listener
                    Facility facility = new Facility(name, email, address, uniqueID, phone);
                    listener.createFacility(facility, uniqueID);
                    listener.onFacilityCreated(); // Notify that the facility was created

                    Toast.makeText(getActivity(), "Facility Created!", Toast.LENGTH_SHORT).show();
                    isCreateButtonClicked = true;
                    dialog.dismiss();
                }
            });

            Button noButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            noButton.setOnClickListener(v -> {
                onCancel(dialog);
                dialog.dismiss();
            });
        });
        return dialog;
    }

    /**
     * Validates the user inputs for facility creation.
     * @param editFacilityName     The EditText for facility name.
     * @param editFacilityEmail    The EditText for facility email.
     * @param editFacilityAddress   The EditText for facility address.
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateInputs(EditText editFacilityName, EditText editFacilityEmail, EditText editFacilityAddress) {
        if (editFacilityName.getText().toString().trim().isEmpty()) {
            editFacilityName.setError("Facility name cannot be empty");
            editFacilityName.requestFocus();
            return false;
        }
        if (editFacilityEmail.getText().toString().trim().isEmpty()) {
            editFacilityEmail.setError("Facility email cannot be empty");
            editFacilityEmail.requestFocus();
            return false;
        }
        if (editFacilityAddress.getText().toString().trim().isEmpty()) {
            editFacilityAddress.setError("Facility address cannot be empty");
            editFacilityAddress.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Called when the dialog is canceled. This method informs the user that facility creation was
     * canceled and navigates back to the home fragment.
     */
    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        // Inform the user that facility creation was canceled
        Toast.makeText(getActivity(), "Facility creation canceled. You can start again anytime!", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(CreateFacilityFragment.this)
                .popBackStack(R.id.nav_home, false);
    }

    /**
     * Called when the dialog is dismissed. This method informs the user that facility creation was
     * canceled and navigates back to the home fragment.
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!isCreateButtonClicked) {
            // Inform the user that the facility creation was canceled
            Toast.makeText(getActivity(), "Facility creation canceled. You can start again anytime!", Toast.LENGTH_SHORT).show();
            // Navigate back to the home screen
            NavHostFragment.findNavController(CreateFacilityFragment.this)
                    .popBackStack(R.id.nav_home, false);
        }
    }

}
