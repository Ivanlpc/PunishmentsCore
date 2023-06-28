package me.ivanlpc.punishmentsgui;

import me.ivanlpc.punishmentsgui.commands.Punish;
import me.ivanlpc.punishmentsgui.listeners.InventoryEvents;
import me.ivanlpc.punishmentsgui.listeners.PlayerEvents;
import me.ivanlpc.punishmentsgui.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class PunishmentsGUI extends JavaPlugin {

    private final PluginDescriptionFile pluginfile = getDescription();
    public final MenuManager menuManager = new MenuManager();

    @Override
    public void onEnable() {
        long ms = System.currentTimeMillis();
        this.saveDefaultConfig();

        if(Bukkit.getPluginManager().getPlugin("LiteBans") == null) {
            throw new RuntimeException("LiteBans is required to run this plugin!");
        }

        //Registering events
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);

        //Registering commands
        this.getCommand("punish").setExecutor(new Punish(this));

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW  + pluginfile.getName() + " has been enabled in " + (System.currentTimeMillis() - ms) + " ms. Version: " + pluginfile.getVersion());
    }

    @Override
    public void onDisable() {
        this.menuManager.closeAllMenus();
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED  + pluginfile.getName() + " has been disabled. Version: " + pluginfile.getVersion());
    }
}
