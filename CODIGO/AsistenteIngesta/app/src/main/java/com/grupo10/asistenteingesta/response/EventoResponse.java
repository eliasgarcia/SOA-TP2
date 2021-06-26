package com.grupo10.asistenteingesta.response;

import com.google.gson.annotations.SerializedName;

public class EventoResponse {

    private Boolean success;
    private String env;
    @SerializedName("event")
    private EventResponse event;


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public EventResponse getEvent() {
        return event;
    }

    public void setEvent(EventResponse event) {
        this.event = event;
    }
}
