package me.prouge.tryjump.core;

import me.prouge.tryjump.core.commands.SkipCMD;
import me.prouge.tryjump.core.commands.TestCMD;
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
    private TestCMD testCMD;


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
        new InjectionModule(this);
        registerCommands();
        language.setup();

        if (!(new File(this.getDataFolder().getPath() + File.separator + "config.yml").exists())) {
            this.getConfig().options().copyDefaults(true);
            this.saveConfig();
        }


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
        getCommand("shop").setExecutor(testCMD);
        getCommand("skip").setExecutor(skipCMD);
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
