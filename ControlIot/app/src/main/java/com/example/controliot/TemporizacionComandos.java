package com.example.controliot;

import android.os.CountDownTimer;

class TemporizacionComandos {


    onTemporizacionComandos listener;
    private CountDownTimer temporizador;
    private dispositivoIot idDispositivo;

    public interface onTemporizacionComandos {
        void temporizacionVencida(dispositivoIot idDispositivo);

    }

    TemporizacionComandos(dispositivoIot idDispositivo) {
        this.idDispositivo = idDispositivo;

    }

    // metodo para crear el listener.
    public void setOnTemporizacionComandos(onTemporizacionComandos listener) {
        this.listener = listener;
    }




    public void crearTemporizacion(long tiempo, long intervalo) {

        //CountDownTimer temporizador;
        temporizador = new CountDownTimer(tiempo, intervalo) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                listener.temporizacionVencida(idDispositivo);


            }
        }.start();

    }

}
