package com.grupo10.asistenteingesta.client;

import com.grupo10.asistenteingesta.dto.UsuarioDTO;
import com.grupo10.asistenteingesta.response.RegistroResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioClient {

    @POST("api/api/register")
    Call<RegistroResponse> registrarUsuario(@Body UsuarioDTO usuarioDTO);
}
