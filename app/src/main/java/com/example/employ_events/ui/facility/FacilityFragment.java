package com.example.employ_events.ui.facility;

import com.example.employ_events.ui.events.Event;
import com.example.employ_events.ui.events.AddEventFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentFacilityBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FacilityFragment extends Fragment {
    private ListView eventList;
    private FragmentFacilityBinding binding;
    private FirebaseFirestore db;
    private ArrayAdapter<Event> bookAdapter;
    String android_id;
    ArrayList<Event> dataList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FacilityViewModel facilityViewModel =
                new ViewModelProvider(this).get(FacilityViewModel.class);

        binding = FragmentFacilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String uniqueID;
        uniqueID = sharedPreferences.getString("uniqueID", null);
        db = FirebaseFirestore.getInstance();


        // Set the button click listener to navigate to AddEventFragment
        binding.createEventButton.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_facility_to_addEvent)
        );



        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
