package com.mygdx.game.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mygdx.game.Gtab3.IHMActivity;
import com.mygdx.game.R;

public class MainActivity extends Activity {
    public static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO=123;
    private int permissionCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        //Toast.makeText(this, ""+permissionCheck, Toast.LENGTH_LONG).show();
        Log.d("PERMIISSION", "Ask la permission "+permissionCheck);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            Log.d("PERMIISSION", "Cest bon la permission");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMIISSION","Jai la permission");
                } else {
                    Log.d("PERMIISSION","Jai pas la permission");
                    Intent intent = new Intent(this, RefusedPermissionActivity.class);
                    startActivity(intent);
                }
                return;

            }
        }
    }

    public void openGtab(View view) {
        Intent intent = new Intent(getApplicationContext(),IHMActivity.class);
        startActivity(intent);
    }

    public void openAccordeur(View view) {
        Intent intent = new Intent(getApplicationContext(),TunerActivity.class);
        startActivity(intent);
    }
}
