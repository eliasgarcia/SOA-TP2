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
import android.widget.TextView;
import android.widget.Toast;

import com.grupo10.asistenteingesta.broadcast.AlarmaReceiver;
import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.constantes.Constante;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.modelo.Usuario;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;

public class EditarIngestaActivity extends AppCompatActivity {

    private TextView txtTipoIngesta;
    private EditText txtTipoIngestaNombre;
    private EditText txtTipoIngestaFrecuencia;
    private Button btnGuardarIngesta;
    private Button btnCancelarIngesta;
    private ProgressBar progressBar;
    private static PersistenciaLocal persistenciaLocal;
    private Bundle bundle;
    private String tipoIngesta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ingesta);
        txtTipoIngestaNombre = findViewById(R.id.txtIngestaNombreNuevo);
        txtTipoIngestaFrecuencia = findViewById(R.id.txtIngestaFrecuenciaNuevo);
        btnCancelarIngesta = findViewById(R.id.btnCancelarIngesta);
        btnGuardarIngesta = findViewById(R.id.btnConfirmarIngesta);
        txtTipoIngesta = findViewById(R.id.txtTipoIngesta);
        btnCancelarIngesta.setOnClickListener(botonesListeners);
        btnGuardarIngesta.setOnClickListener(botonesListeners);
        persistenciaLocal = persistenciaLocal.getInstancia(this);
        progressBar = findViewById(R.id.pgbEditarIngesta);
        progressBar.setVisibility(View.INVISIBLE);
        bundle = getIntent().getExtras();
        tipoIngesta = bundle.getString(Constante.TIPO_INGESTA.name());
        setValoresTipoIngesta();

    }

    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnCancelarIngesta:
                    finish();
                    break;
                case R.id.btnConfirmarIngesta:
                    validarYGuardarIngestas();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();

            }
        }
    };

    private void validarYGuardarIngestas(){
        if(camposValidos()){
            activarLoading();
            Ingesta ingesta = new Ingesta();
            ingesta.setFrecuencia(Integer.parseInt(txtTipoIngestaFrecuencia.getText().toString()));
            ingesta.setNombre(txtTipoIngestaNombre.getText().toString());
            if(Constante.BEBIDA.name().equals(tipoIngesta)){
                persistenciaLocal.setBebida(ingesta);
            }else {
                persistenciaLocal.setMedicamento(ingesta);
            }
            setAlarma();
            finish();
        }
    }

    private boolean camposValidos(){
        boolean esValido = true;
        if(TextUtils.isEmpty(txtTipoIngestaFrecuencia.getText())){
            txtTipoIngestaFrecuencia.setError("Debe ingresar una frecuencia en minutos. Ej: 2");
            esValido = false;
        }
        if(TextUtils.isEmpty(txtTipoIngestaNombre.getText())){
            txtTipoIngestaNombre.setError("Debe ingresar un nombre.");
            esValido = false;
        }
        return esValido;
    }

    private void setValoresTipoIngesta(){
        txtTipoIngesta.setText(tipoIngesta);
        Ingesta ingesta;
        if(Constante.BEBIDA.name().equals(tipoIngesta)){
            ingesta = persistenciaLocal.getBebida();
        }else{
            ingesta = persistenciaLocal.getMedicamento();
        }
        txtTipoIngestaFrecuencia.setText(ingesta!=null?ingesta.getFrecuencia().toString():"");
        txtTipoIngestaNombre.setText(ingesta!=null?ingesta.getNombre():"");
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
        intent.putExtra(Constante.TIPO_INGESTA.name(), tipoIngesta);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
                + tiempoEnMilis, pendingIntent);
    }
}
