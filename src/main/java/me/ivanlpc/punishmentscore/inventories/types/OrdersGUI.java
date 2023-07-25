package me.ivanlpc.punishmentscore.inventories.types;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.api.database.entities.Order;
import me.ivanlpc.punishmentscore.inventories.PunishmentInventory;
import me.ivanlpc.punishmentscore.inventories.builders.PaginatedInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OrdersGUI extends PaginatedInventory implements PunishmentInventory {

    private final PunishmentsCore plugin = PunishmentsCore.getPlugin(PunishmentsCore.class);
    private final List<Order> ordersList;

    public OrdersGUI(List<Order> ordersList) {
        super("orders.yml");
        this.ordersList = ordersList;
        this.pages = (int) Math.ceil((double) ordersList.size() / (size - 9));
        if(size == 9) throw new IllegalArgumentException("Orders.yml size must be greater than 9");
        inventories = new ItemStack[pages][size];
        if(pages > 1) setPaginationItems();
    }

    @Override
    public void build() {
        int slot = 0;
        int page = 0;
        for(Order o : ordersList) {
            String materialName = inventoryConfiguration.getString("item.material");
            int durability = inventoryConfiguration.getInt("item.damage");
            int id = o.getId();
            String name = inventoryConfiguration.getString("item.displayName");
            name = name.replaceAll("%id%", String.valueOf(id));
            name = name.replaceAll("%player%", o.getUserPunished());
            Material m = Material.matchMaterial(materialName);
            ItemStack is = getItem(m, durability, name, parseLore(o));
            if(slot == 45) {
                page++;
                slot = 0;
            }
            NBT.modify(is, nbti -> {
                nbti.setInteger("id", id);
            });
            this.inventories[page][slot] = is;
            slot++;
        }
    }

    public List<String> parseLore(Order o) {
        List<String> lore = new ArrayList<>();
        List<String> format = inventoryConfiguration.getStringList("item.lore");
        for(String s: format) {
            String line = s.replaceAll("%staff%", o.getUsername());
            line = line.replaceAll("%punishment%", o.getPunishment());
            line = line.replaceAll("%player%", o.getUserPunished());
            line = line.replaceAll("%reason%", o.getReason());
            line = line.replaceAll("%date%", o.getDate().toString());
            lore.add(line);
        }
        return lore;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        NBTItem nbti = new NBTItem(event.getCurrentItem());
        Player p = (Player) event.getWhoClicked();
        if(nbti.hasTag("key")){
            String key = nbti.getString("key");
            if(key.equals("backPage")) {
                PunishmentGUI pg = (PunishmentGUI) this.plugin.getInventoryManager().getCurrentInventory(p);
                ItemStack[] is = pg.getBackPage();
                event.getClickedInventory().setContents(is);
                p.updateInventory();
            } else if(key.equals("nextPage")) {
                PunishmentGUI pg = (PunishmentGUI) this.plugin.getInventoryManager().getCurrentInventory(p);
                ItemStack[] is = pg.getNextPage();
                event.getClickedInventory().setContents(is);
                p.updateInventory();
            }
            return;
        }
        int id = nbti.getInteger("id");

        if(event.getClick().isRightClick()) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                this.plugin.getDbManager().deleteOrder(id);
                Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                    String msg = this.plugin.getMessages().getString("Messages.order_deleted");
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    p.closeInventory();
                    return null;
                });
            });
        } else if(event.getClick().isLeftClick()) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                List<String> commands = this.plugin.getDbManager().getCommandsFromOrder(id);
                if(commands.size() > 0) {
                    Bukkit.getScheduler().callSyncMethod(this.plugin, () -> {
                        executeCommands(p, commands);
                        return null;
                    });
                }
                this.plugin.getDbManager().deleteOrder(id);
                Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                    p.closeInventory();
                    return null;
                });
            });
        }
    }

    @Override
    public String getPunishedPlayer() {
        return "";
    }
}
