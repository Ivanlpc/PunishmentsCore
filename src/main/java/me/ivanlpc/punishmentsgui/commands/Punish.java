package me.ivanlpc.punishmentsgui.commands;

import me.ivanlpc.punishmentsgui.PunishmentsGUI;
import me.ivanlpc.punishmentsgui.api.LitebansAPI;
import me.ivanlpc.punishmentsgui.menu.Menu;
import me.ivanlpc.punishmentsgui.menu.MenuBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Map;


public class Punish implements CommandExecutor {

    private PunishmentsGUI plugin;
    public Punish(PunishmentsGUI plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        if(!sender.hasPermission("punishmentsgui.use")) {
            String msg = this.plugin.getConfig().getString("Messages.no_permission");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }
        if(args.length != 1){
            String msg = this.plugin.getConfig().getString("Messages.usage");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }
        if(args[0].equals("reload")) {

            if(!sender.hasPermission("punishmentsgui.reload")) {
                String msg = this.plugin.getConfig().getString("Messages.no_permission");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return true;
            }

            String msg = this.plugin.getConfig().getString("Messages.reload");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            this.plugin.reloadConfig();
            return true;
        }

        Player player = (Player) sender;
        String userToPunish = args[0];
        if(player.getDisplayName().equals(userToPunish)) {
            String msg = plugin.getConfig().getString("Messages.same_player");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }
        //LitebansAPI will fetch mysql to get the punishments of the player
        //We don't want to block the main thread
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Map<String, Integer> punishmentsList = LitebansAPI.getAllPunishments(userToPunish);
            if(punishmentsList == null){
                String msg = plugin.getConfig().getString("Messages.error");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return;
            }
            if(punishmentsList.size() == 0){
                String msg = plugin.getConfig().getString("Messages.no_punishments");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            Menu menu = new MenuBuilder(player, userToPunish, plugin.getConfig(), punishmentsList).build();
            plugin.menuManager.menuOpened(player, menu);
            player.openInventory(menu.getFirstInventory());
        });
        return true;
    }




}
