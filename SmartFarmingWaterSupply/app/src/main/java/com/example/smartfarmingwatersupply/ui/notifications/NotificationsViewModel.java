package com.example.smartfarmingwatersupply.ui.notifications;


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

import com.example.smartfarmingwatersupply.responseType.AlarmResponse;
import com.example.smartfarmingwatersupply.Node;
import com.example.smartfarmingwatersupply.NodeRepository;
import com.example.smartfarmingwatersupply.ThingsboardService;
import com.example.smartfarmingwatersupply.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Node>> nodesLiveData = new MutableLiveData<>();
    private NodeRepository nodeRepository;
    private final MutableLiveData<List<AlarmResponse.AlarmData>> alarmsLiveData = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long POLLING_INTERVAL = TimeUnit.SECONDS.toMillis(5); // 5 seconds
    private String token;
    private Node selectedNode;
    private String selectedNodeId;

    public NotificationsViewModel(@NonNull Application application) {
        super(application);
        nodeRepository = new NodeRepository();
        nodesLiveData.postValue(nodeRepository.getNodes().getValue());

        // Get the token from SharedPreferences
        SharedPreferences sharedPreferences = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("UserToken", "");

        // Start the polling to fetch latest alarms data
        handler.post(fetchAlarmsRunnable);
    }

    public void setSelectedNode(Node node) {
        if (node != null) {
            selectedNodeId = node.getDeviceId();
            Log.e("Alarm", "selectedNodeID:" + selectedNodeId);
            fetchAlarmsForNode();
        } else {
            selectedNodeId = null;
            alarmsLiveData.postValue(new ArrayList<>());
        }
    }

    public LiveData<List<Node>> getNodesLiveData() {
        return nodesLiveData;
    }

    public LiveData<List<AlarmResponse.AlarmData>> getAlarmsLiveData() {
        return alarmsLiveData;
    }


    private final Runnable fetchAlarmsRunnable = new Runnable() {
        @Override
        public void run() {
            fetchAlarmsForNode();
            handler.postDelayed(this, POLLING_INTERVAL);
        }
    };

    private void fetchAlarmsForNode() {
        if (selectedNodeId != null && token != null && !token.isEmpty()) {
            ThingsboardService service = ServiceGenerator.createService(ThingsboardService.class);
            String authHeader = "Bearer " + token; // prefixing the token with "Bearer "
            Call<AlarmResponse> call = service.getAlarms(authHeader, selectedNodeId, 3, 0);

            call.enqueue(new Callback<AlarmResponse>() {
                @Override
                public void onResponse(@NonNull Call<AlarmResponse> call, @NonNull Response<AlarmResponse> response) {
                    if (response.isSuccessful()) {
                        AlarmResponse alarmResponse = response.body();
                        if (alarmResponse != null) {
                            // Post the alarms data to LiveData
                            List<AlarmResponse.AlarmData> filteredAlarms = filterAlarmsByNode(alarmResponse.getData(), selectedNodeId);
                            alarmsLiveData.postValue(filteredAlarms);
                            Log.e("NotificationsViewModel", "Response from alarms data is successful" + alarmResponse);
                        }
                    } else {
                        Log.e("NotificationsViewModel", "Response from alarms data is not successful");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AlarmResponse> call, @NonNull Throwable t) {
                    // Handle failure to communicate with the server
                }
            });
        }
    }

    private List<AlarmResponse.AlarmData> filterAlarmsByNode(List<AlarmResponse.AlarmData> alarms, String nodeId) {
        List<AlarmResponse.AlarmData> filteredAlarms = new ArrayList<>();
        Log.d("NotificationsViewModel", "Filtering alarms for node ID: " + nodeId);
        for (AlarmResponse.AlarmData alarm : alarms) {
            if (alarm.getOriginator() != null && nodeId.equals(alarm.getOriginator().getId())) {
                filteredAlarms.add(alarm);
            }
        }
        return filteredAlarms;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(fetchAlarmsRunnable); // Stop polling
    }

    public void startPolling() {
        handler.post(fetchAlarmsRunnable);
    }
    public void stopPolling() {
        handler.removeCallbacks(fetchAlarmsRunnable);
    }

}