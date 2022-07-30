package me.prouge.tryjump.util;

import me.prouge.tryjump.TryJump;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;

public class MessageHandler {

    private HashMap<String, FileConfiguration> languageHashMap = new HashMap<>();

    @Inject
    private TryJump plugin;

    public MessageHandler() throws Exception {
        File directoryPath = new File(plugin.getDataFolder().getPath() + "/languages");
        String[] languageFiles = directoryPath.list();
        if(languageFiles == null){
            throw new Exception("NO LANGUAGE FILES");
        }

        for (String language : languageFiles) {
            File languageFile = new File(plugin.getDataFolder().getPath() + "/language/" + language);

            String languageName = language.substring(0, language.lastIndexOf('.'));

            this.languageHashMap.put(languageName, YamlConfiguration.loadConfiguration(languageFile));
        }


    }

    public String getMessage(String language, String path){
        String message = this.languageHashMap.get(language).get(path).toString();

        return message.replace("&", "§");
    }

}
