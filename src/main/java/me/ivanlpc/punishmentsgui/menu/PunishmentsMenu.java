package me.ivanlpc.punishmentsgui.menu;

import org.bukkit.inventory.Inventory;


public interface PunishmentsMenu {
    PunishmentSlot getSlotData(int slot);
    Inventory getInventory();
}
