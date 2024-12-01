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

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private final ArrayList<Profile> profileList;
    private final Context context;
    private ProfileClickListener listener;

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


    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public void updateProfileList(List<Profile> profiles) {
        this.profileList.clear();
        this.profileList.addAll(profiles);
        notifyDataSetChanged();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        CardView profileCard;
        private final TextView nameTextView;
        private final TextView emailTextView;
        private final TextView phoneNumberTextView;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCard = itemView.findViewById(R.id.profile_card);
            nameTextView = itemView.findViewById(R.id.profile_name);
            emailTextView = itemView.findViewById(R.id.profile_email);
            phoneNumberTextView = itemView.findViewById(R.id.profile_phone_number);
        }
    }
}
