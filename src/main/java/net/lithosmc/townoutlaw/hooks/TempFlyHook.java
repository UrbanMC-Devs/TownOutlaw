package net.lithosmc.townoutlaw.hooks;

import com.moneybags.tempfly.TempFly;
import com.moneybags.tempfly.event.FlightEnabledEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.lithosmc.townoutlaw.OutlawUtil;
import net.lithosmc.townoutlaw.TownOutlawConfig;
import net.lithosmc.townoutlaw.objects.interfaces.IConfigReaderTO;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TempFlyHook implements IHookTO, IConfigReaderTO, Listener {
    private boolean disableFly = true;

    @Override
    public void readConfig(TownOutlawConfig config) {
        disableFly = config.disableFlight();
    }

    @Override
    public void onOutlawInTown(Player player, Resident res, Town town) {
        if (!disableFly
                || player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR)
            return;

        var tempFlyAPI = TempFly.getAPI();
        var flightUser = tempFlyAPI.getUser(player);
        // Check if flight is enabled
        if (flightUser.hasFlightEnabled()) {
            flightUser.disableFlight(0, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onUserEnableFlight(FlightEnabledEvent event) {
        if (!disableFly
                || !OutlawUtil.isInOutlawedLoc(event.getPlayer()))
            return;

        var player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR)
            return;

        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You cannot toggle flight in an outlawed town!");
    }
}
