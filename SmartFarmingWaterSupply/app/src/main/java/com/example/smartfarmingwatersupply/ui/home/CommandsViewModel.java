package com.example.smartfarmingwatersupply.ui.home;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartfarmingwatersupply.responseType.AttributeResponse;
import com.example.smartfarmingwatersupply.CommandDataset;
import com.example.smartfarmingwatersupply.Node;
import com.example.smartfarmingwatersupply.NodeRepository;
import com.example.smartfarmingwatersupply.ServiceGenerator;
import com.example.smartfarmingwatersupply.ThingsboardService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommandsViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Node>> nodesLiveData = new MutableLiveData<>();
    private final NodeRepository nodeRepository;
    private final MutableLiveData<CommandDataset> commandDatasetMutableLiveData = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long POLLING_INTERVAL = TimeUnit.SECONDS.toMillis(5); // 5 seconds
    private String token;
    private String selectedNodeId;
    private String deviceAccessToken;
    String keys = "deviceID,deviceState,limitConductivityUPPER,limitPhLower,limitPhUPPER,limitWaterLevelLOWER,limitWaterLevelMidLOWER,limitWaterLevelMidUPPER,limitWaterLevelUPPER,valveState";

    public CommandsViewModel(@NonNull Application application) {
        super(application);
        nodeRepository = new NodeRepository(); // Initialize your repository
        nodesLiveData.postValue(nodeRepository.getNodes().getValue());

        // Get the token from SharedPreferences
        SharedPreferences sharedPreferences = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("UserToken", "");

        // Start the polling to fetch latest alarms data
        handler.post(fetchCommandsRunnable);
    }

    public void setSelectedNode(Node node) {
        if (node != null) {
            selectedNodeId = node.getDeviceId();
            deviceAccessToken = node.getDeviceAccessToken();
            Log.e("Alarm", "selectedNodeID:" + selectedNodeId);
            fetchAttributeValuesForNode(node);
        } else {
            selectedNodeId = null;
            deviceAccessToken = null;
            commandDatasetMutableLiveData.postValue(new CommandDataset());
        }
    }

    public LiveData<List<Node>> getNodesLiveData() {
        return nodesLiveData;
    }

    public MutableLiveData<CommandDataset> getCommandDatasetMutableLiveData() {
        return commandDatasetMutableLiveData;
    }

    // Add a getter for the deviceAccessToken
    public String getDeviceAccessToken() {
        return deviceAccessToken;
    }

    private final Runnable fetchCommandsRunnable = new Runnable() {
        @Override
        public void run() {
            Node selectedNode = nodeRepository.getNodeFromDeviceId(selectedNodeId);
            fetchAttributeValuesForNode(selectedNode);
            handler.postDelayed(this, POLLING_INTERVAL);
        }
    };

    private void fetchAttributeValuesForNode(Node node) {
        if (selectedNodeId != null && token != null && !token.isEmpty()) {
            ThingsboardService service = ServiceGenerator.createService(ThingsboardService.class);
            String authHeader = "Bearer " + token; // prefixing the token with "Bearer "
            Call<AttributeResponse> call = service.getDeviceAttributes(authHeader, node.getDeviceAccessToken(),keys);
            call.enqueue(new Callback<AttributeResponse>() {
                @Override
                public void onResponse(@NonNull Call<AttributeResponse> call, @NonNull Response<AttributeResponse> response) {
                    if (response.isSuccessful()) {
                        AttributeResponse attributeResponse = response.body();
                        if (attributeResponse != null) {
                            CommandDataset commandDataset = new CommandDataset();
                            updateCommandsValues(attributeResponse, commandDataset);
                            // Post the updated dataset to LiveData
                            Log.e("CommandsViewModel", "New Value to post" + commandDataset.getCommandAtPosition(4).getCurrentValue());
                            commandDatasetMutableLiveData.postValue(commandDataset);

                            Log.e("CommandsViewModel", "Response from commands data is successful" + attributeResponse);
                        }
                    } else {
                        Log.e("CommandsViewModel", "Response from commands data is not successful");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AttributeResponse> call, @NonNull Throwable t) {
                    // Handle failure to communicate with the server
                }
            });
        }
    }

    private static void updateCommandsValues(AttributeResponse attributeResponse, CommandDataset commandDataset) {
        AttributeResponse.SharedAttributes sharedAttributes = attributeResponse.getShared();

        Log.e("CommandsViewModel", "Shared Attributes" + sharedAttributes.toString());

        commandDataset.updateCommand("Device State", String.valueOf(sharedAttributes.isDeviceState()));
        commandDataset.updateCommand("Valve State", String.valueOf(sharedAttributes.isValveState()));
        commandDataset.updateCommand("Pump State", String.valueOf(sharedAttributes.isPumpState()));
        commandDataset.updateCommand("Conductivity Upper limit", String.valueOf(sharedAttributes.getLimitConductivityUpper()));
        commandDataset.updateCommand("Ph Lower limit", String.valueOf(sharedAttributes.getLimitPhLower()));
        commandDataset.updateCommand("Ph Upper limit", String.valueOf(sharedAttributes.getLimitPhUpper()));
        commandDataset.updateCommand("Water Level Lower limit", String.valueOf(sharedAttributes.getLimitWaterLevelLower()));
        commandDataset.updateCommand("Water Level Mid Lower limit", String.valueOf(sharedAttributes.getLimitWaterLevelMidLower()));
        commandDataset.updateCommand("Water Level Mid Upper limit", String.valueOf(sharedAttributes.getLimitWaterLevelMidUpper()));
        commandDataset.updateCommand("Water Level Upper limit", String.valueOf(sharedAttributes.getLimitWaterLevelUpper()));
    }

    public void startPolling() {
        handler.post(fetchCommandsRunnable);
    }
    public void stopPolling() {
        handler.removeCallbacks(fetchCommandsRunnable);
    }
}