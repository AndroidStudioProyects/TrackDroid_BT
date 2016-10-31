package com.example.giovanazzi.trackdroid_bt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_conectar,btn_offline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        levantarXML();
        botones_accion();
    }

    private void levantarXML() {
        btn_conectar=(Button)findViewById(R.id.btn_conectar);

    }

    private void botones_accion() {

        btn_conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intento=new Intent(getApplicationContext(),Homescreen.class);
                startActivity(intento);

            }
        });

    }


}
