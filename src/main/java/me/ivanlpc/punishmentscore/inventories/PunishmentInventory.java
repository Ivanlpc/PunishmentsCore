package me.ivanlpc.punishmentscore.inventories;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface PunishmentInventory {
    void handleClick(InventoryClickEvent event);
    String getPunishedPlayer();

}
