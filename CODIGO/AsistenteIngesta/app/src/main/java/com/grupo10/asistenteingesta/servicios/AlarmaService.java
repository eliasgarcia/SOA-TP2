package com.grupo10.asistenteingesta.servicios;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.grupo10.asistenteingesta.broadcast.AlarmaReceiver;
import com.grupo10.asistenteingesta.util.CodigoIngesta;
import com.grupo10.asistenteingesta.util.Constante;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    /*
        REQUEST_CODE es el id de la alarma.
        RTC_WAKEUP:"Activa" el dispositivo a fin de activar el intent pendiente a la "hora especificada".
    */

    public void crearAlarma(Constante constante, String email, int frecuencia){
        int REQUEST_CODE = Constante.MEDICAMENTO.equals(constante)? CodigoIngesta.MEDICAMENTO.getValue():CodigoIngesta.BEBIDA.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(GregorianCalendar.MINUTE, frecuencia);
        Intent intent = new Intent(context, AlarmaReceiver.class);
        intent.putExtra(Constante.EMAIL.name(), email);
        intent.putExtra(Constante.TIPO_INGESTA.name(), constante.name());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * frecuencia, pendingIntent);
    }
}
