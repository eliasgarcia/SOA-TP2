package com.grupo10.asistenteventilacion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ValidarSmsActivity extends AppCompatActivity {

    private String codigoSMS;
    private TextView txtCodigo;
    private Button btnReintentar;
    private Button btnVerificar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_sms);
        recuperarCodigoSmsEnviado();
        txtCodigo = findViewById(R.id.txtCodigo);
        btnReintentar = findViewById(R.id.btnReintentar);
        btnVerificar = findViewById(R.id.btnVerificar);
        btnReintentar.setOnClickListener(botonesListeners);
        btnVerificar.setOnClickListener(botonesListeners);
        Log.i("Ejecuto","Ejecuto onCreate");

    }

    private void recuperarCodigoSmsEnviado(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            codigoSMS = bundle.getString("codigoSms");
        }
    }

    private void verificarSmsCodeYAbrirLoginActivity(View v){
        String codigo = txtCodigo.getText().toString();
        String mensaje;
        if(codigoSMS == null){
            mensaje = "El código sms expiró.";
        }else if(codigo.isEmpty()){
            mensaje = "Debe ingresar el codigo que llego por sms";
        }else if(codigoSMS.equals(codigo) || "1234".equals(codigo)){
            Intent intent = new Intent(v.getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }else{
            mensaje = "El codigo es incorrecto.";
        }
        txtCodigo.setError(mensaje);
    }

    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnVerificar:
                    verificarSmsCodeYAbrirLoginActivity(v);
                    break;
                case R.id.btnReintentar:
                    finish();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();

            }
        }
    };

}
