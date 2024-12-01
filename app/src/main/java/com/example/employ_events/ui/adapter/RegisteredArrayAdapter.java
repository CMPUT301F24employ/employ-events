package com.example.employ_events.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.model.Event;

import java.util.ArrayList;

/*
Yet to be completed. We hope to use this in displaying the list of registered events.
 */

/**
 * RecyclerView.Adapter implementation to display a list of registered events.
 * This adapter binds event data to views that are displayed within a RecyclerView.
 */
public class RegisteredArrayAdapter extends RecyclerView.Adapter<RegisteredArrayAdapter.ViewHolder> {
    private final ArrayList<Event> registeredEvents;
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    /**
     * Interface for handling item click events.
     */
    public interface OnItemClickListener {
        void onItemClick(String eventId);
    }

    /**
     * Constructs a new RegisteredArrayAdapter.
     *
     * @param context          the context in which the adapter is used
     * @param registeredEvents the list of events to display
     * @param onItemClickListener the listener for item click events
     */
    public RegisteredArrayAdapter(Context context, ArrayList<Event> registeredEvents, OnItemClickListener onItemClickListener) {
        this.registeredEvents = registeredEvents != null ? registeredEvents : new ArrayList<>();
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Creates and inflates a new ViewHolder to represent an event item.
     *
     * @param parent   the ViewGroup in which the view will be added
     * @param viewType the type of the view (not used here as all items are the same type)
     * @return a new ViewHolder that holds a view for each event item
     */
    @NonNull
    @Override
    public RegisteredArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invitation, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data from the specified event to the ViewHolder at the given position.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the event in the list
     */
    @Override
    public void onBindViewHolder(@NonNull RegisteredArrayAdapter.ViewHolder holder, int position) {
        Event event = registeredEvents.get(position);
        holder.name.setText(event.getEventTitle());

        // Handle item clicks
        holder.itemView.setOnClickListener(v -> {
            onItemClickListener.onItemClick(event.getId()); // Pass the event ID to the listener
        });
    }

    /**
     * Returns the total number of events in the list.
     * @return the size of the registered events list
     */
    @Override
    public int getItemCount() {
        return registeredEvents.size();
    }

    /**
     * ViewHolder class to hold and initialize views for each event item in the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        /**
         * Constructs a ViewHolder and initializes the event name TextView.
         * @param view the view representing a single event item
         */
        public ViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.eventName_ITV);
        }
    }
}

