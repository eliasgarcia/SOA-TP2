package com.grupo10.asistenteingesta.broadcast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;
import com.grupo10.asistenteingesta.ui.ConfirmarIngestaActivity;
import com.grupo10.asistenteingesta.background.LecturaSensorAsynctask;

public class AlarmaReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    public static int NOTIFICATION_ID = 1 ;
    private static PersistenciaLocal persistenciaLocal;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALARMAAAAAAAAAAAAa", Toast.LENGTH_LONG).show();
        Bundle bundle = intent.getExtras();
        Intent i = new Intent(context,ConfirmarIngestaActivity.class);
        i.putExtra("EMAIL",bundle.getString("EMAIL"));
        i.putExtra("INGESTA",bundle.getString("INGESTA"));

        notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE ) ;
        NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , NotificationManager. IMPORTANCE_HIGH) ;
        //assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel) ;
        notificationManager.notify(NOTIFICATION_ID , getNotification("Alarma notiff!",context)) ;

        persistenciaLocal = PersistenciaLocal.getInstancia(context);
        //todo: cambiar por beebida
        if("MEDICAMENTO".equals(bundle.getString("INGESTA"))){
            //Toast.makeText(context, "NO se muestra alarma porque es de noche", Toast.LENGTH_LONG).show();
            /*Intent intentbebida = new Intent(context, SensorLuzService.class);
            intentbebida.putExtra("EMAIL",bundle.getString("EMAIL"));
            intentbebida.putExtra("INGESTA",bundle.getString("INGESTA"));
            context.startService(intentbebida);
            */ //Hasta aca teniamos con Service..aunque tenia errores random

            //Desde aca es con asyncTask
            LecturaSensorAsynctask sensorAsyncTask = new LecturaSensorAsynctask(context,bundle.getString("EMAIL"),bundle.getString("INGESTA"));
            sensorAsyncTask.execute();

        }else{
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    private Notification getNotification (String content, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( context, default_notification_channel_id ) ;
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }
}
