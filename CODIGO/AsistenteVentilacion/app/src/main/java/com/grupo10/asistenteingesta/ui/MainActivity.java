package com.grupo10.asistenteingesta.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.AlphabeticIndex;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;

public class MainActivity extends AppCompatActivity {

    private TextView lblMedicamentoNombre;
    private TextView lblMedicamentoFrecuencia;
    private Button btnEditarMedicamento;
    private ProgressBar progressBar;
    private static PersistenciaLocal persistenciaLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblMedicamentoFrecuencia = findViewById(R.id.lblMedicamentoFrecuencia);
        lblMedicamentoNombre = findViewById(R.id.lblMedicamentoNombre);
        btnEditarMedicamento = findViewById(R.id.btnEditarMedicamento);
        btnEditarMedicamento.setOnClickListener(botonesListeners);
        persistenciaLocal = persistenciaLocal.getInstancia(this);
    }

    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnEditarMedicamento:
                    Intent intent = new Intent(MainActivity.this, EditarIngestaMedicamentoActivity.class);
                    startActivity(intent);
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

    @Override
    protected void onResume(){
        super.onResume();
        //carga campos siempre que vuelva pantalla a primer plano
        setMedicamento();
    }
}
