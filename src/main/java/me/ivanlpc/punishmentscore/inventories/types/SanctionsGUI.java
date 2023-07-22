package me.ivanlpc.punishmentscore.inventories.types;

import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.api.database.entities.Sanction;
import me.ivanlpc.punishmentscore.inventories.PunishmentInventory;
import me.ivanlpc.punishmentscore.inventories.builders.InventoryBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class SanctionsGUI extends InventoryBuilder implements PunishmentInventory {

    private final PunishmentsCore plugin;
    private final Map<String, Sanction> sanction;
    private final ItemStack[][] inventory;

    public SanctionsGUI(Map<String, Sanction> sanction, int size) {
        this.plugin = PunishmentsCore.getPlugin(PunishmentsCore.class);
        this.sanction = sanction;
        this.inventory = new ItemStack[1][size];
    }

    @Override
    public ItemStack[][] build() {
        String[] types = new String[]{"mute", "warn", "kick", "ban"};
        for(String type : types) {
            List<String> lore;
            ConfigurationSection cs = this.plugin.getConfig().getConfigurationSection("SanctionsGUI.items.last-" + type);
            int slot = cs.getInt("slot");
            String materialName = cs.getString("material");
            String displayName = cs.getString("displayName");
            if(sanction.containsKey(type)) {
                lore = cs.getStringList("lore");
            } else {
                lore = cs.getStringList("no-punishments-lore");
            }
            ItemStack item = getItem(Material.matchMaterial(materialName), 0, displayName, lore);
            this.inventory[0][slot] = item;
        }
        return this.inventory;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {

    }

    @Override
    public String getPunishedPlayer() {
        return "";
    }
}
