package jajica;


import org.json.JSONObject;




public class Main {
    public static void main(String[] args) {
        

        Context context;
        System.out.println("Hello world!");
        ConjuntoDispositivosIot conf;
        OPERACION_CONFIGURACION_DISPOSITIVOS op;
        conf = new ConjuntoDispositivosIot();
        op = conf.cargarDispositivos();
        int i;
        JSONObject lista;
        if (conf.getNumeroDispositivos() > 0) {
            for (i=0;i< conf.getDispositivosIot().size();i++) {

                lista = conf.getDispositivosIot().get(i).dispositivo2Json();
                System.out.println(lista.toString());
            }


            i = conf.buscarDispositivoPorNombre("pepe");
            System.out.println("El valor encontrado es :" + i);
            i = conf.buscarDispositivoPorId("556632554a");
            System.out.println("El valor encontrado es :" + i);
        }


        Ficheros file;
        file = new Ficheros();
        ESTADO_FICHEROS estado;
        estado = file.leerFichero("/home/t126401/jajicaAndroid/libIot/out/artifacts/libIot_jar/datosDispositivos.conf.bak");

        if (estado == ESTADO_FICHEROS.FICHERO_OK) {

            System.out.println(file.getTextoFichero());
        }



        if ((op = conf.insertarDispositivoDesdeTexto(file.getTextoFichero())) == OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO) {
            System.out.println("Insertado con éxito");
        } else {
            System.out.println("Error al insertar");
        }

        estado = file.leerFichero("/home/t126401/jajicaAndroid/libIot/out/artifacts/libIot_jar/datosDispositivos.conf.bak1");

        if (estado == ESTADO_FICHEROS.FICHERO_OK) {

            System.out.println(file.getTextoFichero());
        }


        if ((op = conf.insertarDispositivoDesdeTexto(file.getTextoFichero())) == OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO) {
            System.out.println("Insertado con éxito");
        } else {
            System.out.println("Error al insertar");
        }
        estado = file.leerFichero("/home/t126401/jajicaAndroid/libIot/out/artifacts/libIot_jar/datosDispositivos.conf.bak2");

        if (estado == ESTADO_FICHEROS.FICHERO_OK) {

            System.out.println(file.getTextoFichero());
        }


        JSONObject objeto;
        objeto = new JSONObject(file.getTextoFichero());
        if ((op = conf.insertarDispositivoDesdeJson(objeto)) == OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO) {
            System.out.println("Insertado con éxito");
        } else {
            System.out.println("Error al insertar");
        }

        estado = file.leerFichero("/home/t126401/jajicaAndroid/libIot/out/artifacts/libIot_jar/datosDispositivos.conf.bak1");

        if (estado == ESTADO_FICHEROS.FICHERO_OK) {

            System.out.println(file.getTextoFichero());
        }

        objeto = new JSONObject(file.getTextoFichero());
        if ((op = conf.insertarDispositivoDesdeJson(objeto)) == OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO) {
            System.out.println("Insertado con éxito");
        } else {
            System.out.println("Error al insertar");
        }
        estado = file.leerFichero("/home/t126401/jajicaAndroid/libIot/out/artifacts/libIot_jar/datosDispositivos.conf.bak2");

        if (estado == ESTADO_FICHEROS.FICHERO_OK) {

            System.out.println(file.getTextoFichero());
        }

        objeto = new JSONObject(file.getTextoFichero());

        if ((op = conf.insertarDispositivoDesdeJson(objeto)) == OPERACION_CONFIGURACION_DISPOSITIVOS.DISPOSITIVO_INSERTADO) {
            System.out.println("Insertado con éxito");
        } else {
            System.out.println("Error al insertar");
        }



        //op = conf.eliminarDispositivoPorId("84CCA85E8060");
        //op = conf.eliminarDispositivoPorId("84DCA85E8060");
        //op = conf.eliminarDispositivoPorId("556632554a");
        op = conf.eliminarDispositivoPorNombre("pepe");
        op = conf.eliminarDispositivoPorNombre("fdgfd");
        op = conf.eliminarDispositivoPorNombre("pepeillo");






    }
}