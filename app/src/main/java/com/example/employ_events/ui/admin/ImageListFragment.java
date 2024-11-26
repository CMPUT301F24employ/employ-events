package com.example.employ_events.ui.admin;

import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.employ_events.databinding.FragmentImageBinding;
import com.example.employ_events.R;
import com.example.employ_events.databinding.FragmentImageListBinding;
import com.example.employ_events.ui.facility.FacilityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.function.Consumer;


public class ImageListFragment extends Fragment {
    private FirebaseFirestore db;
    private FragmentImageListBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<Image> arrayList;
    private Button remove;
    private ImageAdapter adapter;
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
        adapter = new ImageAdapter(getContext(), arrayList);
        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
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

        // Load images from "posters" or another folder
        loadImagesFromFolder("pfps", adapter);

        remove = root.findViewById(R.id.adminAction);

        return root;
    }

    private void loadImagesFromFolder(String folderName, ImageAdapter adapter) {
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

