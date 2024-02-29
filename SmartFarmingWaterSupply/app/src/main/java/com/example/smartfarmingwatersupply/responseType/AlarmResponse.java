package com.example.smartfarmingwatersupply.responseType;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * The AlarmResponse class represents the response structure for alarm data received from the server.
 * It primarily contains a list of {@link AlarmData} objects, each representing an individual alarm's details.
 */
public class AlarmResponse {
    private List<AlarmData> data;

    /**
     * Returns the list of alarm data.
     *
     * @return A list of {@link AlarmData} objects.
     */
    public List<AlarmData> getData() {
        return data;
    }

    /**
     * The AlarmData class represents the detailed data of an individual alarm,
     * including its type, severity, status, creation time, and end time among other details.
     */
    public static class AlarmData {
        private long createdTime;
        private String details;
        private long endTs;
        private IdWrapper id;
        private String name;
        private Originator originator;
        private String severity;
        private String status;
        private String type;

        /**
         * Constructs an AlarmData object with specified alarm details.
         *
         * @param type The type of the alarm.
         * @param severity The severity level of the alarm.
         * @param status The current status of the alarm.
         * @param createdTime The creation timestamp of the alarm.
         */
        public AlarmData(String type, String severity, String status, long createdTime) {
            this.type = type;
            this.severity = severity;
            this.status = status;
            this.createdTime = createdTime;
        }

        // Getters for each field with Javadoc omitted for brevity
        public long getCreatedTime() {
            return createdTime;
        }

        public IdWrapper getId() {
            return id;
        }

        public Originator getOriginator() {
            return originator;
        }

        public String getSeverity() {
            return severity;
        }

        public String getStatus() {
            return status;
        }

        public String getType() {
            return type;
        }

        /**
         * Provides a string representation of the AlarmData object, including its critical attributes.
         *
         * @return A string representation of the AlarmData object.
         */
        @NonNull
        @Override
        public String toString() {
            return "AlarmData{" +
                    ", createdTime=" + createdTime +
                    ", name='" + name + '\'' +
                    ", severity='" + severity + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }

        /**
         * The IdWrapper class is a simple wrapper for the ID of an alarm,
         * providing a straightforward way to access the alarm ID.
         */
        public static class IdWrapper {
            private String id;

            public String getId() {
                return id;
            }
        }

        /**
         * The Originator class represents the originator of an alarm,
         * detailing its entity type and ID.
         */
        public static class Originator {
            @SerializedName("entityType")
            private String entityType;
            private String id;

            public String getId() {
                return id;
            }
        }
    }
}
