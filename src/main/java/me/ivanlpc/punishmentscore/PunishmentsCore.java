package me.ivanlpc.punishmentscore;

import me.ivanlpc.punishmentscore.api.database.DatabaseManager;
import me.ivanlpc.punishmentscore.commands.Orders;
import me.ivanlpc.punishmentscore.commands.Punish;
import me.ivanlpc.punishmentscore.commands.Sanctions;
import me.ivanlpc.punishmentscore.listeners.InventoryEvents;
import me.ivanlpc.punishmentscore.listeners.PlayerEvents;
import me.ivanlpc.punishmentscore.inventories.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public final class PunishmentsCore extends JavaPlugin {

    private final PluginDescriptionFile pluginfile = getDescription();
    private final InventoryManager inventoryManager = new InventoryManager();
    private FileConfiguration messages, database;
    private DatabaseManager dbManager;
    @Override
    public void onEnable() {
        long ms = System.currentTimeMillis();
        this.saveDefaultConfig();
        loadFiles();
        if(Bukkit.getPluginManager().getPlugin("LiteBans") == null) {
            throw new RuntimeException("LiteBans is required to run this plugin!");
        }
        //Registering events
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);

        //Registering commands
        this.getCommand("punish").setExecutor(new Punish(this));
        this.getCommand("orders").setExecutor(new Orders(this));
        this.getCommand("sanctions").setExecutor(new Sanctions(this));

        if(this.getDatabase().getBoolean("use")) {
            this.dbManager = new DatabaseManager();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN  + pluginfile.getName() + " has been enabled in " + (System.currentTimeMillis() - ms) + " ms. Version: " + pluginfile.getVersion());
    }

    @Override
    public void onDisable() {
        this.inventoryManager.closeAllInventories();
        this.dbManager.close();
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED  + pluginfile.getName() + " has been disabled. Version: " + pluginfile.getVersion());
    }
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }
    private void loadFiles() {
        File messages = new File(getDataFolder(), "messages.yml");
        if(!messages.exists()) {
            messages.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }
        File storage = new File(getDataFolder(), "storage.yml");
        if(!storage.exists()) {
            storage.getParentFile().mkdirs();
            saveResource("storage.yml", false);
        }
        try {
            this.messages = YamlConfiguration.loadConfiguration(messages);
            this.database = YamlConfiguration.loadConfiguration(storage);
        } catch (IllegalArgumentException e) {
            this.messages = new YamlConfiguration();
            this.database = new YamlConfiguration();
            e.printStackTrace();
        }
    }

    public void reloadFiles() {
        this.loadFiles();
        this.reloadConfig();
        this.dbManager.reloadConnection();
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public FileConfiguration getDatabase() {
        return database;
    }
    public DatabaseManager getDbManager() {
        return this.dbManager;
    }
}
