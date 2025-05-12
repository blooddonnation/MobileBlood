package com.example.dami.retrofit;

import com.example.dami.models.BloodDonationRequest;
import com.example.dami.models.BloodDonationRequestResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestApi {
    @POST("api/requests")
    Call<BloodDonationRequestResponse> createRequest(@Body BloodDonationRequest request);
}