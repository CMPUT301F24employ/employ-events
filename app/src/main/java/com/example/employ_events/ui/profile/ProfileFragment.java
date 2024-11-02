package com.example.employ_events.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentProfileBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * A fragment that displays the user's profile information.
 * It retrieves the profile data from Firestore based on a unique identifier.
 */
public class ProfileFragment extends Fragment{

    private FragmentProfileBinding binding;
    private TextView name, email, phone_number;
    private Button editProfileButton;
    private ImageView pfp;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Grab uniqueID and initialize the firebase.
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;
        uniqueID = sharedPreferences.getString("uniqueID", null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference profilesRef = db.collection("userProfiles");

        initializeViews();

        // Display the profile information.
        DocumentReference docRef = profilesRef.document(uniqueID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    displayProfile(document, profileViewModel);
                }
            }
        });

        editProfileButton.setOnClickListener(v->
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_nav_profile_to_nav_edit_profile));

        return root;
    }

    /**
     * Initializes the views for the fragment.
     */
    private void initializeViews() {

        name = binding.profileName;
        phone_number = binding.profilePhoneNumber;
        email = binding.profileEmail;
        editProfileButton = binding.editProfileButton;
        pfp = binding.userPFP;
    }

    /**
     * Displays the user's profile information in the UI.
     * @param document            The Firestore document containing the user's profile data.
     * @param profileViewModel The ViewModel associated with this fragment.
     */
    private void displayProfile(DocumentSnapshot document, ProfileViewModel profileViewModel) {
        //organizer_notifications.setChecked(Boolean.TRUE.equals(document.getBoolean("organizerNotifications")));
        //admin_notifications.setChecked(Boolean.TRUE.equals(document.getBoolean("adminNotifications")));
        // if NULL, it will be blank. Otherwise, display the users info.
        if (document.getString("name") != null) {
            name.setText(Objects.requireNonNull(document.get("name")).toString());
            profileViewModel.getText().observe(getViewLifecycleOwner(), name::setText);
        }
        if (document.getString("email") != null) {
            email.setText(Objects.requireNonNull(document.get("email")).toString());
            profileViewModel.getText().observe(getViewLifecycleOwner(), email::setText);
        }

        if (document.getString("phoneNumber") != null && !document.get("phoneNumber").toString().equals("0")) {
            phone_number.setText(Objects.requireNonNull(document.get("phoneNumber")).toString());
            profileViewModel.getText().observe(getViewLifecycleOwner(), phone_number::setText);
        }
        if (document.getString("pfpURI") != null) {
            String uri = document.getString("pfpURI");
            loadImageFromUrl(uri);
        }

    }

    private void loadImageFromUrl(String url) {
        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                getActivity().runOnUiThread(() -> pfp.setImageBitmap(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ProfileFragment", "Error loading image: " + e.getMessage());
            }
        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}