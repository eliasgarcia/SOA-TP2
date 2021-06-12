package com.grupo10.asistenteventilacion.modelo;

public class Usuario {
    private String nombre;
    private String apellido;
    private Integer dni;
    private String email;
    private String contrasenia;
    private Integer comision = 3900;// comision dia miercoles
    private Integer grupo = 10;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
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

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public Integer getComision() {
        return comision;
    }

    public Integer getGrupo() {
        return grupo;
    }

}