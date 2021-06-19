package com.grupo10.asistenteingesta.servicios;

import android.content.Context;
import android.content.SharedPreferences;

import com.grupo10.asistenteingesta.modelo.Historial;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.modelo.Usuario;
import com.grupo10.asistenteingesta.util.JsonConverter;

public class PersistenciaLocal {

    private static final String NOMBBRE_PREFERENCE = "AsistenteIngestaGrupo5";
    private static final String KEY_USUARIO = "key_usuario";
    private static final String KEY_HISTORIAL = "key_historial";
    private static final String KEY_MEDICAMENTO = "key_medicamento";
    private static final String KEY_BEBIDA = "key_bebida";
    private static final String KEY_LUX = "key_lux";
    private static final String KEY_PROXIMIDAD = "key_proximidad";

    private SharedPreferences sPreference;
    private SharedPreferences.Editor editor;
    private Context contexto;
    private static PersistenciaLocal instancia;
    private static final String EMPTY = "EMPTY";

    private PersistenciaLocal(Context contexto) {
        this.contexto = contexto;
        this.sPreference = this.contexto.getSharedPreferences(NOMBBRE_PREFERENCE, Context.MODE_PRIVATE);
        editor = sPreference.edit();
    }

    public static PersistenciaLocal getInstancia(Context context) {
        if (instancia == null) {
            instancia = new PersistenciaLocal(context);
        }
        return instancia;
    }


    public Usuario getUsuario() {
        String resultado = sPreference.getString(KEY_USUARIO, EMPTY);
        if (EMPTY.equals(resultado)) {
            return null;
        }
        return JsonConverter.getUsuario(resultado);
    }

    public void setUsuario(Usuario usuario) {
        editor.putString(KEY_USUARIO, JsonConverter.toJsonString(usuario));
        editor.apply();
    }

    public Ingesta getMedicamento() {
        String resultado = sPreference.getString(KEY_MEDICAMENTO, EMPTY);
        if (EMPTY.equals(resultado)) {
            return null;
        }
        return JsonConverter.getIngesta(resultado);
    }

    public void setMedicamento(Ingesta ingesta) {
        editor.putString(KEY_MEDICAMENTO, JsonConverter.toJsonString(ingesta));
        editor.apply();
    }

    public Ingesta getBebida() {
        String resultado = sPreference.getString(KEY_BEBIDA, EMPTY);
        if (EMPTY.equals(resultado)) {
            return null;
        }
        return JsonConverter.getIngesta(resultado);
    }

    public void setBebida(Ingesta ingesta) {
        editor.putString(KEY_BEBIDA, JsonConverter.toJsonString(ingesta));
        editor.apply();
    }

    public Historial getHistorial() {
        String resultado = sPreference.getString(KEY_HISTORIAL, EMPTY);
        if (EMPTY.equals(resultado)) {
            return null;
        }
        return JsonConverter.getHistorial(resultado);
    }

    public void setHistorial(Historial historial) {
        editor.putString(KEY_HISTORIAL, JsonConverter.toJsonString(historial));
        editor.apply();
    }

    public void eliminarMedicamento() {
        editor.remove(KEY_MEDICAMENTO);
        editor.apply();
    }

    public void eliminarBebida() {
        editor.remove(KEY_BEBIDA);
        editor.apply();
    }

    public void eliminarHistorial() {
        editor.remove(KEY_HISTORIAL);
        editor.apply();
    }

    //deberia limpiarse cuando ingresa un nuevo usuario;
    public void limpiar(){
        editor.clear();
        editor.apply();
    }

}
