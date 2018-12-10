package com.example.martin.cimbrecompiled;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/*
    TODO:
    1. onCreate search for saved picture
 */

public class PictureLoginActivity extends AppCompatActivity {

    private static final String TAG = "Gesture";
    private static final String gestureName = "secret_gesture";
    private static final String gestureFileName = "hazzen";
    private static final String savedImageFileName = "safetyImage";

    private GestureOverlayView gestures;
    private GestureLibrary gLib;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picture_login);
        gestures = findViewById(R.id.login_gesture);

        setGesturePicture();

        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + gestureFileName);
        gLib.load();

        gestures.addOnGesturePerformedListener(handleGestureListener);
        gestures.setGestureStrokeAngleThreshold(90.0f);
    }

    private GestureOverlayView.OnGesturePerformedListener handleGestureListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            ArrayList<Prediction> predictions = gLib.recognize(gesture);
            if (predictions.size() > 0) {
                Prediction prediction = predictions.get(0);
                if (prediction.score > 5.0) {
                    showToast("Correct gesture!");
                    enableDoneButton();
                } else {
                    showToast("Wrong gesture!");
                }
            }
        }
    };

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void enableDoneButton() {
        Intent intent = new Intent(getBaseContext(), LoginSuccessActivity.class);
        startActivity(intent);
    }

    private void setGesturePicture() {
        File imgFile = new File(getExternalFilesDir(null) + "/" + savedImageFileName + ".jpg");
        if (!imgFile.exists()) {
            return;
        } else {
            // load the image into gesture background
            Bitmap image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            gestures.setBackground(new BitmapDrawable(getResources(), image));
        }
    }
}
