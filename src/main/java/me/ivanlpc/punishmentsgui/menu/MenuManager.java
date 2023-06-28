package me.ivanlpc.punishmentsgui.menu;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MenuManager {
    private Map<Player, PunishmentsMenu> openedMenus = new HashMap<>();

    public void menuOpened(Player p, PunishmentsMenu i) {
        openedMenus.put(p, i);
    }

    public void menuClosed(Player p) {
        openedMenus.remove(p);
    }

    public boolean hasOpenedMenu(Player p) {
        return openedMenus.containsKey(p);
    }

    public PunishmentsMenu getInventory(Player p) {
        return openedMenus.get(p);
    }

    public void closeAllMenus() {
        Set<Player> players_using_menu =  new HashSet<>(openedMenus.keySet());
        for(Player p: players_using_menu) {
            p.closeInventory();
            openedMenus.remove(p);
        }
    }
}
