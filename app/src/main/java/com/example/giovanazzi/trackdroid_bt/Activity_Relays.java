package com.example.giovanazzi.trackdroid_bt;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Diego on 01/11/2016.
 */

public class Activity_Relays extends Activity {


    ToggleButton tb_Relay1,tb_Relay2,tb_Relay3,tb_Relay4,tb_Relay5,tb_Relay6,tb_Relay7,tb_Relay8,tb_Relay9,tb_Relay10;
    Button btn_Disp,btn_Config,btn_Leer;
    TextView txt_dato1,txt_dato2,txt_dato3,txt_dato4,txt_dato5,txt_dato6,txt_dato7,txt_dato8,txt_dato9,txt_dato10,txt_dato11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relays);
        LevantarXML();
        Botones();

    }

    private void Botones() {
    }

    private void LevantarXML() {
        tb_Relay1=(ToggleButton) findViewById(R.id.tb_Relay1);
        tb_Relay2=(ToggleButton) findViewById(R.id.tb_Relay2);
        tb_Relay3=(ToggleButton) findViewById(R.id.tb_Relay3);
        tb_Relay4=(ToggleButton) findViewById(R.id.tb_Relay4);
        tb_Relay5=(ToggleButton) findViewById(R.id.tb_Relay5);
        tb_Relay6=(ToggleButton) findViewById(R.id.tb_Relay6);
        tb_Relay7=(ToggleButton) findViewById(R.id.tb_Relay7);
        tb_Relay8=(ToggleButton) findViewById(R.id.tb_Relay8);
        tb_Relay9=(ToggleButton) findViewById(R.id.tb_Relay9);
        tb_Relay10=(ToggleButton) findViewById(R.id.tb_Relay10);

        btn_Disp=(Button)findViewById(R.id.btn_Disp);
        btn_Config=(Button)findViewById(R.id.btn_Config);
        btn_Leer=(Button)findViewById(R.id.btn_Leer);

        txt_dato1=(TextView) findViewById(R.id.txt_dato1);
        txt_dato2=(TextView) findViewById(R.id.txt_dato2);
        txt_dato3=(TextView) findViewById(R.id.txt_dato3);
        txt_dato4=(TextView) findViewById(R.id.txt_dato4);
        txt_dato5=(TextView) findViewById(R.id.txt_dato5);
        txt_dato6=(TextView) findViewById(R.id.txt_dato6);
        txt_dato7=(TextView) findViewById(R.id.txt_dato7);
        txt_dato8=(TextView) findViewById(R.id.txt_dato8);
        txt_dato9=(TextView) findViewById(R.id.txt_dato9);
        txt_dato10=(TextView) findViewById(R.id.txt_dato10);
        txt_dato11=(TextView) findViewById(R.id.txt_dato11);




    }
}
