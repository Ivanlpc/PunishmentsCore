package me.ivanlpc.punishmentscore.commands;

import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.api.LitebansAPI;
import me.ivanlpc.punishmentscore.inventories.builders.PunishmentGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


public class Punish implements CommandExecutor {

    private final PunishmentsCore plugin;
    public Punish(PunishmentsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        if(!sender.hasPermission("punishmentscore.use")) {
            String msg = this.plugin.getMessages().getString("Messages.no_permission");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }
        if(args.length < 1){
            String msg = this.plugin.getMessages().getString("Messages.usage");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }
        if(args[0].equals("reload")) {

            if(!sender.hasPermission("punishmentscore.reload")) {
                String msg = this.plugin.getMessages().getString("Messages.no_permission");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return true;
            }

            String msg = this.plugin.getMessages().getString("Messages.reload");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            this.plugin.reloadFiles();
            return true;
        }

        Player player = (Player) sender;
        String userToPunish = args[0];
        if(player.getPlayerListName().equals(userToPunish)) {
            String msg = this.plugin.getMessages().getString("Messages.same_player");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }
        if(args.length == 1 || (args.length == 2 && !args[1].equals("force"))) {
            if(this.plugin.getInventoryManager().isMenuOpen(userToPunish)) {
                String msg = this.plugin.getMessages().getString("Messages.use_force");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return true;
            }
        } else {
            if(!sender.hasPermission("punishmentscore.force")) {
                String msg = this.plugin.getMessages().getString("Messages.no_permission");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return true;
            }
        }

        //LitebansAPI will fetch mysql to get the punishments of the player
        //We don't want to block the main thread
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Map<String, Integer> punishmentsList = LitebansAPI.getAllPunishments(userToPunish);
            if(punishmentsList == null){
                String msg = this.plugin.getMessages().getString("Messages.error");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return;
            }
            if(punishmentsList.size() == 0){
                String msg = this.plugin.getMessages().getString("Messages.no_punishments");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            PunishmentGUI pg = new PunishmentGUI(punishmentsList, userToPunish);
            ItemStack[][] invs = pg.build();
            plugin.getInventoryManager().openInventory(player, pg);

            //Using the Bukkit API in the main thread
            Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                String invname = this.plugin.getConfig().getString("GUI.name").replace("%player%", userToPunish);
                Inventory inv = Bukkit.createInventory(player, this.plugin.getConfig().getInt("GUI.size"), invname);
                inv.setContents(invs[0]);
                player.openInventory(inv);
                return null;
            });
        });
        return true;
    }




}
