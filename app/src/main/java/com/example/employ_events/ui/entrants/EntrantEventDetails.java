package com.example.employ_events.ui.entrants;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.employ_events.R;


public class EntrantEventDetails extends Fragment {

    TextView name;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.event_details, container, false);


        if (getArguments() != null) {
            String eventID = getArguments().getString("eventData");
            name = view.findViewById(R.id.eventName);
            name.setText(eventID);
        }




        return view;
    }
}