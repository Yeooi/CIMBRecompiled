package com.example.martin.cimbrecompiled;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SuccessfulSetup extends AppCompatActivity {
    private String strUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strUsername = getIntent().getStringExtra("username");
        Log.d("tag",strUsername);
        setContentView(R.layout.activity_successful_setup);
        Button successButton = findViewById(R.id.btn_success);

        successButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);

                SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
                settings.edit().putString(strUsername,"fingerprint").commit();
                Log.d("tag",settings.getString(strUsername,""));
                finish();
            }
        });

    }


}
