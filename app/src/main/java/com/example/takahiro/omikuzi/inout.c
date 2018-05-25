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
        btn.setText("保存");
        btn.setOnClickListener( new hozon() );
        gamen.addView(btn);

        Button btn2 = new Button(this);
        btn2.setText("出力");
        btn2.setOnClickListener( new syuturyoku() );
        gamen.addView(btn2);

        et = new EditText(this);
        gamen.addView(et);
    }

    class hozon implements OnClickListener{

        public void onClick(View v) {
            String Union;
            String day = "2000/1/1";
            String place = "あいうえ神社";
            String result = "大吉";
            Union = '"' + "日時\"：" + '"' +  day   + "\",\r\n\t" +
                    '"' + "場所\"：" + '"' + place  + "\",\r\n\t" +
                    '"' + "結果\"：" + '"' + result + "\",\r\n" ;
            try{
                FileOutputStream out = openFileOutput( "omikuzi.txt", MODE_PRIVATE );
                out.write("{\r\n\t".getBytes());
                out.write( Union.getBytes());
                out.write("}\r\n\t".getBytes());
            }catch( IOException e ){
                e.printStackTrace();
            }
        }
    }

    class syuturyoku implements OnClickListener{
        @Override
        public void onClick(View v) {

            try{
                FileInputStream in = openFileInput( "omikuzi.txt" );
                BufferedReader reader = new BufferedReader( new InputStreamReader( in , "UTF-8") );
                String tmp;

                et.setText("");

                while( (tmp = reader.readLine()) != null ){
                    et.append(tmp + "\n");

                }
                reader.close();
            }catch( IOException e ){
                e.printStackTrace();
            }
        }
    }
}