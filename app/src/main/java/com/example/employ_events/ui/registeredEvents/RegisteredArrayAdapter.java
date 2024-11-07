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

/**
 * RecyclerView.Adapter implementation to display a list of registered events.
 * This adapter binds event data to views that are displayed within a RecyclerView.
 */
public class RegisteredArrayAdapter extends RecyclerView.Adapter<RegisteredArrayAdapter.ViewHolder> {
    private ArrayList<Event> registeredEvents;
    private Context context;

    /**
     * Constructs a new RegisteredArrayAdapter with the specified context and list of registered events.
     *
     * @param context          the context in which the adapter is used
     * @param registeredEvents the list of events to display
     */
    public RegisteredArrayAdapter(Context context, ArrayList<Event> registeredEvents) {
        this.registeredEvents = registeredEvents;
        this.context = context;
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_registered_events, parent, false);
        return new ViewHolder(view);
        //return null;
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
        holder.itemView.setOnClickListener(v -> {
        });

    }

    /**
     * Returns the total number of events in the list.
     *
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
         *
         * @param view the view representing a single event item
         */
        public ViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.eventName);
        }
    }
}

