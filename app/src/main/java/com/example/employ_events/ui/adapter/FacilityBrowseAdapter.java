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
import com.example.employ_events.model.Facility;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jasleen
 * Adapter for displaying a list of facilities in a RecyclerView.
 * This adapter binds each facility's data to a view and handles click facilities.
 */
public class FacilityBrowseAdapter extends RecyclerView.Adapter<FacilityBrowseAdapter.FacilityViewHolder> {

    private final ArrayList<Facility> facilityList;
    private final Context context;
    private final FacilityClickListener listener;

    /**
     * @author Jasleen
     * Constructor for initializing the adapter with context, facility list, and click listener.
     *
     * @param context      the context in which the adapter is used
     * @param facilityList the initial list of facilities
     * @param listener     the click listener for handling facility item clicks
     */
    public FacilityBrowseAdapter(Context context, ArrayList<Facility> facilityList, FacilityClickListener listener) {
        this.context = context;
        this.facilityList = new ArrayList<>(facilityList);
        this.listener = listener;
    }

    /**
     * @author Jasleen
     * Interface for handling item click events.
     */
    public interface FacilityClickListener {
        void onItemClick(Facility facility);
    }

    /**
     * @author Jasleen
     * Called when a new ViewHolder is created.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the type of view to be created
     * @return a new FacilityViewHolder instance
     */
    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.facility_item, parent, false);
        return new FacilityViewHolder(view);
    }

    /**
     * @author Jasleen
     * Binds data to a ViewHolder at a specific position.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position);

        holder.facilityCard.setOnClickListener(view -> listener.onItemClick(facility));

        holder.nameTextView.setText(facility.getName());
        holder.emailTextView.setText(facility.getEmail());
        holder.addressTextView.setText(facility.getAddress());
    }

    /**
     * @author Jasleen
     * Returns the total number of items in the facility list.
     *
     * @return the number of items
     */
    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    /**
     * @author Jasleen
     * Updates the current facility list with new data and refreshes the RecyclerView.
     *
     * @param facilities the new list of facilities to display
     */
    public void updateFacilityList(List<Facility> facilities) {
        this.facilityList.clear();
        this.facilityList.addAll(facilities);
        notifyDataSetChanged();
    }

    /**
     * @author Jasleen
     * ViewHolder class for managing views within a facility item.
     */
    public static class FacilityViewHolder extends RecyclerView.ViewHolder {

        CardView facilityCard;
        private final TextView nameTextView;
        private final TextView emailTextView;
        private final TextView addressTextView;

        /**
         * @author Jasleen
         * Constructor for initializing the views of a facility item.
         *
         * @param itemView the root view of the facility item layout
         */
        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityCard = itemView.findViewById(R.id.facility_card);
            nameTextView = itemView.findViewById(R.id.facility_name);
            emailTextView = itemView.findViewById(R.id.facility_email);
            addressTextView = itemView.findViewById(R.id.facility_address);
        }
    }
}
