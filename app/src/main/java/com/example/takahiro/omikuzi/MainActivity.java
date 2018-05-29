package com.example.takahiro.omikuzi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    LocationManager locationManager;

    private GoogleApiClient mGoogleApiClient;
    private final int PLACE_PICKER_REQUEST = 1;

    private Button button1, button2, button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        setScreenMain();
    }

    private void setScreenMain() {
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.qrButton);
        button2 = (Button) findViewById(R.id.gpsButton);

        button1.setOnClickListener((v) -> { setScreenQR(); });
        button2.setOnClickListener((v) -> { setScreenPlaceAPI(); });
    }

    private void setScreenQR() {
        setContentView(R.layout.activity_qr);

        button1 = (Button) findViewById(R.id.homeButtonQR);

        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();

        button1.setOnClickListener((v) -> { setScreenMain(); });
    }

    private void setScreenPlaceAPI() {
        setContentView(R.layout.activity_place_api);

        button1 = (Button) findViewById(R.id.homeButtonPlaceAPI);


        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
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
            setScreenPlaceAPI();
        }
        else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                TextView pleceResult = (TextView) findViewById(R.id.result);
                TextView name = (TextView) findViewById(R.id.name);
                TextView address = (TextView) findViewById(R.id.address);

                Place place = PlacePicker.getPlace( data, this );
                // toastMsg = String.format( "Place: %s\n/%s", place.getName(), place.getAddress());
                String placeData = String.format( "%s",place.toString() );
                // Toast.makeText( this, toastMsg, Toast.LENGTH_LONG ).show();
                pleceResult.setText(placeData);
                name.setText(place.getName());
                address.setText(place.getAddress());
            } else {
                Toast.makeText(this, "失敗:" + requestCode, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void fileSave() {
        String Union;
        String day = "2000/1/1";
        String place = "あいうえ神社";
        String result = "大吉";
        Union = '"' + "日時\"：" + '"' + day + "\",\r\n\t" +
                '"' + "場所\"：" + '"' + place + "\",\r\n\t" +
                '"' + "結果\"：" + '"' + result + "\",\r\n";
        try {
            FileOutputStream out = openFileOutput("omikuzi.txt", MODE_APPEND);
            out.write("{\r\n\t".getBytes());
            out.write(Union.getBytes());
            out.write("}\r\n\t".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

