package com.example.martin.cimbrecompiled;

import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
    TODO:
    1. Save the picture on click next
    2. Check if picture is set, show a pop up to ask if want to use default picture
    3. Remove unnecessary toast
 */


public class PictureActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;

    private GestureLibrary gLib;
    private boolean mGestureDrawn;
    private Gesture mCurrentGesture;
    private static final String TAG = "Gesture";
    private static final String gestureName = "secret_gesture";
    private static final String gestureFileName = "hazzen";
    private static final String savedImageFileName = "safetyImage";
    private boolean isImageDefault;
    private GestureOverlayView gestures;
    private String strUsername;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strUsername = getIntent().getStringExtra("username");
        setContentView(R.layout.activity_picture);
        isImageDefault = true;
        Button nextButton = findViewById(R.id.buttonNext);
        Button setPictureButton = findViewById(R.id.buttonSetPicture);
        Button resetButton = findViewById(R.id.buttonReset);

        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + gestureFileName);
        gLib.load();

        gestures = (GestureOverlayView) findViewById(R.id.gesture);
        gestures.addOnGestureListener(mGestureListener);

        reset();

        setPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGestureDrawn) {
                    saveGesture();
                    if (isImageDefault) {
                        showDefaultImageAlert();
                    } else {
                        saveBackgroundImage();
                        Intent intent = new Intent(getBaseContext(), ConfirmGestureActivity.class);
                        intent.putExtra("username",strUsername);
                        startActivity(intent);
                    }
                } else {
                    showToast("Draw some gestures on the picture!");
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDrawingBoard();
            }
        });

    }

    private GestureOverlayView.OnGestureListener mGestureListener = new GestureOverlayView.OnGestureListener() {
        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            mGestureDrawn = true;
            Log.d(TAG, "Gesture started");
        }

        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
            mCurrentGesture = overlay.getGesture();
        }

        @Override
        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
            Log.d(TAG, "Gesture ended");
        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            Log.d(TAG, "Gesture cancelled");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    Uri imageURI = data.getData();
                    Bitmap chosenImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
                    findViewById(R.id.gesture).setBackground(new BitmapDrawable(getResources(), chosenImage));
                    isImageDefault = false;
                } catch (Exception e) {

                }
            }
        }
    }

    private void reset() {
        mGestureDrawn = false;
        mCurrentGesture = null;
        gLib.removeEntry(gestureName);
    }

    private void resetDrawingBoard () {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void saveGesture() {
        gLib.addGesture(gestureName, mCurrentGesture);
        if (!gLib.save()) {
            Log.e(TAG, "Error occurred when saving gesture.");
            showToast("Error occurred when saving gesture.");
        } else {
            showToast("Save gesture successful.");
        }
    }

    private void saveBackgroundImage() {
        String path = this.getExternalFilesDir(null).toString();
        OutputStream fOut = null;
        Drawable background = findViewById(R.id.gesture).getBackground();
        Bitmap image = ((BitmapDrawable) background).getBitmap();
        try {
            File file = new File(path, savedImageFileName + ".jpg");
            fOut = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
            showToast("Image saved " + file.getAbsolutePath());
        } catch (Exception e){
            showToast("Failed to save image");
        }
    }

    private void showDefaultImageAlert() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        String warning = String.format("The current security image is set to the default one. Do you want to continue?%n" +
            "You can change the image later inside your profile settings");
        alertBuilder.setMessage(warning);
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton(
            "Yes",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    saveBackgroundImage();
                    Intent intent = new Intent(getBaseContext(), ConfirmGestureActivity.class);
                    intent.putExtra("username",strUsername);
                    startActivity(intent);
                }
            });

        alertBuilder.setNegativeButton(
            "No",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
