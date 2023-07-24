package me.ivanlpc.punishmentscore.inventories;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class InventoryManager {

    private final Map<String, YamlConfiguration> inventoryConfiguration;
    private final Map<Player, PunishmentInventory> openedMenus = new HashMap<>();
    private final List<Player> skipClose;

    public InventoryManager(Map<String, YamlConfiguration> inventoryConfiguration) {
        this.inventoryConfiguration = inventoryConfiguration;
        this.skipClose = new ArrayList<>();
    }

    public void openInventory(Player p, PunishmentInventory invs) {
        openedMenus.put(p, invs);
    }

    public void closeInventory(Player p) {
        if(!skipClose.contains(p)) openedMenus.remove(p);
        else skipClose.remove(p);
    }

    public boolean hasInventory(Player p) {
        return !openedMenus.containsKey(p);
    }

    public PunishmentInventory getCurrentInventory(Player p) {
        return openedMenus.get(p);
    }

    public boolean isMenuOpen(String name) {
        for(PunishmentInventory pi : openedMenus.values()) {
            if(pi.getPunishedPlayer().equals(name)){
                return true;
            }
        }
        return false;
    }

    public void closeAllInventories() {
        for(Player p: openedMenus.keySet()) {
            p.closeInventory();
        }
        skipClose.clear();
        openedMenus.clear();
    }
    public YamlConfiguration getInventoryConfiguration(String name) {
        return this.inventoryConfiguration.getOrDefault(name, new YamlConfiguration());
    }
    public void skipCloseAdd(Player p) {
        this.skipClose.add(p);
    }
}
