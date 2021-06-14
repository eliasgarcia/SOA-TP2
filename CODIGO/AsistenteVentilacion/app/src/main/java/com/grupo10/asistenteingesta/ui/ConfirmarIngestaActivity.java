package com.grupo10.asistenteingesta.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.modelo.EstadoIngesta;
import com.grupo10.asistenteingesta.modelo.Historial;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ConfirmarIngestaActivity extends AppCompatActivity {

    private static final int SHAKE_THRESHOLD = 800;
    private TextView lblTipoIngestaConfirmar;
    private TextView lblIngestaConfirmaNombre;
    private TextView lblIngestaConfirmaFecuencia;
    private Button btnConfirmarIngesta;
    private Button btnRechazaIngesta;
    private ProgressBar progressBar;
    private static PersistenciaLocal persistenciaLocal;
    private Bundle bundle;
    private Ingesta ingesta;
    private SensorManager sensorManager;
    private float last_x = 0;
    private float last_y = 0;
    private float last_z = 0;
    private long lastUpdate = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_ingesta);

        lblTipoIngestaConfirmar = findViewById(R.id.lblTipoIngestaConfirmar);
        lblIngestaConfirmaNombre = findViewById(R.id.lblIngestaNombreConfirmar);
        lblIngestaConfirmaFecuencia = findViewById(R.id.lblIngestaFrecuenciaConfirmar);
        btnConfirmarIngesta = findViewById(R.id.btnConfirmaIngesta);
        btnRechazaIngesta = findViewById(R.id.btnRechazaIngesta);;
        btnConfirmarIngesta.setOnClickListener(botonesListeners);
        btnRechazaIngesta.setOnClickListener(botonesListeners);
        persistenciaLocal = persistenciaLocal.getInstancia(this);
        bundle = getIntent().getExtras();
        if("MEDICAMENTO".equals(bundle.getString("INGESTA"))){
            ingesta = persistenciaLocal.getMedicamento();
        }else{
            ingesta = persistenciaLocal.getBebida();
        }
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        cargarLabels();
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //Intent intent = new Intent(ConfirmarIngestaActivity.this,MainActivity.class);


            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    Toast.makeText(ConfirmarIngestaActivity.this, "SHAKEEEEE", Toast.LENGTH_LONG).show();
                    confirmaIngesta();
                    //startActivity(intent);
                    finish();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v) {
            Intent intent = new Intent(ConfirmarIngestaActivity.this,MainActivity.class);
            switch (v.getId())
            {
                case R.id.btnConfirmaIngesta:
                    confirmaIngesta();
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btnRechazaIngesta:
                    rechazaIngesta();
                    startActivity(intent);
                    finish();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();

            }
        }
    };

    private void cargarLabels(){
        lblTipoIngestaConfirmar.setText(bundle.getString("INGESTA"));
        lblIngestaConfirmaNombre.setText("Nombre: " + ingesta.getNombre());
        lblIngestaConfirmaFecuencia.setText("Frecuencia: " + ingesta.getFrecuencia());
    }


    private void  confirmaIngesta(){
        guardarEstadoIngesta(Boolean.TRUE);
    }

    private void rechazaIngesta(){
        guardarEstadoIngesta(Boolean.FALSE);
    }

    private void guardarEstadoIngesta(Boolean estado){
        Historial historial = persistenciaLocal.getHistorial();
        List<EstadoIngesta> ingestas;
        EstadoIngesta estadoIngesta;
        historial = historial == null?new Historial(): historial;
        if(historial.getIngestas()!=null){
            ingestas = historial.getIngestas();
        }else{
            ingestas = new ArrayList<>();
        }
        estadoIngesta = new EstadoIngesta(ingesta, estado);
        ingestas.add(estadoIngesta);
        historial.setIngestas(ingestas);
        persistenciaLocal.setHistorial(historial);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorListener);
        super.onPause();
    }
}
