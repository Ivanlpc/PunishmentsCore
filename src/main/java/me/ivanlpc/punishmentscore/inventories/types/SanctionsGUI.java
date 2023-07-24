package me.ivanlpc.punishmentscore.inventories.types;

import me.ivanlpc.punishmentscore.api.database.entities.Sanction;
import me.ivanlpc.punishmentscore.inventories.PunishmentInventory;
import me.ivanlpc.punishmentscore.inventories.builders.InventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SanctionsGUI extends InventoryBuilder implements PunishmentInventory {

    private final Map<String, Sanction> sanction;
    private final ItemStack[][] inventory;
    private final int size;

    public SanctionsGUI(Map<String, Sanction> sanction) {
        super("sanctions.yml");
        this.size = inventoryConfiguration.getInt("size");
        this.sanction = sanction;
        this.inventory = new ItemStack[1][size];
        this.inventoryName = inventoryConfiguration.getString("name");

    }

    @Override
    public void build() {
        ConfigurationSection types = inventoryConfiguration.getConfigurationSection("items");
        for(String type : types.getKeys(false)) {
            List<String> lore;
            String displayName;
            ConfigurationSection cs = types.getConfigurationSection(type);
            int slot = cs.getInt("slot");
            String materialName = cs.getString("material");
            if(sanction.containsKey(type)) {
                int id = this.sanction.get(type).getId();
                displayName = cs.getString("displayName").replaceAll("%id%", String.valueOf(id));
                lore = formatLore(type);
            } else {
                displayName = cs.getString("no-punishments-name");
                lore = cs.getStringList("no-punishments-lore");
            }
            ItemStack item = getItem(Material.matchMaterial(materialName), 0, displayName, lore);
            this.inventory[0][slot] = item;
        }
    }

    @Override
    public Inventory getFirstInventory() {
        Inventory inv = Bukkit.createInventory(null, this.size, this.inventoryName);
        inv.setContents(inventory[0]);
        return inv;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {

    }

    @Override
    public String getPunishedPlayer() {
        return "";
    }

    private List<String> formatLore(String key) {
        List<String> unformattedLore = inventoryConfiguration.getStringList("items." + key + ".lore");
        Sanction sanction = this.sanction.get(key);
        List<String> lore = new ArrayList<>();
        String date = formatTimestamp(sanction.getDate());
        String until = formatTimestamp(sanction.getUntil());

        for(String line : unformattedLore) {
            String formated = line.replaceAll("%reason%", sanction.getReason());
            formated = formated.replaceAll("%staff%", sanction.getStaff());
            formated = formated.replaceAll("%date%", date);
            formated = formated.replaceAll("%until%", until);
            lore.add(formated);
        }
        return lore;
    }

    private String formatTimestamp(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}
