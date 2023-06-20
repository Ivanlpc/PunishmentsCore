package me.ivanlpc.punishmentsgui.menu;

import java.util.ArrayList;
import java.util.List;

public class PunishmentInventory {
    private List<List<String>> commands;
    public PunishmentInventory(int size) {
        this.commands = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            this.commands.add(null);
        }
    }

    public void setCommand(int slot, List<String> command) {
        this.commands.set(slot, command);
    }

    public List<String> getCommand(int slot) {
        return commands.get(slot);
    }
}
