package com.grupo10.asistenteventilacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setListeners();
    }

    private void setListeners(){
        setListenerBotonRegistro();
        setListenerBotonLogin();
    }

    private void setListenerBotonRegistro(){
        Button bRegistrarse = findViewById(R.id.btnRegistrarse);
        bRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegistroActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void setListenerBotonLogin(){
        Button bLogin = findViewById(R.id.btnLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(v.getContext(), ValidarSmsActivity.class);
                startActivityForResult(intent, 0);
                 */
            }
        });
    }

}
