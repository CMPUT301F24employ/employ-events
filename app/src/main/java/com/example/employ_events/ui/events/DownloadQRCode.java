package com.example.employ_events.ui.events;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.employ_events.databinding.DownloadQrCodeBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * Fragment to download and display a QR code image from a Firebase Firestore URL.
 */
public class DownloadQRCode extends Fragment {
    private DownloadQrCodeBinding binding;
    private ImageView qrCode;
    private String qrUri;
    private Button downloadButton;
    private FirebaseFirestore db;

    /**
     * Initializes the fragment view, loads the QR code URL from Firestore,
     * and sets up a download button to save the QR code image to storage.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DownloadQrCodeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        qrCode = binding.imageQRCode;
        downloadButton = binding.downloadButton;

        if (getArguments() != null) {
            String eventID = getArguments().getString("EVENT_ID");
            if (eventID != null) {
                DocumentReference eventRef = db.collection("events").document(eventID);
                eventRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            qrUri = document.getString("QRCodeUrl");
                            loadImageFromUrl(qrUri, false);
                        }
                    }
                });
            }
        }

        downloadButton.setOnClickListener(view -> {
            loadImageFromUrl(qrUri, true);
        });

        return root;
    }

    /**
     * Loads an image from a URL in the background and sets it in the ImageView.
     * Calls {@link #saveImageToStorage(Bitmap)} to save the image once it is loaded.
     *
     * @param url The URL of the image to load.
     */
    private void loadImageFromUrl(String url, boolean save) {
        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                requireActivity().runOnUiThread(() -> {
                    qrCode.setImageBitmap(bitmap);
                    if (save) saveImageToStorage(bitmap); // Pass the Bitmap directly
                });
            } catch (IOException e) {
                Log.e("DownloadQRCode", "Error loading image: " + e.getMessage());
            }
        }).start();
    }


    /**
     * Saves a Bitmap image to external storage in the Pictures directory.
     *
     * @param bitmap The Bitmap image to save.
     */
    private void saveImageToStorage(Bitmap bitmap) {
        ContentResolver resolver = requireContext().getContentResolver();
        String imageFileName = "downloaded_image.png";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, Objects.requireNonNull(outputStream));
                Toast.makeText(getContext(), "Image downloaded successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Uri null!", Toast.LENGTH_SHORT).show();
        }
    }
}
