package com.example.dami.retrofit;

import com.example.dami.models.BloodCenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CenterApi {
    @GET("api/centers")
    Call<List<BloodCenter>> getAllCenters();
}