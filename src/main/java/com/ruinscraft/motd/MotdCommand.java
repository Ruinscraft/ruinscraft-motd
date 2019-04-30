package com.ruinscraft.motd;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MotdCommand extends Command {

    public MotdCommand() {
        super("ruinscraft-motd", "ruinscraft.motd");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("ruinscraft-motd");

        if (MotdPlugin.get().getMotdStorage() instanceof MotdStorageGoogleSheets) {
            MotdStorageGoogleSheets motdStorageGoogleSheets = (MotdStorageGoogleSheets) MotdPlugin.get().getMotdStorage();

            sender.sendMessage("Updating Google Sheets cache...");

            motdStorageGoogleSheets.updateCache();
        }
    }

}
