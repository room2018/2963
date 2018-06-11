package com.example.takahiro.omikuzi;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    LocationManager locationManager;

    private GoogleApiClient mGoogleApiClient;
    private final int PLACE_PICKER_REQUEST = 1;

    private Button button1, button2;

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
        button2 = (Button) findViewById(R.id.dataButton);

        button1.setOnClickListener((v) -> { setScreenQR(); });
        button2.setOnClickListener((v) -> { setScreenViewData(); });
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

    private void setScreenViewData() {
        setContentView(R.layout.activity_view_data);

        button1 = (Button) findViewById(R.id.homeButtonViewData);
        button2 = (Button) findViewById(R.id.delete);
        TextView viewData = (TextView) findViewById(R.id.data);

        String viewDay = null;
        String viewPlace = null;
        String viewResult = null;
        String data = null;

        try {
            FileInputStream input = openFileInput("omikuji.json");
            BufferedReader inputText = new BufferedReader(new InputStreamReader(input));

            JSONArray jsonArray = new JSONArray(inputText.readLine());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);

                viewDay = json.getString("day");
                viewPlace = json.getString("place");
                viewResult = json.getString("result");

                Log.d("omikuji", viewDay);
                Log.d("omikuji", viewPlace);
                Log.d("omikuji", viewResult);

                data = data + viewDay + viewPlace + viewResult + "\n";
            }
            viewData.setText(data);

            inputText.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        button1.setOnClickListener((v) -> { setScreenMain(); });
        button2.setOnClickListener((v) -> { deleteFile( "omikuji.json" ); });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        Info info = new Info();

        if (scanResult != null) {
            TextView qResultView = (TextView) findViewById(R.id.qr_text_view);
            qResultView.setText(scanResult.getContents());
            Log.d("scan", "==-----:  " + scanResult.getContents());
            info.result = scanResult.getContents();

            setScreenPlaceAPI();
        }
        else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                TextView pleceResult = (TextView) findViewById(R.id.result);
                TextView name = (TextView) findViewById(R.id.name);
                TextView address = (TextView) findViewById(R.id.address);

                Place place = PlacePicker.getPlace( data, this );
                String placeData = String.format( "%s",place.toString() );

                pleceResult.setText(placeData);
                name.setText(place.getName());
                address.setText(place.getAddress());

                info.day = getNowDate();
                info.place = place.getAddress().toString();

                fileSave(info);
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

    class Info {
        public String day;
        public String place;
        public String result;
    }

    public static String getNowDate(){
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    public void fileSave(Info info) {
        String oldData = null;

        try{
            FileInputStream in = openFileInput( "omikuji.json" );
            BufferedReader reader = new BufferedReader( new InputStreamReader( in , "UTF-8") );

            oldData = reader.readLine();
            Log.d("aaa", "fileSave: " +  oldData);

            reader.close();
        }catch( IOException e ){
            e.printStackTrace();
        }
        try {
            FileOutputStream out = openFileOutput( "omikuji.json", MODE_APPEND );
            Log.d("aaa", info.day + info.place + info.result);

            out.write( oldData.getBytes());
            out.write("[{\"day\":\"2018/06/08\",\"place\":\"日本、〒918-8231 福井県福井市問屋町３丁目６０９\",\"result\":\"だいきちぃ～\"}],".getBytes());
            out.write(( "[{\"day\":\"" + info.day +
                    "\",\"place\":\"" + info.place +
                    "\",\"result\":\"" + info.result + "\"}]," ).getBytes());

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

