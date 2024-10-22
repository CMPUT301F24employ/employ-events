package com.example.employ_events.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employ_events.R;

import java.util.List;

//Manages how each entrant's data is displayed in RecyclerView
//Binds each individual entrant to specific list item and updates the UI to show entrants' name

public class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.EntrantViewHolder> {
    private List<Entrant> entrantsList;

    public EntrantsAdapter(List<Entrant> entrantsList) {
        this.entrantsList = entrantsList;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrant_item, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        Entrant entrant = entrantsList.get(position);
        holder.nameTextView.setText(entrant.getName());
    }

    @Override
    public int getItemCount() {
        return entrantsList.size();
    }

    public void updateEntrants(List<Entrant> newEntrants) {
        entrantsList.clear();
        entrantsList.addAll(newEntrants);
        notifyDataSetChanged();
    }

    static class EntrantViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}
