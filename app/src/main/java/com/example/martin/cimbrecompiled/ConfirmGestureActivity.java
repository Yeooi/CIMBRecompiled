package com.example.martin.cimbrecompiled;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class ConfirmGestureActivity extends AppCompatActivity {

    private static final String TAG = "Gesture";
    private static final String gestureName = "secret_gesture";
    private static final String gestureFileName = "hazzen";
    private static final String savedImageFileName = "safetyImage";

    private GestureOverlayView gestures;
    private GestureLibrary gLib;
    private Button backButton;
    private Button doneButton;
    private String strUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strUsername = getIntent().getStringExtra("username");
        setContentView(R.layout.activity_confirm_gesture);

        backButton = findViewById(R.id.buttonBack);
        doneButton = findViewById(R.id.buttonDone);
        gestures = findViewById(R.id.confirm_gesture);

        setGesturePicture();

        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + gestureFileName);
        gLib.load();

        gestures.addOnGesturePerformedListener(handleGestureListener);
        gestures.setGestureStrokeAngleThreshold(90.0f);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmGestureActivity.super.onBackPressed();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
                settings.edit().putString(strUsername,"picture").commit();
                Log.d("tag",settings.getString(strUsername,""));
                startActivity(intent);
            }
        });

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
        doneButton.setTextColor(backButton.getTextColors());
        doneButton.setClickable(true);
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
