package com.grupo10.asistenteingesta.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;

public class MainActivity extends AppCompatActivity {

    private TextView lblMedicamentoNombre;
    private TextView lblMedicamentoFrecuencia;
    private TextView lblBebidaNombre;
    private TextView lblBebidaFrecuencia;
    private Button btnEditarMedicamento;
    private Button btnEliminarMedicamento;
    private Button btnEditarBebida;
    private Button btnEliminarBebida;
    private ProgressBar progressBar;
    private static PersistenciaLocal persistenciaLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblMedicamentoFrecuencia = findViewById(R.id.lblMedicamentoFrecuencia);
        lblMedicamentoNombre = findViewById(R.id.lblMedicamentoNombre);
        btnEditarMedicamento = findViewById(R.id.btnEditarMedicamento);
        btnEliminarMedicamento = findViewById(R.id.btnEliminarMedicamento);;
        btnEditarMedicamento.setOnClickListener(botonesListeners);
        btnEliminarMedicamento.setOnClickListener(botonesListeners);

        lblBebidaFrecuencia = findViewById(R.id.lblBebidaFrecuencia);
        lblBebidaNombre = findViewById(R.id.lblBebidaNombre);
        btnEditarBebida = findViewById(R.id.btnEditarBebida);
        btnEliminarBebida = findViewById(R.id.btnEliminarBebida);;
        btnEditarBebida.setOnClickListener(botonesListeners);
        btnEliminarBebida.setOnClickListener(botonesListeners);

        persistenciaLocal = persistenciaLocal.getInstancia(this);
    }

    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v) {
            Intent intent;
            switch (v.getId())
            {
                case R.id.btnEditarMedicamento:
                    intent = new Intent(MainActivity.this, EditarIngestaMedicamentoActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btnEliminarMedicamento:
                    persistenciaLocal.eliminarMedicamento();
                    setMedicamento();
                    break;
                case R.id.btnEditarBebida:
                    intent = new Intent(MainActivity.this, EditarIngestaBebidaActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btnEliminarBebida:
                    persistenciaLocal.eliminarBebida();
                    setBebida();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();

            }
        }
    };

    private void setMedicamento(){
        Ingesta medicamento = persistenciaLocal.getMedicamento();
        String nombre = "Nombre: -",frecuencia="Frecuencia: -";
        if(medicamento != null){
            nombre = "Nombre: " + medicamento.getNombre();
            frecuencia = "Frecuencia: " + medicamento.getFrecuencia().toString();
        }
        lblMedicamentoFrecuencia.setText(frecuencia);
        lblMedicamentoNombre.setText(nombre);
    }

    private void setBebida(){
        Ingesta bebida = persistenciaLocal.getBebida();
        String nombre = "Nombre: -",frecuencia="Frecuencia: -";
        if(bebida != null){
            nombre = "Nombre: " + bebida.getNombre();
            frecuencia = "Frecuencia: " + bebida.getFrecuencia().toString();
        }
        lblBebidaFrecuencia.setText(frecuencia);
        lblBebidaNombre.setText(nombre);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //carga campos siempre que vuelva pantalla a primer plano
        setMedicamento();
        setBebida();
    }
}
