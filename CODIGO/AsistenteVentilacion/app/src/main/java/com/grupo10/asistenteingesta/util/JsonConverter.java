package com.grupo10.asistenteingesta.util;

import com.google.gson.Gson;
import com.grupo10.asistenteingesta.modelo.Historial;
import com.grupo10.asistenteingesta.modelo.Ingesta;
import com.grupo10.asistenteingesta.modelo.Usuario;

public class JsonConverter {

    private static Gson gson = new Gson();

    public static Usuario getUsuario(String json){
        return gson.fromJson(json,Usuario.class);
    }

    public static Ingesta getIngesta(String json){
        return gson.fromJson(json, Ingesta.class);
    }


    public static Historial getHistorial(String json){
        return gson.fromJson(json,Historial.class);
    }

    public static String toJsonString(Object o){
        return gson.toJson(o);
    }
}
