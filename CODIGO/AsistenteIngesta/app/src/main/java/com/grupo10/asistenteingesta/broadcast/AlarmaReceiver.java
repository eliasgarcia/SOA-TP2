package com.grupo10.asistenteingesta.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.util.Constante;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;
import com.grupo10.asistenteingesta.ui.ConfirmarIngestaActivity;
import com.grupo10.asistenteingesta.background.LecturaSensorAsynctask;

import java.util.Calendar;

public class AlarmaReceiver extends BroadcastReceiver {
    private final static String TAG = "BR_ALARMA";
    private static PersistenciaLocal persistenciaLocal;
    private String tipoIngesta;
    private String email;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"OnReceive");
        Bundle bundle = intent.getExtras();
        email = bundle.getString(Constante.EMAIL.name());
        tipoIngesta = bundle.getString(Constante.TIPO_INGESTA.name());
        persistenciaLocal = PersistenciaLocal.getInstancia(context);
        if(Constante.BEBIDA.name().equals(bundle.getString(Constante.TIPO_INGESTA.name()))){
            LecturaSensorAsynctask sensorAsyncTask = new LecturaSensorAsynctask(context,email,tipoIngesta);
            sensorAsyncTask.execute();
        }else{
            Ingesta medicamento = persistenciaLocal.getMedicamento();
            Calendar proxima = Calendar.getInstance();
            proxima.add(Calendar.MINUTE,medicamento.getDistancia());
            medicamento.setProxima(proxima);
            persistenciaLocal.setMedicamento(medicamento);

            Intent i = new Intent(context,ConfirmarIngestaActivity.class);
            i.putExtra(Constante.EMAIL.name(),email);
            i.putExtra(Constante.TIPO_INGESTA.name(),tipoIngesta);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
