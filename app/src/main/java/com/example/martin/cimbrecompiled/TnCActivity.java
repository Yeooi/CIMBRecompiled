package com.example.martin.cimbrecompiled;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

public class TnCActivity extends AppCompatActivity {

    boolean found = false;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnc);
        TextView tv = findViewById(R.id.TnCText);
        tv.setMovementMethod(new ScrollingMovementMethod());
        Button agreeButton = findViewById(R.id.agreeButton);

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                found = false;
                LaunchNextActivity();
            }
        });
    }

    private void LaunchNextActivity() {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("FirstTime","True");
        Log.d("Changing Activity","Main Activity");
        this.startActivity(myIntent);
        // record the fact that the app has been started at least once
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        settings.edit().putBoolean("my_first_time", false).commit();
        finish();
    }
}
