package com.example.controliot;

import android.os.CountDownTimer;

class TemporizacionComandos {

    public CountDownTimer temporizador;
    private COMANDO_IOT comando;
    private int idTemporizador;
    private String clave;
    private String idDispositivo;
    private onTemporizacionComandos listener;

    public interface onTemporizacionComandos {
        void temporizacionVencida(COMANDO_IOT comando, String clave, String idDispositivo);
        void informeIntermedio(COMANDO_IOT comando);
    }

    TemporizacionComandos(COMANDO_IOT comando, String idDispositivo, String clave) {
        this.comando = comando;
        this.idDispositivo = idDispositivo;
        this.clave = clave;

    }

    public int getIdTemporizador() {
        return this.idTemporizador;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getClave() {
        return this.clave;
    }

    public void setComando(COMANDO_IOT comando) {
        this.comando = comando;
    }

    public COMANDO_IOT getComando() {
        return this.comando;
    }

    // metodo para crear el listener.
    public void setOnTemporizacionComandos(TemporizacionComandos.onTemporizacionComandos listener) {
        this.listener = listener;
    }
    public void crearTemporizacion(long tiempo, long intervalo) {

        //CountDownTimer temporizador;
        temporizador = new CountDownTimer(tiempo, intervalo) {
            @Override
            public void onTick(long millisUntilFinished) {
                listener.informeIntermedio(comando);

            }

            @Override
            public void onFinish() {
                listener.temporizacionVencida(comando, clave, idDispositivo);


            }
        }.start();
        idTemporizador++;
    }

}
