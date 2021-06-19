package com.grupo10.asistenteingesta.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.constantes.Constante;
import com.grupo10.asistenteingesta.modelo.EstadoIngesta;
import com.grupo10.asistenteingesta.modelo.Historial;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;

import java.util.ArrayList;
import java.util.List;

public class ConfirmarIngestaActivity extends AppCompatActivity {

    private static final int UMBRAL_SHAKE = 30;
    private TextView lblTipoIngestaConfirmar;
    private TextView lblIngestaConfirmaNombre;
    private TextView lblIngestaConfirmaFecuencia;
    private Button btnConfirmarIngesta;
    private Button btnRechazaIngesta;
    private static PersistenciaLocal persistenciaLocal;
    private Bundle bundle;
    private Ingesta ingesta;
    private SensorManager sensorManager;
    private MediaPlayer mediaPlayer;

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
        if(Constante.MEDICAMENTO.name().equals(bundle.getString(Constante.TIPO_INGESTA.name()))){
            ingesta = persistenciaLocal.getMedicamento();
        }else{
            ingesta = persistenciaLocal.getBebida();
        }
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        cargarLabels();
        mediaPlayer = MediaPlayer.create(this, R.raw.alarma);
        mediaPlayer.start();
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //Intent intent = new Intent(ConfirmarIngestaActivity.this,MainActivity.class);
            synchronized (this) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = sensorEvent.values[0];
                    float y = sensorEvent.values[1];
                    float z = sensorEvent.values[2];
                    if (x > UMBRAL_SHAKE || y > UMBRAL_SHAKE || z > UMBRAL_SHAKE) {
                        Toast.makeText(ConfirmarIngestaActivity.this, "SHAKEEEEE", Toast.LENGTH_LONG).show();
                        confirmaIngesta();
                        //startActivity(intent);
                        finish();//o unregister
                    }
                }
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
        lblTipoIngestaConfirmar.setText(bundle.getString(Constante.TIPO_INGESTA.name()));
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
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorListener);
        super.onPause();
    }
}
