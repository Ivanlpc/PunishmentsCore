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
    private Menu menu;
    private int size;
    private Player opener;
    private String vitcim;
    private Inventory[] inventories;
    public MenuBuilder(Player opener,String victim, FileConfiguration config,  Map<String, Integer> punishmentList) {
        this.config = config;
        this.opener = opener;
        this.punishmentList = punishmentList;
        this.vitcim = victim;
        this.size = this.config.getInt("GUI.size");
        int totalPages = this.config.getInt("GUI.pages");
        this.inventories = new Inventory[totalPages];
        String invname = this.config.getString("GUI.name");
        invname = invname.replace("%player%", victim);
        for(Integer i = 0; i < inventories.length; i++) {
            inventories[i] = Bukkit.createInventory(opener, size, invname.replace("%page%", i.toString()));
        }
        this.menu = new Menu(inventories, totalPages, size);
    }

    public Menu build() {
        ConfigurationSection sec = this.config.getConfigurationSection("GUI.items");
        for(String key : sec.getKeys(false)) {
            List<String> lore;
            String name = this.config.getString("GUI.items." + key + ".displayName");
            int slot = this.config.getInt("GUI.items." + key + ".slot");
            int page = this.config.getInt("GUI.items." + key + ".page") - 1;
            boolean nextPage = this.config.getBoolean("GUI.items." + key + ".nextPage");
            boolean prevPage = this.config.getBoolean("GUI.items." + key + ".backPage");

            if(nextPage) {
                lore = this.config.getStringList("GUI.items." + key + ".lore");
                List<String> next = new ArrayList<>();
                next.add("next");
                this.menu.setCommands(page, slot, next);
            }else if(prevPage) {
                lore = this.config.getStringList("GUI.items." + key + ".lore");
                List<String> back = new ArrayList<>();
                back.add("back");
                this.menu.setCommands(page, slot, back);
            } else {
                int command_id = getCommandId(key);
                lore = this.config.getStringList("GUI.items." + key + ".levels." + command_id + ".lore");
                List<String> commands = formatCommandFromId(key, command_id);
                this.menu.setCommands(page, slot, commands);
            }
            ItemStack is = getItem(key, name, lore);
            inventories[page].setItem(slot, is);
        }
        return this.menu;
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
    private List<String> formatCommandFromId(String punishment_index, int id) {
        List<String> commands = this.config.getStringList("GUI.items." + punishment_index + ".levels." + id + ".commands");
        commands = commands.stream().map(cmd -> {
            cmd = cmd.replaceAll("%sender%", opener.getDisplayName());
            cmd = cmd.replaceAll("%player%", vitcim);
            cmd = cmd.replaceAll("%uuid%", opener.getUniqueId().toString());
            cmd = cmd.replace("%reason%", this.config.getString("GUI.items." + punishment_index + ".reason"));
            return cmd;
        }).collect(Collectors.toList());

        return commands;
    }
}
