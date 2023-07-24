package me.ivanlpc.punishmentscore.inventories.builders;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class PaginatedInventory extends InventoryBuilder {

    protected ItemStack[][] inventories;
    protected int pages;
    private int currPage;

    public PaginatedInventory(String configuration) {
        super(configuration);
        this.currPage = 0;
    }

    protected void setPaginationItems() {
        for(int i = 0; i < inventories.length; i++) {
            if(i == 0) {
                int slot = inventoryConfiguration.getInt("nextPage.slot");
                ItemStack nextPageItem = buildPaginationItem("nextPage");
                inventories[i][slot] = nextPageItem;
            } else if (i == inventories.length - 1) {
                int slot = inventoryConfiguration.getInt("backPage.slot");
                ItemStack backPageItem = buildPaginationItem("backPage");
                inventories[i][slot] = backPageItem;
            } else {
                int slot_next = inventoryConfiguration.getInt("nextPage.slot");
                ItemStack nextPageItem = buildPaginationItem("nextPage");
                inventories[i][slot_next] = nextPageItem;
                int slot_back = inventoryConfiguration.getInt("backPage.slot");
                ItemStack backPageItem = buildPaginationItem("backPage");
                inventories[i][slot_back] = backPageItem;
            }
        }
    }
    private ItemStack buildPaginationItem (String action) {
        String displayName = inventoryConfiguration.getString( action + ".item.displayName");
        String itemName = inventoryConfiguration.getString(action + ".item.material");
        Material m = Material.matchMaterial(itemName);
        int durability = inventoryConfiguration.getInt(action + ".item.damage", 0);
        List<String> lore = inventoryConfiguration.getStringList(action + ".item.lore");
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
    @Override
    public Inventory getFirstInventory() {
        assert inventories != null;
        assert inventoryName != null;
        Inventory inv = Bukkit.createInventory(null, inventories[0].length, inventoryName);
        inv.setContents(inventories[0]);
        return inv;
    }
}
