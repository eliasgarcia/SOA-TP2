package com.grupo10.asistenteingesta.modelo;

import java.util.List;

public class Historial {
    private List<EstadoIngesta> ingestas;


    public Historial(List<EstadoIngesta> ingestas) {
        this.ingestas = ingestas;
    }

    public Historial() {}

    public List<EstadoIngesta> getIngestas() {
        return ingestas;
    }

    public void setIngestas(List<EstadoIngesta> ingestas) {
        this.ingestas = ingestas;
    }
}
