package me.prouge.tryjump.core;

import de.dytanic.cloudnet.ext.bridge.server.BridgeServerHelper;
import me.prouge.tryjump.core.commands.HelpBlockResetCMD;
import me.prouge.tryjump.core.commands.SkipCMD;
import me.prouge.tryjump.core.inject.InjectionModule;
import me.prouge.tryjump.core.listener.*;
import me.prouge.tryjump.core.utils.Language;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.io.File;

public class TryJump extends JavaPlugin {

    @Inject
    private SkipCMD skipCMD;

    @Inject
    private Language language;

    @Inject
    private HelpBlockResetCMD helpBlockResetCMD;


    @Inject
    private DeathmatchListener deathmatchListener;

    @Inject
    private Enchanting enchanting;

    @Inject
    private GameListener gameListener;

    @Inject
    private LobbyListener lobbyListener;

    @Inject
    private PlayerListener playerListener;

    @Inject
    private ShopListener shopListener;

    @Inject
    private WorldGameListener worldGameListener;

    @Inject
    private WorldSecurityListener worldSecurityListener;

    @Override
    public void onEnable() {
        BridgeServerHelper.setMaxPlayers(getConfig().getInt("maxPlayers"));
        BridgeServerHelper.setMotd((String) getConfig().get("deathmatch"));
        int numberOfTeams = getConfig().getInt("maxPlayers") / getConfig().getInt("teamSize");
        String teams = numberOfTeams + "x" + getConfig().getInt("teamSize");
        BridgeServerHelper.setExtra(teams);
        BridgeServerHelper.updateServiceInfo();
        if (!(new File(this.getDataFolder().getPath() + File.separator + "config.yml").exists())) {
            this.getConfig().options().copyDefaults(true);
            this.saveConfig();
        }
        new InjectionModule(this);
        registerCommands();
        language.setup();

        World world = new WorldCreator("tryjump").createWorld();
        world.setAutoSave(false);
        World pvp = new WorldCreator("deathmatch").createWorld();
        pvp.setAutoSave(false);
        registerListener();
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().unloadWorld("tryjump", true);
    }

    private void registerCommands() {
        getCommand("skip").setExecutor(skipCMD);
        getCommand("reset").setExecutor(helpBlockResetCMD);
    }

    private void registerListener() {
        this.getServer().getPluginManager().registerEvents(deathmatchListener, this);
        this.getServer().getPluginManager().registerEvents(enchanting, this);
        this.getServer().getPluginManager().registerEvents(gameListener, this);
        this.getServer().getPluginManager().registerEvents(lobbyListener, this);
        this.getServer().getPluginManager().registerEvents(playerListener, this);
        this.getServer().getPluginManager().registerEvents(shopListener, this);
        this.getServer().getPluginManager().registerEvents(worldGameListener, this);
        this.getServer().getPluginManager().registerEvents(worldSecurityListener, this);
    }


}
