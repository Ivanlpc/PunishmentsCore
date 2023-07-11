package me.ivanlpc.punishmentscore.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.ivanlpc.punishmentscore.PunishmentsCore;

import java.sql.SQLException;

public class HikariConnection {
    private final HikariDataSource ds;

    public HikariConnection() {
        PunishmentsCore plugin = PunishmentsCore.getPlugin(PunishmentsCore.class);
        String ip = plugin.getDatabase().getString("Database.ip");
        String port = plugin.getDatabase().getString("Database.port");
        String user = plugin.getDatabase().getString("Database.user");
        String password = plugin.getDatabase().getString("Database.password");
        String database = plugin.getDatabase().getString("Database.database");
        int poolSize = plugin.getDatabase().getInt("Database.poolSize", 10);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( "jdbc:mysql://" + ip + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty( "autoReconnect" , "true" );
        config.addDataSourceProperty( "verifyServerCertificate" , "false" );
        config.addDataSourceProperty( "leakDetectionThreshold" , "true" );
        config.addDataSourceProperty( "useSSL" , "false" );
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
