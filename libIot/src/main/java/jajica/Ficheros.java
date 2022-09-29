/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jajica;

import java.io.*;

/**
 *
 * @author t126401
 * Esta clase gestiona la lectura y escritura en disco de la configuracion de la aplicacion para los dispositivos
 * y cualquier configuracion que fuera necesaria.
 */

/**
 * Enum que gestiona el estado de las operaciones sobre los ficheros.
 */
enum ESTADO_FICHEROS {

    ERROR_CERRAR_FICHERO(-4),
    ERROR_ESCRIBIR_FICHERO(-3),
    ERROR_LEER_FICHERO(-2),
    FICHERO_NO_EXISTE(-1),
    FICHERO_OK(0);


    private int estadoFichero;

    ESTADO_FICHEROS(int estadoFichero) {
        this.estadoFichero = estadoFichero;
    }

    public int getEstadoFichero() {
        return this.estadoFichero;
    }

    public ESTADO_FICHEROS fromId(int id) {

        for (ESTADO_FICHEROS estado : values()) {

            if (estado.getEstadoFichero() == id) return estado;

        }
        return null;

    }
}


public class Ficheros {


    private String textoFichero;

    public String getTextoFichero() {
        return this.textoFichero;
    }

    public Ficheros() {
        textoFichero = null;
    }


    /**
     * Método para leer la configuaracion de la aplicacion.
     * @param fichero Es el nombre del fichero a leer
     * @return Devuelve el estado de la operacion. Es necesario utilizar el metodo de la clase para recuperar el
     * contenido del fichero.
     *
     */
    public ESTADO_FICHEROS leerFichero(String fichero)  {

        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        String texto="";
        ESTADO_FICHEROS estado;

        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File (fichero);
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;
            while((linea=br.readLine())!=null) {
                //System.out.println(linea);
                texto += linea;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return ESTADO_FICHEROS.ERROR_LEER_FICHERO;
        }finally{
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta
            // una excepcion.
            try{
                if( null != fr ){
                    fr.close();
                }
            }catch (Exception e2){
                e2.printStackTrace();
                return ESTADO_FICHEROS.ERROR_CERRAR_FICHERO;
            }
        }
        this.textoFichero = texto;
        return ESTADO_FICHEROS.FICHERO_OK;
    }


    /**
     * Método para escribir la configuracion de los dispositivos.
     * @param nombreFichero Es el nombre del fichero a escribir
     * @return Devuelve el estado de la operacion.
     */
    public ESTADO_FICHEROS escribirFichero(String nombreFichero) {


        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            fichero = new FileWriter(nombreFichero);
            pw = new PrintWriter(fichero);

            for (int i = 0; i < 10; i++)
                pw.println("Linea " + i);

        } catch (Exception e) {
            e.printStackTrace();
            return ESTADO_FICHEROS.ERROR_ESCRIBIR_FICHERO;
        } finally {
            try {
                // Nuevamente aprovechamos el finally para
                // asegurarnos que se cierra el fichero.
                if (null != fichero)
                    fichero.close();
            } catch (Exception e2) {
                e2.printStackTrace();
                return ESTADO_FICHEROS.ERROR_CERRAR_FICHERO;
            }
        }
        return ESTADO_FICHEROS.FICHERO_OK;
    }





}
