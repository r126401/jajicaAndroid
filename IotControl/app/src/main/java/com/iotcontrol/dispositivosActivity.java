package com.iotcontrol;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class dispositivosActivity extends AppCompatActivity implements View.OnClickListener{

    Button botonGuardarDisp;
    EditText idDisp;
    EditText nombreDisp;
    EditText publicacion;
    EditText subscripcion;
    int tipoDispositivo = -1;

    final String ID= "id";
    final String NOMBRE_DISPOSITIVO = "nombre";
    final String PUBLICACION = "publicacion";
    final String SUBSCRIPCION = "subscripcion";
    final String DISPOSITIVOS = "dispositivos";
    String nombreDispositivo;
    int idAccion = -1;
    static final int MODIFICAR_CONFIGURACION_MQTT = 3;
    final String APP = "iotOnOff";

    private comandosIot comando;



    protected void guardarDispositivo() {

        //comandosIot comando;

        JSONArray array = null;
        JSONObject dispositivo = null;
        JSONObject root = null;
        int i=25;
        Boolean a;

        dispositivo = new JSONObject();



        try {

            dispositivo.put(comando.ID_DISPOSITIVO, idDisp.getText().toString() );
            dispositivo.put(comando.NOMBRE_DISPOSITIVO, nombreDisp.getText().toString());
            dispositivo.put(comando.TOPIC_PUBLICACION, publicacion.getText().toString());
            dispositivo.put(comando.TOPIC_SUBSCRIPCION, subscripcion.getText().toString());
            dispositivo.put(comando.DEVICE, -1);


            comando.PonerNuevoDispositivoIot(dispositivo, getApplication());
/*
            //leemos el fichero actual para añadir el nuevo id
            comando.leerConfiguracion(getApplicationContext(), comando.ficheroDispositivos);




            if (comando.datosDispositivos == null) {
                root = new JSONObject();
                array = new JSONArray();
                array.put(dispositivo);
                root.put(DISPOSITIVOS, array);
                comando.escribirConfiguracion(comando.ficheroDispositivos, root.toString(), getApplicationContext());

            } else {
                //Crearemos el fichero con el dispositivo como fichero nuevo.
                comando.datosDispositivos.getJSONArray(DISPOSITIVOS).put(dispositivo);
                comando.escribirConfiguracion(comando.ficheroDispositivos, comando.datosDispositivos.toString(), getApplicationContext());
            }
*/

        } catch (JSONException e) {
            e.printStackTrace();
        }



        //this.finish();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos);

        JSONObject dispositivo;
        idDisp = (EditText) findViewById(R.id.editDispositivo);
        nombreDisp = (EditText) findViewById(R.id.editnombreDispositivo);
        publicacion = (EditText) findViewById(R.id.editPublicacion);
        subscripcion = (EditText) findViewById(R.id.editSubscripcion);



        botonGuardarDisp = (Button) findViewById(R.id.botonGuardarDispositivo);
        botonGuardarDisp.setOnClickListener(this);
        comando = new comandosIot();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            nombreDispositivo = (String) bundle.get("NOMBRE_DISPOSITIVO");
            idAccion = (int) bundle.get("ID_ACCION");
        }

        if (idAccion == MODIFICAR_CONFIGURACION_MQTT) {
            Log.w(APP, "vamos a modificar la configuracion mqtt");
            idDisp.setKeyListener(null);
            botonGuardarDisp.setText("Modificar");
            comando.leerConfiguracion(getApplicationContext(), comando.ficheroDispositivos);
            dispositivo = comando.buscarDispositivoPorNombre(nombreDispositivo);
            if (dispositivo != null) {
                idDisp.setText(dispositivo.optString(comando.ID_DISPOSITIVO));
                nombreDisp.setText((dispositivo.optString(comando.NOMBRE_DISPOSITIVO)));
                publicacion.setText(dispositivo.optString(comando.TOPIC_PUBLICACION));
                subscripcion.setText(dispositivo.optString(comando.TOPIC_SUBSCRIPCION));

            }
        }



    }

    private void modificarDispositivo() {

        comando.modificarDispositivoIot(nombreDispositivo, nombreDisp.getText().toString(),
                idDisp.getText().toString(), publicacion.getText().toString(),
                subscripcion.getText().toString(), tipoDispositivo, getApplicationContext());


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.botonGuardarDispositivo):
                if (idAccion == MODIFICAR_CONFIGURACION_MQTT) {
                    modificarDispositivo();
                    Intent datosDevueltos = new Intent();
                    datosDevueltos.setData(Uri.parse("hola"));
                    setResult(RESULT_OK, datosDevueltos);
                    finish();

                } else {
                    guardarDispositivo();
                    finish();
                }

                break;

            default:
                break;

        }

    }
}
