package com.example.smartfarmingwatersupply;

import com.example.smartfarmingwatersupply.responseType.AlarmResponse;
import com.example.smartfarmingwatersupply.responseType.AttributeResponse;
import com.example.smartfarmingwatersupply.responseType.TelemetryResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ThingsboardService {

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("auth/login")
    Call<JsonObject> getUserToken(@Body JsonObject user);

    @Headers({"Accept: application/json"})
    @GET("plugins/telemetry/DEVICE/{device_id}/values/timeseries")
    Call<TelemetryResponse> getLatestTelemetry(
            @Header("X-Authorization") String token,
            @Path("device_id") String deviceId,
            @Query("keys") String keys
    );

    @Headers({"Accept: text/plain", "Content-Type: application/json"})
    @POST("v1/{device_access_token}/telemetry")
    Call<Void> sendTel(@Body JsonObject tele, @Path ("device_access_token") String device_access_token);

    @Headers({"Accept: application/json"})
    @GET("v1/{device_access_token}/attributes?sharedKeys-mode")
    Call<JsonObject> getMode(@Path ("device_access_token") String device_access_token);

    @Headers({"Accept: application/json"})
    @GET("v1/{device_access_token}/attributes")
    Call<AttributeResponse> getDeviceAttributes(
            @Header("X-Authorization") String token,
            @Path("device_access_token") String deviceAccessToken,
            @Query("keys") String keys // This will be a comma-separated list of attribute keys
    );

    @GET("alarm/DEVICE/{entityId}")
    Call<AlarmResponse> getAlarms(
            @Header("X-Authorization") String authHeader,
            @Path("entityId") String entityId,
            @Query("pageSize") int pageSize,
            @Query("page") int page
    );

}
