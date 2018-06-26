package com.example.takahiro.omikuzi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
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
import java.io.FileNotFoundException;
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
    private ImageView view, read;

    JSONArray resultData = null;

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

        view = findViewById(R.id.viewBtn);
        read = findViewById(R.id.readBtn);

        read.setOnClickListener((v) -> { setScreenQR(); });
        view.setOnClickListener((v) -> { setScreenViewData(); });
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
        resultData = null;

        button1 = (Button) findViewById(R.id.homeButtonViewData);
        button2 = (Button) findViewById(R.id.delete);

        String resultString = "[\n" +
                "    {\n" +
                "        \"id\": \"0\",\n" +
                "        \"結果\": \"大吉\",\n" +
                "        \"願望\": \"目上の人の助を得て思わず早く調う\",\n" +
                "        \"待人\": \"早く来る\",\n" +
                "        \"恋愛\": \"誠意を尽して接せよ\",\n" +
                "        \"縁談\": \"他人の言うまゝにしてよし必ず叶う\",\n" +
                "        \"お産\": \"肥立もよく　安し\",\n" +
                "        \"進学\": \"安心して勉学せよ\",\n" +
                "        \"就職\": \"早めにすれば叶う\",\n" +
                "        \"家庭\": \"開運は家庭の団楽にあり\",\n" +
                "        \"病気\": \"すべて信心でなおる\",\n" +
                "        \"旅行\": \"行先に利得あり\",\n" +
                "        \"事業\": \"金運あり　お祈りせよ\",\n" +
                "        \"訴訟\": \"恨みを抱くと負けます\",\n" +
                "        \"転居\": \"移りなさい　良事あり\",\n" +
                "        \"失物\": \"早く出る　物の間\",\n" +
                "        \"相場（賭）\": \"見合わせ今が大切\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"1\",\n" +
                "        \"結果\": \"末吉\",\n" +
                "        \"願望\": \"のぞみのまゝです　人の言葉に迷うな\",\n" +
                "        \"待人\": \"音信なし　来る\",\n" +
                "        \"恋愛\": \"この人より他になし\",\n" +
                "        \"縁談\": \"多くて困ることあり　静かに心を定めなさい\",\n" +
                "        \"お産\": \"やすし　安心せよ\",\n" +
                "        \"進学\": \"少し無理をしなさい\",\n" +
                "        \"就職\": \"早く見るけるが吉\",\n" +
                "        \"家庭\": \"持てません\",\n" +
                "        \"病気\": \"早く全快します\",\n" +
                "        \"旅行\": \"よし　連の人に注意\",\n" +
                "        \"事業\": \"利益あり進んで吉\",\n" +
                "        \"訴訟\": \"恨みを抱くと負けます\",\n" +
                "        \"転居\": \"移りなさい　良事あり\",\n" +
                "        \"失物\": \"家の中にはない　外で見つかる\",\n" +
                "        \"相場（賭）\": \"買え　今が最上\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"2\",\n" +
                "        \"結果\": \"凶\",\n" +
                "        \"願望\": \"叶いにくい\",\n" +
                "        \"待人\": \"現れない\",\n" +
                "        \"恋愛\": \"悪い結果を招く\",\n" +
                "        \"縁談\": \"全て良くならない\",\n" +
                "        \"お産\": \"肥立もよく　安し\",\n" +
                "        \"進学\": \"目上の人の意見に従え\",\n" +
                "        \"就職\": \"望みを下げろ\",\n" +
                "        \"家庭\": \"良い家庭は望むものでなく築くもの\",\n" +
                "        \"病気\": \"おぼつかない\",\n" +
                "        \"旅行\": \"病気、火難に注意\",\n" +
                "        \"事業\": \"人の口車に乗るな\",\n" +
                "        \"訴訟\": \"恨みを抱くと負けます\",\n" +
                "        \"転居\": \"今のままよし\",\n" +
                "        \"失物\": \"出にくいでしょう\",\n" +
                "        \"相場（賭）\": \"やめた方がよい\"\n" +
                "    }\n" +
                "]";

        try {
            resultData = new JSONArray(resultString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout linearLayout = findViewById(R.id.scrollLinear);

        String viewDay = null;
        String viewPlace = null;
        String resultID = null;
        String viewResult = null;
        String data = null;

        try {
            FileInputStream input = openFileInput("omikuji.json");
            BufferedReader inputText = new BufferedReader(new InputStreamReader(input));

            JSONArray jsonArray = new JSONArray(inputText.readLine());
            
            input.close();
            inputText.close();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);

                viewDay = json.getString("day");
                viewPlace = json.getString("place");
                resultID = json.getString("result");

                JSONObject rData = resultData.getJSONObject(Integer.parseInt(resultID));
                viewResult = rData.getString("結果");


                Log.d("omikuji", viewDay);
                Log.d("omikuji", viewPlace);
                Log.d("omikuji", viewResult);

                data = viewDay + " " + viewPlace + " " + viewResult + "\n";

                BootstrapButton btn = new BootstrapButton(this);

                btn.setBackgroundColor(Color.rgb(255, 104, 104));
                btn.setHighlightColor(Color.rgb(255, 104, 104));
                btn.setTextColor(Color.rgb(255, 104, 104));
                btn.setBootstrapSize((float) 1.5);

                btn.setBootstrapText((new BootstrapText.Builder(this, true)).
                        addFontAwesomeIcon("fa_check").
                        addText(viewDay + " \n").
                        addText(viewPlace + " \n").
                        addText(viewResult + " ").
                        build());

                linearLayout.addView(btn);

                String finalResultID = resultID;
                String finalViewPlace = viewPlace;
                String finalViewDay = viewDay;
                String finalViewResult = viewResult;
                btn.setOnClickListener((View v) -> {
                    setContentView(R.layout.activity_result);
                    button1 = findViewById(R.id.back_result);

                    JSONObject rrData = null;

                    try {
                        rrData = resultData.getJSONObject(Integer.parseInt(finalResultID));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    TextView kekka = findViewById(R.id.kekka);
                    TextView date = findViewById(R.id.date);
                    TextView taibou = findViewById(R.id.taibou);
                    TextView machibito = findViewById(R.id.machibito);
                    TextView renai = findViewById(R.id.renai);
                    TextView endan = findViewById(R.id.endan);
                    TextView place = findViewById(R.id.place);

                    try {
            kekka.setText(finalViewResult);
            date.setText(finalViewDay);
                        taibou.setText("願望：　" + rrData.getString("願望"));
                        machibito.setText("待人：　" + rrData.getString("待人"));
                        renai.setText("恋愛：　" + rrData.getString("恋愛"));
                        endan.setText("縁談：　" + rrData.getString("縁談"));
            place.setText(finalViewPlace);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    button1.setOnClickListener((view) -> { setScreenMain(); });
                });
            }

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
                info.place = place.getName().toString();

                fileSave(info);

                /*
                FileOutputStream out = null;
                try {
                    out = openFileOutput("omikuji.json", MODE_APPEND);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String sss = "[{\"day\":\"2018/01/1\",\"place\":\"出雲大社\",\"result\":\"0\"},{\"day\":\"2018/01/02\",\"place\":\"永和神社\",\"result\":\"1\"},{\"day\":\"2018/02/24\",\"place\":\"敢國神社\",\"result\":\"2\"},{\"day\":\"2018/03/14\",\"place\":\"赤城神社\",\"result\":\"1\"},{\"day\":\"2018/04/25\",\"place\":\"浅草神社\",\"result\":\"1\"},{\"day\":\"2018/05/03\",\"place\":\"福井神社\",\"result\":\"0\"},{\"day\":\"2018/06/01\",\"place\":\"足羽神社\",\"result\":\"2\"}]";

                try {
                    out.write(sss.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */

                setScreenMain();
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
        String t_day = "";
        String t_place = "";
        String t_result = "";
        String union = "";
        JSONObject obj = new JSONObject();

        try {
            FileInputStream input = openFileInput("omikuji.json");
            BufferedReader inputtext = new BufferedReader(new InputStreamReader(input));
            JSONArray jsonArray = new JSONArray(inputtext.readLine());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                t_day = json.getString("day");
                t_place = json.getString("place");
                t_result = json.getString("result");

                obj.put("day", t_day);
                obj.put("place", t_place);
                obj.put("result", t_result);
                union += obj.toString();
                union += ",";
            }
            inputtext.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            deleteFile("omikuji.json");
            FileOutputStream out = openFileOutput("omikuji.json", MODE_APPEND);

            obj.put("day", info.day);
            obj.put("place", info.place);
            obj.put("result", info.result);
            union += obj.toString();
            union = "[" + union + "]";
            out.write(union.getBytes());
            out.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }
}

