package com.example.smartfarmingwatersupply;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommandAdapter extends RecyclerView.Adapter<CommandAdapter.CommandViewHolder>{

    private CommandDataset commandDataset;
    private String deviceAccessToken;
    private String deviceId;
    private String token;

    public CommandAdapter(CommandDataset commandDataset, Context context) {
        this.commandDataset = commandDataset;
        // Get the token from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        this.token = sharedPreferences.getString("UserToken", "");
    }

    @NonNull
    @Override
    public CommandAdapter.CommandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.command_item, parent, false);
        return new CommandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommandAdapter.CommandViewHolder holder, int position) {
        Command command = commandDataset.getCommandAtPosition(position);
        holder.imageView.setImageResource(command.getImageResourceId());
        holder.attributeKey.setText(command.getAttributeKey());
        holder.currentValue.setText(command.getCurrentValue());

        holder.bSendCommand.setOnClickListener(view -> {
            String newValueToSend = holder.newValue.getText().toString();

            // Perform validation for specific attributes
            if (isBooleanValidationRequired(command.getAttributeKey())) {
                if (!isValidValueForStates(newValueToSend)) {
                    // Show toast message if the value is not valid
                    Toast.makeText(holder.itemView.getContext(),
                            "Value must be 0 or 1", Toast.LENGTH_SHORT).show();
                    return; // Do not proceed with sending the value
                }
            } else if (isPhValidationRequired(command.getAttributeKey())) {
                if (!isValidValueForPh(newValueToSend)) {
                    // Show toast message if the value is not valid
                    Toast.makeText(holder.itemView.getContext(),
                            "Value must be between 0 and 14", Toast.LENGTH_SHORT).show();
                    return; // Do not proceed with sending the value
                }
            } else if (isValidationRequired(command.getAttributeKey())) {
                if (!isValidValue(newValueToSend)) {
                    // Show toast message if the value is not valid
                    Toast.makeText(holder.itemView.getContext(),
                            "Value must be between 0 and 100", Toast.LENGTH_SHORT).show();
                    return; // Do not proceed with sending the value
                }
            }

            Log.e("SendingCommands", "Sending Value: " + newValueToSend);
            sendValueToServer(command.getAttributeKey(), newValueToSend);
        });
    }

    private boolean isValidationRequired(String attributeKey) {
        // Define which attributes require validation
        return attributeKey.equals("Conductivity Upper limit") ||
                attributeKey.equals("Water Level Lower limit") ||
                attributeKey.equals("Water Level Mid Lower limit") ||
                attributeKey.equals("Water Level Mid Upper limit") ||
                attributeKey.equals("Water Level Upper limit");
    }

    private boolean isPhValidationRequired(String attributeKey) {
        return attributeKey.equals("Ph Lower limit") ||
                attributeKey.equals("Ph Upper limit");
    }

    private boolean isBooleanValidationRequired(String attributeKey) {
        return attributeKey.equals("Device State") ||
                attributeKey.equals("Valve State") ||
                attributeKey.equals("Pump State") ;
    }

    private boolean isValidValue(String value) {
        try {
            int intValue = Integer.parseInt(value);
            // Check if the value is between 0 and 100
            return intValue >= 0 && intValue <= 100;
        } catch (NumberFormatException e) {
            // Value is not an integer
            return false;
        }
    }

    private boolean isValidValueForPh(String value) {
        try {
            int intValue = Integer.parseInt(value);
            // Check if the value is between 0 and 100
            return intValue >= 0 && intValue <= 14;
        } catch (NumberFormatException e) {
            // Value is not an integer
            return false;
        }
    }

    private boolean isValidValueForStates(String value) {
        try {
            int intValue = Integer.parseInt(value);
            // Check if the value is between 0 and 100
            return intValue >= 0 && intValue <= 1;
        } catch (NumberFormatException e) {
            // Value is not an integer
            return false;
        }
    }

    private void sendValueToServer(String attributeKey, String newValue) {
        if (deviceAccessToken == null || deviceAccessToken.isEmpty()) {
            // Handle case where device access token is not available
            return;
        }

        JsonObject telemetryData = new JsonObject();
        String attributeKeyToSend = convertAttributeKey(attributeKey);
        telemetryData.addProperty(attributeKeyToSend, newValue);

        // Now make the API call using Retrofit
        ThingsboardService service = ServiceGenerator.createService(ThingsboardService.class);
        Call<Void> call = service.sendTel(telemetryData, deviceAccessToken);
        //Call<Void> call = service.sendTelemetry(token, telemetryData, deviceId);
        Log.e("SendingCommands", "Sending");
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    Log.e("SendingCommands", "Response successful");
                } else {
                    // Handle unsuccessful response
                    Log.e("SendingCommands", "Response unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure to communicate with the server
            }
        });
    }

    private String convertAttributeKey(String attributeKey) {
        String convertedKey = "";
        switch (attributeKey) {
            case "Device State":
                convertedKey = "deviceState";
                break;
            case "Valve State":
                convertedKey = "valveState";
                break;
            case "Pump State":
                convertedKey = "pumpState";
                break;
            case "Conductivity Upper limit":
                convertedKey = "limitConductivityUPPER";
                break;
            case "Ph Lower limit":
                convertedKey = "limitPhLOWER";
                break;
            case "Ph Upper limit":
                convertedKey = "limitPhUPPER";
                break;
            case "Water Level Lower limit":
                convertedKey = "limitWaterLevelLOWER";
                break;
            case "Water Level Mid Lower limit":
                convertedKey = "limitWaterLevelMidLOWER";
                break;
            case "Water Level Mid Upper limit":
                convertedKey = "limitWaterLevelMidUPPER";
                break;
            case "Water Level Upper limit":
                convertedKey = "limitWaterLevelUPPER";
                break;
        }

        return convertedKey;
    }


    @Override
    public int getItemCount() {
        return commandDataset.getSize();
    }

    public void setAttributes(CommandDataset commandDataset) {
        this.commandDataset = commandDataset;
        notifyDataSetChanged();
    }

    // Setter for the device access token
    public void setDeviceAccessToken(String deviceAccessToken) {
        this.deviceAccessToken = deviceAccessToken;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public static class CommandViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView attributeKey, currentValue;
        EditText newValue;
        ImageButton bSendCommand;

        public CommandViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            attributeKey = itemView.findViewById(R.id.attributeKey);
            currentValue = itemView.findViewById(R.id.attributeCurrentValue);
            newValue = itemView.findViewById(R.id.attributeNewValue);
            bSendCommand = itemView.findViewById(R.id.imageButton);
        }
    }
}
