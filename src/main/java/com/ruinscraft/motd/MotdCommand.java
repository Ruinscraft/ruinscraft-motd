package com.ruinscraft.motd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MotdCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MotdPlugin.get().getMotdStorage() instanceof MotdStorageGoogleSheets) {
            MotdStorageGoogleSheets motdStorageGoogleSheets = (MotdStorageGoogleSheets) MotdPlugin.get().getMotdStorage();
            sender.sendMessage("Updating Google Sheets cache...");
            motdStorageGoogleSheets.updateCache();
        }
        return true;
    }

}
