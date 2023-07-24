package me.ivanlpc.punishmentscore.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.ivanlpc.punishmentscore.PunishmentsCore;

import java.sql.SQLException;

public class HikariConnection {
    private final HikariDataSource ds;

    public HikariConnection() {
        PunishmentsCore plugin = PunishmentsCore.getPlugin(PunishmentsCore.class);
        String ip = plugin.getConfig().getString("Database.ip");
        String port = plugin.getConfig().getString("Database.port");
        String user = plugin.getConfig().getString("Database.user");
        String password = plugin.getConfig().getString("Database.password");
        String database = plugin.getConfig().getString("Database.database");
        int poolSize = plugin.getConfig().getInt("Database.poolSize", 10);
        boolean useSSL = plugin.getConfig().getBoolean("Database.useSSL");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( "jdbc:mysql://" + ip + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty( "autoReconnect" , "true" );
        config.addDataSourceProperty( "verifyServerCertificate" , "false" );
        config.addDataSourceProperty( "leakDetectionThreshold" , "true" );
        config.addDataSourceProperty( "useSSL" , useSSL );
        config.setMaximumPoolSize(poolSize);
        config.setConnectionTimeout(5000);
        ds = new HikariDataSource(config);
    }
    public java.sql.Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    public void close() {
        if(ds != null) {
            ds.close();
        }
    }
}
