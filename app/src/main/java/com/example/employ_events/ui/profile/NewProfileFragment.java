package com.example.employ_events.ui.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.employ_events.R;

/**
 * A fragment that displays a dialog for creating a new user profile.
 * The user must enter their name and email address to create the profile.
 */
public class NewProfileFragment extends DialogFragment {
    private NewProfileDialogListener listener;

    /**
     * Interface for communicating with the host activity.
     */
    public interface NewProfileDialogListener {
        /**
         * Called when a new profile is added.
         *
         */
        void provideInfo(String name, String email, String uniqueID);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NewProfileDialogListener) {
            listener = (NewProfileDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement NewProfileDialogListener");
        }
    }

    /**
     * Creates a dialog for the user to enter their name and email.
     * The dialog requires the user to provide this information before proceeding.
     *
     * @param savedInstanceState If non-null, this dialog is being re-created from a previous saved state.
     * @return A dialog with input fields for the user's name and email.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;
        uniqueID = sharedPreferences.getString("uniqueID", null);
        View view = getLayoutInflater().inflate(R.layout.provide_info, null);
        EditText edit_name = view.findViewById(R.id.editName);
        EditText edit_email = view.findViewById(R.id.editEmailAddress);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setView(view)
                .setTitle("Name and Email Required")
                .setPositiveButton("Confirm", null); // Initially set to null

        // Create the dialog
        AlertDialog dialog = builder.create();

        // User must enter name and email.
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Set up the positive button after the dialog is shown
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String name = edit_name.getText().toString().trim();
                String email = edit_email.getText().toString().trim();

                if (name.isEmpty()) {
                    edit_name.setError("Name cannot be empty");
                    edit_name.requestFocus();
                } else if (email.isEmpty()) {
                    edit_email.setError("Email cannot be empty");
                    edit_email.requestFocus();
                } else {
                    listener.provideInfo(name, email, uniqueID);
                    dialog.dismiss(); // Dismiss the dialog if input is valid
                }
            });
        });

        return dialog;
    }

}


