package me.ivanlpc.punishmentscore.api.database.entities;

import java.sql.Date;

public class Order {
    private final int id;
    private final String username;
    private final String uuid;
    private final String userPunished;
    private final String punishment;
    private final String reason;
    private final Date date;

    public Order(int id, String username, String uuid, String userPunished, String punishment, String reason, Date date) {
        this.id = id;
        this.username = username;
        this.uuid = uuid;
        this.userPunished = userPunished;
        this.punishment = punishment;
        this.reason = reason;
        this.date = date;
    }
    public int getId() {
        return this.id;
    }
    public String getUsername() {
        return this.username;
    }
    public String getUuid() {
        return this.uuid;
    }
    public String getUserPunished() {
        return this.userPunished;
    }
    public String getPunishment() {
        return this.punishment;
    }
    public String getReason() {
        return this.reason;
    }
    public Date getDate() {
        return this.date;
    }
}
