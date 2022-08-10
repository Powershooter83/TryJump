package me.prouge.tryjump.core.module;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MLoader {

    @SuppressWarnings("UnstableApiUsage")
    private Module loadModule(String name, String difficulty) throws IOException {
        File module = new File("plugins" +
                File.separator +
                "TryJump-Modules" +
                File.separator +
                difficulty + File.separator +
                name);
        String content = com.google.common.io.Files.asCharSource(module, Charsets.UTF_8).read();
        String[] contentSplit = content.split("01001023010000140141024023415433543");
        String[] splitInformation = contentSplit[0].split(";");
        return new Module(splitInformation[1],
                splitInformation[0],
                Enum.valueOf(MDifficulty.class, splitInformation[2].toUpperCase()),
                new ObjectMapper().readValue(contentSplit[1], new TypeReference<List<MBlock>>() {
                }));
    }


    public HashMap<MDifficulty, ArrayList<Module>> getAllModules() {
        HashMap<MDifficulty, ArrayList<Module>> modules = new HashMap<>();
        File easy = new File("plugins" + File.separator + "TryJump-Modules" + File.separator + "easy");
        File medium = new File("plugins" + File.separator + "TryJump-Modules" + File.separator + "medium");
        File hard = new File("plugins" + File.separator + "TryJump-Modules" + File.separator + "hard");
        File extreme = new File("plugins" + File.separator + "TryJump-Modules" + File.separator + "extreme");

        for (String name : Objects.requireNonNull(easy.list())) {
            try {
                modules.computeIfAbsent(MDifficulty.EASY, k -> new ArrayList<>());
                modules.get(MDifficulty.EASY).add(loadModule(name, "easy"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (String name : Objects.requireNonNull(medium.list())) {
            try {
                modules.computeIfAbsent(MDifficulty.MEDIUM, k -> new ArrayList<>());
                modules.get(MDifficulty.MEDIUM).add(loadModule(name, "medium"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (String name : Objects.requireNonNull(hard.list())) {
            try {
                modules.computeIfAbsent(MDifficulty.HARD, k -> new ArrayList<>());
                modules.get(MDifficulty.HARD).add(loadModule(name, "hard"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (String name : Objects.requireNonNull(extreme.list())) {
            try {
                modules.computeIfAbsent(MDifficulty.EXTREME, k -> new ArrayList<>());
                modules.get(MDifficulty.EXTREME).add(loadModule(name, "extreme"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return modules;

    }


}
