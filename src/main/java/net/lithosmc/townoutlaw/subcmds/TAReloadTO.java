package net.lithosmc.townoutlaw.subcmds;

import net.lithosmc.townoutlaw.TownOutlaw;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// /ta reloadtownoutlaw
public class TAReloadTO implements CommandExecutor {

    private final TownOutlaw plugin;

    public TAReloadTO(TownOutlaw plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("townoutlaw.reload"))
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");

        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Reloaded TownOutlaw plugin!");
        return true;
    }
}
