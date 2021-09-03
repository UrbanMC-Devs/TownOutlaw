package net.lithosmc.townoutlaw.hooks;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;

public interface IHookTO {

    default void onOutlawDeath(Player player, Resident res, Town deathTown) {}

    default void onOutlawInTown(Player player, Resident res, Town town) {}
}
