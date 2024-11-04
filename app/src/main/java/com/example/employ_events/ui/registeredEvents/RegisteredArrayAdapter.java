package com.example.employ_events.ui.registeredEvents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.ui.events.Event;

import java.util.ArrayList;

public class RegisteredArrayAdapter extends RecyclerView.Adapter<RegisteredArrayAdapter.ViewHolder> {
    private ArrayList<Event> registeredEvents;
    private Context context;

    // Constructor for initialization
    public RegisteredArrayAdapter(Context context, ArrayList<Event> registeredEvents) {
        this.registeredEvents = registeredEvents;
        this.context = context;
    }

    @NonNull
    @Override
    public RegisteredArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_registered_events, parent, false);

        // Passing view to ViewHolder
        //RegisteredArrayAdapter.ViewHolder viewHolder = new RegisteredArrayAdapter(view);
        //return viewHolder;
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RegisteredArrayAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return registeredEvents.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.eventName);
        }
    }
}

