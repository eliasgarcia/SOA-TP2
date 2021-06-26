package com.grupo10.asistenteingesta.ui;

import androidx.appcompat.app.AppCompatActivity;
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

import com.grupo10.asistenteingesta.R;
import com.grupo10.asistenteingesta.client.EventoClient;
import com.grupo10.asistenteingesta.client.EventoClientBuilder;
import com.grupo10.asistenteingesta.client.RefreshTokenBuilder;
import com.grupo10.asistenteingesta.client.RefreshTokenClient;
import com.grupo10.asistenteingesta.dto.ErrorDTO;
import com.grupo10.asistenteingesta.dto.EventoDTO;
import com.grupo10.asistenteingesta.response.EventoResponse;
import com.grupo10.asistenteingesta.response.RefreshTokenResponse;
import com.grupo10.asistenteingesta.servicios.AlarmaService;
import com.grupo10.asistenteingesta.servicios.InternetStatus;
import com.grupo10.asistenteingesta.util.Constante;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.modelo.Usuario;
import com.grupo10.asistenteingesta.servicios.PersistenciaLocal;
import com.grupo10.asistenteingesta.util.JsonConverter;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarIngestaActivity extends AppCompatActivity {

    private final static String TAG = "ACT_EDITAR_INGESTA";
    private TextView txtTipoIngesta;
    private EditText txtTipoIngestaNombre;
    private EditText txtTipoIngestaDistancia;
    private Button btnGuardarIngesta;
    private Button btnCancelarIngesta;
    private ProgressBar progressBar;
    private static PersistenciaLocal persistenciaLocal;
    private Bundle bundle;
    private String tipoIngesta;
    private AlarmaService alarmaService;
    private InternetStatus internetStatus;
    private RefreshTokenClient refreshTokenClient;
    private EventoClient eventoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ingesta);
        txtTipoIngestaNombre = findViewById(R.id.txtIngestaNombreNuevo);
        txtTipoIngestaDistancia = findViewById(R.id.txtIngestaDistanciaNueva);
        btnCancelarIngesta = findViewById(R.id.btnCancelarIngesta);
        btnGuardarIngesta = findViewById(R.id.btnConfirmarIngesta);
        txtTipoIngesta = findViewById(R.id.txtTipoIngesta);
        btnCancelarIngesta.setOnClickListener(botonesListeners);
        btnGuardarIngesta.setOnClickListener(botonesListeners);
        persistenciaLocal = persistenciaLocal.getInstancia(this);
        progressBar = findViewById(R.id.pgbEditarIngesta);
        progressBar.setVisibility(View.INVISIBLE);
        bundle = getIntent().getExtras();
        tipoIngesta = bundle.getString(Constante.TIPO_INGESTA.name());
        setValoresTipoIngesta();
        alarmaService = alarmaService.getInstance(this);
        internetStatus = internetStatus.getInstance(this);
        refreshTokenClient = RefreshTokenBuilder.getClient();
        eventoClient = EventoClientBuilder.getClient();
        Log.i(TAG,"Ejecuto onCreate");
    }

    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnCancelarIngesta:
                    finish();
                    break;
                case R.id.btnConfirmarIngesta:
                    validarYGuardarIngestas();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();
            }
        }
    };

    private void validarYGuardarIngestas(){
        if(camposValidos()){
            if(!internetStatus.isConnected()){
                Log.i(TAG,"No hay acceso a internet. Intente mas tarde");
                return;
            }
            activarLoading();
            registrarEventoEnServidor(Constante.LLAMADO_INICIAL);
        }
    }

    private void guardarIngesta(){
        Ingesta ingesta = new Ingesta();
        ingesta.setDistancia(Integer.parseInt(txtTipoIngestaDistancia.getText().toString()));
        ingesta.setNombre(txtTipoIngestaNombre.getText().toString());
        Calendar proxima = Calendar.getInstance();
        proxima.add(Calendar.MINUTE,ingesta.getDistancia());
        ingesta.setProxima(proxima);
        if(Constante.BEBIDA.name().equals(tipoIngesta)){
            persistenciaLocal.setBebida(ingesta);
        }else {
            persistenciaLocal.setMedicamento(ingesta);
        }
        Toast.makeText(EditarIngestaActivity.this, "Recordatorio guardado", Toast.LENGTH_LONG).show();
        setAlarma();
        finish();
    }

    private boolean camposValidos(){
        boolean esValido = true;
        if(TextUtils.isEmpty(txtTipoIngestaDistancia.getText())){
            txtTipoIngestaDistancia.setError("Debe ingresar una distancia en minutos. Ej: 2");
            esValido = false;
        }
        if(TextUtils.isEmpty(txtTipoIngestaNombre.getText())){
            txtTipoIngestaNombre.setError("Debe ingresar un nombre.");
            esValido = false;
        }
        return esValido;
    }

    private void setValoresTipoIngesta(){
        txtTipoIngesta.setText(tipoIngesta);
        Ingesta ingesta;
        if(Constante.BEBIDA.name().equals(tipoIngesta)){
            ingesta = persistenciaLocal.getBebida();
        }else{
            ingesta = persistenciaLocal.getMedicamento();
        }
        txtTipoIngestaDistancia.setText(ingesta!=null?ingesta.getDistancia().toString():"");
        txtTipoIngestaNombre.setText(ingesta!=null?ingesta.getNombre():"");
    }

    private void activarLoading(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void setAlarma() {
        Log.i(TAG,"Seteando alarma");
        alarmaService.eliminarAlarmaSiExiste(Constante.MEDICAMENTO.name().equals(tipoIngesta)?Constante.MEDICAMENTO:Constante.BEBIDA);
        Usuario usuario = persistenciaLocal.getUsuario();
        alarmaService.crearAlarma(Constante.MEDICAMENTO.name().equals(tipoIngesta)?Constante.MEDICAMENTO:Constante.BEBIDA,
                usuario.getEmail(),Integer.valueOf(txtTipoIngestaDistancia.getText().toString()));
    }

    private void registrarEventoEnServidor(Constante constante){
        String descripcion = "Nombre: " + txtTipoIngestaNombre.getText().toString() + ";Distancia: " + txtTipoIngestaDistancia.getText().toString();
        String token = persistenciaLocal.getUsuario().getToken();
        EventoDTO eventoDTO = new EventoDTO(Constante.TIPO_EVENTO_ALARMA_REGISTRADA.name(), descripcion );
        Call<EventoResponse> call = eventoClient.registrarEvento("Bearer "+token, eventoDTO);
        call.enqueue( new Callback<EventoResponse>() {
                          @Override
                          public void onResponse(Call<EventoResponse> call, Response<EventoResponse> response) {
                              progressBar.setVisibility(View.INVISIBLE);
                              if(!response.isSuccessful()){
                                  registroEventoNoExitoso(response, constante);
                                  return;
                              }
                              Log.i(TAG,"Registro evento exitoso: " + descripcion);
                              guardarIngesta();
                          }

                          @Override
                          public void onFailure(Call<EventoResponse> call, Throwable t) {
                              progressBar.setVisibility(View.INVISIBLE);
                              Toast.makeText(EditarIngestaActivity.this, "Fallo. onFailure", Toast.LENGTH_LONG).show();
                          }
                      }

        );
    }

    private void registroEventoNoExitoso( Response<EventoResponse> response, Constante constante){
        try {
            ErrorDTO error = JsonConverter.getError(response.errorBody().string());
            Log.i(TAG,"Registro evento no exitoso - " + error.getMsg());
            if(Constante.LLAMADO_INICIAL.equals(constante) && response.raw().code() == 401){
                Log.i(TAG, error.getMsg());
                refrescarJWT();
            }else{
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(EditarIngestaActivity.this, "Error: "+ error.getMsg(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refrescarJWT(){
        Log.i(TAG,"Refrescando token");
        String refresh = persistenciaLocal.getUsuario().getToken_refresh();
        Call<RefreshTokenResponse> call = refreshTokenClient.actualizarToken("Bearer " + refresh);
        call.enqueue( new Callback<RefreshTokenResponse>() {
                          @Override
                          public void onResponse(Call<RefreshTokenResponse> call, Response<RefreshTokenResponse> response) {
                              progressBar.setVisibility(View.INVISIBLE);
                              if(!response.isSuccessful()){
                                  refreshTokenNoExitoso(response);
                                  return;
                              }
                              Log.i(TAG,"Actualizó Token con éxito");
                              Usuario usuario = persistenciaLocal.getUsuario();
                              usuario.setToken(response.body().getToken());
                              usuario.setToken_refresh(response.body().getTokenRefresh());
                              persistenciaLocal.setUsuario(usuario);
                              registrarEventoEnServidor(Constante.LLAMADO_POST_ACTUALIZACION_TOKEN);
                          }

                          @Override
                          public void onFailure(Call<RefreshTokenResponse> call, Throwable t) {
                              progressBar.setVisibility(View.INVISIBLE);
                              Toast.makeText(EditarIngestaActivity.this, "Fallo. onFailure", Toast.LENGTH_LONG).show();
                          }
                      }

        );
    }

    private void refreshTokenNoExitoso( Response<RefreshTokenResponse> response){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            if(response.raw().code() == 400){
                ErrorDTO error = JsonConverter.getError(response.errorBody().string());
                Toast.makeText(EditarIngestaActivity.this,"Error: " + error.getMsg(), Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG,"No actualizó Token con éxito");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
