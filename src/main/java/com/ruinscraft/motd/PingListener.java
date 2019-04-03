package com.ruinscraft.motd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingListener implements Listener {

	@EventHandler
	public void onPing(ProxyPingEvent event) {
		try {
			String firstLine = MotdPlugin.get().getMotdStorage().getRandomFirstLine().call();
			String secondLine = ChatColor.LIGHT_PURPLE + MotdPlugin.get().getMotdStorage().getRandomSecondLine().call();
			String message = color(firstLine + "\n" + secondLine);
			event.getResponse().setDescription(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String color(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
}
