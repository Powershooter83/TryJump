package me.prouge.tryjump.core;

import me.prouge.tryjump.core.commands.TestCMD;
import me.prouge.tryjump.core.inject.InjectionModule;
import me.prouge.tryjump.core.listener.PlayerListener;
import me.prouge.tryjump.core.listener.WorldSecurityListener;
import me.prouge.tryjump.core.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.io.File;

public class TryJump extends JavaPlugin {


    @Inject
    private PlayerListener playerListener;
    @Inject
    private WorldSecurityListener worldSecurityListener;
    @Inject
    private Language language;

    @Inject
    private TestCMD testCMD;

    @Override
    public void onEnable() {
        new InjectionModule(this);
        registerListeners();
        registerCommands();
        language.setup();

        if (!(new File(this.getDataFolder().getPath() + File.separator + "config.yml").exists())) {
            this.getConfig().options().copyDefaults(true);
            this.saveConfig();
        }


        World world = new WorldCreator("tryjump").createWorld();
        world.setAutoSave(false);


    }
    @Override
    public void onDisable(){
        Bukkit.getServer().unloadWorld("tryjump", true);
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(playerListener, this);
        this.getServer().getPluginManager().registerEvents(worldSecurityListener, this);
    }

    private void registerCommands() {
        getCommand("test").setExecutor(testCMD);
    }


}
