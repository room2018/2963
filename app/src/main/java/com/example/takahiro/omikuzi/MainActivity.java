package com.example.takahiro.omikuzi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements LocationListener{

    LocationManager locationManager;

    private Button button1, button2, button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setScreenMain();


    }

    private void locationStart(){
        Log.d("debug","locationStart()");

        // LocationManager 繧､繝ｳ繧ｹ繧ｿ繝ｳ繧ｹ逕滓��
        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled");
        } else {
            // GPS繧定ｨｭ螳壹☆繧九ｈ縺�縺ｫ菫�縺�
            Intent settingsIntent =
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 50, this);

    }

    // 邨先棡縺ｮ蜿励￠蜿悶ｊ
    /**
     * Android Quickstart:
     * https://developers.google.com/sheets/api/quickstart/android
     *
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[]permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            // 菴ｿ逕ｨ縺瑚ｨｱ蜿ｯ縺輔ｌ縺�
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");

                locationStart();

            } else {
                // 縺昴ｌ縺ｧ繧よ拠蜷ｦ縺輔ｌ縺滓凾縺ｮ蟇ｾ蠢�
                Toast toast = Toast.makeText(this,
                        "縺薙ｌ莉･荳翫↑縺ｫ繧ゅ〒縺阪∪縺帙ｓ", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // 邱ｯ蠎ｦ縺ｮ陦ｨ遉ｺ
        TextView textView1 = (TextView) findViewById(R.id.text_view1);
        String str1 = "Latitude:"+location.getLatitude();
        textView1.setText(str1);

        // 邨悟ｺｦ縺ｮ陦ｨ遉ｺ
        TextView textView2 = (TextView) findViewById(R.id.text_view2);
        String str2 = "Longtude:"+location.getLongitude();
        textView2.setText(str2);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void setScreenMain() {
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.qrButton);
        button2 = (Button) findViewById(R.id.gpsButton);

        button1.setOnClickListener((v) -> { setScreenQR(); });
        button2.setOnClickListener((v) -> { setScreenGPS(); });
    }

    private void setScreenQR() {
        setContentView(R.layout.activity_qr);

        button1 = (Button) findViewById(R.id.homeButtonQR);

        new IntentIntegrator(MainActivity.this).initiateScan();

        /*
        IntentIntegrator integrator = new IntentIntegrator(this);

        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0); // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
        */




        button1.setOnClickListener((v) -> { setScreenMain(); });
    }

    private void setScreenGPS() {
        setContentView(R.layout.activity_gps);

        button1 = (Button) findViewById(R.id.homeButtonGPS);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    1000);
        }
        else{
            locationStart();

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 50, this);

        }

        button1.setOnClickListener((v) -> { setScreenMain(); });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            TextView qResultView = (TextView) findViewById(R.id.qr_text_view);
            qResultView.setText(scanResult.getContents());
            Log.d("scan", "==-----:  " + scanResult.getContents());
            Toast.makeText(this, "Scanned: " + scanResult.getContents(),Toast.LENGTH_LONG).show();

        }

    }
}

