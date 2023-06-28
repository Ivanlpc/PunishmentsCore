package me.ivanlpc.punishmentsgui.menu;

import java.util.List;

public class PunishmentSlot {
    private final List<String> commands;
    private final boolean needsConfirmation;
    private final String permission;

    public PunishmentSlot(List<String> command, String permission, boolean needsConfirmation) {
        this.permission = permission;
        this.commands = command;
        this.needsConfirmation = needsConfirmation;
    }
    public boolean needsConfirmation() {
        return needsConfirmation;
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getCommands() {
        return commands;
    }
}
