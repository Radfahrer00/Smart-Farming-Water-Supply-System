package com.example.smartfarmingwatersupply;

import java.util.ArrayList;
import java.util.List;

public class CommandDataset {

    private List<Command> listOfCommands;

    public CommandDataset() {
        listOfCommands = new ArrayList<>();
        listOfCommands.add(new Command("Device State", R.drawable.switch_icon));
        listOfCommands.add(new Command("Valve State", R.drawable.valve_icon));
        listOfCommands.add(new Command("Pump State", R.drawable.pump_icon));
        listOfCommands.add(new Command("Conductivity Upper limit", R.drawable.conductivity_icon));
        listOfCommands.add(new Command("Ph Lower limit", R.drawable.ph_icon));
        listOfCommands.add(new Command("Ph Upper limit", R.drawable.ph_icon));
        listOfCommands.add(new Command("Water Level Lower limit", R.drawable.water_level_icon));
        listOfCommands.add(new Command("Water Level Mid Lower limit", R.drawable.water_level_icon));
        listOfCommands.add(new Command("Water Level Mid Upper limit", R.drawable.water_level_icon));
        listOfCommands.add(new Command("Water Level Upper limit", R.drawable.water_level_icon));
    }

    public int getSize() {
        return listOfCommands.size();
    }

    public Command getCommandAtPosition(int pos) {
        return listOfCommands.get(pos);
    }

    public void updateCommand(String attributeKey, String currentValue) {
        for (Command command : listOfCommands) {
            if (command.getAttributeKey().equals(attributeKey)) {
                command.setCurrentValue(currentValue);
                return;
            }
        }
    }
}
