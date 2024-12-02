package com.example.employ_events.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;
import com.example.employ_events.model.Entrant;

import java.util.ArrayList;
import java.util.List;

/*
This class is used for displaying the RecyclerView lists of entrants.
No outstanding issues at the moment.
 */

/**
 * Adapter for managing and displaying a list of entrants in a RecyclerView
 * @author Aasvi
 * @author Sahara
 * @author Tina
 */
public class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.EntrantViewHolder> {
    private final ArrayList<Entrant> entrantList;
    private final Context context;
    private OnItemClickListener listener;


    /**
     * Constructs an EntrantsAdapter with the specified context and list of entrants.
     * @param context    the context in which the adapter is used
     * @param entrantList the list of entrants to be displayed
     */
    public EntrantsAdapter(Context context, ArrayList<Entrant> entrantList, OnItemClickListener listener) {
        this.context = context;
        this.entrantList = new ArrayList<>(entrantList);
        this.listener = listener;
    }

    /**
     * Inflates the layout for an individual entrant item and returns a new ViewHolder.
     * @param parent   the parent ViewGroup
     * @param viewType the view type of the new View
     * @return a new instance of EntrantViewHolder
     */
    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entrant_item, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Updates the current list of entrants and refreshes the RecyclerView.
     * @param entrants the new list of entrants
     */
    public void updateEntrantsList(List<Entrant> entrants) {
        this.entrantList.clear();
        this.entrantList.addAll(entrants);
        notifyDataSetChanged();
    }

    /**
     * Binds entrant data to the specified ViewHolder.
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the entrant in the list
     */
    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        Entrant entrant = entrantList.get(position);
        holder.nameTextView.setText(entrant.getName() != null ? entrant.getName() : "N/A");
        holder.emailTextView.setText(entrant.getEmail() != null ? entrant.getEmail() : "N/A");

        // Set up the click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(entrant.getUniqueID());
            }
        });
    }

    /**
     * Returns the total number of entrants in the list.
     * @return the size of the entrant list
     */
    @Override
    public int getItemCount() {
        return entrantList.size();
    }

    /**
     * ViewHolder class for holding views of individual entrant items.
     */
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView, emailTextView;

        /**
         * Constructs an EntrantViewHolder and initializes its views.
         * @param itemView the view of the entrant item
         */
        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.entrant_name);
            emailTextView = itemView.findViewById(R.id.entrant_email);
        }
    }

    /**
     * Interface to handle click entrant on the RecyclerView items.
     */
    public interface OnItemClickListener {
        void onItemClick(String uniqueID);
    }


}
