package com.example.dami.api;

import com.example.dami.model.GpsPosition;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PositionApi {
    @POST("api/positions")
    Call<GpsPosition> updatePosition(@Body GpsPosition position);
} 