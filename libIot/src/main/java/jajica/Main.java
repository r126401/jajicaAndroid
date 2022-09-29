package jajica;


import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        

        System.out.println("Hello world!");
        ConjuntoDispositivosIot conf;
        conf = new ConjuntoDispositivosIot();
        conf.cargarDispositivos();
        int i;
        JSONObject lista;
        for (i=0;i< conf.getDispositivosIot().size();i++) {

            lista = conf.getDispositivosIot().get(i).dispositivo2Json();
            System.out.println(lista.toString());




        }


        i = conf.buscarDispositivoPorNombre("pepe");
        System.out.println("El valor encontrado es :" + i);
        i = conf.buscarDispositivoPorId("556632554a");
        System.out.println("El valor encontrado es :" + i);


    }
}