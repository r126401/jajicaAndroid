package com.iotcontrol;

import org.json.JSONObject;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class programacionDispositivo implements Serializable {




    public String idProgramacion;
    public int tipoPrograma;
    public int ano;
    public int mes;
    public int dia;
    public int hora;
    public int minuto;
    public int segundo;
    public int diaSemana;
    public int estadoPrograma;
    public int estadoRele;
    public int mascara;
    public int duracion;
    public double temperaturaUmbral;
    int tipoDispositivo;




    public programacionDispositivo() {
        super();
        idProgramacion = null;
        tipoPrograma =0;
        hora = 0;
        minuto = 0;
        segundo = 0;
        diaSemana = 0;
        estadoPrograma = 0;
        estadoRele = 0;
        mascara = 0;
        duracion = 0;
        ano = 0;
        mes = 0;
        dia = 0;
        temperaturaUmbral = -100;
        tipoDispositivo = -1;
    }

    public String adaptarDato(int dato) {

        String datoConvertido = null;

        if (dato < 10) {
            datoConvertido = "0" + Integer.toString(dato);

        } else {
            datoConvertido = Integer.toString(dato);
        }



        return datoConvertido;
    }


}


