package com.grupo10.asistenteingesta.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.servicios.InternetStatus;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;
import com.grupo10.asistenteingesta.ui.MainActivity;
import com.grupo10.asistenteingesta.util.Constante;

import java.util.Calendar;

public class MinutosRestantesService extends Service {
    private final static String TAG = "SVC_MINUTOS_RESTANTES";
    private Ingesta medicamento;
    private Ingesta bebida;
    private PersistenciaLocal persistenciaLocal;
    private ServiceThread serviceThread;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate(){
        persistenciaLocal = persistenciaLocal.getInstancia(this);
        medicamento = persistenciaLocal.getMedicamento();
        bebida = persistenciaLocal.getBebida();
        Log.i(TAG,"Ejecuto onCreate");
    }

    public void onDestroy(){
        serviceThread.interrupt();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"Solicita creacion de hilo");
        serviceThread = new ServiceThread();
        serviceThread.start();
        return Service.START_REDELIVER_INTENT;
    }

    public class ServiceThread extends Thread{
        public void run(){
            Log.i(TAG,"Crea hilo");
            actualizarMinutosRestantes();
        }
    }

    public void actualizarMinutosRestantes(){
        //Cada 10 segundos se actualizan los minutos restantes
        Intent intent;
        try{
            Long diferenciaMilisMedicamento, diferenciaMilisBebida;
            Integer tiempoRestanteMedicamento, tiempoRestanteBebida;
            while(true){
                Calendar horaActual = Calendar.getInstance();
                intent = new Intent(Constante.RECEIVER_TIEMPO_RESTANTE.name());
                if(medicamento != null){
                    //Sumo uno porque redondea para baja. Es una mala experiencia ver
                    //Sonará en 0 minutos
                    diferenciaMilisMedicamento = medicamento.getProxima().getTimeInMillis() - horaActual.getTimeInMillis();
                    tiempoRestanteMedicamento = Math.toIntExact((diferenciaMilisMedicamento / 1000) / 60)+1;
                    intent.putExtra(Constante.TIEMPO_RESTANTE_MEDICAMENTO.name(),tiempoRestanteMedicamento);
                }
                if(bebida != null){
                    diferenciaMilisBebida = bebida.getProxima().getTimeInMillis() - horaActual.getTimeInMillis();
                    tiempoRestanteBebida = Math.toIntExact((diferenciaMilisBebida / 1000) / 60)+1;
                    intent.putExtra(Constante.TIEMPO_RESTANTE_BEBIDA.name(),tiempoRestanteBebida);
                }
                sendBroadcast(intent);
                Thread.sleep(10000);
                medicamento = persistenciaLocal.getMedicamento();
                bebida = persistenciaLocal.getBebida();
            }

        }catch (InterruptedException e){
            Log.i(TAG,"Thread INTERRUMPIDO");
        }
    }


}
