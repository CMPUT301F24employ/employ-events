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

import java.util.ArrayList;
import java.util.List;

public class FacilityBrowseAdapter extends RecyclerView.Adapter<FacilityBrowseAdapter.FacilityViewHolder> {

    private final ArrayList<Facility> facilityList;
    private final Context context;
    private final FacilityClickListener listener;

    public FacilityBrowseAdapter(Context context, ArrayList<Facility> facilityList, FacilityClickListener listener) {
        this.context = context;
        this.facilityList = new ArrayList<>(facilityList);
        this.listener = listener;
    }

    /**
     * Interface for handling item click events.
     */
    public interface FacilityClickListener {
        void onItemClick(Facility facility);
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.facility_item, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position);

        holder.facilityCard.setOnClickListener(view -> listener.onItemClick(facility));

        holder.nameTextView.setText(facility.getName() != null ? facility.getName() : "No Name Provided");
        holder.emailTextView.setText(facility.getEmail() != null ? facility.getEmail() : "No Email Provided");
        holder.phoneNumberTextView.setText(facility.getPhone_number() != null ? facility.getPhone_number() : "No Phone Number Provided");
        holder.addressTextView.setText(facility.getAddress() != null ? facility.getAddress() : "No Address Provided");
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    public void updateFacilityList(List<Facility> facilities) {
        this.facilityList.clear();
        this.facilityList.addAll(facilities);
        notifyDataSetChanged();
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {

        CardView facilityCard;
        private final TextView nameTextView;
        private final TextView emailTextView;
        private final TextView addressTextView;
        private final TextView phoneNumberTextView;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityCard = itemView.findViewById(R.id.facility_card);
            nameTextView = itemView.findViewById(R.id.facility_name);
            emailTextView = itemView.findViewById(R.id.facility_email);
            addressTextView = itemView.findViewById(R.id.facility_address);
            phoneNumberTextView = itemView.findViewById(R.id.facility_phone_number);
        }
    }
}
