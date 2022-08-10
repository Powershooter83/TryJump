package me.prouge.tryjump.core.util;

import me.prouge.tryjump.core.TryJump;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Language {

    @Inject
    private TryJump plugin;

    public void setup() {
        loadLanguageFileFromResource("de.yml");
    }

    private void loadLanguageFileFromResource(String language) {
        File defaultLanguageFile = new File(plugin.getDataFolder(), "languages/" + language);
        File languageDirectory = new File(plugin.getDataFolder(), "languages/");
        if (!languageDirectory.isDirectory()) {
            languageDirectory.mkdir();
            try {
                InputStream stream = plugin.getResource("languages/" + language);
                FileUtils.copyInputStreamToFile(stream, defaultLanguageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
