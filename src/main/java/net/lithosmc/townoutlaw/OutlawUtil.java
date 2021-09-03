package net.lithosmc.townoutlaw;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.entity.Player;

public class OutlawUtil {

    public static boolean isInOutlawedLoc(Player player) {
        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(player.getLocation());

        if (townBlock == null || !townBlock.hasTown())
            return false;

        Resident res = TownyAPI.getInstance().getResident(player);
        return res != null && townBlock.getTownOrNull().hasOutlaw(res);
    }

}
