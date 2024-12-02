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
import com.example.employ_events.model.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for managing a list of profiles in a RecyclerView.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private final ArrayList<Profile> profileList;
    private final Context context;
    private ProfileClickListener listener;

    /**
     * Constructor for initializing the adapter with context, profile list, and click listener.
     * @param context     the context in which the adapter is used
     * @param profileList the initial list of profiles
     * @param listener    the click listener for handling profile item clicks
     */
    public ProfileAdapter(Context context, ArrayList<Profile> profileList, ProfileClickListener listener) {
        this.context = context;
        this.profileList = new ArrayList<>(profileList);
        this.listener = listener;
    }

    /**
     * Interface for handling item click events.
     */
    public interface ProfileClickListener {
        void onItemClick(Profile profile);
    }

    /**
     * Called when a new ViewHolder is created.
     * @param parent   the parent ViewGroup
     * @param viewType the type of view to be created
     * @return a new ProfileViewHolder instance
     */
    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(view);
    }

    /**
     * Binds data to a ViewHolder at a specific position.
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {

        Profile profile = profileList.get(position);
        holder.profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(profile);
            }
        });
        holder.nameTextView.setText(profile.getName() == null ? profile.getUniqueID() : profile.getName());
        holder.emailTextView.setText(profile.getEmail() != null ? profile.getEmail() : "No Email provided");
        holder.phoneNumberTextView.setText(profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "No phone number provided");
    }

    /**
     * Returns the total number of items in the profile list.
     * @return the number of items
     */
    @Override
    public int getItemCount() {
        return profileList.size();
    }

    /**
     * Updates the current profile list with new data and refreshes the RecyclerView.
     * @param profiles the new list of profiles to display
     */
    public void updateProfileList(List<Profile> profiles) {
        this.profileList.clear();
        this.profileList.addAll(profiles);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for managing views within a profile item.
     */
    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        CardView profileCard;
        private final TextView nameTextView;
        private final TextView emailTextView;
        private final TextView phoneNumberTextView;

        /**
         * Constructor for initializing the views of a profile item.
         * @param itemView the root view of the profile item layout
         */
        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCard = itemView.findViewById(R.id.profile_card);
            nameTextView = itemView.findViewById(R.id.profile_name);
            emailTextView = itemView.findViewById(R.id.profile_email);
            phoneNumberTextView = itemView.findViewById(R.id.profile_phone_number);
        }
    }
}
