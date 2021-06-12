package com.grupo10.asistenteventilacion.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo10.asistenteventilacion.R;
import com.grupo10.asistenteventilacion.client.UsuarioClient;
import com.grupo10.asistenteventilacion.client.UsuarioClientBuilder;
import com.grupo10.asistenteventilacion.dto.UsuarioDTO;
import com.grupo10.asistenteventilacion.modelo.Usuario;
import com.grupo10.asistenteventilacion.response.RegistroResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText txtNombre;
    private EditText txtApellido;
    private EditText txtDNI;
    private EditText txtEmail;
    private EditText txtContrasenia;
    private Button btnGuardarNuevoUsuario;
    private UsuarioClient usuarioClient;
    private ProgressBar progressBar;


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
        usuarioClient = UsuarioClientBuilder.getClient();
        progressBar = findViewById(R.id.pgbRegistro);
        progressBar.setVisibility(View.INVISIBLE);
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
            registrarUsuario();
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

    private UsuarioDTO obtenerUsuarioDTO(){
        Usuario usuario = new Usuario();
        usuario.setNombre(txtNombre.getText().toString());
        usuario.setApellido(txtApellido.getText().toString());
        usuario.setEmail(txtEmail.getText().toString());
        usuario.setContrasenia(txtContrasenia.getText().toString());
        usuario.setDni(Integer.valueOf(txtDNI.getText().toString()));
        return new UsuarioDTO(usuario);
    }
    private void registrarUsuario(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        UsuarioDTO usuarioDTO = obtenerUsuarioDTO();
        Call<RegistroResponse> call = usuarioClient.registrarUsuario(usuarioDTO);
        call.enqueue(new Callback<RegistroResponse>() {
                        //Cuando le pudo pegar al servicio
                         @Override
                         public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                             progressBar.setVisibility(View.INVISIBLE);
                             if(!response.isSuccessful()){
                                 Toast.makeText(RegistroActivity.this, "Registro NO Exitoso", Toast.LENGTH_LONG).show();
                                 return;
                             }
                             RegistroResponse registroResponse = response.body();
                             Toast.makeText(RegistroActivity.this, "Registro Exitoso" +registroResponse.getToken(), Toast.LENGTH_LONG).show();

                         }

                        //Cuando falla al intentar hacer request
                         @Override
                         public void onFailure(Call<RegistroResponse> call, Throwable t) {
                             progressBar.setVisibility(View.INVISIBLE);
                             Toast.makeText(RegistroActivity.this, "Fallo. onFailure", Toast.LENGTH_LONG).show();
                         }
                     }

        );
    }

}
