package me.ivanlpc.punishmentscore.inventories;

import org.bukkit.entity.Player;

import java.util.*;

public class InventoryManager {
    private final Map<Player, PunishmentInventory> openedMenus = new HashMap<>();

    public void openInventory(Player p, PunishmentInventory invs) {
        openedMenus.put(p, invs);
    }

    public void closeInventory(Player p) {
        openedMenus.remove(p);
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
        openedMenus.clear();
    }
}
