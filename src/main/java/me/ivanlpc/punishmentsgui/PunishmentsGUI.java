package me.ivanlpc.punishmentsgui;

import me.ivanlpc.punishmentsgui.commands.Punish;
import me.ivanlpc.punishmentsgui.listeners.InventoryEvents;
import me.ivanlpc.punishmentsgui.listeners.PlayerEvents;
import me.ivanlpc.punishmentsgui.menu.PunishmentInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PunishmentsGUI extends JavaPlugin {

    private final PluginDescriptionFile pdffile = getDescription();
    Map<Player, PunishmentInventory> openedMenus = new HashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if(Bukkit.getPluginManager().getPlugin("LiteBans") == null) {
            throw new RuntimeException("LiteBans is required to run this plugin!");
        }
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);

        this.getCommand("punish").setExecutor(new Punish(this));
        this.getLogger().info(ChatColor.YELLOW  + pdffile.getName() + " has been enabled. Version: " + pdffile.getVersion());

    }

    @Override
    public void onDisable() {
        closeAllMenus();
        this.getLogger().info(ChatColor.RED  + pdffile.getName() + " has been disabled. Version: " + pdffile.getVersion());

    }

    public void menuOpened(Player p, PunishmentInventory i) {
        openedMenus.put(p, i);
    }
    public void menuClosed(Player p) {
        openedMenus.remove(p);
    }
    public boolean hasOpenedMenu(Player p) {
        return openedMenus.containsKey(p);
    }
    public PunishmentInventory getInventory(Player p) {
        return openedMenus.get(p);
    }

    public void executePunishment(String cmd) {
        CommandSender sender = this.getServer().getConsoleSender();
        this.getServer().dispatchCommand(sender, cmd);
    }
    private void closeAllMenus() {
        Set<Player> players_using_menu =  new HashSet<>(openedMenus.keySet());
        for(Player p: players_using_menu) {
            p.closeInventory();
            openedMenus.remove(p);
        }
    }

}
