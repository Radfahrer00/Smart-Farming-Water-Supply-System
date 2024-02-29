package com.example.smartfarmingwatersupply.ui.dashboard;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.smartfarmingwatersupply.Node;
import com.example.smartfarmingwatersupply.NodeRepository;
import com.example.smartfarmingwatersupply.SensorData;
import com.example.smartfarmingwatersupply.ServiceGenerator;
import com.example.smartfarmingwatersupply.responseType.TelemetryResponse;
import com.example.smartfarmingwatersupply.ThingsboardService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.os.Handler;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Node>> nodesLiveData = new MutableLiveData<>();
    private final NodeRepository nodeRepository;
    private final Observer<List<Node>> nodeDataObserver;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long POLLING_INTERVAL = TimeUnit.SECONDS.toMillis(5); // 5 seconds
    private final String token;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        nodeRepository = new NodeRepository(); // Initialize your repository
        LiveData<List<Node>> initialNodeData = nodeRepository.getNodes(); // Fetch initial data

        // Get the token from SharedPreferences
        SharedPreferences sharedPreferences = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("UserToken", "");

        nodeDataObserver = nodesLiveData::postValue;

        // Observe the initialNodeData and post its value to nodesLiveData
        initialNodeData.observeForever(nodeDataObserver);

        // Start the polling to fetch latest data
        handler.post(fetchDataRunnable);
    }

    public LiveData<List<Node>> getNodesLiveData() {
        return nodesLiveData;
    }

    private final Runnable fetchDataRunnable = new Runnable() {
        @Override
        public void run() {
            fetchLatestTelemetryData();
            handler.postDelayed(this, POLLING_INTERVAL);
        }
    };

    private void fetchLatestTelemetryData() {
        List<Node> nodes = nodesLiveData.getValue(); // Get current list of nodes
        if (nodes != null) {
            // Clone the list to avoid concurrent modification
            List<Node> updatedNodes = new ArrayList<>(nodes);

            for (Node node : updatedNodes) {
                ThingsboardService service = ServiceGenerator.createService(ThingsboardService.class);
                String authHeader = "Bearer " + token; // prefixing the token with "Bearer "
                Call<TelemetryResponse> call = service.getLatestTelemetry(authHeader, node.getDeviceId(), "conductivity,ph,water level");
                call.enqueue(new Callback<TelemetryResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TelemetryResponse> call, @NonNull Response<TelemetryResponse> response) {
                        if (response.isSuccessful()) {
                            TelemetryResponse telemetryResponse = response.body();
                            if (telemetryResponse != null) {
                                // Assuming you've ensured that each list has exactly one element
                                SensorData conductivityData = telemetryResponse.getConductivity().get(0);
                                SensorData phData = telemetryResponse.getPh().get(0);
                                SensorData waterLevelData = telemetryResponse.getWaterLevel().get(0);
                                // Update the node with the new telemetry data
                                node.setWaterConductivity(conductivityData.getValue());
                                node.setPHValue(phData.getValue());
                                node.setWaterLevel(waterLevelData.getValue());

                                // Update LiveData on the main thread
                                handler.post(() -> {
                                    // Post the updated list to LiveData
                                    nodesLiveData.postValue(updatedNodes);
                                });

                                Log.e("DashboardViewModel", "Response from telemetry data is successful" + telemetryResponse);
                            }
                        } else {
                            Log.e("DashboardViewModel", "Response from telemetry data is not successful");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TelemetryResponse> call, @NonNull Throwable t) {
                        Log.e("DashboardViewModel", "Failed to fetch telemetry data", t);
                    }
                });
            }
            // Post the updated list to LiveData
            nodesLiveData.postValue(updatedNodes);
        }
    }

    public void startPolling() {
        handler.post(fetchDataRunnable);
    }
    public void stopPolling() {
        handler.removeCallbacks(fetchDataRunnable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        nodeRepository.getNodes().removeObserver(nodeDataObserver);
        handler.removeCallbacks(fetchDataRunnable); // Stop polling
    }
}