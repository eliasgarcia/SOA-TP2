package com.grupo10.asistenteingesta.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.servicios.AlarmaService;
import com.grupo10.asistenteingesta.util.Constante;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.modelo.Usuario;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;

public class EditarIngestaActivity extends AppCompatActivity {

    private TextView txtTipoIngesta;
    private EditText txtTipoIngestaNombre;
    private EditText txtTipoIngestaDistancia;
    private Button btnGuardarIngesta;
    private Button btnCancelarIngesta;
    private ProgressBar progressBar;
    private static PersistenciaLocal persistenciaLocal;
    private Bundle bundle;
    private String tipoIngesta;
    private AlarmaService alarmaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ingesta);
        txtTipoIngestaNombre = findViewById(R.id.txtIngestaNombreNuevo);
        txtTipoIngestaDistancia = findViewById(R.id.txtIngestaDistanciaNueva);
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
        alarmaService = alarmaService.getInstance(this);

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
            ingesta.setDistancia(Integer.parseInt(txtTipoIngestaDistancia.getText().toString()));
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
        if(TextUtils.isEmpty(txtTipoIngestaDistancia.getText())){
            txtTipoIngestaDistancia.setError("Debe ingresar una distancia en minutos. Ej: 2");
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
        txtTipoIngestaDistancia.setText(ingesta!=null?ingesta.getDistancia().toString():"");
        txtTipoIngestaNombre.setText(ingesta!=null?ingesta.getNombre():"");
    }

    private void activarLoading(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void setAlarma() {
        alarmaService.eliminarAlarmaSiExiste(Constante.MEDICAMENTO.name().equals(tipoIngesta)?Constante.MEDICAMENTO:Constante.BEBIDA);
        Usuario usuario = persistenciaLocal.getUsuario();
        alarmaService.crearAlarma(Constante.MEDICAMENTO.name().equals(tipoIngesta)?Constante.MEDICAMENTO:Constante.BEBIDA,
                usuario.getEmail(),Integer.valueOf(txtTipoIngestaDistancia.getText().toString()));
    }
}
