package com.example.controliot;




import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Random;

public class Notificaciones  {

    private String nombreCanal;
    private String idCanal;
    private int importancia;
    private NotificationChannel canalNotificacion;
    private NotificationManager notificador;
    private String descripcion;
    Context contexto;
    NotificationCompat.Builder creador;


    public void setNombreCanal(String nombreCanal) {
        this.nombreCanal = nombreCanal;
    }

    public void setIdCanal(String idCanal) {
        this.idCanal = idCanal;
    }

    public void setImportancia(int importancia) {

    }

    public String getNombreCanal() {
        return this.nombreCanal;
    }

    public String getIdCanal() {
        return this.idCanal;
    }

    public int getImportancia() {
        return this.importancia;
    }




    public Notificaciones(String nombreCanal, String idCanal, String descripcion, Context contexto) {

        this.nombreCanal = nombreCanal;
        this.idCanal = idCanal;
        this.importancia = NotificationManager.IMPORTANCE_HIGH;
        this.contexto = contexto;
        this.descripcion = descripcion;


    }




    public void crearNotificacion() {

        notificador = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);



        creador = new NotificationCompat.Builder(contexto, idCanal);
        // Si nuestro dispositivo tiene Android 8 (API 26, Oreo) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            canalNotificacion = new NotificationChannel(idCanal, nombreCanal, importancia);
            canalNotificacion.setDescription(descripcion);
            canalNotificacion.enableLights(true);
            canalNotificacion.setLightColor(Color.BLUE); // Esto no lo soportan todos los dispositivos
            canalNotificacion.enableVibration(true);
            notificador.createNotificationChannel(canalNotificacion);
            Log.i(getClass().toString(), "hola");



        }
    }

    public void enviarNotificacion(String titulo, String mensaje, int iconoGrande, int iconoSmall) {

        int idNotificacion;

        Bitmap iconoNotifica = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.ic_info);

        creador.setSmallIcon(iconoSmall);
        creador.setLargeIcon(iconoNotifica);
        creador.setContentTitle(titulo);
        creador.setContentText(mensaje);
        creador.setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje));
        creador.setChannelId(idCanal);
        Random random;
        random = new Random();
        idNotificacion = random.nextInt(1000);
        notificador.notify(idNotificacion, creador.build());
    }




}
