package com.grupo10.asistenteingesta.client;

import retrofit2.converter.gson.GsonConverterFactory;

public class RefreshTokenBuilder {
    private static RefreshTokenClient cliente;
    private static final String HOST = "http://so-unlam.net.ar/";

    public static RefreshTokenClient getClient() {
        if (cliente == null) {
            cliente = new retrofit2.Retrofit.Builder()
                    .baseUrl(HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RefreshTokenClient.class);
        }
        return cliente;
    }
}
