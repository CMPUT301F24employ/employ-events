package com.example.employ_events.ui.facility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.ui.events.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FacilityEventsAdapter extends RecyclerView.Adapter<FacilityEventsAdapter.FEViewHolder> {

    // For storing info
    Context context;
    ArrayList<Event> eventArrayList;
    private FEClickListener listener;

    // Constructor
    public FacilityEventsAdapter(Context context, ArrayList<Event> eventArrayList, FEClickListener listener) {
        this.context = context;
        this.eventArrayList = eventArrayList;
        this.listener = listener;
    }

    public interface FEClickListener {
        void onItemClick(Event event);
    }

    @NonNull
    @Override
    // Creating and inflating the layout
    public FacilityEventsAdapter.FEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.facility_event, parent, false);
        return new FEViewHolder(view);
    }

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

    @Override
    // Number of items to display
    public int getItemCount() {
        return eventArrayList.size();
    }

    // Manages the views to be used in the class
    public static class FEViewHolder extends RecyclerView.ViewHolder {
        CardView eventCard;
        TextView eventName, eventDate;

        public FEViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_card_name);
            eventDate = itemView.findViewById(R.id.event_card_date);
            eventCard = itemView.findViewById(R.id.event_card);
        }
    }
}