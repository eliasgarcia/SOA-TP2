package com.grupo10.asistenteingesta.modelo;

public class Ingesta {
    private String nombre;
    private Integer distancia;


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
}
