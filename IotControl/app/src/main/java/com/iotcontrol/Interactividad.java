package com.iotcontrol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Esta clase trabaja el dialogo interactivo con el dispositivo.
 * Abre una temporizacion definida por el usuario y avisa a la actividad de cuando temporiza la
 * comunicacion con el dispositivo.
 *
 * El constructor se invoca pasandole el progressBar de la aplicacion.
 * Una vez que la aplicacion ejecuta la accion, se llama a introducir interactividad pasandole
 * la cantidad de milisegundos que dura la animacion y el intervalo de callback intermedio para
 * reportar interactividad.
 *
 * Si la accion termina antes del timeout hay que llamar a finalizarInteractividad.
 * Si la accion temporiza, la aplicacion debe implementar OnTimeout para ejectuar la accion
 * timeout que se requiera.
 */


public class Interactividad {


    Interactividad.onInteractividad listener;
    private boolean activo;


    public interface onInteractividad {

        void onTimeout(ProgressBar animacion);


    }

    ProgressBar animacion;

    public Interactividad(ProgressBar animacion) {

        this.animacion = animacion;
        activo = true;
    }



    public void introducirInteractividad(long cuenta, long reporte) {

        CountDownTimer contador;
        String textoComando;


        animacion.setVisibility(View.VISIBLE);
        contador = new CountDownTimer(cuenta, reporte) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(getClass().toString(), "tick!!");

            }

            @Override
            public void onFinish() {
                finalizarInteractividad(animacion);
                listener.onTimeout(animacion);
                //animacion.setVisibility(View.INVISIBLE);


            }
        }.start();
    }

    public void finalizarInteractividad(ProgressBar animacion) {

        animacion.setVisibility(View.INVISIBLE);
        listener.onTimeout(animacion);

    }

    public void setOnTimeout(Interactividad.onInteractividad listener) {

        this.listener = listener;
    }


    public void download(View view, final ProgressBar progress){

        progress.setIndeterminate(false);
        progress.setProgress(0);;

        final int totalProgressTime = 100;
        final Thread t = new Thread() {
            @Override
            public void run() {
                int jumpTime = 0;

                while(jumpTime < totalProgressTime) {
                    try {
                        sleep(200);
                        jumpTime += 5;
                        progress.setProgress(jumpTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    public void barraProgreso(final int minimo, final int maximo, final int intervalo, final ProgressBar progreso) {


        activo = true;
        progreso.setVisibility(View.VISIBLE);
        Thread tarea = new Thread() {

            @Override
            public void run() {
                int i;
                for (i= minimo; i< maximo; i++) {
                    try {
                        sleep(intervalo);
                        if (activo == false)  {
                            i = maximo;
                            break;
                        }
                        progreso.setProgress(i);
                        Log.i(getClass().toString(), "progreso: " + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        tarea.start();


    }

    public void pararBarraProgreso(final ProgressBar progreso) {

        activo = false;
        progreso.setVisibility(View.INVISIBLE);

    }




}
