package net.jajica.myhomeiot;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Formatter;

public class MyHomeIotTools {

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

    public String formatTemperature(String dat) {

        DecimalFormat decimalFormat = new DecimalFormat("##.#");
        String dato;
        dato = String.valueOf(decimalFormat.format(dat));

        dat = dato.substring(0,2);
        if (dato.length() > 2) {
            dat += ".";
            dat += dato.substring(3);
        } else {
            dat += ".0";
        }

        return dato;

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


}
