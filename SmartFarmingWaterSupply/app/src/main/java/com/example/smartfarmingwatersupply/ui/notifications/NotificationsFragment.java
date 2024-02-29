package com.example.smartfarmingwatersupply.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfarmingwatersupply.AlarmAdapter;
import com.example.smartfarmingwatersupply.responseType.AlarmResponse;
import com.example.smartfarmingwatersupply.Node;
import com.example.smartfarmingwatersupply.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsViewModel notificationsViewModel;
    private Spinner nodeSpinner;
    private TextView nodeTitle;
    private RecyclerView alarmsRecyclerView;
    private ArrayAdapter<Node> nodeAdapter;
    private AlarmAdapter alarmAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Spinner and RecyclerView
        nodeSpinner = binding.nodeSpinnerN;
        nodeTitle = binding.nodeTitleN;

        alarmsRecyclerView = binding.alarmsRecyclerView;
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //List<AlarmResponse.AlarmData> dummyData = generateDummyData();
        alarmAdapter = new AlarmAdapter(new ArrayList<>());
        alarmsRecyclerView.setAdapter(alarmAdapter);

        // Setup ArrayAdapter for the Spinner with nodes
        nodeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        nodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nodeSpinner.setAdapter(nodeAdapter);

        // Set up Spinner on item selected listener
        nodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Node selectedNode = (Node) parent.getItemAtPosition(position);
                if (selectedNode != null) {
                    nodeTitle.setText(selectedNode.toString());
                    notificationsViewModel.setSelectedNode(selectedNode);
                    alarmAdapter.setAlarms(new ArrayList<>());
                } else {
                    Log.e("NotificationsFragment", "Selected node is null");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                notificationsViewModel.setSelectedNode(null);
            }
        });


        // Subscribe to updates in nodes from another LiveData in the ViewModel
        notificationsViewModel.getNodesLiveData().observe(getViewLifecycleOwner(), nodes -> {
            nodeAdapter.clear();
            nodeAdapter.addAll(nodes);
            nodeAdapter.notifyDataSetChanged();
        });

        // Subscribe to updates in alarms from LiveData in the ViewModel
        notificationsViewModel.getAlarmsLiveData().observe(getViewLifecycleOwner(), alarms -> {
            for (AlarmResponse.AlarmData alarm : alarms) {
                alarmAdapter.addAlarmIfNew(alarm);
            }
            // Scroll to the top if you want to show the latest item
            alarmsRecyclerView.scrollToPosition(0);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        notificationsViewModel.startPolling();
    }

    @Override
    public void onPause() {
        super.onPause();
        notificationsViewModel.stopPolling();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}