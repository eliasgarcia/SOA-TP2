package com.grupo10.asistenteingesta.client;

import com.grupo10.asistenteingesta.dto.LoginDTO;
import com.grupo10.asistenteingesta.response.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginClient {

    @POST("api/api/login")
    Call<LoginResponse> login(@Body LoginDTO loginDTO);
}
