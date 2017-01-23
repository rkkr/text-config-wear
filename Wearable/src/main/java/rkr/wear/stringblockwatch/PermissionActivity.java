package rkr.wear.stringblockwatch;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class PermissionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_permission);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Intent intent = new Intent();
                    //intent.setAction("rkr.wear.stringblockwatch.WEATHER_UPDATE");
                    //sendBroadcast(intent);

                    Intent intent = new Intent("text.config.wear.SETTING_CHANGED");
                    sendBroadcast(intent);
                } else {
                    //don't ask again?
                }
                break;
            }
        }
        finish();
    }
}
