package net.lithosmc.townoutlaw;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.event.resident.ResidentUnjailEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translation;
import com.palmergames.bukkit.towny.object.jail.Jail;
import net.lithosmc.townoutlaw.objects.interfaces.IConfigReaderTO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class JailManager implements IConfigReaderTO {

    // Jail Constants
    private static final String JAILED_METADATA = "townyoutlawjailed";
    private static final String JAILED_PROTECT_METADATA = "townyjailprotect";
    // 5 min jail time
    private long jailTimeMs = TimeUnit.MINUTES.toMillis(5);
    // 2 mins before jailed again.
    private long jailProtectTimeMs = TimeUnit.MINUTES.toMillis(2);

    private final TownOutlaw plugin;

    public JailManager(TownOutlaw plugin) {
        this.plugin = plugin;
    }

    @Override
    public void readConfig(TownOutlawConfig config) {
        jailTimeMs = TimeUnit.SECONDS.toMillis(config.getJailTime());
        jailProtectTimeMs = TimeUnit.SECONDS.toMillis(config.getJailProtectTime());
    }

    public static boolean isJailedOutlaw(Player player) {
        return player.hasMetadata(JAILED_METADATA);
    }

    private String formatDuration(long msDuration) {
        final StringBuilder builder = new StringBuilder();
        BiConsumer<Long, String> appendDuration = (duration, type) -> {
            if (duration > 0) {
                if(!builder.isEmpty())
                    builder.append(" ");

                builder.append(duration).append(" ").append(type);
            }
        };

        // Convert to seconds
        long sDuration = TimeUnit.MILLISECONDS.toSeconds(msDuration);

        // Convert to days
        long days = TimeUnit.SECONDS.toDays(sDuration);
        sDuration -= TimeUnit.DAYS.toSeconds(days);
        appendDuration.accept(days, "days");

        long hours = TimeUnit.SECONDS.toHours(sDuration);
        sDuration -= TimeUnit.HOURS.toSeconds(hours);
        appendDuration.accept(hours, "hours");

        long minutes = TimeUnit.SECONDS.toMinutes(sDuration);
        sDuration -= TimeUnit.MINUTES.toSeconds(minutes);
        appendDuration.accept(minutes, "minutes");

        appendDuration.accept(sDuration, "seconds");

        return builder.toString();
    }

    // Returns true if outlaw should still be jailed
    // Returns false if outlaw should be free'd
    public boolean validateOutlawJail(Player player, Resident resident) {
        if (!isJailedOutlaw(player))
            return false;

        long endTime = player.getMetadata(JAILED_METADATA).get(0).asLong();
        long currTime = System.currentTimeMillis();

        // Should be unjailed
        if (currTime > endTime) {
            unjailOutlaw(resident);
            TownyMessaging.sendMsg(player, Translation.of("msg_outlaw_freed"));
            return false;
        }

        long msJailDuration = endTime - currTime;
        String duration = formatDuration(msJailDuration);
        player.sendMessage(ChatColor.RED + "You are jailed for " + duration + "!");

        return true;
    }

    private void applyJailFlag(Player player) {
        long unjailTime = System.currentTimeMillis() + jailTimeMs;
        player.setMetadata(JAILED_METADATA, new FixedMetadataValue(plugin, unjailTime));
    }

    private void removeJailFlag(Player player) {
        player.removeMetadata(JAILED_METADATA, plugin);
    }

    public boolean isJailProtected(Player player) {
        if (!player.hasMetadata(JAILED_PROTECT_METADATA))
            return false;

        long endProtectTime = player.getMetadata(JAILED_PROTECT_METADATA).get(0).asLong();

        boolean isJailProtected = (endProtectTime > 0) &&
                                    System.currentTimeMillis() > endProtectTime;

        // Remove jail protect time within check
        if (!isJailProtected) {
            player.removeMetadata(JAILED_PROTECT_METADATA, plugin);
        }

        return isJailProtected;
    }

    private boolean hasJailedExpired(Player player) {
        var jailMeta = player.getMetadata(JAILED_METADATA);
        if (jailMeta.isEmpty())
            return true;

        var jailEndTimestamp = jailMeta.get(0).asLong();
        return System.currentTimeMillis() > jailEndTimestamp;
    }

    public void handleOutlawDeath(Player player, Resident resident, Town deathTown) {
        if (resident == null || player == null
            || jailTimeMs == 0)
            return;
        // Remove corrupted outlaw jail
        if (isJailedOutlaw(player)) {
            boolean jailExpired = hasJailedExpired(player);
            if (jailExpired) {
                unjailOutlaw(resident);
            }
            else if (!resident.isJailed()) {
                removeJailFlag(player);
            }
            return;
        }

        if (!deathTown.hasJails() || !deathTown.hasOutlaw(resident)
            || isJailProtected(player))
            return;

        Jail jail = deathTown.getPrimaryJail();
        resident.setJail(jail);
        applyJailFlag(player);
        player.sendMessage(ChatColor.RED + "You were sent to jail in " + deathTown.getName() + " since you died as an outlaw!");
        TownyMessaging.sendTownMessagePrefixed(
                deathTown,
                ChatColor.RED + "Outlaw " + player.getName() + " was sent to jail for dying!"
        );
        resident.save();
    }

    private void applyJailProtection(Player player) {
        if (jailProtectTimeMs == 0)
            return;

        long msEndProtectTime = System.currentTimeMillis() + jailProtectTimeMs;

        player.setMetadata(JAILED_PROTECT_METADATA,
                new FixedMetadataValue(plugin, msEndProtectTime));
    }

    public void handleResidentUnjail(Player player) {
        if (isJailedOutlaw(player)) {
            removeJailFlag(player);
            applyJailProtection(player);
        }
    }

    public static void unjailOutlaw(Resident r) {
        if (!r.isJailed()) {
            return;
        }

        Town jailTown = r.getJail().getTown();
        // Notify
        if (jailTown != null)
            TownyMessaging.sendPrefixedTownMessage(jailTown, Translation.of("msg_player_has_been_freed_from_jail_number", r.getName(), r.getJailSpawn()));

        TownyMessaging.sendMsg(r, Translation.of("msg_you_have_been_freed_from_jail"));

        r.setJail(null);
        r.save();
        Bukkit.getPluginManager().callEvent(new ResidentUnjailEvent(r));
    }

}
