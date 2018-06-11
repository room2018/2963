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
import java.io.File;
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
//    private ObjectMapper mapper = new ObjectMapper();

    EditText et;
    Info info = new Info();

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
                


        try {
            FileInputStream input = openFileInput("omikuji.json");
            BufferedReader inputText = new BufferedReader(new InputStreamReader(input));

            JSONArray jsona = new JSONArray(inputText.readLine());
            JSONObject json = jsona.getJSONObject(0);


            viewDay = json.getString("day");
            viewPlace = json.getString("place");
            viewResult = json.getString("result");


            Log.d("aaa", viewDay);
            Log.d("aaa", viewPlace);
            Log.d("aaa", viewResult);

            viewData.setText(viewDay + viewPlace + viewResult + "\n");


//            et.append(viewDay + viewPlace + viewResult + "\n");
//            et.setText(viewDay);
            /*
            JsonNode node = mapper.readTree(new File("omikuji.json"));

            String view_day = node.get("day").asText();
            String view_place = node.get("place").asText();
            String view_result = node.get("result").asText();


            et.append(view_day + view_place + view_result + "\n");
            et.setText(view_day);
            */
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

        if (scanResult != null) {
            TextView qResultView = (TextView) findViewById(R.id.qr_text_view);
            qResultView.setText(scanResult.getContents());
            Log.d("scan", "==-----:  " + scanResult.getContents());
//            Toast.makeText(this, "Scanned: " + scanResult.getContents(),Toast.LENGTH_LONG).show();
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

                fileSave();

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

    public void fileSave() {
        String Union = "\0";
        String tmp= "\0";

//        info.day    = "2000/1/1";
//        info.place  = "あいうえ神社";
//        info.result = "大吉";

//        ObjectMapper mapper = new ObjectMapper();

        try{
            FileInputStream in = openFileInput( "omikuji.json" );
            BufferedReader reader = new BufferedReader( new InputStreamReader( in , "UTF-8") );
            String tmp2;

//            et.setText("");

            while( (tmp2 = reader.readLine()) != null ){
                tmp=tmp + tmp2;

            }
            reader.close();
        }catch( IOException e ){
            e.printStackTrace();
        }

        try {
//            Union = mapper.writeValueAsString(info);
            FileOutputStream out = openFileOutput( "omikuji.json", MODE_PRIVATE );
//            out.write( tmp.getBytes());
//            out.write( Union.getBytes());
            out.write("[{\"day\":\"2018/06/08\",\"place\":\"日本、〒918-8231 福井県福井市問屋町３丁目６０９\",\"result\":\"だいきちぃ～\"}]".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

