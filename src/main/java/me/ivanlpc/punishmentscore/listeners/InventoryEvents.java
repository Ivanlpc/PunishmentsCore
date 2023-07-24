package me.ivanlpc.punishmentscore.listeners;

import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.inventories.PunishmentInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryEvents implements Listener {

    private final PunishmentsCore plugin;
    public InventoryEvents(PunishmentsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if(event.isCancelled()) return;
        Player p = (Player) event.getWhoClicked();
        if(this.plugin.getInventoryManager().hasInventory(p)) return;
        event.setCancelled(true);

        if(event.getCurrentItem() == null || event.getCurrentItem().getType().name().contains("AIR")){
            return;
        }
        if((event.getSlotType() == null)){
            return;
        }
        PunishmentInventory pi = this.plugin.getInventoryManager().getCurrentInventory(p);
        try {
            pi.handleClick(event);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = this.plugin.getMessages().getString("Messages.error");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if(p == null) return;
        if(this.plugin.getInventoryManager().hasInventory(p)) return;
        this.plugin.getInventoryManager().closeInventory(p);
    }
}
