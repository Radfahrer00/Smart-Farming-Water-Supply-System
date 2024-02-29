package com.example.smartfarmingwatersupply.ui.home;

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

import com.example.smartfarmingwatersupply.CommandAdapter;
import com.example.smartfarmingwatersupply.CommandDataset;
import com.example.smartfarmingwatersupply.Node;
import com.example.smartfarmingwatersupply.databinding.FragmentCommandsBinding;

public class CommandsFragment extends Fragment {

    private FragmentCommandsBinding binding;
    private CommandsViewModel commandsViewModel;
    private Spinner nodeSpinner;
    private TextView nodeTitle;
    private RecyclerView commandsRecyclerView;
    private ArrayAdapter<Node> nodeAdapter;
    private CommandAdapter commandAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        commandsViewModel = new ViewModelProvider(this).get(CommandsViewModel.class);

        binding = FragmentCommandsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Spinner and RecyclerView
        nodeSpinner = binding.nodeSpinnerCommands;
        nodeTitle = binding.nodeTitleCommands;

        commandsRecyclerView = binding.parametersRecyclerView;
        commandsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commandAdapter = new CommandAdapter(new CommandDataset(), getContext());
        commandsRecyclerView.setAdapter(commandAdapter);

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
                    commandsViewModel.setSelectedNode(selectedNode);
                    commandAdapter.setDeviceAccessToken(selectedNode.getDeviceAccessToken());
                    commandAdapter.setDeviceId(selectedNode.getDeviceId());
                    commandAdapter.setAttributes(new CommandDataset());
                } else {
                    Log.e("NotificationsFragment", "Selected node is null");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                commandsViewModel.setSelectedNode(null);
            }
        });

        // Subscribe to updates in nodes from another LiveData in the ViewModel
        commandsViewModel.getNodesLiveData().observe(getViewLifecycleOwner(), nodes -> {
            nodeAdapter.clear();
            nodeAdapter.addAll(nodes);
            nodeAdapter.notifyDataSetChanged();

            // Get the currently selected node's device access token and update the adapter
            String currentAccessToken = commandsViewModel.getDeviceAccessToken();
            commandAdapter.setDeviceAccessToken(currentAccessToken);
        });

        commandsViewModel.getCommandDatasetMutableLiveData().observe(getViewLifecycleOwner(), newCommandDataset -> {
            if (newCommandDataset != null) {
                commandAdapter.setAttributes(newCommandDataset);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        commandsViewModel.startPolling();
    }

    @Override
    public void onPause() {
        super.onPause();
        commandsViewModel.stopPolling();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}