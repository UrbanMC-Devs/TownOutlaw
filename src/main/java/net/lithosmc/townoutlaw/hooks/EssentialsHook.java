package net.lithosmc.townoutlaw.hooks;

import com.earth2me.essentials.Essentials;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.ess3.api.events.FlyStatusChangeEvent;
import net.ess3.api.events.GodStatusChangeEvent;
import net.lithosmc.townoutlaw.OutlawUtil;
import net.lithosmc.townoutlaw.TownOutlawConfig;
import net.lithosmc.townoutlaw.objects.interfaces.IConfigReaderTO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssentialsHook implements IHookTO, IConfigReaderTO, Listener {

    private final Essentials essentials;
    private boolean disableFlight = true;
    private boolean disableGod = true;

    public EssentialsHook() {
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    @Override
    public void readConfig(TownOutlawConfig config) {
        disableFlight = config.disableFlight();
        disableGod = config.disableGod();
    }

    @Override
    public void onOutlawInTown(Player player, Resident res, Town town) {
        if (!disableGod)
            return;

        disableGod(player, true,true);
    }

    @EventHandler(ignoreCancelled = true)
    public void godStatusChangeEvent(GodStatusChangeEvent event) {
        if (!event.getValue() || !disableGod)
            return;

        Player player = event.getAffected().getBase();

        if (!shouldHandleEvent(player))
            return;

        if (disableGod(player, false,true))
            event.setCancelled(true);
    }

    // Returns true if god has been disabled for the player
    private boolean disableGod(Player player, boolean checkGod, boolean sendCancelMessage) {
        var user = essentials.getUser(player);

        if (user == null)
            return false;

        // Check if the user has god mode enabled before disabling
        // User do not have god mode enabled inside the status event.
        if (checkGod && !user.isGodModeEnabledRaw())
            return false;

        if (sendCancelMessage) {
            player.sendMessage(ChatColor.RED + "You cannot be in god mode in a town you are outlawed in!");
        }

        user.setGodModeEnabled(false);
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void flyStatusChangeEvent(FlyStatusChangeEvent event) {
        if (!event.getValue() || !disableFlight)
            return;

        var player = event.getAffected().getBase();

        if (!shouldHandleEvent(player))
            return;

        player.setAllowFlight(false);
        player.setFlying(false);

        event.setCancelled(true);

        player.sendMessage(ChatColor.RED + "You cannot fly in this town!");
    }

    private boolean shouldHandleEvent(Player player) {
        if (!player.isOnline() ||
                !OutlawUtil.isInOutlawedLoc(player))
            return false;

        return player.getGameMode() != GameMode.SPECTATOR && player.getGameMode() != GameMode.CREATIVE;
    }
}
