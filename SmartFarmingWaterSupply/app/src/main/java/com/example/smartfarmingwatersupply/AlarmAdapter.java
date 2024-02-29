package com.example.smartfarmingwatersupply;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfarmingwatersupply.responseType.AlarmResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<AlarmResponse.AlarmData> alarmList; // Assuming AlarmData is your data model class for each alarm

    public AlarmAdapter(List<AlarmResponse.AlarmData> alarmList) {
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmResponse.AlarmData alarmData = alarmList.get(position);
        // Convert timestamps to a readable format, assuming they're in milliseconds
        holder.time.setText(formatTime(alarmData.getCreatedTime()));
        holder.type.setText(alarmData.getType());
        holder.severity.setText(alarmData.getSeverity());
        holder.status.setText(alarmData.getStatus());
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void setAlarms(List<AlarmResponse.AlarmData> alarms) {
        this.alarmList = alarms;
        notifyDataSetChanged();
    }

    // Call this method when new alarm data is received
    public void addAlarmIfNew(AlarmResponse.AlarmData newAlarm) {
        // Check if the alarm already exists
        boolean alarmExists = false;
        for (AlarmResponse.AlarmData alarm : alarmList) {
            if (alarm.getId().getId().equals(newAlarm.getId().getId())) {
                // Alarm already exists, so don't add it again
                alarmExists = true;
                Log.d("AlarmAdapter", "Alarm already exists: " + newAlarm.getId());
                break;
            }
        }

        if (!alarmExists) {
            // Add the new alarm at the top
            this.alarmList.add(0, newAlarm);
            notifyItemInserted(0);
        }

        // If the list exceeds 15 items, remove the oldest one
        if (this.alarmList.size() > 15) {
            int lastIndex = this.alarmList.size() - 1;
            this.alarmList.remove(lastIndex);
            notifyItemRemoved(lastIndex);
        }
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView time, type, severity, status;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            type = itemView.findViewById(R.id.type);
            severity = itemView.findViewById(R.id.severity);
            status = itemView.findViewById(R.id.status);
        }
    }

    private String formatTime(long timeInMillis) {
        // Convert the milliseconds to a formatted date string
        // You might need a SimpleDateFormat to format the time nicely
        Date date = new Date(timeInMillis);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return formatter.format(date);
    }
}
