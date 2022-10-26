package net.jajica.libiot;

import java.util.Formatter;

public class IotUtil {

    private String formatearHora(int hora, int minuto) {

        Formatter formato;
        formato = new Formatter();
        return formato.format("%02d:%02d", hora, minuto).toString();

    }

}
