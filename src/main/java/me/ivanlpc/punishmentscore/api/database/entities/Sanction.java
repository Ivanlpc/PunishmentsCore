package me.ivanlpc.punishmentscore.api.database.entities;

import java.util.Date;

public class Sanction {
    private final int id;
    private final String banned_by_name;
    private final String reason;
    private final Date date;
    private final Date expiration;

    public Sanction(int id, String banned_by_name, String reason, Date date, Date expiration) {
        this.id = id;
        this.banned_by_name = banned_by_name;
        this.reason = reason;
        this.date = date;
        this.expiration = expiration;
    }

    public String getStaff() {
        return banned_by_name;
    }

    public String getReason() {
        return reason;
    }

    public Date getDate() {
        return date;
    }

    public Date getExpiration() {
        return expiration;
    }

    public int getId() {
        return this.id;
    }
}
