package com.grupo10.asistenteingesta.modelo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EstadoIngesta extends Ingesta {

    private Boolean realizado;
    private Calendar hora;

    public EstadoIngesta(){}

    public EstadoIngesta(Ingesta ingesta,Boolean realizado,Calendar hora){
        super(ingesta);
        this.realizado = realizado;
        this.hora = hora;
    }

    public EstadoIngesta(Ingesta ingesta){
        super(ingesta);
    }

    public Boolean getRealizado() {
        return realizado;
    }

    public void setRealizado(Boolean realizado) {
        this.realizado = realizado;
    }

    public Calendar getHora() {
        return hora;
    }

    public String getHoraFormateada(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM hh:mm");
        if(hora!=null){
            return format.format(hora.getTime());
        }else{
            return "";
        }
    }

    public void setHora(Calendar hora) {
        this.hora = hora;
    }
}
