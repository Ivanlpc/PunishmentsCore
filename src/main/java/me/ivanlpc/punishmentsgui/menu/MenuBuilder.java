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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuBuilder {
    private final FileConfiguration config;
    private final Map<String, Integer> punishmentList;
    private final Menu menu;
    private final int size;
    private final Player opener;
    private final String vitcim;
    private final Inventory[] inventories;
    private final PunishmentSlot[] commands;

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
        for(int i = 0; i < inventories.length; i++) {
            inventories[i] = Bukkit.createInventory(opener, size, invname.replace("%page%", Integer.toString(i)));
        }
        this.commands = new PunishmentSlot[size * totalPages];
        this.menu = new Menu(inventories, commands, size, this.vitcim);
    }

    public Menu build() {
        ConfigurationSection sec = this.config.getConfigurationSection("GUI.items");
        for(String key : sec.getKeys(false)) {
            List<String> lore;
            String name = sec.getString(key +".displayName");
            int slot = sec.getInt(key +".slot");
            int page = sec.getInt(key +".page", 1) - 1;
            boolean nextPage = sec.getBoolean(key +".nextPage");
            boolean prevPage = sec.getBoolean(key +".backPage");
            boolean needsConfirmation = sec.getBoolean(key +".confirm");
            if(nextPage) {
                lore = sec.getStringList(key + ".lore");
                this.commands[slot + (page * size)] = new PunishmentSlot(Collections.singletonList("next"), "", false);
            } else if(prevPage) {
                lore = sec.getStringList(key + ".lore");
                this.commands[slot + (page * size)] = new PunishmentSlot(Collections.singletonList("back"), "", false);

            } else {
                int command_id = getCommandId(key);
                String permission_required = sec.getString(key +".levels." + command_id + ".permission", "");
                List<String> commands = formatCommandFromId(key, command_id);
                this.commands[slot + (page * size)] = new PunishmentSlot(commands, permission_required, needsConfirmation);
                lore = sec.getStringList(key +".levels." + command_id + ".lore");

            }
            ItemStack is = getItem(key, name, lore);
            inventories[page].setItem(slot, is);

        }
        return this.menu;
    }


    private ItemStack getItem(String material_path, String name, List<String> lore_data) {
        Material m = Material.matchMaterial(this.config.getString("GUI.items." + material_path + ".material"));
        int durability = this.config.getInt("GUI.items." + material_path + ".data", 0);
        ItemStack item = new ItemStack(m, 1,(short) durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> lore = new ArrayList<>(lore_data);
        lore = lore.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList());
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
