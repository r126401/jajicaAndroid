package net.jajica.myhomeiot;




import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Random;

public class Notifications {

    private String channelName;
    private String channelId;
    private final int priority;
    private NotificationManager notifier;
    private final String description;
    Context context;
    NotificationCompat.Builder creator;


    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setPriority(int priority) {

    }

    public String getChannelName() {
        return this.channelName;
    }

    public String getChannelId() {
        return this.channelId;
    }

    public int getPriority() {
        return this.priority;
    }




    public Notifications(String channelName, String channelId, String description, Context context) {

        this.channelName = channelName;
        this.channelId = channelId;
        this.priority = NotificationManager.IMPORTANCE_HIGH;
        this.context = context;
        this.description = description;


    }




    public void createNotification() {

        notifier = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);



        creator = new NotificationCompat.Builder(context, channelId);
        // Si nuestro dispositivo tiene Android 8 (API 26, Oreo) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importancia);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE); // Esto no lo soportan todos los dispositivos
            notificationChannel.enableVibration(true);
            notifier.createNotificationChannel(notificationChannel);
            Log.i(getClass().toString(), "hola");



        }
    }

    public void sendNotification(String titulo, String mensaje, int iconoGrande, int iconoSmall) {

        int idNotificacion;

        Bitmap iconoNotifica = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_info);

        creator.setSmallIcon(iconoSmall);
        creator.setLargeIcon(iconoNotifica);
        creator.setContentTitle(titulo);
        creator.setContentText(mensaje);
        creator.setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje));
        creator.setChannelId(channelId);
        Random random;
        random = new Random();
        idNotificacion = random.nextInt(1000);
        notifier.notify(idNotificacion, creator.build());
    }




}
