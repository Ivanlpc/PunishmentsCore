package me.ivanlpc.punishmentscore.inventories.builders;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.inventories.PunishmentInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class ConfirmationGUI extends InventoryBuilder implements PunishmentInventory {
    private final PunishmentsCore plugin = PunishmentsCore.getPlugin(PunishmentsCore.class);
    private final ItemStack[][] inventories;
    private final int confirmation_slot;
    private final int deny_slot;
    private final ItemStack confirmationItem;
    private final String punishedPlayer;

    public ConfirmationGUI(ItemStack confirmationItem, String punishedPlayer) {
        int size = this.plugin.getConfig().getInt("GUI.size", 54);
        this.confirmation_slot = this.plugin.getConfig().getInt("ConfirmGUI.accept.slot", 12);
        this.deny_slot = this.plugin.getConfig().getInt("ConfirmGUI.deny.slot", 14);
        this.confirmationItem = confirmationItem;
        this.inventories =  new ItemStack[1][size];
        this.punishedPlayer = punishedPlayer;
    }
    @Override
    public ItemStack[][] build() {
        String materialName = this.plugin.getConfig().getString("ConfirmGUI.deny.item.material", "STONE");
        Material m = Material.matchMaterial(materialName);
        int durability = this.plugin.getConfig().getInt("ConfirmGUI.deny.item.damage", 0);
        String displayName = this.plugin.getConfig().getString("ConfirmGUI.deny.item.displayName", "Cancel");
        List<String> lore = this.plugin.getConfig().getStringList("ConfirmGUI.deny.item.lore");
        ItemStack denyItem = getItem(m, durability, displayName, lore);
        NBT.modify(denyItem, nbt -> {
            nbt.setBoolean("isConfirmation", true);
        });
        inventories[0][deny_slot] = denyItem;
        inventories[0][confirmation_slot] = this.confirmationItem;
        return inventories;
    }

    @Override
    public String getPunishedPlayer() {
        return this.punishedPlayer;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        NBTItem nbti = new NBTItem(event.getCurrentItem());
        Player p = (Player) event.getWhoClicked();
        if(!nbti.hasTag("key")) {
            p.closeInventory();
            return;
        }
        String permission = nbti.getString("perm");
        String key = nbti.getString("key");
        String level = nbti.getString("level");
        if(permission.length() > 0 && !p.hasPermission(permission)) {
            boolean db = this.plugin.getDatabase().getBoolean("use");
            String msg = this.plugin.getMessages().getString("Messages.no_permission_staff");
            if(db) {
                String punishment = this.plugin.getConfig().getString("GUI.items." + key + ".reason");
                String name = this.plugin.getConfig().getString("GUI.items." + key + ".levels." + level + ".name");
                List<String> commands = this.plugin.getConfig().getStringList("GUI.items." + key + ".levels." + level + ".commands");
                String reason = this.plugin.getConfig().getString("GUI.items." + key + ".reason");
                List<String> parsedCommands = parseCommands(p, punishedPlayer, reason, commands);
                this.plugin.getDbmanager().createOrder(p, this.punishedPlayer, name, punishment, parsedCommands );
                msg = this.plugin.getMessages().getString("Messages.creating_order");
            }
            p.closeInventory();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return;
        }
        List<String> commands = this.plugin.getConfig().getStringList("GUI.items." + key + ".levels." + level + ".commands");
        String reason = this.plugin.getConfig().getString("GUI.items." + key + ".reason");
        List<String> parsedCommands = parseCommands(p, punishedPlayer, reason, commands);
        executeCommands(p, parsedCommands);
        p.closeInventory();
    }
}
