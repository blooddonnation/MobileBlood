package com.example.dami.retrofit;

import com.example.dami.models.JwtResponse;
import com.example.dami.models.LoginRequest;
import com.example.dami.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/auth/register")
    Call<Void> register(@Body RegisterRequest registerRequest);

    @POST("api/auth/login")
    Call<JwtResponse> login(@Body LoginRequest loginRequest);
} 