package com.grupo10.asistenteingesta.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.grupo10.asistenteingesta.broadcast.AlarmaReceiver;
import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.constantes.Constante;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.modelo.Usuario;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;

public class EditarIngestaMedicamentoActivity extends AppCompatActivity {

    private EditText txtMedicamentoNombre;
    private EditText txtMedicamentoFrecuencia;
    private Button btnGuardarIngesta;
    private Button btnCancelarIngesta;
    private ProgressBar progressBar;
    private static PersistenciaLocal persistenciaLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ingesta_medicamento);
        txtMedicamentoNombre = findViewById(R.id.txtMedicamentoNombreNuevo);
        txtMedicamentoFrecuencia = findViewById(R.id.txtMedicamentoFrecuenciaNuevo);
        btnCancelarIngesta = findViewById(R.id.btnCancelarIngestaMedicamento);
        btnGuardarIngesta = findViewById(R.id.btnGuardarIngestaMedicamento);
        btnCancelarIngesta.setOnClickListener(botonesListeners);
        btnGuardarIngesta.setOnClickListener(botonesListeners);
        persistenciaLocal = persistenciaLocal.getInstancia(this);
        progressBar = findViewById(R.id.pgbEditarIngestaMedicamento);
        progressBar.setVisibility(View.INVISIBLE);
        setMedicamento();

    }

    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnCancelarIngestaMedicamento:
                    finish();
                    break;
                case R.id.btnGuardarIngestaMedicamento:
                    validarYGuardarIngestas();
                    finish();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();

            }
        }
    };

    private void validarYGuardarIngestas(){
        if(camposValidos()){
            activarLoading();
            Ingesta medicamento = new Ingesta();
            medicamento.setFrecuencia(Integer.parseInt(txtMedicamentoFrecuencia.getText().toString()));
            medicamento.setNombre(txtMedicamentoNombre.getText().toString());
            persistenciaLocal.setMedicamento(medicamento);
            setAlarma();
        }
    }

    private boolean camposValidos(){
        boolean esValido = true;
        if(TextUtils.isEmpty(txtMedicamentoFrecuencia.getText())){
            txtMedicamentoFrecuencia.setError("Debe ingresar una frecuencia en minutos. Ej: 2");
            esValido = false;
        }
        if(TextUtils.isEmpty(txtMedicamentoNombre.getText())){
            txtMedicamentoNombre.setError("Debe ingresar un nombre.");
            esValido = false;
        }
        return esValido;
    }

    private void setMedicamento(){
        Ingesta medicamento = persistenciaLocal.getMedicamento();
        if(medicamento != null){
            txtMedicamentoFrecuencia.setText(medicamento.getFrecuencia().toString());
            txtMedicamentoNombre.setText(medicamento.getNombre());
        }
    }

    private void activarLoading(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void setAlarma() {
        Usuario usuario = persistenciaLocal.getUsuario();
        long tiempoEnMilis = 5000; //5 segundos
        Integer REQUEST_CODE = 0; //TODO
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmaReceiver.class);
        intent.putExtra(Constante.EMAIL.name(), usuario.getEmail());
        intent.putExtra(Constante.TIPO_INGESTA.name(), Constante.MEDICAMENTO.name());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
                + tiempoEnMilis, pendingIntent);
    }
}
