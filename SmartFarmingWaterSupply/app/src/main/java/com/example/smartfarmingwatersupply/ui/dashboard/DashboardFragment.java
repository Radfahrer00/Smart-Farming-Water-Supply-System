package com.example.smartfarmingwatersupply.ui.dashboard;

import android.os.Bundle;
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

import com.example.smartfarmingwatersupply.Node;
import com.example.smartfarmingwatersupply.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;
    private Spinner nodeSpinner;
    private TextView nodeTitle;
    private ArrayAdapter<Node> adapter;

    private TextView tvWaterLevelValue;
    private TextView tvPHValue;
    private TextView tvConductivityValue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeFragmentElements();

        // Setup ArrayAdapter for the Spinner
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nodeSpinner.setAdapter(adapter);

        nodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Node selectedNode = (Node) parent.getItemAtPosition(position);
                nodeTitle.setText(selectedNode.toString()); // Update the TextView based on the selected node
                updateUIWithNodeData(selectedNode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Can be ignored if not needed
            }
        });

        // Observe the LiveData from the ViewModel
        dashboardViewModel.getNodesLiveData().observe(getViewLifecycleOwner(), nodes -> {
            // Update the adapter's data and refresh the Spinner
            adapter.clear();
            adapter.addAll(nodes);
            adapter.notifyDataSetChanged();

            // Update the UI for the currently selected node
            Node selectedNode = (Node) nodeSpinner.getSelectedItem();
            if (selectedNode != null) {
                updateUIWithNodeData(selectedNode);
            }
        });

        return root;
    }

    private void initializeFragmentElements() {
        // Initialize Toolbar elements
        nodeSpinner = binding.nodeSpinner;
        nodeTitle = binding.nodeTitle;

        // Initialize TextViews
        tvWaterLevelValue = binding.tvCardValue1;
        tvPHValue = binding.tvCardValue2;
        tvConductivityValue = binding.tvCardValue3;
    }

    private void updateUIWithNodeData(Node node) {
        // Use a helper function to get the value or "No value received" if null
        tvWaterLevelValue.setText(getValueOrPlaceholder(node.getWaterLevel()));
        tvPHValue.setText(getValueOrPlaceholder(node.getPHValue()));
        tvConductivityValue.setText(getValueOrPlaceholder(node.getWaterConductivity()));
    }

    // Helper function to return the value or "No value received" if the value is null
    private String getValueOrPlaceholder(String value) {
        return value != null ? value : "No value received";
    }

    @Override
    public void onResume() {
        super.onResume();
        dashboardViewModel.startPolling();
    }

    @Override
    public void onPause() {
        super.onPause();
        dashboardViewModel.stopPolling();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}