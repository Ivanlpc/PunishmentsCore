package me.ivanlpc.punishmentsgui.listeners;

import me.ivanlpc.punishmentsgui.PunishmentsGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    private PunishmentsGUI plugin;
    public PlayerEvents(PunishmentsGUI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDisconnect (PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!plugin.hasOpenedMenu(p)) return;
        plugin.menuClosed(p);
    }
}
