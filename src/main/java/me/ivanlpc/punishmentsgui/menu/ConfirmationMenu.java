package me.ivanlpc.punishmentsgui.menu;

import org.bukkit.inventory.Inventory;

import java.util.List;

public class ConfirmationMenu implements PunishmentsMenu{

    private PunishmentSlot slotData;
    private final Inventory inv;
    private final int accept_slot;

    protected ConfirmationMenu(Inventory inv, PunishmentSlot slotData, int accept_slot) {
        this.accept_slot = accept_slot;
        this.inv = inv;
        this.slotData = slotData;
    }

    @Override
    public PunishmentSlot getSlotData(int slot) {
        if(slot == accept_slot) return slotData;
        else return null;
    }

    public Inventory getInventory() {
        return inv;
    }
}
