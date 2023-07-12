package me.ivanlpc.punishmentscore.commands;

import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.api.database.entities.Order;
import me.ivanlpc.punishmentscore.inventories.types.OrdersGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Orders implements CommandExecutor {
    private final PunishmentsCore plugin;
    public Orders(PunishmentsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command");
            return true;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("punishmentscore.orders")) {
            String msg = this.plugin.getMessages().getString("Messages.no_permission");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            List<Order> orders = this.plugin.getDbmanager().getOrders();
            if(orders.size() == 0) {
                String msg = this.plugin.getMessages().getString("Messages.no_orders");
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return;
            }
            OrdersGUI inventory = new OrdersGUI(orders);
            ItemStack[][] items = inventory.build();
            Inventory inv = Bukkit.createInventory(p, 54, this.plugin.getConfig().getString("OrdersGUI.name", "Orders"));
            inv.setContents(items[0]);
            this.plugin.getInventoryManager().openInventory(p, inventory);
            Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                p.openInventory(inv);
                return null;
            });
        });
        return true;
    }
}
