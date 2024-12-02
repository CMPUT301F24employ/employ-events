package com.example.employ_events.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.employ_events.R;
import com.example.employ_events.model.EventItem;

import java.util.ArrayList;

/**
 * @author Tina
 * Adapter for displaying a list of event invitations in a RecyclerView.
 * This adapter binds each event's data to a view and handles click events.
 */
public class InvitationsAdapter extends RecyclerView.Adapter<InvitationsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<EventItem> invitationsList;
    private OnItemClickListener listener;


    /**
     * Constructor for InvitationsAdapter.
     *
     * @param context The context used to access resources and layouts.
     * @param invitationsList The list of EventItem objects to be displayed.
     * @param listener The listener that handles click events for individual items.
     */
    public InvitationsAdapter(Context context, ArrayList<EventItem> invitationsList, OnItemClickListener listener) {
        this.context = context;
        this.invitationsList = invitationsList;
        this.listener = listener;
    }

    /**
     * Creates and returns a ViewHolder for an item in the RecyclerView.
     *
     * @param parent The parent ViewGroup in which the item view will be inserted.
     * @param viewType The type of the view.
     * @return A new ViewHolder that contains the item view.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_invitation, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Binds the data from the event item to the UI elements in the ViewHolder.
     * @param holder The ViewHolder which contains the views.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventItem eventItem = invitationsList.get(position);

        // Bind the data to the UI elements
        holder.eventNameTextView.setText(eventItem.getEventName());

        // Set up the click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(eventItem);  // Pass the EventItem object on click
            }
        });
    }

    /**
     * Returns the total number of items in the list.
     * @return The size of the invitations list.
     */
    @Override
    public int getItemCount() {
        return invitationsList.size();
    }

    /**
     * ViewHolder class that holds references to the views in each item of the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;

        /**
         * Constructor for the ViewHolder.
         * @param itemView The view representing an individual item in the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventName_ITV);  // Make sure to use the correct ID for your TextView
        }
    }

    /**
     * Interface to handle click events on the RecyclerView items.
     */
    public interface OnItemClickListener {
        void onItemClick(EventItem event);
    }
}
