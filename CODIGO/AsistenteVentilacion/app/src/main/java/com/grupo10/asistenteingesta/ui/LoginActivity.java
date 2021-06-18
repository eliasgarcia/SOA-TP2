package com.grupo10.asistenteingesta.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.client.LoginClient;
import com.grupo10.asistenteingesta.client.LoginClientBuilder;
import com.grupo10.asistenteingesta.dto.ErrorDTO;
import com.grupo10.asistenteingesta.dto.LoginDTO;
import com.grupo10.asistenteingesta.modelo.Usuario;
import com.grupo10.asistenteingesta.response.LoginResponse;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;
import com.grupo10.asistenteingesta.util.JsonConverter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private Button bRegistrarse;
    private Button bLogin;
    private ProgressBar progressBar;
    private TextView txtEmail;
    private TextView txtContrasenia;
    private LoginClient loginClient;
    private static PersistenciaLocal persistenciaLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bRegistrarse = findViewById(R.id.btnRegistrarse);
        bLogin = findViewById(R.id.btnLogin);
        txtEmail = findViewById(R.id.txtEmailLogin);
        txtContrasenia = findViewById(R.id.txtContraseniaLogin);
        progressBar = findViewById(R.id.pgbLogin);
        progressBar.setVisibility(View.INVISIBLE);
        loginClient = LoginClientBuilder.getClient();
        persistenciaLocal = persistenciaLocal.getInstancia(this);

        Log.i("Ejecuto","Ejecuto onCreate");
        setListeners();
    }

    private void setListeners(){
        setListenerBotonRegistro();
        setListenerBotonLogin();
    }

    private void setListenerBotonRegistro(){
        bRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean camposValidos(){
        boolean sonValidos = true;
        if(hayCamposVacios()){
            sonValidos = false;
        }else{
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmail.getText().toString());
            if(!matcher.find()){
                txtEmail.setError("Formato incorrecto");
                sonValidos = false;
            }
        }
        return sonValidos;
    }

    private boolean hayCamposVacios(){
        if(estaVacio(txtEmail) || estaVacio(txtContrasenia)){
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

    private void setListenerBotonLogin(){
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(camposValidos()){
                    login();
                }
            }
        });
    }

    private void login(){
        progressBar.setVisibility(View.VISIBLE);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
       //         WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        LoginDTO loginDTO = new LoginDTO(txtEmail.getText().toString(), txtContrasenia.getText().toString());
        Call<LoginResponse> call = loginClient.login(loginDTO);
        call.enqueue(new Callback<LoginResponse>() {
                         @Override
                         public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                             progressBar.setVisibility(View.INVISIBLE);
                             if(!response.isSuccessful()){
                                 loginNoExitoso(response);
                                 return;
                             }
                             Toast.makeText(LoginActivity.this, "Login Exitoso", Toast.LENGTH_LONG).show();
                             //todo:sacar el clean
                             persistenciaLocal.limpiar();
                             guardarUsuario(response.body(),loginDTO);
                             abrirMainActivity();
                         }

                         @Override
                         public void onFailure(Call<LoginResponse> call, Throwable t) {
                             progressBar.setVisibility(View.INVISIBLE);
                             Toast.makeText(LoginActivity.this, "Fallo. onFailure", Toast.LENGTH_LONG).show();
                         }
                     }

        );
    }

    private void loginNoExitoso(Response<LoginResponse> response){
        if(response.raw().code() != 400){
            Toast.makeText(LoginActivity.this, "Hubo un error. Intente mas tarde.", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            ErrorDTO error = JsonConverter.getError(response.errorBody().string());
            txtContrasenia.setError("Contraseña no válida.");
            Toast.makeText(LoginActivity.this, "Error: " + error.getMsg(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void abrirMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void guardarUsuario(LoginResponse loginResponse, LoginDTO loginDTO){
        Usuario usuario = new Usuario();
        usuario.setEmail(loginDTO.getEmail());
        usuario.setToken(loginResponse.getToken());
        usuario.setToken_refresh(loginResponse.getTokenRefresh());
        Usuario usuarioAnterior = persistenciaLocal.getUsuario();
        //limpio sharedReference si el usuario actual es distinto al ultimo que hizo login.
        if(usuarioAnterior!=null && !usuarioAnterior.getEmail().equals(usuario.getEmail())){
            persistenciaLocal.limpiar();
        }
        persistenciaLocal.setUsuario(usuario);

    }

}
