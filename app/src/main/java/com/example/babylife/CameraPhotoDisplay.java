package com.example.babylife;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CameraPhotoDisplay extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_PHOTO = 2;
    private int currentImageViewId;
    private final Map<Integer, String> imageViewIdToKeyMap = new HashMap<>();

    private ImageView ivMonth1, ivMonth2, ivMonth4, ivMonth8, ivMonth12, ivMonth24;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_photo_display);
        ivMonth1 = findViewById(R.id.ivMonth1);
        ivMonth2 = findViewById(R.id.ivMonth2);
        ivMonth4 = findViewById(R.id.ivMonth4);
        ivMonth8 = findViewById(R.id.ivMonth8);
        ivMonth12 = findViewById(R.id.ivMonth12);
        ivMonth24 = findViewById(R.id.ivMonth24);
        // Populate the HashMap
        imageViewIdToKeyMap.put(R.id.ivMonth1, "ivMonth1");
        imageViewIdToKeyMap.put(R.id.ivMonth2, "ivMonth2");
        imageViewIdToKeyMap.put(R.id.ivMonth4, "ivMonth4");
        imageViewIdToKeyMap.put(R.id.ivMonth8, "ivMonth8");
        imageViewIdToKeyMap.put(R.id.ivMonth12, "ivMonth12");
        imageViewIdToKeyMap.put(R.id.ivMonth24, "ivMonth24");

        // Set onClickListeners
        ivMonth1.setOnClickListener(this::onImageViewClicked);
        ivMonth2.setOnClickListener(this::onImageViewClicked);
        ivMonth4.setOnClickListener(this::onImageViewClicked);
        ivMonth8.setOnClickListener(this::onImageViewClicked);
        ivMonth12.setOnClickListener(this::onImageViewClicked);
        ivMonth24.setOnClickListener(this::onImageViewClicked);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        loadImageView(R.id.ivMonth1, prefs);
        loadImageView(R.id.ivMonth2, prefs);
        loadImageView(R.id.ivMonth4, prefs);
        loadImageView(R.id.ivMonth8, prefs);
        loadImageView(R.id.ivMonth12, prefs);
        loadImageView(R.id.ivMonth24, prefs);
    }

 /*   private String getPreferenceKey(int imageViewId) {
        switch (imageViewId) {
            case R.id.ivMonth1:
                return "ivMonth1";
            case R.id.ivMonth2:
                return "ivMonth2";
            case R.id.ivMonth3:
                return "ivMonth3";
            case R.id.ivMonth4:
                return "ivMonth4";
            default:
                return "defaultKey";
        }
    }
*/
    private void loadImageView(int imageViewId, SharedPreferences prefs){
        String preferenceKey = getPreferenceKey(imageViewId);
        String imageUriString = prefs.getString(preferenceKey, null);
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            ImageView imageView = findViewById(imageViewId);
            imageView.setImageURI(imageUri);
        }
    }
    private void onImageViewClicked(View view) {
        // Store the clicked ImageView's ID for later use
        currentImageViewId = view.getId();

        // Show a dialog or a bottom sheet to ask the user to choose between camera and gallery
        showImagePickerOptions();
    }
    private String getPreferenceKey(int imageViewId) {
        return imageViewIdToKeyMap.getOrDefault(imageViewId, "defaultKey");
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

    private void saveImageToPreferences(Uri imageUri, String key){
        getPreferences(MODE_PRIVATE).edit().putString(key, imageUri.toString()).apply();

    }

    private Uri getImageUri(Context context, Bitmap bitmap){
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri imageUri;
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = saveImageToInternalStorage(imageBitmap);
                ImageView imageView = findViewById(currentImageViewId);
                imageView.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                imageUri = data.getData();
                ImageView imageView = findViewById(currentImageViewId);
                imageView.setImageURI(imageUri);
            } else {
                return;
            }

            // Now using getPreferenceKey to get the correct key for SharedPreferences
            String preferenceKey = getPreferenceKey(currentImageViewId);
            saveImageToPreferences(imageUri, preferenceKey);
        }
    }


    private Uri saveImageToInternalStorage(Bitmap imageBitmap) {

        // Context.MODE_PRIVATE will make sure that this file is accessible by only this app
        String fileName = "image_" + System.currentTimeMillis() + ".png";
        try {
            // Use the compress method on the Bitmap object to write the image to the OutputStream
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            // Writing the bitmap to the output stream
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();

            // Return the saved image Uri
            return Uri.fromFile(new File(getFilesDir(), fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}