package com.example.employ_events.ui.admin;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.employ_events.databinding.FragmentImageBinding;
import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentImageListBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.function.Consumer;

/*
Authors: Aaron

This fragment is used for displaying the list of image and
on click of Admin Remove, remove the image from the list and firebase.
 */

/**
 * AdminImageLstFragment displays a list of image for all image.
 * It fetches images from Firestore in the storage and allows
 * the admin to view events or remove selected image.
 */
public class AdminImageListFragment extends Fragment {
    private FirebaseFirestore db;
    private FragmentImageListBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<Image> arrayList;
    private Button remove;
    private ImageBrowseAdapter adapter;

    /**
     * Called when the fragment's view is created. It initializes views,
     * sets up the RecyclerView, fetches the images, and sets a listener
     * to remove the selected image.
     *
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImageListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = root.findViewById(R.id.recycler);
        remove = root.findViewById(R.id.adminAction);
        db = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();

        // Initialize ImageAdapter
        adapter = new ImageBrowseAdapter(getContext(), arrayList);
        adapter.setOnItemClickListener(new ImageBrowseAdapter.OnItemClickListener() {
            @Override
            public void onClick(Image image, int position) {
                // Handle remove button
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeImage(image, position);
                    }
                });
            }
        });
        recyclerView.setAdapter(adapter);

        // Load images from "banners"
        loadImagesFromFolder("banners", adapter);

        // Load images from "pfps"
        loadImagesFromFolder("pfps", adapter);

        remove = root.findViewById(R.id.adminAction);

        return root;
    }
    /**
     * Fetches the information from Firestore.
     * If the folder is found, it loads the images for that folder into adapter.
     *
     * @param folderName The unique folder name from Firestore (Storage).
     * @param adapter The unique adapter of the ImageBrowseAdapter (Admin).
     */
    private void loadImagesFromFolder(String folderName, ImageBrowseAdapter adapter) {
        FirebaseStorage.getInstance().getReference().child(folderName).listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        listResult.getItems().forEach(new Consumer<StorageReference>() {
                            @Override
                            public void accept(StorageReference storageReference) {
                                Image image = new Image();
                                image.setName(storageReference.getName());
                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String url = "https://" + task.getResult().getEncodedAuthority() + task.getResult().getEncodedPath() + "?alt=media&token=" + task.getResult().getQueryParameters("token").get(0);
                                            image.setUrl(url);
                                            arrayList.add(image);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Log.e("Firebase", "Failed to get download URL");
                                        }
                                    }
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = "Failed to retrieve images from folder: " + folderName;
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

    }
    /**
     * Fetches the information of the image from the selected image.
     * If the image is found, it deletes the images from the adapter and Firestore.
     *
     * @param image The unique image name from list (Admin).
     * @param position The unique position of the list (Admin).
     */
    private void removeImage(Image image, int position) {
        // Get the reference to the Firebase Storage location of the image
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image.getUrl());

        // Delete the image from Firebase Storage
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Image successfully deleted from Firebase
                Toast.makeText(getContext(), "Image deleted from Firebase", Toast.LENGTH_SHORT).show();

                // Remove the image from the RecyclerView and arrayList
                arrayList.remove(position);
                recyclerView.getAdapter().notifyItemRemoved(position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to delete the image
                Toast.makeText(getContext(), "Failed to delete image from Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

