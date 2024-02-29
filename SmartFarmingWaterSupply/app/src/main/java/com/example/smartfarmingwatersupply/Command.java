package com.example.smartfarmingwatersupply;

public class Command {

    private String attributeKey;
    private String currentValue;
    private float newValue;
    private int imageResourceId;

    public Command(String attributeKey, int imageResourceId) {
        this.attributeKey = attributeKey;
        this.imageResourceId = imageResourceId;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public float getNewValue() {
        return newValue;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public void setNewValue(float newValue) {
        this.newValue = newValue;
    }
}
