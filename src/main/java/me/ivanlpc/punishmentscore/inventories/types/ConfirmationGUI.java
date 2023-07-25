package me.ivanlpc.punishmentscore.inventories.types;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.inventories.PunishmentInventory;
import me.ivanlpc.punishmentscore.inventories.builders.InventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class ConfirmationGUI extends InventoryBuilder implements PunishmentInventory {
    private final PunishmentsCore plugin = PunishmentsCore.getPlugin(PunishmentsCore.class);
    private final int confirmation_slot;
    private final int deny_slot;
    private final ItemStack confirmationItem;
    private final PunishmentGUI gui;
    private final int size;

    public ConfirmationGUI(ItemStack confirmationItem, PunishmentGUI gui) {
        super("confirmation.yml");
        this.size = inventoryConfiguration.getInt("size", 54);
        this.confirmation_slot = inventoryConfiguration.getInt("accept.slot", 12);
        this.deny_slot = inventoryConfiguration.getInt("deny.slot", 14);
        this.confirmationItem = confirmationItem;
        this.gui = gui;
        this.inventoryName = this.inventoryName.replaceAll("%player%", this.gui.getPunishedPlayer());
    }
    @Override
    public void build() {
        String materialName = inventoryConfiguration.getString("deny.item.material", "STONE");
        Material m = Material.matchMaterial(materialName);
        int durability = inventoryConfiguration.getInt("deny.item.damage", 0);
        String displayName = inventoryConfiguration.getString("deny.item.displayName", "Cancel");
        List<String> lore = inventoryConfiguration.getStringList("deny.item.lore");
        ItemStack denyItem = getItem(m, durability, displayName, lore);
        inventories[deny_slot] = denyItem;
        inventories[confirmation_slot] = this.confirmationItem;
    }

    @Override
    public Inventory getFirstInventory() {
        Inventory inv = Bukkit.createInventory(null, this.size, this.inventoryName);
        inv.setContents(inventories);
        return inv;
    }

    @Override
    public String getPunishedPlayer() {
        return this.gui.getPunishedPlayer();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        NBTItem nbti = new NBTItem(event.getCurrentItem());
        Player p = (Player) event.getWhoClicked();
        if(event.getSlot() == this.deny_slot) {
            this.plugin.getInventoryManager().openInventory(p, this.gui);
            this.plugin.getInventoryManager().skipCloseAdd(p);
            p.openInventory(this.gui.getFirstInventory());
            return;
        }
        String permission = nbti.getString("perm");
        String key = nbti.getString("key");
        String level = nbti.getString("level");
        if(permission.length() > 0 && !p.hasPermission(permission)) {
            boolean db = this.plugin.getConfig().getBoolean("Database.use");
            String msg = this.plugin.getMessages().getString("Messages.no_permission_staff");
            if(db) {
                String punishment = this.gui.getPunishment(key);
                String name = this.gui.getName(key, level);
                List<String> commands = this.gui.getCommands(key, level);
                String reason = this.gui.getReason(key);
                List<String> parsedCommands = parseCommands(p, this.gui.getPunishedPlayer(), reason, commands);
                int order = this.plugin.getDbManager().createOrder(p, this.gui.getPunishedPlayer(), name, punishment, parsedCommands );
                if(order == 0) msg = this.plugin.getMessages().getString("Messages.order_error");
                else {
                    msg = this.plugin.getMessages().getString("Messages.creating_order");
                    msg = msg.replaceAll("%order%", String.valueOf(order));
                }
            }
            p.closeInventory();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return;
        }
        List<String> commands = this.gui.getCommands(key, level);
        String reason = this.gui.getReason(key);
        List<String> parsedCommands = parseCommands(p, this.gui.getPunishedPlayer(), reason, commands);
        executeCommands(p, parsedCommands);
        p.closeInventory();
    }
}
