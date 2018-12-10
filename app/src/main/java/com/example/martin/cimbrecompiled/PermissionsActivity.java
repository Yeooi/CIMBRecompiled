package com.example.martin.cimbrecompiled;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;


public class PermissionsActivity extends AppCompatActivity {

    boolean found = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        Button agreeButton = findViewById(R.id.agreeButton);



        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                found = false;
                PermissionsHandler.GetPermissions(PermissionsActivity.this);
                if (PermissionsHandler.CheckAllPermissions(PermissionsActivity.this)) {
                    LaunchNextActivity();
                }
            }
        });
    }

    private void LaunchNextActivity() {
        Intent myIntent = new Intent(this, TnCActivity.class);
        this.startActivity(myIntent);
        finish();
    }



}
