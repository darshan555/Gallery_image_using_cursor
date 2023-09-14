package com.example.gallery_image_using_cursor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 1;
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager); // 3 columns, adjust as needed
        imageAdapter = new ImageAdapter(this);
        recyclerView.setAdapter(imageAdapter);
            // Check if permission is granted
            requestRuntimePermission();
    }

    private void requestRuntimePermission() {
        if (StoragePermissionUtil.isAllStoragePermissionsGranted(this)) {
            // Permissions are granted, you can proceed with your storage-related code here.
            loadImagesFromGallery();
        } else {
            // Permissions are not granted, request them.
            StoragePermissionUtil.requestAllStoragePermissions(this, STORAGE_PERMISSION_CODE);
        }
    }

    private void loadImagesFromGallery() {
        // Query MediaStore for images
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Extract image data and add it to the adapter
                String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                imageAdapter.addImage(imagePath);
            }
            cursor.close();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with your storage-related code.
                loadImagesFromGallery();
            } else {
                // Permission denied, handle the denial gracefully, possibly show a message to the user.
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
