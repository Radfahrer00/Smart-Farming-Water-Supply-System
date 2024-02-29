package com.example.smartfarmingwatersupply.responseType;

import androidx.annotation.NonNull;

import com.example.smartfarmingwatersupply.SensorData;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * The TelemetryResponse class encapsulates the telemetry data response from the server for the sensors,
 * specifically designed for handling sensor data related to conductivity, pH levels,
 * and water levels in a smart farming water supply system. It contains lists of
 * {@link SensorData} objects for each type of telemetry data.
 */
public class TelemetryResponse {
    private List<SensorData> conductivity;
    private List<SensorData> ph;
    @SerializedName("water level")
    private List<SensorData> waterLevel;

    // Getters for each field
    public List<SensorData> getConductivity() {
        return conductivity;
    }

    public List<SensorData> getPh() {
        return ph;
    }

    public List<SensorData> getWaterLevel() {
        return waterLevel;
    }

    /**
     * Provides a string representation of the TelemetryResponse object, including lists of sensor data
     * for conductivity, pH, and water level. This method facilitates easy logging and debugging of
     * telemetry data responses.
     *
     * @return A string representation of the TelemetryResponse object.
     */
    @NonNull
    @Override
    public String toString() {
        return "TelemetryResponse{" +
                ", conductivity=" + conductivity +
                ", ph=" + ph +
                ", waterLevel=" + waterLevel +
                '}';
    }
}

