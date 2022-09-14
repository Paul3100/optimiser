package com.example.front_end;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Apinterface {
    @FormUrlEncoded
    @POST("/optimise")
    Call<pic> getUserInformation(@Field("bytes") String bytes);
}
