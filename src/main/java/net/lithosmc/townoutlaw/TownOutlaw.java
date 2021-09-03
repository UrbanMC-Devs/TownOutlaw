package net.lithosmc.townoutlaw;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import net.lithosmc.townoutlaw.hooks.HookManager;
import net.lithosmc.townoutlaw.listeners.EventListeners;
import net.lithosmc.townoutlaw.subcmds.ResOutlawSub;
import net.lithosmc.townoutlaw.subcmds.TAReloadTO;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public final class TownOutlaw extends JavaPlugin {

    private HookManager hookManager;
    private JailManager jailManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        hookManager = new HookManager(this);
        jailManager = new JailManager(this);

        var config = loadConfig();
        if (config != null) {
            hookManager.readConfig(config);
            jailManager.readConfig(config);
        }

        // Register towny command add-ons
        TownyCommandAddonAPI.addSubCommand(TownyCommandAddonAPI.CommandType.RESIDENT,
                                            "outlaw", new ResOutlawSub());
        TownyCommandAddonAPI.addSubCommand(TownyCommandAddonAPI.CommandType.TOWNYADMIN,
                "reloadtownoutlaw", new TAReloadTO(this));

        Bukkit.getPluginManager().registerEvents(new EventListeners(this, jailManager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Unregister Towny command-addons
        if (Bukkit.getPluginManager().isPluginEnabled("Towny")) {
            TownyCommandAddonAPI.removeSubCommand(TownyCommandAddonAPI.CommandType.RESIDENT,
                                                    "outlaw");
            TownyCommandAddonAPI.removeSubCommand(TownyCommandAddonAPI.CommandType.TOWNYADMIN,
                    "reloadtownoutlaw");
        }
        hookManager = null;
        jailManager = null;
    }

    public void reload() {
        var newConfig = loadConfig();
        if (newConfig != null) {
            hookManager.readConfig(newConfig);
            jailManager.readConfig(newConfig);
        }
    }

    private TownOutlawConfig loadConfig() {
        try {
            return TownOutlawConfig.loadConfig(getDataFolder(), getLogger());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error loading config!", e);
            return null;
        }
    }

    public HookManager getHookManager() {
        return hookManager;
    }
}
