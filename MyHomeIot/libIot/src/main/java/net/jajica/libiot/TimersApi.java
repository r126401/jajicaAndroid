package net.jajica.libiot;


import android.os.CountDownTimer;

class TimersApi {

    public CountDownTimer temporizador;
    private IOT_COMMANDS comando;
    private int idTemporizador;
    private String clave;
    private String idDispositivo;
    private onTemporizacionComandos listener;

    public interface onTemporizacionComandos {
        void temporizacionVencida(IOT_COMMANDS comando, String clave, String idDispositivo);
        void informeIntermedio(IOT_COMMANDS comando);
    }

    TimersApi(IOT_COMMANDS comando, String idDispositivo, String clave) {
        this.comando = comando;
        this.idDispositivo = idDispositivo;
        this.clave = clave;

    }

    TimersApi(IOT_COMMANDS comando, String idDispositivo, String Clave, onTemporizacionComandos listenerTemporizacion) {
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

    public void setComando(IOT_COMMANDS comando) {
        this.comando = comando;
    }

    public IOT_COMMANDS getComando() {
        return this.comando;
    }

    // metodo para crear el listener.
    public void setOnTemporizacionComandos(TimersApi.onTemporizacionComandos listener) {
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
