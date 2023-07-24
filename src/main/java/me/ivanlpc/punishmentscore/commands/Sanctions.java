package me.ivanlpc.punishmentscore.commands;

import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.api.LitebansAPI;
import me.ivanlpc.punishmentscore.api.database.entities.Sanction;
import me.ivanlpc.punishmentscore.inventories.types.SanctionsGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Map;

public class Sanctions implements CommandExecutor {

    public final PunishmentsCore plugin;

    public Sanctions(PunishmentsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command");
            return true;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("punishmentscore.sanctions")) {
            String msg = this.plugin.getMessages().getString("Messages.no_permission");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Map<String, Sanction> sanctions = LitebansAPI.getLastPunishment(p.getUniqueId());
            if(sanctions == null){
                String msg = this.plugin.getMessages().getString("Messages.error");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return;
            }

            SanctionsGUI sg = new SanctionsGUI(sanctions);
            sg.build();
            this.plugin.getInventoryManager().openInventory(p, sg);
            Bukkit.getScheduler().callSyncMethod(this.plugin, () -> {
                p.openInventory(sg.getFirstInventory());
               return null;
            });
        });
        return true;
    }


}
