package com.grupo10.asistenteingesta.dto;

public class ErrorDTO {

    private Boolean success;
    private String msg;

    public ErrorDTO(){
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
