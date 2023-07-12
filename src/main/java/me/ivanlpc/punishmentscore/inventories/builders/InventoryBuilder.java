package me.ivanlpc.punishmentscore.inventories.builders;

import me.ivanlpc.punishmentscore.PunishmentsCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class InventoryBuilder {
    public abstract ItemStack[][] build();

    protected ItemStack getItem(Material m, int durability, String name, List<String> lore_data) {
        ItemStack item = new ItemStack(m, 1,(short) durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> lore = new ArrayList<>(lore_data);
        lore = lore.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    protected void executeCommands(Player p, List<String> commands ) {
        PunishmentsCore plugin = PunishmentsCore.getPlugin(PunishmentsCore.class);
        for(String s : commands ){
            if(s.contains("[close]")) {
                p.closeInventory();
                continue;
            }
            String[] executor = s.split(Pattern.quote("[player]"));
            if(executor.length == 1) {

                Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), s);
            } else {
                p.performCommand(executor[1]);
            }
        }
    }
    protected List<String> parseCommands(Player p, String punishedUser, String reason, List<String> cmd) {
        List<String> commandsParsed = new ArrayList<>();
        for(String s : cmd ){
            String finalCommand = s.replaceAll("%player%", punishedUser);
            finalCommand = finalCommand.trim();
            finalCommand = finalCommand.replaceAll("%sender%", p.getPlayerListName());
            finalCommand = finalCommand.replaceAll("%uuid%", p.getUniqueId().toString());
            finalCommand = finalCommand.replaceAll("%reason%", reason);
            finalCommand = finalCommand.trim();
            commandsParsed.add(finalCommand);
        }
        return commandsParsed;
    }
}