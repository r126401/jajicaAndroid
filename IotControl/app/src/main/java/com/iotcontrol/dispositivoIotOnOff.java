package com.iotcontrol;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class dispositivoIotOnOff extends dispositivoIot implements Serializable {


    protected ESTADO_RELE estadoRele;
    protected ESTADO_PROGRAMACION estadoProgramacion;
    protected ArrayList<ProgramaDispositivoIotOnOff> programas;



    dispositivoIotOnOff() {
        super();
        estadoRele = ESTADO_RELE.INDETERMINADO;
        estadoProgramacion = ESTADO_PROGRAMACION.INDETERMINADO;
        tipoDispositivo = TIPO_DISPOSITIVO_IOT.INTERRUPTOR;

    }




    dispositivoIotOnOff(dispositivoIot padre) {


        this.nombreDispositivo = padre.nombreDispositivo;
        this.idDispositivo = padre.idDispositivo;
        this.tipoDispositivo = TIPO_DISPOSITIVO_IOT.INTERRUPTOR;
        estadoRele = ESTADO_RELE.INDETERMINADO;
        this.topicPublicacion = padre.topicPublicacion;
        this.topicSubscripcion = padre.topicSubscripcion;
        this.versionOta = padre.versionOta;

    }

    public JSONObject dispositivo2Json(String texto) {




        return null;
    }

    public ESTADO_RELE getEstadoRele() {
        return this.estadoRele;
    }


    public void setEstadoRele(ESTADO_RELE estado) {

        this.estadoRele = estado;
    }

    public ArrayList<ProgramaDispositivoIotOnOff> cargarProgramas(String textoRecibido) {

        JSONObject objeto, respuesta;
        JSONObject objetoPrograma = null;
        JSONArray arrayProgramas;
        int i;
        ProgramaDispositivoIotOnOff programa;
        dialogoIot dialogo = new dialogoIot();

        String programaActivo = dialogo.getProgramaActivo(textoRecibido);
        try {
            respuesta = new JSONObject(textoRecibido);
            arrayProgramas = respuesta.getJSONArray(TEXTOS_DIALOGO_IOT.PROGRAMAS.getValorTextoJson());
            for(i=0;i<arrayProgramas.length();i++) {
                if (programas == null) programas = new ArrayList<ProgramaDispositivoIotOnOff>();
                programa = new ProgramaDispositivoIotOnOff();
                objeto = arrayProgramas.getJSONObject(i);
                programa.setIdProgramacion(objeto.getString(TEXTOS_DIALOGO_IOT.ID_PROGRAMA.getValorTextoJson()));
                try {
                    programa.setDuracion(objeto.getInt(TEXTOS_DIALOGO_IOT.DURACION.getValorTextoJson()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.w(getClass().toString(), "El programa no tiene duracion asignada");
                }


                if (programaActivo != null) programa.setProgramaEnCurso(programaActivo);
                anadirPrograma(programa);


            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().toString(), "Error al obtener los programas del dispositivo");
            return null;
        }


        return programas;
    }

    public boolean anadirPrograma(ProgramaDispositivoIotOnOff programa) {

        int i;
        int tam;
        if (programas == null) {
            programas = new ArrayList<ProgramaDispositivoIotOnOff>();
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

    public boolean modificarPrograma(String idPrograma, String idNuevoPrograma, String estadoPrograma, String rele, int duracion) {

        int i;

        i= buscarPrograma(idPrograma);
        idNuevoPrograma += estadoPrograma;
        idNuevoPrograma += rele;
        if (i!= -1) {
            programas.get(i).setIdProgramacion(idNuevoPrograma);
            programas.get(i).setDuracion(duracion);

        }

        return true;
    }

    public boolean eliminarPrograma(String idPrograma) {

        ProgramaDispositivoIotOnOff programa;
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

}
