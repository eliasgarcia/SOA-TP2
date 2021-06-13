package com.grupo10.asistenteingesta.modelo;

public class EstadoIngesta extends Ingesta {

    private Boolean estado;

    public EstadoIngesta(){}

    public EstadoIngesta(Ingesta ingesta,Boolean estado){
        super(ingesta);
        this.estado = estado;
    }

    public EstadoIngesta(Ingesta ingesta){
        super(ingesta);
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
