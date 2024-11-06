package com.example.employ_events.ui.entrants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;

import java.util.ArrayList;
import java.util.List;

//Manages how each entrant's data is displayed in RecyclerView
//Binds each individual entrant to specific list item and updates the UI to show entrants' name

public class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.EntrantViewHolder> {
   // private final Context context;
    private final ArrayList<Entrant> entrantList;
    private final Context context;

    public EntrantsAdapter(Context context, ArrayList<Entrant> entrantList) {
        this.context = context;
        this.entrantList = new ArrayList<>(entrantList); // Initialize with a copy of the list to avoid side effects
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entrant_item, parent, false);
        return new EntrantViewHolder(view);
    }
    public void updateEntrantsList(List<Entrant> entrants) {
        this.entrantList.clear();
        this.entrantList.addAll(entrants);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        Entrant entrant = entrantList.get(position);
        // Check for null values to avoid crashes
        holder.nameTextView.setText(entrant.getName() != null ? entrant.getName() : "N/A");
        holder.emailTextView.setText(entrant.getEmail() != null ? entrant.getEmail() : "N/A");
    }

    @Override
    public int getItemCount() {
        return entrantList.size();
    }

    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView, emailTextView;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.entrant_name);
            emailTextView = itemView.findViewById(R.id.entrant_email);
        }
    }
}
