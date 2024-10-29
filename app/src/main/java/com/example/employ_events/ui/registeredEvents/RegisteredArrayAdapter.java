package com.example.employ_events.ui.registeredEvents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.employ_events.R;
import com.example.employ_events.ui.events.Event;

import java.util.ArrayList;

public class RegisteredArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> registeredEvents;
    private Context context;

    public RegisteredArrayAdapter(Context context, ArrayList<Event> registeredEvents) {
        super(context, 0, registeredEvents);
        this.registeredEvents = registeredEvents;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_details, parent,false);
        }

        Event event = registeredEvents.get(position);

        TextView eventName = view.findViewById(R.id.eventName);
        eventName.setText(event.getEventTitle());

        return view;
    }
}

