package com.grupo10.asistenteventilacion.client;

import retrofit2.converter.gson.GsonConverterFactory;

public class LoginClientBuilder {
    private static LoginClient cliente;
    private static final String HOST = "http://so-unlam.net.ar/";

    public static LoginClient getClient() {
        if (cliente == null) {
            cliente = new retrofit2.Retrofit.Builder()
                    .baseUrl(HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(LoginClient.class);
        }

        return cliente;
    }
}
