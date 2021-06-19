package com.grupo10.asistenteingesta.util;

public enum CodigoIngesta {
    MEDICAMENTO(2000),
    BEBIDA(4000);

    private final int value;

    CodigoIngesta(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}