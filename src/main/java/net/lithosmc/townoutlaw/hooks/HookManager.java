package net.lithosmc.townoutlaw.hooks;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.lithosmc.townoutlaw.TownOutlaw;
import net.lithosmc.townoutlaw.TownOutlawConfig;
import net.lithosmc.townoutlaw.objects.interfaces.IConfigReaderTO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class HookManager {

    private final List<IHookTO> hooks = new ArrayList<>();

    private final TownOutlaw plugin;

    @SuppressWarnings("Convert2MethodRef")
    public HookManager(TownOutlaw plugin) {
        this.plugin = plugin;
        addHook("Essentials", () -> new EssentialsHook());
        addHook("TempFly", () -> new TempFlyHook());
    }

    public void addHook(final String pluginName, final Supplier<IHookTO> hookSupplier) {
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            var hook = hookSupplier.get();

            if (hook instanceof Listener hookListener) {
                Bukkit.getPluginManager().registerEvents(hookListener, plugin);
            }

            hooks.add(hook);
        }
    }

    public void readConfig(TownOutlawConfig config) {
        for (IHookTO hook : hooks) {
            if (hook instanceof IConfigReaderTO readerHook) {
                readerHook.readConfig(config);
            }
        }
    }

    public void onOutlawDeath(Player player, Resident res, Town deathTown) {
        for (IHookTO hook : hooks) {
            hook.onOutlawDeath(player, res, deathTown);
        }
    }

    public void onOutlawInTown(Player player, Resident res, Town town) {
        for (IHookTO hook : hooks) {
            hook.onOutlawInTown(player, res, town);
        }
    }

}
