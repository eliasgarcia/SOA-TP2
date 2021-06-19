package com.grupo10.asistenteingesta.servicios;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.grupo10.asistenteingesta.broadcast.AlarmaReceiver;
import com.grupo10.asistenteingesta.util.CodigoIngesta;
import com.grupo10.asistenteingesta.util.Constante;

public class AlarmaService {

    private static AlarmaService instancia;
    private Context context;
    AlarmManager am;

    private AlarmaService(Context context){
        this.context = context;
    }

    public static AlarmaService getInstance(Context context){
        if(instancia == null){
            instancia = new AlarmaService(context);
        }
        return instancia;
    }

    public void eliminarAlarmaSiExiste(Constante constante){
        Intent intent = new Intent(context, AlarmaReceiver.class);
        int REQUEST_CODE = Constante.MEDICAMENTO.equals(constante)? CodigoIngesta.MEDICAMENTO.getValue():CodigoIngesta.BEBIDA.getValue();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    public void crearAlarma(Constante constante, String email, int frecuencia){
        long tiempoEnMilis = frecuencia*1000;
        int REQUEST_CODE = Constante.MEDICAMENTO.equals(constante)? CodigoIngesta.MEDICAMENTO.getValue():CodigoIngesta.BEBIDA.getValue();

        Intent intent = new Intent(context, AlarmaReceiver.class);
        intent.putExtra(Constante.EMAIL.name(), email);
        intent.putExtra(Constante.TIPO_INGESTA.name(), constante.name());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
                + tiempoEnMilis, pendingIntent);
    }
}
