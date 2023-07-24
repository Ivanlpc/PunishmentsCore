package me.ivanlpc.punishmentscore.api;

import litebans.api.Database;
import me.ivanlpc.punishmentscore.api.database.entities.Sanction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class LitebansAPI {

    public static Map<String, Integer> getAllPunishments(String name) {
        Map<String, Integer> punishments = new HashMap<>();
        String query = "SELECT reason, COUNT(*) as count FROM( " +
                "SELECT a.reason " +
                "FROM {bans} a " +
                "WHERE a.uuid IN (SELECT uuid FROM {history} WHERE name = ?) AND (a.removed_by_reason IS NULL OR a.removed_by_reason != 'ELIMINADA')" +
                "UNION ALL " +
                "SELECT b.reason " +
                "FROM {mutes} b " +
                "WHERE b.uuid IN (SELECT uuid FROM {history} WHERE name = ?) AND (b.removed_by_reason IS NULL OR b.removed_by_reason != 'ELIMINADA')" +
                "UNION ALL " +
                "SELECT c.reason " +
                "FROM {warnings} c" +
                " WHERE c.uuid IN (SELECT uuid FROM {history} WHERE name = ?) AND (c.removed_by_reason IS NULL OR c.removed_by_reason != 'ELIMINADA')" +
                "UNION ALL " +
                "SELECT d.reason " +
                "FROM {kicks} d " +
                "WHERE d.uuid IN (SELECT uuid FROM {history} WHERE name = ?)) as d " +
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

    public static Map<String, Sanction> getLastPunishment(UUID uuid) {
        Map<String, Sanction> sanctions = new HashMap<>();

        String query = "SELECT 'ban' as type, id, banned_by_name, until, time, reason FROM {bans} WHERE uuid = ? AND time = (SELECT MAX(time) FROM {bans} WHERE uuid = ?)" +
                " UNION ALL" +
                " SELECT 'mute' as type, id, banned_by_name, until, time, reason FROM {mutes} WHERE uuid = ? AND time = (SELECT MAX(time) FROM {mutes} WHERE uuid = ?)" +
                " UNION ALL" +
                " SELECT 'kick' as type, id, banned_by_name, until, time, reason FROM {kicks} WHERE uuid = ? AND time = (SELECT MAX(time) FROM {kicks} WHERE uuid = ?)" +
                " UNION ALL" +
                " SELECT 'warn' as type, id, banned_by_name, until, time, reason FROM {warnings} WHERE uuid = ? AND time = (SELECT MAX(time) FROM {warnings} WHERE uuid = ?)";
        try(PreparedStatement stm = Database.get().prepareStatement(query)) {
            for(int i = 1; i <= 8; i++) {
                stm.setString(i, uuid.toString());
            }
            ResultSet resultSet = stm.executeQuery();
            while(resultSet.next()) {
                String type = resultSet.getString("type");
                int id = resultSet.getInt("id");
                String banned_by_name = resultSet.getString("banned_by_name");
                Long until = resultSet.getLong("until");
                Long time = resultSet.getLong("time");
                String reason = resultSet.getString("reason");
                Sanction sanction = new Sanction(id,banned_by_name, reason, time, until);
                sanctions.put(type, sanction);
            }
            return sanctions;
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " Error fetching: " + uuid.toString() + " from LiteBans database");
            return null;
        }
    }
}


