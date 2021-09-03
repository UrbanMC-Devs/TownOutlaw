package net.lithosmc.townoutlaw.subcmds;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.bukkit.towny.object.Translation;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ResOutlawSub implements CommandExecutor {

    private final String PERM = "towny.command.resident.outlaw";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
            return true;
        }

        if (!player.hasPermission(PERM)) {
            player.sendMessage(Translation.of("msg_err_command_disable"));
            return true;
        }

        List<String> outlaws = TownyUniverse.getInstance().getTowns().stream()
                .filter(t -> t.hasOutlaw(player.getName()))
                .map(TownyObject::getName).collect(Collectors.toList());

        final String list = outlaws.isEmpty() ? "You are not outlawed in any town!"
                : String.join(", ", outlaws);

        player.sendMessage(ChatColor.GREEN + "Outlawed in: " + ChatColor.WHITE + list);
        return true;
    }
}
