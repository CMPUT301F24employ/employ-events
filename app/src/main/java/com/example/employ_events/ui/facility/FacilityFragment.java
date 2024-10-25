package com.example.employ_events.ui.facility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentFacilityBinding;

public class FacilityFragment extends Fragment {

    private FragmentFacilityBinding binding;
    private Button a;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FacilityViewModel facilityViewModel =
                new ViewModelProvider(this).get(FacilityViewModel.class);


        binding = FragmentFacilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        a= binding.editFacilityButton;
        //set the edit button to naviagte to editFacility
        a.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.edit_facility)
        );
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
