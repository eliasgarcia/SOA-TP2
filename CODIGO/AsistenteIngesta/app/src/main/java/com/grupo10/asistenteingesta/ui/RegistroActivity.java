package com.grupo10.asistenteingesta.ui;

import android.content.Intent;
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

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.client.UsuarioClient;
import com.grupo10.asistenteingesta.client.UsuarioClientBuilder;
import com.grupo10.asistenteingesta.dto.ErrorDTO;
import com.grupo10.asistenteingesta.dto.UsuarioDTO;
import com.grupo10.asistenteingesta.modelo.Usuario;
import com.grupo10.asistenteingesta.response.RegistroResponse;
import com.grupo10.asistenteingesta.servicios.InternetStatus;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;
import com.grupo10.asistenteingesta.util.JsonConverter;

import java.io.IOException;
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
    private static PersistenciaLocal persistenciaLocal;
    private InternetStatus internetStatus;

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
        persistenciaLocal = persistenciaLocal.getInstancia(this);
        internetStatus = internetStatus.getInstance(this);

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
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setName(txtNombre.getText().toString());
        usuarioDTO.setLastname(txtApellido.getText().toString());
        usuarioDTO.setEmail(txtEmail.getText().toString());
        usuarioDTO.setPassword(txtContrasenia.getText().toString());
        usuarioDTO.setDni(Integer.valueOf(txtDNI.getText().toString()));
        return usuarioDTO;
    }
    private void registrarUsuario(){
        if(!internetStatus.isConnected()){
            return;
        }
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
                                 registroNoExitoso(response);
                                 return;
                             }
                             RegistroResponse registroResponse = response.body();
                             Toast.makeText(RegistroActivity.this, "Registro Exitoso" +registroResponse.getToken(), Toast.LENGTH_LONG).show();
                             guardarUsuario(response.body(),usuarioDTO);
                             abrirMainActivity();
                         }

                        //Cuando falla al intentar hacer request
                         @Override
                         public void onFailure(Call<RegistroResponse> call, Throwable t) {
                             progressBar.setVisibility(View.INVISIBLE);
                             Toast.makeText(RegistroActivity.this, "Fallo. onFailure", Toast.LENGTH_SHORT).show();
                         }
                     }

        );
    }

    private void registroNoExitoso(Response<RegistroResponse> response){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(response.raw().code() != 400){
            Toast.makeText(RegistroActivity.this, "Hubo un error. Intente mas tarde.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ErrorDTO error = JsonConverter.getError(response.errorBody().string());
            Toast.makeText(RegistroActivity.this, error.getMsg(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirMainActivity(){
        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void guardarUsuario(RegistroResponse loginResponse, UsuarioDTO usuarioDTO){
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setToken(loginResponse.getToken());
        usuario.setToken_refresh(loginResponse.getTokenRefresh());
        persistenciaLocal.limpiar();
        persistenciaLocal.setUsuario(usuario);
    }

}
