package com.grupo10.asistenteingesta.client;

import com.grupo10.asistenteingesta.dto.EventoDTO;
import com.grupo10.asistenteingesta.dto.UsuarioDTO;
import com.grupo10.asistenteingesta.response.EventoResponse;
import com.grupo10.asistenteingesta.response.RegistroResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface EventoClient {
    @Headers({
            "content-type: application/json"
    })

    @POST("api/api/event")
    Call<EventoResponse> registrarEvento(@Header("Authorization") String token, @Body EventoDTO eventoDTO);
}
