package com.grupo10.asistenteingesta.modelo;

public class Ingesta {
    private String nombre;
    private Integer frecuencia;


    public Ingesta(Ingesta ingesta){
        this.nombre = ingesta.nombre;
        this.frecuencia = ingesta.frecuencia;
    }

    public Ingesta(){}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(Integer frecuencia) {
        this.frecuencia = frecuencia;
    }
}
