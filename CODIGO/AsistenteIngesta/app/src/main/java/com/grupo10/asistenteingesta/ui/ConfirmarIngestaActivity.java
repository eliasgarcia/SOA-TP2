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
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.util.Constante;
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
    private Vibrator vibrator;
    private String tipoIngesta;

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
        tipoIngesta = bundle.getString(Constante.TIPO_INGESTA.name());
        if(Constante.MEDICAMENTO.name().equals(tipoIngesta)){
            ingesta = persistenciaLocal.getMedicamento();
        }else{
            ingesta = persistenciaLocal.getBebida();
        }
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        cargarLabels();
        sonarAlarma();
    }

    private void sonarAlarma(){
        if(Constante.MEDICAMENTO.name().equals(tipoIngesta)){
            mediaPlayer = MediaPlayer.create(this, R.raw.sonido_sirena);
        }else{
            mediaPlayer = MediaPlayer.create(this, R.raw.sonido_agua);
        }
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 1000};
        VibrationEffect vibrationEffect1 = VibrationEffect.createWaveform(pattern,0);
        mediaPlayer.start();
        vibrator.vibrate(vibrationEffect1);
    }

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Intent intent = new Intent(ConfirmarIngestaActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            synchronized (this) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = sensorEvent.values[0];
                    float y = sensorEvent.values[1];
                    float z = sensorEvent.values[2];
                    if (x > UMBRAL_SHAKE || y > UMBRAL_SHAKE || z > UMBRAL_SHAKE) {
                        Toast.makeText(ConfirmarIngestaActivity.this, "SHAKEEEEE", Toast.LENGTH_LONG).show();
                        sensorManager.unregisterListener(sensorListener);
                        confirmaIngesta();
                        startActivity(intent);
                        finish();
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            switch (v.getId())
            {
                case R.id.btnConfirmaIngesta:
                    bloquearComponenterPantalla();
                    confirmaIngesta();
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btnRechazaIngesta:
                    bloquearComponenterPantalla();
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
        mediaPlayer.stop();
        vibrator.cancel();
        guardarEstadoIngesta(Boolean.TRUE);
    }

    private void rechazaIngesta(){
        mediaPlayer.stop();
        vibrator.cancel();
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

    private void bloquearComponenterPantalla(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
