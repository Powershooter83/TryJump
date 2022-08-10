package me.prouge.tryjump.creator;

import me.prouge.tryjump.creator.commands.ModuleCMD;
import me.prouge.tryjump.creator.inject.InjectionModule;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;

public class TryJump extends JavaPlugin {

    @Inject private ModuleCMD moduleCMD;

    @Override
    public void onEnable() {
        new InjectionModule(this);
        this.getCommand("module").setExecutor(moduleCMD);
    }
}
