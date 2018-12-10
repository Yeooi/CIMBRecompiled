package com.example.martin.cimbrecompiled;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.view.Window;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.media.MediaScannerConnection;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;



public class MainActivity extends AppCompatActivity implements OnCompleteListener {
    boolean found = false;
    private FirebaseAuth mAuth;

    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        mAuth = FirebaseAuth.getInstance();

        //get current user and update UI according to User e.g. name
        //FireBaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        settings = getSharedPreferences("MyPrefsFile", 0);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task
            LaunchGetPermissionActivity();
        }

        Button loginButton = findViewById(R.id.loginButton);

        //login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                found = false;
                SignIn();
            }
        });


    }

    public void onComplete(Task t){

    }

    private void SignIn() {
        final EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        final String strUsername = username.getText().toString();
        String strPassword = password.getText().toString();

        mAuth.signInWithEmailAndPassword(strUsername, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LogIn", "signInWithEmail:success");
                            Toast.makeText(MainActivity.this, "Log In Success.",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            String path = settings.getString(strUsername,"");
                            if(path.equalsIgnoreCase("fingerprint")) {
                                Intent intent = new Intent(getBaseContext(), FPLogin.class);
                                Log.d("tag","FPLogin");
                                startActivity(intent);
                                finish();
                            }
                            else if(path.equalsIgnoreCase("picture")) {
                                Intent intent = new Intent(getBaseContext(), PictureLoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                                LaunchFirstTimeSignIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LogIn", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Log In failed.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    //launch next activity
    private void LaunchFirstTimeSignIn() {
        Intent myIntent = new Intent(this, OTPActivity.class);
        final EditText username = findViewById(R.id.username);
        final String strUsername = username.getText().toString();
        myIntent.putExtra("username",strUsername);
        Log.d("Changing Activity","OTP Activity");
        this.startActivity(myIntent);
        finish();
    }

    private void LaunchSignInAuth() {

    }

    private void LaunchGetPermissionActivity() {
        Intent myIntent = new Intent(this, PermissionsActivity.class);
        Log.d("Changing Activity","PermissionsActivity Activity");
        this.startActivity(myIntent);
        finish();
    }

    private void WriteFile(boolean rewrite) {
        try {
            // Creates a file in the primary external storage space of the
            // current application.
            // If the file does not exists, it is created.
            File testFile = new File(this.getExternalFilesDir(null), "TestFile.txt");
            if (!testFile.exists()) {
                testFile.createNewFile();
                Log.d("WriteFile","New File Created");
            }

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, false /*append*/));
            writer.write("false");
            writer.close();

            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile(this,
                    new String[]{testFile.toString()},
                    null,
                    null);
        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write to the TestFile.txt file.");
        }
    }

    private String ReadFile() {
        String textFromFile = "";
        // Gets the file from the primary external storage space of the
        // current application.
        File testFile = new File(this.getExternalFilesDir(null), "TestFile.txt");
        if (testFile != null) {
            StringBuilder stringBuilder = new StringBuilder();
            // Reads the data from the file
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    textFromFile += line.toString();
                    textFromFile += "\n";
                }
                reader.close();
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TestFile.txt file.");
            }
        }

        return textFromFile;
    }

}
