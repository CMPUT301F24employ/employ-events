package com.example.employ_events.ui.admin;


import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentAdminProfileListBinding;
import com.example.employ_events.ui.profile.Profile;
import com.example.employ_events.ui.profile.ProfileAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


/**
 * @author Jasleen
 * AdminBrowseProfilesFragment allows admins to browse all the profiles in firebase. They are also able to click on
 * the profile to then delete it which resultingly deletes the facility and events associated with it.
 * Cannot delete admin profiles.
 * US 03.05.01 As an administrator, I want to be able to browse profiles.
 * US 03.02.01 As an administrator, I want to be able to remove profiles.
 * @see com.example.employ_events.ui.profile.ProfileFragment
 * Delete profile logic in ProfileFragment
 */
public class AdminBrowseProfilesFragment extends Fragment implements ProfileAdapter.ProfileClickListener{

    private FragmentAdminProfileListBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Profile> profileList;
    private ProfileAdapter profileAdapter;

    /**
     * Sets up the recycler view and queries & adds the profiles to the view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminProfileListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        profileList = new ArrayList<>();
        profileAdapter = new ProfileAdapter(requireContext(), profileList, this);

        setupRecyclerView();
        setAdminStatusTrue();
        loadProfiles();

        return root;
    }

    /**
     * Sets up the RecyclerView for displaying the profiles.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.allProfilesRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(profileAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    /**
     * Loads profiles from Firestore and updates the RecyclerView.
     */
    private void loadProfiles() {
        db.collection("userProfiles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        profileList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String uniqueID = document.getId();
                            Profile profile = new Profile(uniqueID);
                            profile.setName(document.getString("name"));
                            profile.setEmail(document.getString("email"));
                            profile.setPhoneNumber(document.getString("phoneNumber"));

                            profileList.add(profile);
                        }
                        profileAdapter.updateProfileList(profileList);
                    } else {
                        Toast.makeText(getContext(), "Error loading profiles!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Callback method for when a profile item in the RecyclerView is clicked.
     * This method navigates to a detail screen for the selected profile.
     *
     * @param profile The Profile object that was clicked.
     */
    @Override
    public void onItemClick(Profile profile) {
        if (getView() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("uniqueID", profile.getUniqueID());
            bundle.putBoolean("IS_ADMIN", true);
            Navigation.findNavController(getView()).navigate(R.id.action_adminBrowseProfilesFragment_to_profileFragment, bundle);
        }

    }

    /**
     * Sets the 'admin' field in Firebase to true for the current user.
     * This restricts admins from deleting their own profile.
     */
    private void setAdminStatusTrue() {
        String uniqueID = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("uniqueID", null);

        if (uniqueID != null) {
            db.collection("userProfiles")
                    .document(uniqueID)
                    .update("admin", true);
        }
    }
}