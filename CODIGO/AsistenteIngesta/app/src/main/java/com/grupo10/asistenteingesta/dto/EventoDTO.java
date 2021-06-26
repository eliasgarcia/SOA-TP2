package com.grupo10.asistenteingesta.dto;

import com.google.gson.annotations.SerializedName;

public class EventoDTO {
    private String env = "PROD"; //TEST” |”PROD”

    @SerializedName("type_events")
    private String tipoEvento;

    private String description;


    public EventoDTO(){

    }

    public EventoDTO(String tipoEvento, String description){
        this.tipoEvento = tipoEvento;
        this.description = description;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
