package com.grupo10.asistenteingesta.client;

import com.grupo10.asistenteingesta.dto.EventoDTO;
import com.grupo10.asistenteingesta.response.EventoResponse;
import com.grupo10.asistenteingesta.response.RefreshTokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface RefreshTokenClient {
    @Headers({
            "content-type: application/json"
    })

    @PUT("api/api/refresh")
    Call<RefreshTokenResponse> actualizarToken(@Header("Authorization") String refreshToken);
}
