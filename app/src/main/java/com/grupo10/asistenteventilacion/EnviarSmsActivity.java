package com.grupo10.asistenteventilacion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ThreadLocalRandom;

public class EnviarSmsActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_SEND_SMS = 1;
    private String  codigoSMS;
    private final static int MIN_CODE = 100000;
    private final static int MAX_CODE = 999999;
    EditText txtNroCelular;
    Button btnEnviarSms;
    Intent intent;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_sms);
        intent = new Intent(EnviarSmsActivity.this, ValidarSmsActivity.class);
        txtNroCelular = (EditText) findViewById(R.id.txtNroCelular);
        btnEnviarSms = (Button) findViewById(R.id.btnEnviarSms);
        btnEnviarSms.setOnClickListener(botonesListeners);
        Log.i("Ejecuto","Ejecuto onCreate");

    }

    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
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
            txtNroCelular.setError("El nro de celular debe tener 8 dígitos.");
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
}
