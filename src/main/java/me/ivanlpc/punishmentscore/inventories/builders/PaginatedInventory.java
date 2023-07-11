package me.ivanlpc.punishmentscore.inventories.builders;

import de.tr7zw.changeme.nbtapi.NBT;
import me.ivanlpc.punishmentscore.PunishmentsCore;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class PaginatedInventory extends InventoryBuilder {

    protected ItemStack[][] inventories;
    int currPage;

    public PaginatedInventory() {
        this.currPage = 0;
    }
    protected void setPaginationItems(String gui) {
        PunishmentsCore plugin = PunishmentsCore.getPlugin(PunishmentsCore.class);
        for(int i = 0; i < inventories.length; i++) {
            if(i == 0) {
                int slot = plugin.getConfig().getInt(gui +".nextPage.slot");
                ItemStack nextPageItem = buildPaginationItem(plugin, gui,"nextPage");
                inventories[i][slot] = nextPageItem;
            } else if (i == inventories.length - 1) {
                int slot = plugin.getConfig().getInt(gui + ".backPage.slot");
                ItemStack backPageItem = buildPaginationItem(plugin, gui,"backPage");
                inventories[i][slot] = backPageItem;
            } else {
                int slot_next = plugin.getConfig().getInt(gui + ".nextPage.slot");
                ItemStack nextPageItem = buildPaginationItem(plugin, gui,"nextPage");
                inventories[i][slot_next] = nextPageItem;
                int slot_back = plugin.getConfig().getInt(gui + ".backPage.slot");
                ItemStack backPageItem = buildPaginationItem(plugin, gui, "backPage");
                inventories[i][slot_back] = backPageItem;
            }
        }
    }
    private ItemStack buildPaginationItem (PunishmentsCore plugin, String gui, String action) {
        String displayName = plugin.getConfig().getString(gui + "." + action + ".item.displayName");
        String itemName = plugin.getConfig().getString(gui + "." + action + ".item.material");
        Material m = Material.matchMaterial(itemName);
        int durability = plugin.getConfig().getInt(gui + "." + action + ".item.damage", 0);
        List<String> lore = plugin.getConfig().getStringList(gui + "." + action + ".item.lore");
        ItemStack is = getItem(m, durability, displayName, lore);
        NBT.modify(is, nbti -> {
            nbti.setString("key", action);
            nbti.setBoolean("isConfirmation", false);
        });
        return is;
    }
    public ItemStack[] getNextPage() {
        int page;
        if (currPage + 1 < inventories.length) {
            currPage++;
        }
        page = currPage;
        return inventories[page];
    }
    public ItemStack[] getBackPage() {
        int page;
        if(currPage - 1 >= 0) {
            currPage--;
        }
        page = currPage;
        return inventories[page];
    }
}
