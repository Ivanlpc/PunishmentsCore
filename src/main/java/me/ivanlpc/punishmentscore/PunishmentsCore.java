package me.ivanlpc.punishmentscore;

import com.google.common.base.Preconditions;
import me.ivanlpc.punishmentscore.api.database.DatabaseManager;
import me.ivanlpc.punishmentscore.commands.Orders;
import me.ivanlpc.punishmentscore.commands.Punish;
import me.ivanlpc.punishmentscore.commands.Sanctions;
import me.ivanlpc.punishmentscore.listeners.InventoryEvents;
import me.ivanlpc.punishmentscore.listeners.PlayerEvents;
import me.ivanlpc.punishmentscore.inventories.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class PunishmentsCore extends JavaPlugin {

    private final PluginDescriptionFile pluginfile = getDescription();
    private Path dataFolderPath;
    private InventoryManager inventoryManager;
    private YamlConfiguration messages;
    private DatabaseManager dbManager;
    private String version;

    @Override
    public void onEnable() {
        String packageName = getServer().getClass().getPackage().getName();
        version = packageName.substring(packageName.lastIndexOf('.') + 1);
        long ms = System.currentTimeMillis();
        this.dataFolderPath = getDataFolder().toPath();
        this.saveDefaultConfig();
        loadInventories();
        this.messages = loadFile("messages.yml");
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

        if(this.getConfig().getBoolean("Database.use")) {
            this.dbManager = new DatabaseManager();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN  + pluginfile.getName() + " has been enabled in " + (System.currentTimeMillis() - ms) + " ms. Version: " + pluginfile.getVersion());
    }

    @Override
    public void onDisable() {
        this.inventoryManager.closeAllInventories();
        if(this.dbManager != null) this.dbManager.close();
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED  + pluginfile.getName() + " has been disabled. Version: " + pluginfile.getVersion());
    }
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public void reloadFiles() {
        this.messages = loadFile("messages.yml");
        this.loadInventories();
        this.reloadConfig();
        if(this.getConfig().getBoolean("Database.use")) {
            if(this.dbManager == null) this.dbManager = new DatabaseManager();
            else this.dbManager.reloadConnection();
        }
    }

    public YamlConfiguration getMessages() {
        return messages;
    }

    public DatabaseManager getDbManager() {
        return this.dbManager;
    }

    public boolean isNew() {
        return Integer.valueOf(version.split("_")[1]) > 16;
    }

    private YamlConfiguration loadFile(String filePath) {
        YamlConfiguration fileConfiguration;
        File file = new File(getDataFolder(), filePath);
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource(filePath, false);
        }
        try {
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
        } catch (IllegalArgumentException e) {
            fileConfiguration = new YamlConfiguration();
            e.printStackTrace();
        }
        return fileConfiguration;
    }

    private void loadInventories() {
        Map<String, YamlConfiguration> inventoryConfiguration = new HashMap<>();
        String[] inventories = new String[]{"confirmation.yml", "orders.yml", "punish.yml", "sanctions.yml"};
        for(String inventory : inventories) {
            inventoryConfiguration.put(inventory, loadFile("inventories/" + inventory));
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " Loaded " + inventory);
        }
        Preconditions.checkState(Files.isDirectory(dataFolderPath.resolve("inventories")), "inventories folder doesn't exist");
        this.inventoryManager = new InventoryManager(inventoryConfiguration);
    }
}
