package com.example.employ_events.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.employ_events.databinding.FragmentHomeBinding;
/*
Yet to be worked on. We hope to display local events on this screen.
 */

/**
 * Fragment representing the home screen.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    /**
     * Inflates the fragment layout and sets up event listeners for UI components.
     *
     * @param inflater           the LayoutInflater object to inflate views
     * @param container          the parent view that contains the fragment's UI
     * @param savedInstanceState the saved instance state for restoring fragment state
     * @return the root view of the fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    /**
     * Cleans up the binding when the fragment's view is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}