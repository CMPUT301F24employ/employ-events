package com.example.employ_events.ui.facility;

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

import com.example.employ_events.R;

/**
 * A dialog fragment for creating a new facility.
 * This fragment prompts the user to enter a facility name
 * and communicates the result back to the hosting activity or fragment.
 */
public class CreateFacilityFragment extends DialogFragment {
    private CreateFacilityDialogListener listener;

    /**
     * Interface to be implemented by the hosting activity or fragment
     * to handle the creation of a facility.
     */
    public interface CreateFacilityDialogListener {
        /**
         * Called when a facility is created.
         *
         * @param facility The created facility with the name and android ID.
         */
        void createFacility(Facility facility, String uniqueID);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateFacilityDialogListener) {
            listener = (CreateFacilityDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement CreateFacilityDialogListener");
        }
    }


    /**
     * Creates the dialog for entering facility information.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;
        uniqueID = sharedPreferences.getString("uniqueID", null);

        View view = getLayoutInflater().inflate(R.layout.create_facility, null);
        EditText edit_facility_name = view.findViewById(R.id.editFacilityName);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setView(view)
                .setTitle("Create Facility")
                .setPositiveButton("Create", null) // Initially set to null
                .setNegativeButton("Not Now", null);


        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set up the positive button after the dialog is shown
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String facilityName = edit_facility_name.getText().toString().trim();

                if (facilityName.isEmpty()) {
                    edit_facility_name.setError("Facility name cannot be empty");
                    edit_facility_name.requestFocus();
                } else {
                    listener.createFacility(new Facility(facilityName, uniqueID), uniqueID);
                    dialog.dismiss(); // Dismiss the dialog if input is valid
                }
            });

            Button no_button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            no_button.setOnClickListener(v -> {
                onCancel(dialog);
                dialog.dismiss();

            });
        });



        return dialog;
    }
    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        // Handles the cancel action here - shows a Toast.
        Toast.makeText(getActivity(), "Facility creation canceled. You can start again anytime!", Toast.LENGTH_SHORT).show();
    }

}


