package net.lithosmc.townoutlaw.listeners;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.event.resident.ResidentUnjailEvent;
import com.palmergames.bukkit.towny.event.teleport.OutlawTeleportEvent;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.lithosmc.townoutlaw.JailManager;
import net.lithosmc.townoutlaw.TownOutlaw;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EventListeners implements Listener {

    private final TownOutlaw plugin;
    private final JailManager jailManager;

    public EventListeners(TownOutlaw plugin, JailManager jailManager) {
        this.plugin = plugin;
        this.jailManager = jailManager;
    }

    private void disableFly(Player p) {
        if (p.getGameMode().equals(GameMode.SPECTATOR) ||
                p.getGameMode().equals(GameMode.CREATIVE))
            return;

        if (p.isFlying()) {
            if (p.getAllowFlight())
                p.setAllowFlight(false);

            p.setFlying(false);

            p.setFallDistance(-1000000);
        }
    }

    @EventHandler
    public void onOutlawEnterTown(OutlawTeleportEvent event) {
        var res = event.getOutlaw();
        var player = res.getPlayer();
        if (player != null) {
            disableFly(player);
            plugin.getHookManager().onOutlawInTown(player, res, event.getTown());
        }
    }

    @EventHandler
    public void onOutlawChangePlot(PlayerChangePlotEvent event) {
        // There's no PlayerChangeTownPlotEvent
        // So this is a bit imperformant

        var fromTB = TownyAPI.getInstance().getTownBlock(event.getFrom());
        var toTB = TownyAPI.getInstance().getTownBlock(event.getTo());

        // Check that both from and to locs are within towns
        if (fromTB == null || toTB == null
                || !fromTB.hasTown() || !toTB.hasTown())
            return;

        var town = fromTB.getTownOrNull();
        // Both have towns, check if same town
        if (!town.equals(toTB.getTownOrNull()))
            return;

        var player = event.getPlayer();

        var res = TownyAPI.getInstance().getResident(player);
        if (res == null ||
                !town.hasOutlaw(res))
            return;

        disableFly(player);
        plugin.getHookManager().onOutlawInTown(player, res, town);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathloc = player.getLocation();
        TownBlock tb = TownyAPI.getInstance().getTownBlock(deathloc);

        if (tb == null || !tb.hasTown())
            return;

        var deathTown = tb.getTownOrNull();
        var res = TownyAPI.getInstance().getResident(player);

        if (res == null || !deathTown.hasOutlaw(res))
            return;

        jailManager.handleOutlawDeath(player, res, deathTown);
        plugin.getHookManager().onOutlawDeath(player, res, deathTown);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Respawn jailed outlaws inside the jail cell
        var player = event.getPlayer();

        if (!JailManager.isJailedOutlaw(player))
            return;

        var res = TownyAPI.getInstance().getResident(player);

        if (res == null || !res.isJailed())
            return;

        var jailSpawn = res.getJailSpawn();
        if (jailSpawn != null)
            event.setRespawnLocation(jailSpawn);
    }

    @EventHandler
    public void onResidentUnjail(ResidentUnjailEvent event) {
        var res = event.getResident();
        var player = res.getPlayer();
        if (player != null) {
            jailManager.handleResidentUnjail(player);
        }
    }

    // Higher priority than Towny's command pre-process event
    @EventHandler(priority = EventPriority.HIGH)
    public void onJailPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!event.isCancelled())
            return;

        var res = TownyAPI.getInstance().getResident(event.getPlayer());
        if (res == null)
            return;

        // Make sure outlaw is still jailed
        if (!jailManager.validateOutlawJail(event.getPlayer(), res)) {
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJailedOutlawTeleport(PlayerTeleportEvent event) {
        var player = event.getPlayer();

        boolean isJailedOutlaw = JailManager.isJailedOutlaw(player);

        if (!isJailedOutlaw)
            return;

        var res = TownyAPI.getInstance().getResident(player.getUniqueId());
        if (res == null)
            return;

        if (!jailManager.validateOutlawJail(player, res)) {
            event.setCancelled(false);
            return;
        }

        event.setCancelled(true);
    }
}
