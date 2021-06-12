package com.grupo10.asistenteventilacion.dto;

import com.grupo10.asistenteventilacion.modelo.Usuario;

public class UsuarioDTO {

    private String env = "TEST"; //TEST” |”PROD”
    private String name;
    private String lastname;
    private Integer dni;
    private String email;
    private String password;
    private Integer commission;
    private Integer group;

    public UsuarioDTO(Usuario usuario){
        this.name = usuario.getNombre();
        this.lastname = usuario.getApellido();
        this.dni = usuario.getDni();
        this.email = usuario.getEmail();
        this.password = usuario.getContrasenia();
        this.commission = usuario.getComision();
        this.group = usuario.getGrupo();
    }

    public UsuarioDTO(){
    }


    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCommission() {
        return commission;
    }

    public void setCommission(Integer commission) {
        this.commission = commission;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }
}
