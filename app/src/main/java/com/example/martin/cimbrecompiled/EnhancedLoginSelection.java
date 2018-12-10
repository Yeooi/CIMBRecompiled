package com.example.martin.cimbrecompiled;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class EnhancedLoginSelection extends AppCompatActivity {

    private String strUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strUsername = getIntent().getStringExtra("username");
        setContentView(R.layout.activity_enhanced_login_selection);
        Button pictureUnlockBtn = findViewById(R.id.btn_ELPU);
        Button fingerprintBtn = findViewById(R.id.btn_ELFP);




        pictureUnlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),PictureActivity.class);
                intent.putExtra("username",strUsername);
                startActivity(intent);
            }
        });

        fingerprintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),FingerprintActivity.class);
                intent.putExtra("username",strUsername);
                startActivity(intent);
            }
        });

    }


}
