package com.iotcontrol;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class AnadirDispositivo extends AppCompatActivity implements View.OnClickListener {

    androidx.appcompat.widget.Toolbar toolbar;
    EditText idDispositivo;
    EditText nombreDispositivo;
    ImageView botonAceptar;
    ImageView botonCancelar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_dispositivo);

        capturarControles();
    }


    private void capturarControles() {

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarAnadirDispositivo);
        toolbar.setTitle(R.string.anadirDispositivo);
        toolbar.setNavigationIcon(R.drawable.anadir_dispositivo);
       // setSupportActionBar(toolbar);
        idDispositivo = (EditText) findViewById(R.id.editIdDispositivo);
        nombreDispositivo = (EditText) findViewById(R.id.editNombreDispositivo);
        botonAceptar = (ImageView) findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(this);
        botonCancelar = (ImageView) findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {

        dispositivoIot dispositivo;
        String textoDispositivo;
        TIPO_DISPOSITIVO_IOT tipo = TIPO_DISPOSITIVO_IOT.DESCONOCIDO;
        String a;

        switch (v.getId()) {

            case R.id.botonAceptar:
                Log.i(getClass().toString(), ": he apretado el boton de añadir dispositivo");
                //Comprobar que los Editext estan vacios
                if ((idDispositivo.getText().toString().isEmpty() == true) ||
                        (nombreDispositivo.getText().toString().isEmpty() == true)) {

                    Log.e(getClass().toString(), ": Los campos estan vacios");
                    return;
                }else {

                    dispositivo = new dispositivoIotDesconocido(nombreDispositivo.getText().toString(),
                            idDispositivo.getText().toString(), TIPO_DISPOSITIVO_IOT.DESCONOCIDO);
                    dispositivo.guardarDispositivo(getApplicationContext());
                    setResult(1);
                    finish();
                }
                break;

            case R.id.botonCancelar:
                finish();
                break;

        }


    }

    private void errorEnControles() {




    }
}



