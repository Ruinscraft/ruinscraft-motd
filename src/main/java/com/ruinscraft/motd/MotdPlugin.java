package com.ruinscraft.motd;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class MotdPlugin extends JavaPlugin {

    private MotdStorage motdStorage;

    public MotdStorage getMotdStorage() {
        return motdStorage;
    }

    @Override
    public void onEnable() {
        singleton = this;

        saveDefaultConfig();

        final String sheetId = getConfig().getString("google-sheet-id");
        final String A1_query = getConfig().getString("google-sheet-A1-query");

        try {
            motdStorage = new MotdStorageGoogleSheets(new File(getDataFolder(), "credentials.json"), sheetId, A1_query);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        getCommand("ruinscraft-motd").setExecutor(new MotdCommand());
        getServer().getPluginManager().registerEvents(new PingListener(), this);
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
