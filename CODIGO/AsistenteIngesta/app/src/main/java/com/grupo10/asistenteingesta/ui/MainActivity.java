package com.grupo10.asistenteingesta.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.servicios.AlarmaService;
import com.grupo10.asistenteingesta.util.Constante;
import com.grupo10.asistenteingesta.modelo.EstadoIngesta;
import com.grupo10.asistenteingesta.modelo.Historial;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView lblMedicamentoNombre;
    private TextView lblMedicamentoFrecuencia;
    private TextView lblBebidaNombre;
    private TextView lblBebidaFrecuencia;
    private ImageButton btnEditarMedicamento;
    private ImageButton btnEliminarMedicamento;
    private ImageButton btnEditarBebida;
    private ImageButton btnEliminarBebida;
    private Button btnEliminarHistorial;
    private TableLayout tabla;
    private static PersistenciaLocal persistenciaLocal;
    private AlarmaService alarmaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblMedicamentoFrecuencia = findViewById(R.id.lblMedicamentoFrecuencia);
        lblMedicamentoNombre = findViewById(R.id.lblMedicamentoNombre);
        btnEditarMedicamento = findViewById(R.id.btnEditarMedicamento);
        btnEliminarMedicamento = findViewById(R.id.btnEliminarMedicamento);
        btnEditarMedicamento.setOnClickListener(botonesListeners);
        btnEliminarMedicamento.setOnClickListener(botonesListeners);

        lblBebidaFrecuencia = findViewById(R.id.lblBebidaFrecuencia);
        lblBebidaNombre = findViewById(R.id.lblBebidaNombre);
        btnEditarBebida = findViewById(R.id.btnEditarBebida);
        btnEliminarBebida = findViewById(R.id.btnEliminarBebida);
        btnEditarBebida.setOnClickListener(botonesListeners);
        btnEliminarBebida.setOnClickListener(botonesListeners);
        btnEliminarHistorial = findViewById(R.id.btnLimpiarHistorial);
        btnEliminarHistorial.setOnClickListener(botonesListeners);
        tabla = findViewById(R.id.table);
        persistenciaLocal = persistenciaLocal.getInstancia(this);
        alarmaService = alarmaService.getInstance(this);
    }

    private View.OnClickListener botonesListeners = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.btnEditarMedicamento:
                    intent = new Intent(MainActivity.this, EditarIngestaActivity.class);
                    intent.putExtra(Constante.TIPO_INGESTA.name(), Constante.MEDICAMENTO.name());
                    startActivity(intent);
                    break;
                case R.id.btnEliminarMedicamento:
                    mostrarAlertaEliminacion(Constante.MEDICAMENTO);
                    break;
                case R.id.btnEditarBebida:
                    intent = new Intent(MainActivity.this, EditarIngestaActivity.class);
                    intent.putExtra(Constante.TIPO_INGESTA.name(), Constante.BEBIDA.name());
                    startActivity(intent);
                    break;
                case R.id.btnEliminarBebida:
                    mostrarAlertaEliminacion(Constante.BEBIDA);
                    break;
                case R.id.btnLimpiarHistorial:
                    persistenciaLocal.eliminarHistorial();
                    setTabla();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Error en Listener de botones", Toast.LENGTH_LONG).show();

            }
        }
    };

    private void setMedicamento() {
        Ingesta medicamento = persistenciaLocal.getMedicamento();
        String nombre = "Nombre: -", frecuencia = "Frecuencia: -";
        if (medicamento != null) {
            nombre = "Nombre: " + medicamento.getNombre();
            frecuencia = "Frecuencia: " + medicamento.getFrecuencia().toString();
        }
        lblMedicamentoFrecuencia.setText(frecuencia);
        lblMedicamentoNombre.setText(nombre);
    }

    private void setBebida() {
        Ingesta bebida = persistenciaLocal.getBebida();
        String nombre = "Nombre: -", frecuencia = "Frecuencia: -";
        if (bebida != null) {
            nombre = "Nombre: " + bebida.getNombre();
            frecuencia = "Frecuencia: " + bebida.getFrecuencia().toString();
        }
        lblBebidaFrecuencia.setText(frecuencia);
        lblBebidaNombre.setText(nombre);
    }

    private void setTabla() {
        Historial historial = persistenciaLocal.getHistorial();
        List<EstadoIngesta> ingestas;
        if (historial != null) {
            ingestas = historial.getIngestas() != null ? historial.getIngestas() : new ArrayList<>();
        } else {
            ingestas = new ArrayList<>();
        }
        tabla.removeAllViews();
        TableRow tr1 = new TableRow(this);
        tr1.addView(getCell("TIPO", Color.rgb(192, 192, 192)), getTableRowParams());
        tr1.addView(getCell("NOMBRE", Color.rgb(192, 192, 192)), getTableRowParams());
        tr1.addView(getCell("FRECUENCIA", Color.rgb(192, 192, 192)), getTableRowParams());
        tabla.addView(tr1, getTableRowParams());
        for (EstadoIngesta ingesta : ingestas) {
            TableRow tr = new TableRow(this);
            tr.addView(getCell(ingesta.getEstado().toString(), Color.rgb(153, 255, 153)), getTableRowParams());
            tr.addView(getCell(ingesta.getNombre(), Color.rgb(153, 255, 153)), getTableRowParams());
            tr.addView(getCell(ingesta.getFrecuencia().toString(), Color.rgb(153, 255, 153)), getTableRowParams());
            tabla.addView(tr, getTableRowParams());
        }
    }

    private TextView getCell(String text, int color) {
        TextView cell = new TextView(this);
        cell.setGravity(Gravity.CENTER);
        cell.setTextSize(15);
        cell.setText(text);
        cell.setBackgroundColor(color);
        cell.setTextColor(Color.BLACK);
        return cell;
    }

    private TableRow.LayoutParams getTableRowParams() {
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setMargins(1, 1, 1, 1);
        params.weight = 1;
        return params;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //carga campos siempre que vuelva pantalla a primer plano
        setMedicamento();
        setBebida();
        setTabla();
    }

    private void mostrarAlertaEliminacion(Constante tipoIngesta) {
        Ingesta ingesta;
        if (Constante.BEBIDA.equals(tipoIngesta)) {
            ingesta = persistenciaLocal.getBebida();
        } else {
            ingesta = persistenciaLocal.getMedicamento();
        }
        if(ingesta == null){
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setMessage("¿Estás seguro de eliminar la ingesta: " + ingesta.getNombre() + "?");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (Constante.BEBIDA.equals(tipoIngesta)) {
                            alarmaService.eliminarAlarmaSiExiste(Constante.BEBIDA);
                            persistenciaLocal.eliminarBebida();
                            setBebida();
                        } else {
                            alarmaService.eliminarAlarmaSiExiste(Constante.MEDICAMENTO);
                            persistenciaLocal.eliminarMedicamento();
                            setMedicamento();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        alertDialog.dismiss();
                        break;
                }

            }
        };
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "ACEPTAR", dialogClickListener);
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCELAR", dialogClickListener);
        alertDialog.show();
    }
}