package com.ruinscraft.motd;


import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        try {
            String firstLine = MotdPlugin.get().getMotdStorage().getRandomFirstLine().call();
            String secondLine = ChatColor.LIGHT_PURPLE + MotdPlugin.get().getMotdStorage().getRandomSecondLine().call();
            String message = color(firstLine + "\n" + secondLine);
            event.setMotd(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
