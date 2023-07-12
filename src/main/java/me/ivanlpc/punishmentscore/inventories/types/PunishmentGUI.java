package me.ivanlpc.punishmentscore.inventories.types;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.inventories.PunishmentInventory;
import me.ivanlpc.punishmentscore.inventories.builders.PaginatedInventory;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class PunishmentGUI extends PaginatedInventory implements PunishmentInventory {

    private final PunishmentsCore plugin = PunishmentsCore.getPlugin(PunishmentsCore.class);
    private final Map<String, Integer> punishmentList;
    private final String punishedPlayer;

    public PunishmentGUI(Map<String, Integer> punishmentList, String punishedPlayer) {
        super();
        int pages = this.plugin.getConfig().getInt("GUI.pages");
        int size = this.plugin.getConfig().getInt("GUI.size", 54);
        this.punishmentList = punishmentList;
        inventories = new ItemStack[pages][size];
        this.punishedPlayer = punishedPlayer;
    }
    @Override
    public ItemStack[][] build() {
        if(this.inventories.length > 1) setPaginationItems("GUI");
        ConfigurationSection sec = this.plugin.getConfig().getConfigurationSection("GUI.items");
        for(String key : sec.getKeys(false)) {
            List<String> lore;
            String name = sec.getString(key +".displayName");
            int slot = sec.getInt(key +".slot");
            int page = sec.getInt(key +".page", 1) - 1;
            boolean needsConfirmation = sec.getBoolean(key +".confirm");
            int command_id = getCommandId(key);
            String materialName = sec.getString(key + ".material", "STONE");
            int durability = sec.getInt(key + ".damage", 0);
            Material m = Material.matchMaterial(materialName);
            String permission_required = sec.getString(key +".levels." + command_id + ".permission", "");
            lore = sec.getStringList(key +".levels." + command_id + ".lore");
            ItemStack is = getItem(m, durability, name, lore);
            NBT.modify(is, nbti -> {
                nbti.setBoolean("conf", needsConfirmation);
                nbti.setBoolean("isConfirmation", false);
                nbti.setString("perm", permission_required);
                nbti.setString("key", key);
                nbti.setString("level", Integer.toString(command_id));
            });
            inventories[page][slot] = is;
        }
        return this.inventories;
    }


    private int getCommandId(String key) {
        String reason = this.plugin.getConfig().getString("GUI.items." + key + ".reason");
        ConfigurationSection cs = this.plugin.getConfig().getConfigurationSection("GUI.items." + key + ".levels");
        int max = 0;
        for(String s: cs.getKeys(false)) {
            int num = Integer.parseInt(s);
            if(num > max ){
                max = num;
            }
        }
        int punishmentCount = punishmentList.getOrDefault(reason, 0);
        return Math.min(punishmentCount, max);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        NBTItem nbti = new NBTItem(event.getCurrentItem());
        Player p = (Player) event.getWhoClicked();
        boolean confirmation = nbti.getBoolean("conf");
        String key = nbti.getString("key");
        String level = nbti.getString("level");
        if(key.equals("backPage")) {
            PunishmentGUI pg = (PunishmentGUI) this.plugin.getInventoryManager().getCurrentInventory(p);
            ItemStack[] is = pg.getBackPage();
            event.getClickedInventory().setContents(is);
            p.updateInventory();
            return;
        } else if(key.equals("nextPage")) {
            PunishmentGUI pg = (PunishmentGUI) this.plugin.getInventoryManager().getCurrentInventory(p);
            ItemStack[] is = pg.getNextPage();
            event.getClickedInventory().setContents(is);
            p.updateInventory();
            return;
        }
        if(confirmation) {
            nbti.setBoolean("isConfirmation", true);
            ConfirmationGUI cg = new ConfirmationGUI(nbti.getItem(), punishedPlayer);
            this.plugin.getInventoryManager().openInventory(p, cg);
            ItemStack[][] items = cg.build();
            event.getClickedInventory().setContents(items[0]);
            p.updateInventory();
            return;
        }
        List<String> commands = this.plugin.getConfig().getStringList("GUI.items." + key + ".levels." + level + ".commands");
        String reason = this.plugin.getConfig().getString("GUI.items." + key + ".reason");
        List<String> parsedCommands = parseCommands(p, punishedPlayer, reason, commands);
        executeCommands(p, parsedCommands);
    }

    @Override
    public String getPunishedPlayer() {
        return this.punishedPlayer;
    }
}
