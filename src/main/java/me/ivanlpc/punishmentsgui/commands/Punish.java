package me.ivanlpc.punishmentsgui.commands;

import me.ivanlpc.punishmentsgui.PunishmentsGUI;
import me.ivanlpc.punishmentsgui.api.LitebansAPI;
import me.ivanlpc.punishmentsgui.menu.MenuBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Map;


public class Punish implements CommandExecutor {

    PunishmentsGUI plugin;
    public Punish(PunishmentsGUI plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        if(args.length == 0){
            sender.sendMessage("Usage: /punish <player>");
            return true;
        }

        Player player = (Player) sender;
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                Map<String, Integer> punishmentsList = LitebansAPI.getAllPunishments(args[0]);
                if(punishmentsList.size() == 0){
                    sender.sendMessage("Este jugador no tiene sanciones...");
                }
                MenuBuilder mb = new MenuBuilder(player, punishmentsList, plugin.getConfig(), args[0]);
                Inventory inv = mb.buildInventory();
                plugin.menuOpened(player, mb.getInventory());
                player.openInventory(inv);
            }
        });


        return true;
    }




}
