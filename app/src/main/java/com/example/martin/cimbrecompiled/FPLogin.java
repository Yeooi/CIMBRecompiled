package com.example.martin.cimbrecompiled;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

@TargetApi(Build.VERSION_CODES.M)
public class FPLogin extends AppCompatActivity{

    private TextView mHeadingLabel;
    private ImageView mFingerprintImage;
    private TextView mParaLabel;
    private Button mButton;

    //private FingerprintManager fingerprintManager2;
    private FingerprintManager fingerprintManager2;
    private KeyguardManager keyguardManager2;

    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fplogin);

        mHeadingLabel = (TextView) findViewById(R.id.FPLoginlbl);
        mFingerprintImage = (ImageView) findViewById(R.id.FPLoginImg);
        mParaLabel = (TextView) findViewById(R.id.FPLoginPara);
        mButton = findViewById(R.id.FPLoginBtn);

        mButton.setVisibility(View.INVISIBLE);
        mButton.setClickable(false);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginSuccessActivity.class);
                startActivity(intent);

            }
        });

        // Check 1: Android version should be greater or equal to Marshmallow
        // Check 2: Device has FingerprintActivity Scanner
        // Check 3: Have permission to use fingerprint scanner in the app
        // Check 4: Lock screen is secured with atleast 1 type of lock
        // Check 5: Atleast 1 FingerprintActivity is registered

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            fingerprintManager2 = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager2 = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if(!fingerprintManager2.isHardwareDetected()){

                mParaLabel.setText("FingerprintActivity Scanner not detected in Device");

            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){

                mParaLabel.setText("Permission not granted to use FingerprintActivity Scanner");

            } else if (!keyguardManager2.isKeyguardSecure()){

                mParaLabel.setText("Add Lock to your Phone in Settings");

            } else if (!fingerprintManager2.hasEnrolledFingerprints()){

                mParaLabel.setText("You should add atleast 1 FingerprintActivity to use this Feature");

            } else {

                mParaLabel.setText("Place your Finger on Scanner to Access the App.");

                generateKey();

                if (cipherInit()){

                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FPLoginHandler fingerprintHandler2 = new FPLoginHandler(this);
                    fingerprintHandler2.startAuth(fingerprintManager2, cryptoObject);
                    Log.d("authen" , "init authen");



                }
            }

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {

        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException e) {

            e.printStackTrace();

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {

            keyStore.load(null);

            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }

    }

}
