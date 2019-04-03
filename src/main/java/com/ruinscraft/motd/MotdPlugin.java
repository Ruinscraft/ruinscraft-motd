package com.ruinscraft.motd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.plugin.Plugin;

public class MotdPlugin extends Plugin {

	private MotdStorage motdStorage;

	public MotdStorage getMotdStorage() {
		return motdStorage;
	}

	@Override
	public void onEnable() {
		singleton = this;

		getDataFolder().mkdirs();

		File configFile = new File(getDataFolder(), "config.properties");

		if (!configFile.exists()) {
			try {
				configFile.createNewFile();

				try (InputStream is = getResourceAsStream("config.properties");
						OutputStream os = new FileOutputStream(configFile)) {
					ByteStreams.copy(is, os);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/* Only storage implementation is Google Sheets for now */
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(configFile));
			
			final String sheetId = properties.getProperty("google-sheet-id");
			final String A1_query = properties.getProperty("google-sheet-A1-query");
			
			motdStorage = new MotdStorageGoogleSheets(new File(getDataFolder(), "credentials.json"), sheetId, A1_query);
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}

		getProxy().getPluginManager().registerListener(this, new PingListener());
		getProxy().getPluginManager().registerCommand(this, new MotdCommand());
	}

	@Override
	public void onDisable() {
		motdStorage.shutdown();

		singleton = null;
	}

	/* singleton */
	private static MotdPlugin singleton;

	public static MotdPlugin get() {
		return singleton;
	}
	/* singleton */
}
