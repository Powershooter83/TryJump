package me.prouge.tryjump.core.util;

import me.prouge.tryjump.core.TryJump;
import me.prouge.tryjump.core.managers.TryPlayer;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;

public class ChatWriter {

    @Inject
    private TryJump plugin;
    private HashMap<String, FileConfiguration> languages = new HashMap<>();

    public void print(TryPlayer p, Message msg, String[][] values) {
        if (this.languages.isEmpty()) {
            loadLanguages();
        }

        p.toPlayer().sendMessage(getFinishedMessage(p, msg,values));
    }
    public void sendActionbar(TryPlayer p, Message msg, String[][] values){
        if (this.languages.isEmpty()) {
            loadLanguages();
        }

        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(getFinishedMessage(p,msg,values)), (byte) 2);
        ((CraftPlayer) p.toPlayer()).getHandle().playerConnection.sendPacket(packet);
    }

    private String getFinishedMessage(TryPlayer p, Message msg, String[][] values){
        String message = (String) this.languages.get(p.language()).get(msg.toString());
        if (values != null) {
            for (String[] value : values) {
                message = message.replace("{" + value[0] + "}", value[1]);
            }
        }
        return message.replace("&", "\u00a7");

    }

    private void loadLanguages() {
        File directoryPath = new File(plugin.getDataFolder().getPath() + "/languages");
        String[] languageFiles = directoryPath.list();
        if (languageFiles == null) {
            return;
        }
        for (String language : languageFiles) {
            File languageFile = new File(plugin.getDataFolder().getPath() + "/languages/" + language);
            String languageName = language.substring(0, language.lastIndexOf('.'));
            this.languages.put(languageName, YamlConfiguration.loadConfiguration(languageFile));
        }
    }


}
