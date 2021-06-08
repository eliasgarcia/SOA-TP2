package com.grupo10.asistenteventilacion;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    EditText txtNombre;
    EditText txtApellido;
    EditText txtDNI;
    EditText txtEmail;
    EditText txtContrasenia;
    Button btnGuardarNuevoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        txtNombre = findViewById(R.id.txtNombreNuevo);
        txtApellido = findViewById(R.id.txtApellidoNuevo);
        txtDNI = findViewById(R.id.txtDNINuevo);
        txtEmail = findViewById(R.id.txtEmailNuevo);
        txtContrasenia = findViewById(R.id.txtContraseniaNueva);
        btnGuardarNuevoUsuario = findViewById(R.id.btnGuardarNuevoUsuario);
        btnGuardarNuevoUsuario.setOnClickListener(botonesListeners);
        Log.i("Ejecuto","Ejecuto onCreate");
    }

    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnGuardarNuevoUsuario:
                    validarYRegistrarUsuario(v);
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();

            }
        }
    };

    private void validarYRegistrarUsuario(View v){
        if(camposValidos()){
            Toast.makeText(getApplicationContext(),"Todo joya!!",Toast.LENGTH_LONG).show();
        }
    }

    private boolean camposValidos(){
        boolean sonValidos = true;
        if(hayCamposVacios()){
            sonValidos = false;
        }else{
            if(txtContrasenia.getText().toString().length()<8){
                txtContrasenia.setError("Debe tener mas de 8 caracteres.");
                sonValidos = false;
            }
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmail.getText().toString());
            if(!matcher.find()){
                txtEmail.setError("Formato incorrecto");
                sonValidos = false;            }
        }
        return sonValidos;
    }

    private boolean hayCamposVacios(){
        if(estaVacio(txtNombre) || estaVacio(txtApellido) || estaVacio(txtDNI)
                || estaVacio(txtContrasenia) || estaVacio(txtEmail)){
            return true;
        }
        return false;
    }

    private boolean estaVacio(TextView tv){
        if(TextUtils.isEmpty(tv.getText())){
            tv.setError("Campo obligatorio.");
            return true;
        }
        return false;
    }

}
