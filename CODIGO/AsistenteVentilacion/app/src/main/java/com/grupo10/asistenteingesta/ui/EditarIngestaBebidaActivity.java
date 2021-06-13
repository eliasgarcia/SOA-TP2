package com.grupo10.asistenteingesta.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;

public class EditarIngestaBebidaActivity extends AppCompatActivity {

    private EditText txtBebidaNombre;
    private EditText txtBebidaFrecuencia;
    private Button btnGuardarIngesta;
    private Button btnCancelarIngesta;
    private ProgressBar progressBar;
    private static PersistenciaLocal persistenciaLocal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ingesta_bebida);
        txtBebidaNombre = findViewById(R.id.txtIngestaBebidaNombreNueva);
        txtBebidaFrecuencia = findViewById(R.id.txtIngestaBebidaFrecuenciaNueva);
        btnCancelarIngesta = findViewById(R.id.btnCancelarBebidaNueva);
        btnGuardarIngesta = findViewById(R.id.btnGuardarBebidaNueva);
        btnCancelarIngesta.setOnClickListener(botonesListeners);
        btnGuardarIngesta.setOnClickListener(botonesListeners);
        persistenciaLocal = persistenciaLocal.getInstancia(this);
        progressBar = findViewById(R.id.pgbBebidaNueva);
        progressBar.setVisibility(View.INVISIBLE);
        setBebida();
    }


    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnCancelarBebidaNueva:
                    finish();
                    break;
                case R.id.btnGuardarBebidaNueva:
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
            Ingesta bebida = new Ingesta();
            bebida.setFrecuencia(Integer.parseInt(txtBebidaFrecuencia.getText().toString()));
            bebida.setNombre(txtBebidaNombre.getText().toString());
            persistenciaLocal.setBebida(bebida);
            finish();
        }
    }

    private boolean camposValidos(){
        boolean esValido = true;
        if(TextUtils.isEmpty(txtBebidaFrecuencia.getText())){
            txtBebidaFrecuencia.setError("Debe ingresar una frecuencia en minutos. Ej: 2");
            esValido = false;
        }
        if(TextUtils.isEmpty(txtBebidaNombre.getText())){
            txtBebidaNombre.setError("Debe ingresar un nombre.");
            esValido = false;
        }
        return esValido;
    }

    private void setBebida(){
        Ingesta bebida = persistenciaLocal.getBebida();
        if(bebida != null){
            txtBebidaFrecuencia.setText(bebida.getFrecuencia().toString());
            txtBebidaNombre.setText(bebida.getNombre());
        }
    }

    private void activarLoading(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
