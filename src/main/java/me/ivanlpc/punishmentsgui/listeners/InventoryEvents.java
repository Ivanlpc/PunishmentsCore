package me.ivanlpc.punishmentsgui.listeners;

import me.ivanlpc.punishmentsgui.PunishmentsGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryEvents implements Listener {

    private PunishmentsGUI plugin;
    public InventoryEvents(PunishmentsGUI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if(event.isCancelled()) return;
        Player p = (Player) event.getWhoClicked();
        if(!this.plugin.hasOpenedMenu(p)) return;
        if(event.getCurrentItem() == null || event.getCurrentItem().getType().name().contains("AIR")){
            event.setCancelled(true);
            return;
        }
        if((event.getSlotType() == null)){
            event.setCancelled(true);
            return;
        }
        System.out.println("wtf");
        event.setCancelled(true);
        int slot = event.getSlot();
        List<String> commands = this.plugin.getInventory(p).getCommand(slot);
        for(String s: commands) {
            this.plugin.executePunishment(s);
        }
        p.closeInventory();
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if(p == null) return;
        if(!this.plugin.hasOpenedMenu(p)) return;
        this.plugin.menuClosed(p);

    }

}
