package com.example.babylife;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

public class CameraPhotoDisplay extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_PHOTO = 2;
    private int currentImageViewId;
    ImageView ivMonth1, ivMonth2, ivMonth3, ivMonth4;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_photo_display);
        ivMonth1 = findViewById(R.id.ivMonth1);
        ivMonth2 = findViewById(R.id.ivMonth2);
        ivMonth3 = findViewById(R.id.ivMonth3);
        ivMonth4 = findViewById(R.id.ivMonth4);

        ivMonth1.setOnClickListener(view -> onImageViewClicked(view));
        ivMonth2.setOnClickListener(view -> onImageViewClicked(view));
        ivMonth3.setOnClickListener(view -> onImageViewClicked(view));
        ivMonth4.setOnClickListener(view -> onImageViewClicked(view));
    }

    private void onImageViewClicked(View view) {
        // Store the clicked ImageView's ID for later use
        currentImageViewId = view.getId();

        // Show a dialog or a bottom sheet to ask the user to choose between camera and gallery
        showImagePickerOptions();
    }

    private void showImagePickerOptions() {
        CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, (dialog, which) -> {
            if (items[which].equals("Take Photo")) {
                dispatchTakePictureIntent();
            } else if (items[which].equals("Choose from Gallery")) {
                dispatchGalleryIntent();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchGalleryIntent() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_GALLERY_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView imageView = findViewById(currentImageViewId);
                imageView.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                ImageView imageView = findViewById(currentImageViewId);
                imageView.setImageURI(selectedImage);
            }
        }
    }
}