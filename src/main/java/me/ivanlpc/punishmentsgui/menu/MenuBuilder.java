package me.ivanlpc.punishmentsgui.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuBuilder {
    private FileConfiguration config;
    private Map<String, Integer> punishmentList;
    private PunishmentInventory inv;
    private int size;
    private Player opener;
    private String vitcim;
    public MenuBuilder(Player opener, Map<String, Integer> punishmentList, FileConfiguration config, String victim) {
        this.config = config;
        this.opener = opener;
        this.punishmentList = punishmentList;
        this.vitcim = victim;
        this.size = this.config.getInt("GUI.size");
        this.inv = new PunishmentInventory(size);
    }

    public Inventory buildInventory() {
        String invname = this.config.getString("GUI.name").replace("%player%", vitcim);
        Inventory inv = Bukkit.createInventory(opener, size, invname);
        ConfigurationSection sec = this.config.getConfigurationSection("GUI.items");
        for(String key : sec.getKeys(false)) {
            String name = this.config.getString("GUI.items." + key + ".displayName");
            int slot = this.config.getInt("GUI.items." + key + ".slot");
            int command_id = getCommandId(key);
            List<String> lore = this.config.getStringList("GUI.items." + key + ".levels." + command_id + ".lore");
            List<String> command = formatCommandFromId(key, command_id);
            this.inv.setCommand(slot, command);
            ItemStack is = getItem(key, name, lore);
            inv.setItem(slot, is);
        }

        return inv;
    }
    public PunishmentInventory getInventory() {
        return inv;
    }
    private ItemStack getItem(String material_path, String name, List<String> lore_data) {
        Material m = Material.matchMaterial(this.config.getString("GUI.items." + material_path + ".material"));
        int durability = this.config.getInt("GUI.items." + material_path + ".data");
        ItemStack item = new ItemStack(m, 1,(short) durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> lore = new ArrayList<>();

        for(String s : lore_data) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private int getCommandId(String key) {
        String reason = this.config.getString("GUI.items." + key + ".reason");
        ConfigurationSection cs = this.config.getConfigurationSection("GUI.items." + key + ".levels");
        int max = 0;
        for(String s: cs.getKeys(false)) {
            int num = Integer.parseInt(s);
            if(num > max ){
                max = num;
            }
        }

        int punishmentCount = punishmentList.getOrDefault(reason, 0);
        return Math.min(punishmentCount, max);
    }
    private List<String> formatCommandFromId(String punishment_key, int id) {
        List<String> commands = this.config.getStringList("GUI.items." + punishment_key + ".levels." + id + ".commands");
        commands = commands.stream().map(cmd -> {
            cmd = cmd.replaceAll("%sender%", opener.getDisplayName());
            cmd = cmd.replaceAll("%player%", vitcim);
            cmd = cmd.replaceAll("%uuid%", opener.getUniqueId().toString());
            cmd = cmd.replace("%reason%", this.config.getString("GUI.items." + punishment_key + ".reason"));
            return cmd;
        }).collect(Collectors.toList());

        return commands;
    }
}
