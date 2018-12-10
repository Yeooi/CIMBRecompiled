package com.example.martin.cimbrecompiled;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.M)
public class FPLoginHandler extends FingerprintManager.AuthenticationCallback {

    private static boolean authenticated = false;

    private Context context;

    public FPLoginHandler(Context context){

        this.context = context;

    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        this.update("There was an Authentication Error. " + errString, false);


    }

    @Override
    public void onAuthenticationFailed() {

        this.update("Authentication Failed. ", false);

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        this.update("Error: " + helpString, false);

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        this.update("Your fingerprint has been verified.", true);

    }

    private void update(String s, boolean b) {

        TextView paraLabel = (TextView) ((Activity)context).findViewById(R.id.FPLoginPara);
        ImageView imageView = (ImageView) ((Activity)context).findViewById(R.id.FPLoginImg);
       // Button mButton = (Button)  ((Activity)context).findViewById(R.id.btnFP);
        Button mButton2 = (Button)  ((Activity)context).findViewById(R.id.FPLoginBtn);



        if(b == false){

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        } else {

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            mButton2.setVisibility(View.VISIBLE);
            mButton2.setClickable(true);
//            mButton2.setVisibility(View.VISIBLE);
//            mButton2.setClickable(true);
            paraLabel.setText(s);
            //imageView.setImageResource(R.mipmap.action_done);

        }

    }

    protected static boolean isAuthenticated(){
        return authenticated;
    }
}
