package com.example.employ_events.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.example.employ_events.R;

import java.util.ArrayList;
import java.util.function.Consumer;
public class LoadImage extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image_list);

        FirebaseApp.initializeApp(this);

        RecyclerView recyclerView = findViewById(R.id.recycler);

        FirebaseStorage.getInstance().getReference().child("banners").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                ArrayList<Image> arrayList = new ArrayList<>();
                ImageAdapter adapter = new ImageAdapter(LoadImage.this, arrayList);
                adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Image image) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(image.getUrl()));
                        intent.setDataAndType(Uri.parse(image.getUrl()), "image/*");
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adapter);
                listResult.getItems().forEach(new Consumer<StorageReference>() {
                    @Override
                    public void accept(StorageReference storageReference) {
                        Image image = new Image();
                        image.setName(storageReference.getName());
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
//                                String url = "https://" + task.getResult().getEncodedAuthority() + task.getResult().getEncodedPath() + "?alt=media&token=" + task.getResult().getQueryParameters("token").get(0);
//                                image.setUrl(url);
//                                arrayList.add(image);
//                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoadImage.this, "Failed to retrieve images", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
