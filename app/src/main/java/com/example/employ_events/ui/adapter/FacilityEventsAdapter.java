package com.example.employ_events.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
The purpose of this adapter is to assist in displaying a list of a facility's events on their
manage events page.
 */

/**
 * Adapter for displaying events in a RecyclerView within a facility.
 */
public class FacilityEventsAdapter extends RecyclerView.Adapter<FacilityEventsAdapter.FEViewHolder> {

    // For storing info
    Context context;
    ArrayList<Event> eventArrayList;
    private FEClickListener listener;

    /**
     * Constructor for the FacilityEventsAdapter.
     *
     * @param context        The context in which the adapter is operating.
     * @param eventArrayList The list of events to display.
     * @param listener       The listener for handling click events.
     */
    public FacilityEventsAdapter(Context context, ArrayList<Event> eventArrayList, FEClickListener listener) {
        this.context = context;
        this.eventArrayList = eventArrayList;
        this.listener = listener;
    }

    /**
     * Interface for handling item click events.
     */
    public interface FEClickListener {
        void onItemClick(Event event);
    }

    /**
     * Creates new views (invoked by the layout manager).
     *
     * @param parent   The view group that this adapter's views will be attached to.
     * @param viewType The type of the new view.
     * @return A new view holder that holds the view for an event.
     */
    @NonNull
    @Override
    public FacilityEventsAdapter.FEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.facility_event, parent, false);
        return new FEViewHolder(view);
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager).
     *
     * @param holder   The view holder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    // Assigning values to views
    public void onBindViewHolder(@NonNull FacilityEventsAdapter.FEViewHolder holder, int position) {
        // The event we want to display
        Event e = eventArrayList.get(position);
        // Looking into the holder, and setting the event card to show the details of the item we want to display
        holder.eventName.setText(e.getEventTitle());
        holder.eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(e);
            }
        });

        if (e.getEventDate() == null) {
            holder.eventDate.setText("No Event Date Found");
        } else {
            // Reformatting Date
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = e.getEventDate();
            holder.eventDate.setText(s.format(date));
        }
    }

    /**
     * Returns the size of the event list.
     *
     * @return The number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    /**
     * ViewHolder class that holds the views for each event item.
     */
    public static class FEViewHolder extends RecyclerView.ViewHolder {
        CardView eventCard;
        TextView eventName, eventDate;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view of the event item.
         */
        public FEViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_card_name);
            eventDate = itemView.findViewById(R.id.event_card_date);
            eventCard = itemView.findViewById(R.id.event_card);
        }
    }
}