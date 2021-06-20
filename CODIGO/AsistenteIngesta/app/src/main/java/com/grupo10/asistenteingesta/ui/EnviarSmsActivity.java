package com.grupo10.asistenteingesta.ui;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.grupo10.asistenteingesta.R;

import java.util.concurrent.ThreadLocalRandom;

public class EnviarSmsActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_SEND_SMS = 1;
    private String  codigoSMS;
    private final static int MIN_CODE = 100000;
    private final static int MAX_CODE = 999999;
    private EditText txtNroCelular;
    private Button btnEnviarSms;
    private Intent intent;
    private Bundle bundle = new Bundle();
    private ProgressBar progressBar;
    private TextView txtCargaBateria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_sms);
        intent = new Intent(EnviarSmsActivity.this, ValidarSmsActivity.class);
        txtNroCelular = findViewById(R.id.txtNroCelular);
        btnEnviarSms = findViewById(R.id.btnEnviarSms);
        btnEnviarSms.setOnClickListener(botonesListeners);
        progressBar = findViewById(R.id.pgbEnviarSms);
        progressBar.setVisibility(View.INVISIBLE);
        txtCargaBateria = findViewById(R.id.txtBateriaCarga);
        Log.i("Ejecuto","Ejecuto onCreate");
        mostrarDatosBateria();
    }

    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private View.OnClickListener botonesListeners = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnEnviarSms:
                    enviarSmsYAbrirActivityValidarSms();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();
            }
        }
    };

    private void enviarSmsYAbrirActivityValidarSms(){
        if(esUnNroCelularValido() && tienePermisoParaEnviarSms()){
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            enviarSms();
            abrirActivityValidarSms();
        }
    }

    private void abrirActivityValidarSms(){
        bundle.putString("codigoSms", codigoSMS);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private boolean esUnNroCelularValido(){
        boolean esValido = true;
        if (txtNroCelular.getText().toString().isEmpty()){
            txtNroCelular.setError("Debe ingresar un nro de celular.");
            esValido = false;
        }else if(txtNroCelular.getText().toString().length()!=10){
            txtNroCelular.setError("El nro de celular debe tener 10 dígitos.");
            esValido = false;
        }
        return esValido;
    }

    private boolean tienePermisoParaEnviarSms(){
        if(checkSMSStatePermission()){
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERMISSION_SEND_SMS);
            return false;
        }
    }

    private boolean checkSMSStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    private void enviarSms(){
        generarCodigo();
        Toast.makeText(this, "Enviando SMS", Toast.LENGTH_SHORT).show();
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(txtNroCelular.getText().toString(), null, codigoSMS , null, null);
    }

    private void generarCodigo(){
        codigoSMS = String.valueOf(ThreadLocalRandom.current().nextInt(MIN_CODE, MAX_CODE ));
        Log.i("codigoSMS",codigoSMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_SEND_SMS:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.i("SMS Permiso","Aceptado");
                    enviarSms();
                    abrirActivityValidarSms();
                }else{
                    Log.i("SMS Permiso","No aceptado");
                    Toast.makeText(this, "Debe aceptar los permisos para continuar.", Toast.LENGTH_LONG).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void mostrarDatosBateria(){
        IntentFilter iFilterBateria = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent estadoBateria = this.registerReceiver(null, iFilterBateria);
        int nivel = estadoBateria.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int escala = estadoBateria.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float porcentajeBateria = nivel * 100 / (float)escala;
        txtCargaBateria.setText((int)porcentajeBateria+ "%");
    }
}
