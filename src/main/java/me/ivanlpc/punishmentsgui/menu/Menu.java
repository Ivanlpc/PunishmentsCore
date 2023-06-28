package me.ivanlpc.punishmentsgui.menu;

import org.bukkit.inventory.Inventory;

import java.util.List;

public class Menu implements PunishmentsMenu{

    private final Inventory[] inventoryList;
    private final PunishmentSlot[] commands;
    private int current_page;
    private int menu_size;
    private String victim;

    protected Menu(Inventory[] inventoryList, PunishmentSlot[] commands, int menu_size, String victim) {
        this.inventoryList = inventoryList;
        this.commands = commands;
        this.menu_size = menu_size;
        this.current_page = 0;
        this.victim = victim;
    }
    public PunishmentSlot getSlotData(int slot) {
        return commands[slot + (current_page * this.menu_size)];
    }

    public Inventory getInventory() {
        return this.inventoryList[0];
    }

    public String getVictim() {
        return this.victim;
    }
    public Inventory nextPage() {
        current_page++;
        return inventoryList[current_page];
    }
    public Inventory prevPage() {
        current_page--;
        return inventoryList[current_page];
    }

}
