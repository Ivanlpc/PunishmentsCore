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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfirmationMenuBuilder {

    private final PunishmentSlot slotData;
    private final Player opener;
    private final int size;
    private final Inventory inv;
    private final FileConfiguration config;
    private final int confirmation_slot = 12;
    private final int deny_slot = 14;
    private final List<String> punishment_lore;

    public ConfirmationMenuBuilder(Player opener, String victim, List<String> punishment_lore, PunishmentSlot slotData, FileConfiguration config) {
        this.opener = opener;
        this.size = 27;
        String invname = config.getString("ConfirmGUI.name").replace("%player%", victim);
        this.inv = Bukkit.createInventory(opener, size, invname);
        this.slotData = slotData;
        this.config = config;
        this.punishment_lore = punishment_lore;
    }

    public ConfirmationMenu build() {
        List<String> accept_lore = this.punishment_lore;
        List<String> deny_lore = this.config.getStringList("ConfirmGUI.deny_lore");
        String accept_name = this.config.getString("ConfirmGUI.accept");
        String deny_name = this.config.getString("ConfirmGUI.deny");
        ItemStack accept_itemstack = getItem(Material.WOOL, accept_name, 13, accept_lore);
        ItemStack deny_itemstack = getItem(Material.WOOL, deny_name, 14, deny_lore);
        this.inv.setItem(this.confirmation_slot, accept_itemstack);
        this.inv.setItem(this.deny_slot, deny_itemstack);

        return new ConfirmationMenu(this.inv, slotData, this.confirmation_slot);
    }
    private ItemStack getItem(Material m, String name, int damage, List<String> lore_data) {
        ItemStack item = new ItemStack(m, 1, (short) damage);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> lore = new ArrayList<>();

        for (String s : lore_data) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}


