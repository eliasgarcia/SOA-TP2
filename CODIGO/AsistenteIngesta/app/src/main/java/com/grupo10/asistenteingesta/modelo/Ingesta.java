package com.grupo10.asistenteingesta.modelo;

import java.util.Calendar;

public class Ingesta {
    private String nombre;
    private Integer distancia;
    private Calendar proxima;

    public Ingesta(Ingesta ingesta){
        this.nombre = ingesta.nombre;
        this.distancia = ingesta.distancia;
    }

    public Ingesta(){}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getDistancia() {
        return distancia;
    }

    public void setDistancia(Integer distancia) {
        this.distancia = distancia;
    }

    public Calendar getProxima() {
        return proxima;
    }

    public void setProxima(Calendar proxima) {
        this.proxima = proxima;
    }
}
