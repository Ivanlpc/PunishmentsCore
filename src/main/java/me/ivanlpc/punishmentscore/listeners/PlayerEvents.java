package me.ivanlpc.punishmentscore.listeners;

import me.ivanlpc.punishmentscore.PunishmentsCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
}
