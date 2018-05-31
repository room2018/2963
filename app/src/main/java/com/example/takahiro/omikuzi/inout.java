import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;




class Info {
    public String day;
    public String place;
    public String result;
}

public class MainActivity extends Activity {

    EditText et;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout gamen = new LinearLayout(this);
        gamen.setOrientation(LinearLayout.VERTICAL);
        setContentView(gamen);



        Button btn = new Button(this);
        btn.setText("ï€ë∂");
        btn.setOnClickListener( new hozon() );
        gamen.addView(btn);

        Button btn2 = new Button(this);
        btn2.setText("èoóÕ");
        btn2.setOnClickListener( new syuturyoku() );
        gamen.addView(btn2);

        Button btn3 = new Button(this);
        btn3.setText("çÌèú");
        btn3.setOnClickListener( new DeleteClick() );
        gamen.addView(btn3);

        Button btn4 = new Button(this);
        btn4.setText("JSONVIEW");
        btn4.setOnClickListener( new jsonclick() );
        gamen.addView(btn4);

        et = new EditText(this);
        gamen.addView(et);
    }
//////////////////////////////////////////////////////////////////////////
    class hozon implements OnClickListener{
        public void onClick(View v) {

            String Union = "\0";
            String tmp= "\0";

            Info info = new Info();
            info.day    = "2000/1/1";
            info.place  = "Ç†Ç¢Ç§Ç¶ê_é–";
            info.result = "ëÂãg";

            ObjectMapper mapper = new ObjectMapper();

            try{
                FileInputStream in = openFileInput( "omikuzi.json" );
                BufferedReader reader = new BufferedReader( new InputStreamReader( in , "UTF-8") );
                String tmp2;

                et.setText("");

                while( (tmp2 = reader.readLine()) != null ){
                    tmp=tmp + tmp2;

                }
                reader.close();
            }catch( IOException e ){
                e.printStackTrace();
            }

            try {
                Union = mapper.writeValueAsString(info);
                FileOutputStream out = openFileOutput( "omikuzi.json", MODE_PRIVATE );
                out.write( tmp.getBytes());
                out.write( Union.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
/////////////////////////////////////////////////////////////////////////////////
    class syuturyoku implements OnClickListener{
        @Override
        public void onClick(View v) {

            try{
                FileInputStream in = openFileInput( "omikuzi.json" );
                BufferedReader reader = new BufferedReader( new InputStreamReader( in , "UTF-8") );
                String tmp2;

                et.setText("");

                while( (tmp2 = reader.readLine()) != null ){
                    et.append(tmp2 + "\n");

                }
                reader.close();
            }catch( IOException e ){
                e.printStackTrace();
            }
        }
    }
    class DeleteClick implements OnClickListener{
        @Override
        public void onClick(View v) {
            deleteFile( "omikuzi.json" );
        }
    }
    class jsonclick implements OnClickListener{
        @Override
        public void onClick(View v) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode node = mapper.readTree(new File("omikuzi.json"));

                String view_day = node.get("day").asText();
                String view_place = node.get("place").asText();
                String view_result = node.get("result").asText();
                String tmp2=view_day;


                et.append(view_day + "\n");
                et.setText(view_day);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}