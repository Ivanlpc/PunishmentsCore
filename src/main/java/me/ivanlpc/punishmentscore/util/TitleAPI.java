package me.ivanlpc.punishmentscore.util;

import me.ivanlpc.punishmentscore.PunishmentsCore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleAPI {
        public static void sendPacket(Player player, Object packet) {
            try {
                Object handle = player.getClass().getMethod("getHandle").invoke(player);
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static Class<?> getNMSClass(String name) {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            try {
                return Class.forName("net.minecraft.server." + version + "." + name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static void sendTitle(PunishmentsCore plugin, Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
            if(plugin.isNew()) {
                if(title.isEmpty()) {
                    title = " ";
                }
                if(subtitle.isEmpty()) {
                    subtitle = " ";
                }
                player.sendTitle(getColoredMessageNew(title), getColoredMessageNew(subtitle), fadeIn, stay, fadeOut);
                return;
            }
            try {
                Object e;
                Object chatTitle;
                Object chatSubtitle;
                Constructor subtitleConstructor;
                Object titlePacket;
                Object subtitlePacket;

                if (title != null) {
                    title = ChatColor.translateAlternateColorCodes('&', title);
                    title = title.replaceAll("%player%", player.getDisplayName());
                    // Times packets
                    e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
                    chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
                    subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
                    titlePacket = subtitleConstructor.newInstance(new Object[]{e, chatTitle, fadeIn, stay, fadeOut});
                    sendPacket(player, titlePacket);

                    e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get((Object) null);
                    chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
                    subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent")});
                    titlePacket = subtitleConstructor.newInstance(new Object[]{e, chatTitle});
                    sendPacket(player, titlePacket);
                }

                if (subtitle != null) {
                    subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                    subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                    // Times packets
                    e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
                    chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
                    subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
                    subtitlePacket = subtitleConstructor.newInstance(new Object[]{e, chatSubtitle, fadeIn, stay, fadeOut});
                    sendPacket(player, subtitlePacket);

                    e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get((Object) null);
                    chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + subtitle + "\"}"});
                    subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
                    subtitlePacket = subtitleConstructor.newInstance(new Object[]{e, chatSubtitle, fadeIn, stay, fadeOut});
                    sendPacket(player, subtitlePacket);
                }
            } catch (Exception var11) {
                var11.printStackTrace();
            }
        }

        private static String getColoredMessageNew(String message) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher match = pattern.matcher(message);

            while(match.find()) {
                String color = message.substring(match.start(),match.end());
                message = message.replace(color, ChatColor.of(color)+"");

                match = pattern.matcher(message);
            }
            message = ChatColor.translateAlternateColorCodes('&', message);
            return message;
        }
}

