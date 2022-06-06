package com.example.controliot;

import android.util.Log;

import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;

public class CalculoFechas {


    ArrayList<ArrayList<Integer>> intervalos;


    CalculoFechas() {
        intervalos = null;

    }

    private int fechaASegundos(int hora, int minuto) {

        int segundos;
        segundos = hora * 3600 + minuto * 60;

        return segundos;


    }

    public int horaStringASegundos(String fecha) {

        int hora;
        int minuto;
        hora = Integer.valueOf(fecha.substring(0,2));
        minuto = Integer.valueOf(fecha.substring(3,5));

        return fechaASegundos(hora, minuto);
    }


    public void insertarIntervalo(int hora, int minuto, int duracion, int mascara) {

        int segundos;
        if (intervalos == null) {
            intervalos = new ArrayList<ArrayList<Integer>>();
        }

        segundos = fechaASegundos(hora, minuto);


        ArrayList<Integer> elemento;
        elemento = new ArrayList<Integer>();
        elemento.add(segundos);
        elemento.add(segundos + duracion);
        elemento.add(mascara);
        intervalos.add(elemento);

    }


    public boolean intervaloAsignable(int hora, int minuto, int duracion, int mascara) {

        int intervalo;
        int i;
        int inferior;
        int superior;
        int j;
        Boolean diaActivo;
        Boolean diaProgramaListaActivo;

        intervalo = fechaASegundos(hora, minuto);

        if (intervalos == null) {
            return true;
        }

        for (i = 0; i < intervalos.size(); i++) {

            for (j = 0; j < 7; j++) {
                diaActivo = getMascaraDia(j, mascara);
                diaProgramaListaActivo = getMascaraDia(j, intervalos.get(i).get(2));
                if ((diaActivo == true) && (diaProgramaListaActivo == true)) {
                    inferior = intervalos.get(i).get(0);
                    superior = intervalos.get(i).get(1);
                    if (((intervalo > inferior) && (intervalo < superior)) ||
                            (((intervalo + duracion) > inferior) && ((intervalo + duracion) < superior))) {
                        Log.w(getClass().toString(), "intervalo no asignable");
                        return false;
                    }

                }
            }


        }

        return true;


    }


    public int restarHoras(String hora1, String hora2) {

        Calendar fecha1;
        Calendar fecha2;
        int hora;
        int minuto;


        hora = Integer.valueOf(hora1.substring(0,2));
        minuto = Integer.valueOf(hora1.substring(3,5));
        fecha1 = Calendar.getInstance();
        fecha2 = Calendar.getInstance();
        fecha1.set(fecha1.get(Calendar.YEAR), fecha1.get(Calendar.MONTH), fecha1.get(Calendar.DAY_OF_MONTH), Integer.valueOf(hora), Integer.valueOf(minuto));
        hora = Integer.valueOf(hora2.substring(0,2));
        minuto = Integer.valueOf(hora2.substring(3,5));
        return (int) ((fecha2.getTime().getTime() - fecha1.getTime().getTime())/1000);
    }


    public String duracionAfecha(String inicio, int duracion) {

        Calendar fecha;
        int hora;
        int minuto;
        String horaFinal;

        fecha = Calendar.getInstance();
        hora = Integer.valueOf(inicio.substring(0,2));
        minuto = Integer.valueOf(inicio.substring(3,5));
        fecha.set(fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH), hora, minuto);
        fecha.setTimeInMillis(fecha.getTimeInMillis() + duracion * 1000);

        horaFinal = formatearHora(fecha.get(Calendar.HOUR_OF_DAY), fecha.get(Calendar.MINUTE));
        return horaFinal;


    }

    public int fechaADuracion(String inicio) {

        Calendar fechaInicio, fechaFin;
        int hora;
        int minuto;
        long milisFin, milisInicio;
        hora = Integer.valueOf(inicio.substring(0,2));
        minuto = Integer.valueOf(inicio.substring(3,5));
        fechaFin = Calendar.getInstance();
        fechaInicio = Calendar.getInstance();
        fechaInicio.set(fechaInicio.get(Calendar.YEAR), fechaInicio.get(Calendar.MONTH), fechaInicio.get(Calendar.DAY_OF_MONTH), hora, minuto);
        milisInicio = fechaInicio.getTimeInMillis()/1000;
        milisFin = fechaFin.getTimeInMillis()/1000;


        return (int) (milisFin - milisInicio);



    }

    public String formatearHora(int hora, int minuto) {

        Formatter formato;
        formato = new Formatter();
        return formato.format("%02d:%02d", hora, minuto).toString();

    }


    public String ponerFechaDeHoy() {
        Calendar calendario;
        String hoy;
        long tiempo;

        calendario = Calendar.getInstance();
        tiempo = calendario.getTime().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        hoy = formatter.format(tiempo);
        return hoy;


    }

    public int restarFechas(int hora1, int minuto1, int hora2, int minuto2) {

        Calendar fecha1;
        Calendar fecha2;

        fecha1 = Calendar.getInstance();
        fecha2 = Calendar.getInstance();
        fecha1.set(fecha1.get(Calendar.YEAR), fecha1.get(Calendar.MONTH), fecha1.get(Calendar.DAY_OF_MONTH), hora1, minuto1);
        fecha2.set(fecha1.get(Calendar.YEAR), fecha1.get(Calendar.MONTH), fecha1.get(Calendar.DAY_OF_MONTH), hora2, minuto2);
        return (int) ((fecha2.getTime().getTime() - fecha1.getTime().getTime())/1000);
    }

    public Boolean getMascaraDia(int dia, int mask) {

        int mascara = 1;
        int i;
        int resultado = 0;

        if (dia == 0) {
            resultado = mask & 1;
            if (resultado == 1) return true;
            else return false;
        }
        if (dia == 1) {
            resultado = mask & 2;
            if (resultado == 2) return true;
            else return false;
        }

        if (dia == 2) {
            resultado = mask & 4;
            if (resultado == 4) return true;
            else return false;
        }
        if (dia == 3) {
            resultado = mask & 8;
            if (resultado == 8) return true;
            else return false;
        }
        if (dia == 4) {
            resultado = mask & 16;
            if (resultado == 16) return true;
            else return false;
        }
        if (dia == 5) {
            resultado = mask & 32;
            if (resultado == 32) return true;
            else return false;
        }
        if (dia == 6) {
            resultado = mask & 64;
            if (resultado == 64) return true;
            else return false;
        }

        return false;
    }



}
