package me.ivanlpc.punishmentsgui.menu;

import java.util.List;
import org.bukkit.inventory.Inventory;

public class Menu {

    private final Inventory[] inventoryList;
    private int current_page;
    private final String[][] commands;
    private final int menu_size;

    protected Menu(Inventory[] inventoryList, int max_pages, int menu_size) {
        this.inventoryList = inventoryList;
        this.commands = new String[max_pages * menu_size ][30];
        this.menu_size = menu_size;
        this.current_page = 0;
    }

    public void setCommands(int page, int slot, List<String> command_list) {
        int index = slot + (page * this.menu_size);
        for(int i = 0; i < command_list.size(); i++){
            commands[index][i] = command_list.get(i);
        }
    }
    public Inventory getFirstInventory() {
        return this.inventoryList[0];
    }

    public String[] getCommandsBySlot(int slot) {
        return commands[slot + (current_page * this.menu_size)];
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
