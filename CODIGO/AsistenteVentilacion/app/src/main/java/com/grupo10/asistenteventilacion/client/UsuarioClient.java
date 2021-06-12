package com.grupo10.asistenteventilacion.client;

import com.grupo10.asistenteventilacion.dto.UsuarioDTO;
import com.grupo10.asistenteventilacion.response.RegistroResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioClient {

    @POST("api/api/register")
    Call<RegistroResponse> registrarUsuario(@Body UsuarioDTO usuarioDTO);
}
