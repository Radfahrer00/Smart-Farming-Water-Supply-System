package com.example.smartfarmingwatersupply.responseType;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * The AttributeResponse class encapsulates the response structure for shared attributes,
 * specifically designed for handling responses related to device attributes in
 * a smart farming water supply system.
 * It contains a nested {@link SharedAttributes} class that holds detailed attribute information.
 */
public class AttributeResponse {

    @SerializedName("shared")
    private SharedAttributes shared;

    /**
     * Retrieves the shared attributes from the response.
     *
     * @return An instance of {@link SharedAttributes} containing the detailed attributes.
     */
    public SharedAttributes getShared() {
        return shared;
    }

    /**
     * The SharedAttributes class represents a collection of device-related attributes
     * such as device state, pump state, valve state, and various limit settings for
     * conductivity, pH levels, and water levels. Each attribute is annotated with
     * {@link SerializedName} to ensure proper mapping between JSON fields and class properties.
     */
    public static class SharedAttributes {
        @SerializedName("deviceID")
        private int deviceId;

        @SerializedName("deviceState")
        private boolean deviceState;

        @SerializedName("limitConductivityUPPER")
        private int limitConductivityUpper;

        @SerializedName("limitPhLOWER")
        private int limitPhLower;

        @SerializedName("limitPhUPPER")
        private int limitPhUpper;

        @SerializedName("limitWaterLevelLOWER")
        private int limitWaterLevelLower;

        @SerializedName("limitWaterLevelMidLOWER")
        private int limitWaterLevelMidLower;

        @SerializedName("limitWaterLevelMidUPPER")
        private int limitWaterLevelMidUpper;

        @SerializedName("limitWaterLevelUPPER")
        private int limitWaterLevelUpper;

        @SerializedName("pumpState")
        private boolean pumpState;

        @SerializedName("valveState")
        private boolean valveState;

        // Getters for each attribute with Javadoc omitted for brevity
        public boolean isDeviceState() {
            return deviceState;
        }

        public int getLimitConductivityUpper() {
            return limitConductivityUpper;
        }

        public int getLimitPhLower() {
            return limitPhLower;
        }

        public int getLimitPhUpper() {
            return limitPhUpper;
        }

        public int getLimitWaterLevelLower() {
            return limitWaterLevelLower;
        }

        public int getLimitWaterLevelMidLower() {
            return limitWaterLevelMidLower;
        }

        public int getLimitWaterLevelMidUpper() {
            return limitWaterLevelMidUpper;
        }

        public int getLimitWaterLevelUpper() {
            return limitWaterLevelUpper;
        }

        public boolean isPumpState() {
            return pumpState;
        }

        public boolean isValveState() {
            return valveState;
        }

        /**
         * Provides a string representation of the SharedAttributes object,
         * including its device and limit attributes.
         *
         * @return A string representation of the SharedAttributes object.
         */
        @NonNull
        @Override
        public String toString() {
            return "SharedAttributes{" +
                    "deviceId=" + deviceId +
                    ", deviceState=" + deviceState +
                    ", limitConductivityUpper=" + limitConductivityUpper +
                    ", limitPhLower=" + limitPhLower +
                    ", limitPhUpper=" + limitPhUpper +
                    ", limitWaterLevelLower=" + limitWaterLevelLower +
                    ", limitWaterLevelMidLower=" + limitWaterLevelMidLower +
                    ", limitWaterLevelMidUpper=" + limitWaterLevelMidUpper +
                    ", limitWaterLevelUpper=" + limitWaterLevelUpper +
                    ", pumpState=" + pumpState +
                    ", valveState=" + valveState +
                    '}';
        }
    }

}

