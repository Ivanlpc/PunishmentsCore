package me.ivanlpc.punishmentscore.api.database;

import me.ivanlpc.punishmentscore.PunishmentsCore;
import me.ivanlpc.punishmentscore.api.database.entities.Order;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private HikariConnection hikari = new HikariConnection();
    public DatabaseManager() {
        this.createTable();
    }
    public int createOrder(Player p, String userPunished, String punishment, String reason, List<String> command) {
        int orderId = -1;
        String query = "INSERT INTO orders (username, uuid, user_punished, punishment, reason) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = this.hikari.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, p.getPlayerListName());
                stmt.setString(2, p.getUniqueId().toString());
                stmt.setString(3, userPunished);
                stmt.setString(4, punishment);
                stmt.setString(5, reason);
                stmt.executeUpdate();

                try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        String insertQuery = "INSERT INTO commands (order_id, command) VALUES (?, ?)";
                        try (PreparedStatement cmdStmt = conn.prepareStatement(insertQuery)) {
                            for (String cmd : command) {
                                cmdStmt.setInt(1, orderId);
                                cmdStmt.setString(2, cmd);
                                cmdStmt.addBatch();
                            }
                            cmdStmt.executeBatch();
                        }
                        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Created new order: " + orderId);
                        orderId = id;
                    } else {
                        throw new SQLException("Order ID not generated");
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderId;
    }
    public void createTable() {
        String createOrdersQuery = "CREATE TABLE IF NOT EXISTS orders (" +
                "`id` int(11) NOT NULL AUTO_INCREMENT," +
                "`username` varchar(40) NOT NULL," +
                "`uuid` varchar(60) NOT NULL," +
                "`user_punished` varchar(40) NOT NULL," +
                "`punishment` varchar(255) NOT NULL," +
                "`reason` varchar(255) NOT NULL," +
                "`date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP()," +
                "PRIMARY KEY (id))";
        String createCommandsQuery = "CREATE TABLE IF NOT EXISTS `commands` (" +
                "`id` int(11) NOT NULL AUTO_INCREMENT," +
                "`order_id` int(11) NOT NULL," +
                "`command` varchar(255) NOT NULL," +
                "PRIMARY KEY(id)," +
                "CONSTRAINT FK_orderId FOREIGN KEY (order_id)" +
                "REFERENCES orders(id) ON DELETE CASCADE)";
        try(Connection conn = this.hikari.getConnection();
            PreparedStatement createOrders = conn.prepareStatement(createOrdersQuery);
            PreparedStatement createCommands = conn.prepareStatement(createCommandsQuery)) {

            createOrders.executeUpdate();
            createCommands.executeUpdate();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MySQL connection established!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders";

        try(Connection conn = this.hikari.getConnection();
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String uuid = rs.getString("uuid");
                    String userPunished = rs.getString("user_punished");
                    String punishment = rs.getString("punishment");
                    String reason = rs.getString("reason");
                    Date date = rs.getDate("date");
                    orders.add(new Order(id, username, uuid, userPunished, punishment, reason, date));
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    public List<String> getCommandsFromOrder(int id) {
        List<String> commands = new ArrayList<>();
        String query = "SELECT * FROM commands WHERE order_id = ?";

        try(Connection conn = this.hikari.getConnection();
            PreparedStatement st = conn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    String cmd = rs.getString("command");
                    commands.add(cmd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commands;
    }
    public void deleteOrder(int id) {
        String query = "DELETE FROM orders WHERE id = ?";
        
        try(Connection conn = this.hikari.getConnection();
            PreparedStatement st = conn.prepareStatement(query)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void reloadConnection() {
        this.hikari.close();
        this.hikari = new HikariConnection();
        createTable();
    }
    public void close() {
        this.hikari.close();
    }
}
