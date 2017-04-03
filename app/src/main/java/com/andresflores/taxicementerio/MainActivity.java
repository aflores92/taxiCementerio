package com.andresflores.taxicementerio;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class MainActivity extends Activity  {

    Switch PasajeroOTaxistaSwitch;
    Button mRegisterButton;
    boolean pasajeroOrTaxista;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        PasajeroOTaxistaSwitch = (Switch) findViewById(R.id.TaxistaOPasajeroSwitch);




        //Almacenar Dato del boton
        mRegisterButton = (Button) findViewById(R.id.button2);
       // getSupportActionBar().hide();

        //Pasar A Segunda Pantalla
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PasajeroOTaxistaSwitch.isChecked()){

                    Intent intent = new Intent(MainActivity.this, Main4Activity.class);
                    startActivity(intent);

                }

                else  {



                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intent);


                }

            }
        });
    }


}
