package com.grupo10.asistenteingesta.background;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.grupo10.asistenteingesta.util.Constante;
import com.grupo10.asistenteingesta.ui.ConfirmarIngestaActivity;

import static android.content.Context.SENSOR_SERVICE;


public class LecturaSensorAsynctask extends AsyncTask<Void, Void, Void> {
    private SensorManager sensorManager;
    private Float lux;
    private Float proximidad;
    private Context mContext;
    private String email;
    private String ingesta;


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
                if (lux != null && proximidad != null) {
                    sensorManager.unregisterListener(sensorListener);
                }
                if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
                    lux = valor;
                }
                if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    proximidad = valor;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected Void doInBackground(Void... voids) {
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        return null;
    }

    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //asumno que tiene el celular en pantalon.
        if (lux < 30 && proximidad < 2) {
            Log.i("SensorLuzAsync", "Sonar Alarma");
            Intent i = new Intent();
            i.setClass(mContext, ConfirmarIngestaActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Constante.EMAIL.name(), email);
            i.putExtra(Constante.TIPO_INGESTA.name(), ingesta);
            mContext.startActivity(i);
        } else {
            //simplemente tirar una notificacion y guardar la ingesta con estado false
            Log.i("SensorLuzService", "Es de noche. Luz:" + lux + ",prox:" + proximidad);
            Toast.makeText(mContext, "Es de noche. Luz:" + lux + ",prox:" + proximidad, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
    }
}
