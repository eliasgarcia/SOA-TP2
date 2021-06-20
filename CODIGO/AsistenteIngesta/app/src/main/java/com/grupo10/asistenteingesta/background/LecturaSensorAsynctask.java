package com.grupo10.asistenteingesta.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.modelo.EstadoIngesta;
import com.grupo10.asistenteingesta.modelo.Historial;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;
import com.grupo10.asistenteingesta.util.Constante;
import com.grupo10.asistenteingesta.ui.ConfirmarIngestaActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;


public class LecturaSensorAsynctask extends AsyncTask<Void, Void, Void> {
    private SensorManager sensorManager;
    private Float lux;
    //private Float proximidad;
    private Context mContext;
    private String email;
    private String ingesta;
    private NotificationManager notificationManager;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    public static int NOTIFICATION_ID = 1;
    private static PersistenciaLocal persistenciaLocal;


    public LecturaSensorAsynctask(Context context, String email, String ingesta) {
        super();
        this.mContext = context;
        this.email = email;
        this.ingesta = ingesta;
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            synchronized (this) {
                float valor = sensorEvent.values[0];
                if (lux != null /*&& proximidad != null*/) {
                    sensorManager.unregisterListener(sensorListener);
                }
                if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
                    lux = valor;
                }
               /* if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    proximidad = valor;
                }
                */
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected Void doInBackground(Void... voids) {
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        return null;
    }

    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (lux > 1 /*&& proximidad < 2*/) {
            Log.i("SensorLuzAsync", "Sonar Alarma");
            Intent i = new Intent();
            i.setClass(mContext, ConfirmarIngestaActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Constante.EMAIL.name(), email);
            i.putExtra(Constante.TIPO_INGESTA.name(), ingesta);
            mContext.startActivity(i);
        } else {
            //Log.i("SensorLuzService", "Es de noche. Luz:" + lux); //+ ",prox:" + proximidad);
            guardarEstadoIngesta();
            notificarAutoRechazoAlarma();
            Toast.makeText(mContext, "Es de noche. Luz:" + lux/* + ",prox:" + proximidad*/, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        persistenciaLocal = persistenciaLocal.getInstancia(mContext);

    }

    private void notificarAutoRechazoAlarma(){
        notificationManager = (NotificationManager) mContext.getSystemService(Context. NOTIFICATION_SERVICE ) ;
        NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , NotificationManager. IMPORTANCE_HIGH) ;
        notificationManager.createNotificationChannel(notificationChannel) ;
        notificationManager.notify(NOTIFICATION_ID , getNotification("La alarma no sonará debido a que es de noche.",mContext)) ;
    }

    private Notification getNotification (String content, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( context, default_notification_channel_id ) ;
        builder.setContentTitle( "Asistente de Ingestas" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }

    private void guardarEstadoIngesta(){
        Historial historial = persistenciaLocal.getHistorial();
        List<EstadoIngesta> ingestas;
        EstadoIngesta estadoIngesta;
        historial = historial == null?new Historial(): historial;
        if(historial.getIngestas()!=null){
            ingestas = historial.getIngestas();
        }else{
            ingestas = new ArrayList<>();
        }
        estadoIngesta = new EstadoIngesta(persistenciaLocal.getBebida(), Boolean.FALSE, Calendar.getInstance());
        ingestas.add(estadoIngesta);
        historial.setIngestas(ingestas);
        persistenciaLocal.setHistorial(historial);
    }
}
