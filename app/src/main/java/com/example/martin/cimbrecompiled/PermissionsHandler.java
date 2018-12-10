package com.example.martin.cimbrecompiled;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class PermissionsHandler {

    private static final int MY_PERMISSION_READ_EXTERNAL_LOCATION = 0;
    private static final int MY_PERMISSION_WRITE_EXTERNAL_LOCATION = 1;

    private static String[] PERMISSIONS =
            {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };

    public static void GetPermissions (Activity act) {

        for(String permission:PERMISSIONS)
        {
            int count = 0;
            int permissionCheck = ContextCompat.checkSelfPermission(act, permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(act, PERMISSIONS, count);
            } else {

            }
            count ++;
        }
    }

    public static boolean CheckAllPermissions(Activity act) {

        for(String permission:PERMISSIONS)
        {
            int permissionCheck = ContextCompat.checkSelfPermission(act, permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //@OVERRIDE
    public static void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_READ_EXTERNAL_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case MY_PERMISSION_WRITE_EXTERNAL_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
