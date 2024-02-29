package com.example.smartfarmingwatersupply;

import androidx.annotation.NonNull;

public class Node {
    private int nodeId;
    private String nodeName;
    private String deviceId;
    private String deviceAccessToken;
    private String pHValue;
    private String waterConductivity;
    private String waterLevel;
    private boolean valveStatus;

    private String attributeKey;
    private String deviceState;
    private String valveState;
    private String 	limitConductivityUpper;
    private String 	limitPhLower;
    private String limitPhUpper;
    private String 	limitWaterLevelLower;
    private String 	limitWaterLevelMidLower;
    private String limitWaterLevelMidUpper;
    private String 	limitWaterLevelUpper;

    public Node(int nodeId, String deviceId, String deviceAccessToken) {
        this.nodeId = nodeId;
        this.nodeName = "Node " + nodeId;
        this.deviceId = deviceId;
        this.deviceAccessToken = deviceAccessToken;
    }

    // Getters
    public int getNodeId() {
        return nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceAccessToken() {
        return deviceAccessToken;
    }

    public String getPHValue() {
        return pHValue;
    }

    public String getWaterConductivity() {
        return waterConductivity;
    }

    public String getWaterLevel() {
        return waterLevel;
    }

    public boolean getValveStatus() {
        return valveStatus;
    }

    public String getDeviceState() {
        return deviceState;
    }

    public String getLimitConductivityUpper() {
        return limitConductivityUpper;
    }

    public String getLimitPhLower() {
        return limitPhLower;
    }

    public String getLimitPhUpper() {
        return limitPhUpper;
    }

    public String getLimitWaterLevelLower() {
        return limitWaterLevelLower;
    }

    public String getLimitWaterLevelMidLower() {
        return limitWaterLevelMidLower;
    }

    public String getLimitWaterLevelMidUpper() {
        return limitWaterLevelMidUpper;
    }

    public String getLimitWaterLevelUpper() {
        return limitWaterLevelUpper;
    }

    // Setters
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void setPHValue(String pHValue) {
        this.pHValue = pHValue;
    }

    public void setWaterConductivity(String waterConductivity) {
        this.waterConductivity = waterConductivity;
    }

    public void setWaterLevel(String waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void setValveStatus(boolean valveStatus) {
        this.valveStatus = valveStatus;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setpHValue(String pHValue) {
        this.pHValue = pHValue;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public void setDeviceState(String deviceState) {
        this.deviceState = deviceState;
    }

    public void setValveState(String valveState) {
        this.valveState = valveState;
    }

    public void setLimitConductivityUpper(String limitConductivityUpper) {
        this.limitConductivityUpper = limitConductivityUpper;
    }

    public void setLimitPhLower(String limitPhLower) {
        this.limitPhLower = limitPhLower;
    }

    public void setLimitPhUpper(String limitPhUpper) {
        this.limitPhUpper = limitPhUpper;
    }

    public void setLimitWaterLevelLower(String limitWaterLevelLower) {
        this.limitWaterLevelLower = limitWaterLevelLower;
    }

    public void setLimitWaterLevelMidLower(String limitWaterLevelMidLower) {
        this.limitWaterLevelMidLower = limitWaterLevelMidLower;
    }

    public void setLimitWaterLevelMidUpper(String limitWaterLevelMidUpper) {
        this.limitWaterLevelMidUpper = limitWaterLevelMidUpper;
    }

    public void setLimitWaterLevelUpper(String limitWaterLevelUpper) {
        this.limitWaterLevelUpper = limitWaterLevelUpper;
    }

    // Override toString() because the ArrayAdapter uses it to determine what text to display for each item
    @NonNull
    @Override
    public String toString() {
        return getNodeName();
    }
}
