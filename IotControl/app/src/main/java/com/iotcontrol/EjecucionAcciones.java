package com.iotcontrol;


import android.os.Handler;

public class EjecucionAcciones  {

    Thread hilo;

    EjecucionAcciones() {

        hilo = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });

        hilo.start();



    }



}
