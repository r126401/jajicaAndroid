package net.jajica.myhomeiot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatTextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;

public class MyHomeIotTools {

    private static final String TAG = "MyHomeIotTools";
    Context context;

    public MyHomeIotTools(Context context) {
        this.context = context;
    }

    public MyHomeIotTools() {
        context = null;
    }

    public void showKeyboard(int action) {

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(action, 0);


    }



    protected double roundData(double valor, int decimales) {

        BigDecimal bd = new BigDecimal(valor);
        bd = bd.setScale(decimales, RoundingMode.HALF_UP);

        return bd.doubleValue();

    }

    public String formatHour(int hora, int minuto) {

        Formatter formato;
        formato = new Formatter();
        return formato.format("%02d:%02d", hora, minuto).toString();

    }

    public String formatData(int data) {

        Formatter formatter;
        formatter = new Formatter();
        return formatter.format("%02d", data).toString();

    }


    public String convertDuration(int hora, int minuto, int duracion) {

        Calendar fecha;
        String horafinal;
        if (duracion == 0) {
            horafinal = "siempre";
        } else {
            fecha = Calendar.getInstance();
            fecha.set(Calendar.HOUR_OF_DAY, hora);
            fecha.set(Calendar.MINUTE, minuto);
            fecha.set(Calendar.SECOND, duracion);


            horafinal = formatHour(fecha.get(Calendar.HOUR_OF_DAY), fecha.get(Calendar.MINUTE));

        }
        return horafinal;


    }

    public String extractMinuteForConvertDuration(String dat) {

        String minute;
        int index;
        index = dat.indexOf(":") + 1;
        minute = dat.substring(index);
        return minute;

    }

    public String extractHourForConvertDuration(String dat ) {

        String hour;
        int index;
        index = dat.indexOf(":");
        hour = dat.substring(0,dat.indexOf(":"));

        return hour;

    }

    public int diffDate(int hour1, int min1, int hour2, int min2) {

        Calendar fecha1;
        Calendar fecha2;

        fecha1 = Calendar.getInstance();
        fecha2 = Calendar.getInstance();
        fecha1.set(fecha1.get(Calendar.YEAR), fecha1.get(Calendar.MONTH), fecha1.get(Calendar.DAY_OF_MONTH), hour1, min1);
        if ((hour2 == 0) && (min2 == 0)) {
            fecha2.set(fecha1.get(Calendar.YEAR), fecha1.get(Calendar.MONTH), 1 + fecha1.get(Calendar.DAY_OF_MONTH), hour2, min2);
        } else {
            fecha2.set(fecha1.get(Calendar.YEAR), fecha1.get(Calendar.MONTH), fecha1.get(Calendar.DAY_OF_MONTH), hour2, min2);
        }

        return (int) ((fecha2.getTime().getTime() - fecha1.getTime().getTime())/1000);
    }


    public void updateWeekDay(AppCompatTextView day, boolean activado) {

        if (activado == false) {
            day.setBackgroundResource(R.drawable.round_corners_deactive_days);
            day.setTag(false);
        } else {
            day.setBackgroundResource(R.drawable.round_corners_active_days);
            day.setTag(true);
        }
    }


    /**
     * Esta funcion recibe los controles de AppCompatTextView, lee el tag y en funcion de ello
     * genera la mascara
     * @param list
     * @return
     */
    public int readMask(ArrayList<AppCompatTextView> list) {

        int i;
        int mask = 0;
        for (i=0;i<list.size();i++) {
            if ((Boolean) list.get(i).getTag()) mask = mask | calculatePower(2, i);
        }
        Log.i(TAG, "Mascara:" + mask);
        return mask;

    }

    /**
     * Esta funcion calcula el resultado de un entero elevado a otro
     * @param num
     * @param pow
     * @return
     */
    private static int calculatePower(int num, int pow) {

        if (pow == 0) {
            return 1;
        } else {
            return num * calculatePower(num, pow - 1);
        }
    }


    public void  mask2controls(ArrayList<AppCompatTextView> list, Boolean[] activeDays) {

        if (list == null) list = new ArrayList<>();


        int i;
        for (i=0;i<list.size();i++) {

            if (activeDays[i]) {
                list.get(i).setTag(true);
                list.get(i).setBackgroundResource(R.drawable.round_corners_active_days);
            } else {
                list.get(i).setTag(false);
                list.get(i).setBackgroundResource(R.drawable.round_corners_deactive_days);
            }
        }

    }


    public int getCurrentHourMinute(Boolean hour) {
        DateFormat formatter;
        Calendar calendar;
        long time;
        String date;

        calendar = Calendar.getInstance();
        time = calendar.getTime().getTime();
        if (hour) {
            formatter = new SimpleDateFormat("HH");
        } else {
            formatter = new SimpleDateFormat("mm");
        }
        date = formatter.format(time);
        return Integer.parseInt(date);

    }


    public int currentHour() {
        Calendar calendario;
        String hoy;
        long tiempo;
        int hora;

        calendario = Calendar.getInstance();
        tiempo = calendario.getTime().getTime();
        DateFormat formatter = new SimpleDateFormat("HH");
        hoy = formatter.format(tiempo);
        hora = Integer.parseInt(hoy);
        return hora;
    }

    public String duration2Date(String inicio, int duracion) {

        Calendar fecha;
        int hora;
        int minuto;
        String horaFinal;

        fecha = Calendar.getInstance();
        hora = Integer.valueOf(inicio.substring(0,2));
        minuto = Integer.valueOf(inicio.substring(3,5));
        fecha.set(fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH), hora, minuto);
        fecha.setTimeInMillis(fecha.getTimeInMillis() + duracion * 1000);

        horaFinal = formatHour(fecha.get(Calendar.HOUR_OF_DAY), fecha.get(Calendar.MINUTE));
        return horaFinal;


    }


    public int currentDate2DurationSchedule(String inicio) {

        Calendar fechaInicio, fechaFin;
        int hora;
        int minuto;
        long milisFin, milisInicio;
        hora = Integer.parseInt(inicio.substring(0,2));
        minuto = Integer.parseInt(inicio.substring(3,5));
        fechaFin = Calendar.getInstance();
        fechaInicio = Calendar.getInstance();
        fechaInicio.set(fechaInicio.get(Calendar.YEAR), fechaInicio.get(Calendar.MONTH), fechaInicio.get(Calendar.DAY_OF_MONTH), hora, minuto);
        milisInicio = fechaInicio.getTimeInMillis()/1000;
        milisFin = fechaFin.getTimeInMillis()/1000;


        return (int) (milisFin - milisInicio);



    }

    public void errorMessage(int icon, int title, int message, Context context) {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setIcon(icon);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog alert;
        alert = builder.create();
        alert.setCancelable(false);
        builder.setCancelable(false);
        alert.show();

    }


}
