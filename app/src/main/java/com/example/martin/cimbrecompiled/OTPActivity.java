package com.example.martin.cimbrecompiled;

import android.content.Intent;
import android.icu.util.MeasureUnit;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import java.util.concurrent.TimeUnit;
import android.util.Log;

public class OTPActivity extends AppCompatActivity implements OnCompleteListener {
    boolean found = false;
    boolean mVerificationInProgress = false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private String mVerificationId;
    private Button verifyButton;
    private TextView resendOTP;
    private TextView phoneNoText;
    private EditText OTPField;
    private EditText phoneNumber;
    private Button sendButton;

    FirebaseAuth mAuth;

    private String strUsername;

    private String phone = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        strUsername = getIntent().getStringExtra("username");
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("OTP", "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("OTP", "onVerificationFailed", e);
                Toast.makeText(OTPActivity.this, "OTP Failed.",Toast.LENGTH_SHORT).show();
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]

                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Toast.makeText(OTPActivity.this, "Quota Exceeded.",Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("OTP", "onCodeSent:" + verificationId);
                Toast.makeText(OTPActivity.this, "OTP Sent.",Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
               // updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]

        setContentView(R.layout.activity_otp);
        mAuth = FirebaseAuth.getInstance();

        verifyButton = findViewById(R.id.verifyButton);
        phoneNoText = findViewById(R.id.phoneNumber);
        OTPField = findViewById(R.id.password);
        phoneNumber = findViewById(R.id.phoneNumberEnter);

        Button skip = findViewById(R.id.skip);
        sendButton = findViewById(R.id.Send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mVerificationInProgress) {
                    try {
                        phone = phoneNumber.getText().toString();
                        startPhoneNumberVerification(phone);
                        phoneNoText.setText(phone);
                    } catch (Exception e) {

                    }
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                found = false;
                LaunchNextAct();
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                found = false;
                String code = OTPField.getText().toString();
                Verify(mVerificationId,code);
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void Verify(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("OTP", "signInWithCredential:success");
                            OTPField.setError("Success code.");
                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                            LaunchNextAct();
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("OTP", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                OTPField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]

                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void LaunchNextAct() {
        Intent myIntent = new Intent(this, EnhancedLoginSelection.class);
        Log.d("Changing Activity","EnhancedLoginSelection Activity");
        myIntent.putExtra("username",strUsername);
        this.startActivity(myIntent);
        finish();
    }

    public void onComplete(Task t){

    }


}

