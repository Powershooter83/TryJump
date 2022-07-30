package me.prouge.tryjump;

import me.prouge.tryjump.inject.InjectionModule;
import org.bukkit.plugin.java.JavaPlugin;

public class TryJump extends JavaPlugin {

    @Override
    public void onEnable(){
        new InjectionModule(this);
    }



}
