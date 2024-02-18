package me.ivanlpc.punishmentscore.listeners;

import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.util.TitleAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    private final PunishmentsCore plugin;
    public PlayerEvents(PunishmentsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDisconnect (PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(plugin.getInventoryManager().hasInventory(p)) return;
        plugin.getInventoryManager().closeInventory(p);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(p == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if(plugin.getDbManager().hasNotifications(p.getName())) {
                String title = plugin.getMessages().getString("Messages.notifications.title");
                String subtitle = plugin.getMessages().getString("Messages.notifications.subtitle");
                String text = plugin.getMessages().getString("Messages.notifications.text");
                Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                    TitleAPI.sendTitle(plugin, p, 10, 70, 20, title, subtitle);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
                    return null;
                });
                plugin.getDbManager().deleteNotifications(p.getName());
            }
        });
    }
}
