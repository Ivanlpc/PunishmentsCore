package me.ivanlpc.punishmentsgui.listeners;

import me.ivanlpc.punishmentsgui.PunishmentsGUI;
import me.ivanlpc.punishmentsgui.menu.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

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
        PunishmentsMenu pm = this.plugin.menuManager.getInventory(p);
        if(pm instanceof ConfirmationMenu) {
            PunishmentSlot ps = pm.getSlotData(slot);
            if(ps != null) {
                executePunishments(ps.getCommands());
            }
            this.plugin.menuManager.menuClosed(p);
            p.closeInventory();
            return;
        }
        Menu m = (Menu) pm;
        List<String> commands = m.getSlotData(slot).getCommands();
        if(commands == null || commands.size() == 0) return;
        if(commands.get(0).equals("close")) {
            p.closeInventory();
            return;
        }
        if(commands.get(0).equals("next")){
            Inventory inv = m.nextPage();
            this.plugin.menuManager.menuClosed(p);
            p.closeInventory();
            p.openInventory(inv);
            this.plugin.menuManager.menuOpened(p, m);

        } else if (commands.get(0).equals("back")) {
            Inventory inv = m.prevPage();
            this.plugin.menuManager.menuClosed(p);
            p.closeInventory();
            p.openInventory(inv);
            this.plugin.menuManager.menuOpened(p, m);
        } else {
            List<String> lore = event.getCurrentItem().getItemMeta().getLore();
            String perm = m.getSlotData(slot).getPermission();
            if(perm.length() > 0 && !p.hasPermission(perm)) {
                String msg = this.plugin.getConfig().getString("Messages.no_permission_staff");
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return;
            }
            if(m.getSlotData(slot).needsConfirmation()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenuBuilder(p, m.getVictim(), lore, m.getSlotData(slot), this.plugin.getConfig()).build();
                this.plugin.menuManager.menuClosed(p);
                p.closeInventory();
                p.openInventory(confirmationMenu.getInventory());
                this.plugin.menuManager.menuOpened(p, confirmationMenu);
            } else {
                executePunishments(commands);
                this.plugin.menuManager.menuClosed(p);
                p.closeInventory();

            }

        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if(p == null) return;
        if(!this.plugin.menuManager.hasOpenedMenu(p)) return;
        this.plugin.menuManager.menuClosed(p);
    }

    private void executePunishments(List<String> cmd) {
        for(String s: cmd) {
            if(s == null) continue;
            if(s.equals("cancel")) break;
            CommandSender sender = this.plugin.getServer().getConsoleSender();
            this.plugin.getServer().dispatchCommand(sender, s);
        }
    }

}
