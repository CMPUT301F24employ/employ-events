package com.example.employ_events.ui.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.employ_events.R;
import com.example.employ_events.model.Image;

import java.util.ArrayList;
/*
This class is used for displaying the RecyclerView lists of image.
No outstanding issues at the moment.
 */

/**
 * Adapter for managing and displaying a list of image in a RecyclerView
 * @author Aaron
 */
public class ImageBrowseAdapter extends RecyclerView.Adapter<ImageBrowseAdapter.ViewHolder> {
    Context context;
    ArrayList<Image> arrayList;
    OnItemClickListener onItemClickListener;

    /**
     * Constructs an ImageBrowseAdapter with the specified context and list of image.
     * @param context    the context in which the adapter is used
     * @param arrayList the list of image to be displayed
     */
    public ImageBrowseAdapter(Context context, ArrayList<Image> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    /**
     * Inflates the layout for an individual image and returns a new ViewHolder.
     * @param parent   the parent ViewGroup
     * @param viewType the view type of the new View
     * @return a new instance of ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.browse_images, parent, false);
        return new ViewHolder(view);
    }
    /**
     * Binds entrant data to the specified ViewHolder.
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the image in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(arrayList.get(position).getUrl()).into(holder.imageView);
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(arrayList.get(position), position);
            }
        });
    }
    /**
     * Returns the total number of images in the list.
     * @return the size of the image list
     */
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    /**
     * ViewHolder class for holding views of individual image.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageRecyclerView);
        }
    }
    /**
     * Interface to set click image on the RecyclerView items.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    /**
     * Interface to handle click image on the RecyclerView items.
     */
    public interface OnItemClickListener {
        void onClick(Image image, int position);
    }
}
