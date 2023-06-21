package me.ivanlpc.punishmentsgui.listeners;

import me.ivanlpc.punishmentsgui.PunishmentsGUI;
import me.ivanlpc.punishmentsgui.menu.Menu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryEvents implements Listener {

    private PunishmentsGUI plugin;
    public InventoryEvents(PunishmentsGUI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if(event.isCancelled()) return;
        Player p = (Player) event.getWhoClicked();
        if(!this.plugin.menuManager.hasOpenedMenu(p)) return;
        if(event.getCurrentItem() == null || event.getCurrentItem().getType().name().contains("AIR")){
            event.setCancelled(true);
            return;
        }
        if((event.getSlotType() == null)){
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        int slot = event.getSlot();
        Menu m = this.plugin.menuManager.getInventory(p);
        String[] commands = m.getCommandsBySlot(slot);
        if(commands[0].equals("close")) {
            p.closeInventory();
            return;
        };
        if(commands[0].equals("next")){
            Inventory inv = m.nextPage();
            this.plugin.menuManager.menuClosed(p);
            p.closeInventory();
            p.openInventory(inv);
            this.plugin.menuManager.menuOpened(p, m);

        } else if (commands[0].equals("back")) {
            Inventory inv = m.prevPage();
            this.plugin.menuManager.menuClosed(p);
            p.closeInventory();
            p.openInventory(inv);
            this.plugin.menuManager.menuOpened(p, m);
        } else {
            for(String s: commands) {
                if(s == null) continue;
                executePunishment(s);
            }
            p.closeInventory();
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if(p == null) return;
        if(!this.plugin.menuManager.hasOpenedMenu(p)) return;
        this.plugin.menuManager.menuClosed(p);
    }

    private void executePunishment(String cmd) {
        CommandSender sender = this.plugin.getServer().getConsoleSender();
        this.plugin.getServer().dispatchCommand(sender, cmd);
    }

}
