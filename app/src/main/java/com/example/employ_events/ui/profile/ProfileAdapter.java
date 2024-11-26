package com.example.employ_events.ui.profile;

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

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private final ArrayList<Profile> profileList;
    private final Context context;

    public ProfileAdapter(Context context, ArrayList<Profile> profileList) {
        this.context = context;
        this.profileList = new ArrayList<>(profileList);

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
        holder.nameTextView.setText(profile.getName() != null ? profile.getName() : "N/A");
        holder.emailTextView.setText(profile.getEmail() != null ? profile.getEmail() : "N/A");
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

        private final TextView nameTextView;
        private final TextView emailTextView;
        private final TextView phoneNumberTextView;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.profile_name);
            emailTextView = itemView.findViewById(R.id.profile_email);
            phoneNumberTextView = itemView.findViewById(R.id.profile_phone_number);
        }
    }
}
