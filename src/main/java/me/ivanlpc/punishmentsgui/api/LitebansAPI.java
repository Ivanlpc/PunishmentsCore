package me.ivanlpc.punishmentsgui.api;

import litebans.api.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class LitebansAPI {

    public static Map<String, Integer> getAllPunishments(String name) {
        Map<String, Integer> punishments = new HashMap<>();
        String query = "SELECT reason, COUNT(*) as count FROM( " +
                "SELECT a.reason " +
                "FROM {bans} a " +
                "WHERE a.uuid = (SELECT uuid FROM {history} WHERE name = ?) AND (a.removed_by_reason IS NULL OR a.removed_by_reason != 'ELIMINADA')" +
                "UNION ALL " +
                "SELECT b.reason " +
                "FROM {mutes} b " +
                "WHERE b.uuid = (SELECT uuid FROM {history} WHERE name = ?) AND (b.removed_by_reason IS NULL OR b.removed_by_reason != 'ELIMINADA')" +
                "UNION ALL " +
                "SELECT c.reason " +
                "FROM {warnings} c" +
                " WHERE c.uuid = (SELECT uuid FROM {history} WHERE name = ?) AND (c.removed_by_reason IS NULL OR c.removed_by_reason != 'ELIMINADA')" +
                "UNION ALL " +
                "SELECT d.reason " +
                "FROM {kicks} d " +
                "WHERE d.uuid = (SELECT uuid FROM {history} WHERE name = ?)) as d " +
                "GROUP BY reason";
        try (PreparedStatement st = Database.get().prepareStatement(query)) {
            st.setString(1, name);
            st.setString(2, name);
            st.setString(3, name);
            st.setString(4, name);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    String reason = rs.getString("reason");
                    int count = rs.getInt("count");
                    punishments.put(reason, count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " Error fetching: " + name + " from LiteBans database");
            return null;
        }
        return punishments;
    }
}


