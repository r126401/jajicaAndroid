package net.jajica.libiot;

import java.io.Serializable;
import java.util.Formatter;

public class IotUtil implements Serializable {

    private String formatearHora(int hora, int minuto) {

        Formatter formato;
        formato = new Formatter();
        return formato.format("%02d:%02d", hora, minuto).toString();

    }

}
