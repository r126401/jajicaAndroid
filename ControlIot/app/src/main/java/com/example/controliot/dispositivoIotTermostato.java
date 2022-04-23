package com.example.controliot;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class dispositivoIotTermostato extends dispositivoIot implements Serializable {

    protected ESTADO_RELE estadoRele;
    protected double temperatura;
    protected double umbralTemperatura;
    protected double humedad;
    protected ESTADO_PROGRAMACION estadoProgramacion;
    protected ArrayList<ProgramaDispositivoIotTermostato> programas;
    protected double margenTemperatura;
    protected int intervaloLectura;
    protected int reintentosLectura;
    protected int intervaloReintentos;
    protected double valorCalibrado;
    protected Boolean master;
    protected String idSensor;



    dispositivoIotTermostato() {
        super();

        estadoRele = ESTADO_RELE.INDETERMINADO;
        temperatura = -1000;
        umbralTemperatura = -1000;
        humedad = -1000;
        estadoProgramacion = ESTADO_PROGRAMACION.INDETERMINADO;
        tipoDispositivo = TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO;
        programas = null;
        intervaloLectura = 0;
        margenTemperatura = 0;
        reintentosLectura = 0;
        intervaloReintentos = 0;
        programas = null;
        master = true;
        idSensor = null;





    }

    dispositivoIotTermostato(dispositivoIot padre) {


        this.nombreDispositivo = padre.nombreDispositivo;
        this.idDispositivo = padre.idDispositivo;
        this.tipoDispositivo = TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO;
        estadoRele = ESTADO_RELE.INDETERMINADO;
        this.topicPublicacion = padre.topicPublicacion;
        this.topicSubscripcion = padre.topicSubscripcion;
        this.versionOta = padre.versionOta;
        temperatura = -1000;
        umbralTemperatura = -1000;
        humedad = -1000;
        intervaloLectura = 0;
        margenTemperatura = 0;
        reintentosLectura = 0;
        intervaloReintentos = 0;
        master = true;
        idSensor = null;


    }
    dispositivoIotTermostato(dispositivoIot padre, TIPO_DISPOSITIVO_IOT tipo) {


        this.nombreDispositivo = padre.nombreDispositivo;
        this.idDispositivo = padre.idDispositivo;
        this.tipoDispositivo = TIPO_DISPOSITIVO_IOT.CRONOTERMOSTATO;
        estadoRele = ESTADO_RELE.INDETERMINADO;
        this.topicPublicacion = padre.topicPublicacion;
        this.topicSubscripcion = padre.topicSubscripcion;
        this.versionOta = padre.versionOta;
        temperatura = -1000;
        umbralTemperatura = -1000;
        humedad = -1000;
        intervaloLectura = 0;
        margenTemperatura = 0;
        reintentosLectura = 0;
        intervaloReintentos = 0;
        this.tipoDispositivo = tipo;
        master = true;
        idSensor = null;



    }

    public void setMargenTemperatura(double margenTemperatura) {

        this.margenTemperatura = margenTemperatura;
    }

    public void setIntervaloLectura(int intervalo) {
        this.intervaloLectura = intervalo;
    }

    public void setReintentoLectura(int reintentoLectura) {
        this.reintentosLectura = reintentoLectura;
    }

    public void setIntervaloReintentos(int intervaloReintentos) {
        this.intervaloReintentos = intervaloReintentos;
    }

    public void setValorCalibrado(double valorCalibrado) {
        this.valorCalibrado = valorCalibrado;
    }

    public void setEstadoRele(ESTADO_RELE estado) {

        this.estadoRele = estado;
    }

    public void setUmbralTemperatura(double umbralTemperatura) {

        this.umbralTemperatura = redondearDatos(umbralTemperatura, 1);

    }

    public void setTemperatura(double temperatura) {

        this.temperatura = redondearDatos(temperatura,1);

    }

    public void setHumedad(double humedad) {

        this.humedad = redondearDatos(humedad, 1);

    }

    public void setEstadoProgramacion(ESTADO_PROGRAMACION estadoProgramacion) {
        this.estadoProgramacion = estadoProgramacion;
    }


    public double getTemperatura() {

        return this.temperatura;
    }

    public ESTADO_RELE getEstadoRele() {

        return estadoRele;
    }

    public double getMargenTemperatura() {
        return margenTemperatura;
    }

    public int getIntervaloReintentos() {
        return intervaloReintentos;
    }

    public int getIntervaloLectura() {
        return intervaloLectura;
    }

    public int getReintentosLectura() {
        return reintentosLectura;
    }

    public double getValorCalibrado() {
        return valorCalibrado;
    }

    public double getHumedad() {
        return this.humedad;
    }

    public double getUmbralTemperatura() {
        return this.umbralTemperatura;
    }

    public ESTADO_PROGRAMACION getEstadoProgramacion() {

        return this.estadoProgramacion;
    }

    public Boolean isSensorLocal() {
        return this.master;
    }

    public String getIdSensor() {

        if (master == false) {
            return this.idSensor;
        } else {
            return null;
        }

    }

    public void setIdSensor(String idSensor) {

        this.idSensor = idSensor;
        if (idSensor == null) {
            this.master = true;
        } else {
            this.master = false;
        }

    }

    public void setSensorMaster(Boolean master) {

        this.master = master;
        if (master == true) this.idSensor = null;
    }



    public ArrayList<ProgramaDispositivoIotTermostato> cargarProgramas(String textoRecibido) {

        JSONObject objeto, respuesta;
        JSONArray arrayProgramas;
        int i;
        int duracion;
        ProgramaDispositivoIotTermostato programa;
        dialogoIot dialogo = new dialogoIot();

        String programaActivo = dialogo.getProgramaActivo(textoRecibido);


        //respuesta = dialogo.obtenerparteJson(textoRecibido, TEXTOS_DIALOGO_IOT.DLG_RESPUESTA);

        try {
            respuesta = new JSONObject(textoRecibido);
            arrayProgramas = respuesta.getJSONArray(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson());
            for(i=0;i<arrayProgramas.length();i++) {
                if (programas == null) programas = new ArrayList<ProgramaDispositivoIotTermostato>();
                programa = new ProgramaDispositivoIotTermostato();
                objeto = arrayProgramas.getJSONObject(i);
                programa.setIdProgramacion(objeto.getString(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson()));
                if (programaActivo != null) programa.setProgramaEnCurso(programaActivo);
                programa.setUmbralTemperatura(objeto.getDouble(TEXTOS_DIALOGO_IOT.UMBRAL_TEMPERATURA.getValorTextoJson()));
                duracion = objeto.getInt(TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson());
                if (duracion > 0 ) {
                    programa.setDuracion(duracion);
                }
                anadirPrograma(programa);
                //programas.add(programa);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "Error al obtener los programas del dispositivo");
            return null;
        }


        return this.programas;
    }


    public ArrayList<ProgramaDispositivoIotTermostato> getProgramasTermostato() {
        return this.programas;
    }


    public int buscarPrograma(String idPrograma) {

        int i;
        int tam = programas.size();
        for(i=0;i<tam;i++) {
            if (programas.get(i).getIdProgramacion().equals(idPrograma)) {
                Log.i(getClass().toString(), "Programa " + idPrograma + " encontrado");
                return i;
            }

        }


        return -1;
    }

    public boolean eliminarPrograma(String idPrograma) {

        ProgramaDispositivoIotTermostato programa;
        int indicePrograma = buscarPrograma(idPrograma);


        if (indicePrograma >= 0) {
            this.programas.remove(indicePrograma);
            Log.i(getClass().toString(), "Programa " + idPrograma + " borrado");
            return true;


        } else {
            Log.w(getClass().toString(), "Programa " + idPrograma + " no encontrado");
            return false;
        }


    }


    public boolean anadirPrograma(ProgramaDispositivoIotTermostato programa) {

        int i;
        int tam;
        if (programas == null) {
            programas = new ArrayList<ProgramaDispositivoIotTermostato>();
            tam = 0;
        } else {
            tam = programas.size();
        }
        for(i=0;i<tam;i++) {

            if (programas.get(i).getIdProgramacion().equals(programa.getIdProgramacion())) {
                Log.i(getClass().toString(), "Programa repetido, no se inserta");
                return  false;
            }

        }
        programas.add(programa);
        return true;



    }

    public void getDatosConfiguracionTermostato(String textoRecibido) {

        JSONObject respuesta;
        dialogoIot dialogo;
        dialogo = new dialogoIot();
        setIntervaloLectura(dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.INTERVALO_LECTURA.getValorTextoJson()));
        setMargenTemperatura(dialogo.extraerDatoJsonDouble(textoRecibido, TEXTOS_DIALOGO_IOT.MARGEN_TEMPERATURA.getValorTextoJson()));
        setIntervaloReintentos(dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.INTERVALO_REINTENTOS.getValorTextoJson()));
        setReintentoLectura(dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.REINTENTOS_LECTURA.getValorTextoJson()));
        setValorCalibrado(dialogo.extraerDatoJsonInt(textoRecibido, TEXTOS_DIALOGO_IOT.VALOR_CALIBRADO.getValorTextoJson()));

    }

    public boolean modificarPrograma(String idPrograma, String idNuevoPrograma, String estadoPrograma, double umbral, int duracion) {

        int i;

        ESTADO_PROGRAMA estado = ESTADO_PROGRAMA.PROGRAMA_DESCONOCIDO;

        i= buscarPrograma(idPrograma);
        idNuevoPrograma += estadoPrograma;
        idNuevoPrograma += "1";
        if (i!= -1) {
            estado = estado.fromId(Integer.valueOf(estadoPrograma));
            programas.get(i).setIdProgramacion(idNuevoPrograma);
            programas.get(i).setUmbralTemperatura(umbral);
            programas.get(i).setDuracion(duracion);
            programas.get(i).setEstadoPrograma(estado);

        }

        return true;
    }
    public void actualizarProgramaActivo(String idPrograma) {

        //Ponemos a false todos los programas como activos y marcamos solo el actual
        int i;
        int tamano = programas.size();

        for (i=0;i< tamano;i++) {
            if(programas.get(i).getIdProgramacion().equals(idPrograma)) {
                programas.get(i).setProgramaEnCurso(true);
            } else {
                programas.get(i).setProgramaEnCurso(false);
            }
        }

    }

    public void setProgramasTermostato(ArrayList<ProgramaDispositivoIotTermostato> programas) {
        this.programas = programas;
    }


}
