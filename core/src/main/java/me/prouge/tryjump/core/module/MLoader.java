package me.prouge.tryjump.core.module;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Charsets;
import com.google.inject.Singleton;
import lombok.Getter;
import me.prouge.tryjump.core.utils.PatternDeserializer;
import org.bukkit.block.banner.Pattern;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class MLoader {


    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule simpleModule = new SimpleModule();
    @Getter
    private Map<MDifficulty, List<Module>> modules = new HashMap<>();

    public MLoader() {
        simpleModule.addDeserializer(Pattern.class, new PatternDeserializer());
        objectMapper.registerModule(simpleModule);
        this.loadModules();
    }

    @SuppressWarnings("UnstableApiUsage")
    private Module loadModule(String name, String difficulty) throws IOException {
        File module = new File("plugins" +
                File.separator +
                "TryJump-Modules" +
                File.separator +
                difficulty + File.separator +
                name);
        String content = com.google.common.io.Files.asCharSource(module, Charsets.UTF_8).read();
        String[] contentSplit = content.split("000000000000000000");
        String[] splitInformation = contentSplit[0].split(";");


        return new Module(splitInformation[1],
                splitInformation[0],
                Enum.valueOf(MDifficulty.class, splitInformation[2].toUpperCase()),
                objectMapper.readValue(contentSplit[1], new TypeReference<List<MBlock>>() {
                }));
    }


    private Map<MDifficulty, List<Module>> getAllModules() {
        final String MODULES_PATH = "plugins/TryJump-Modules/";

        File[] difficultyDirs = {
                new File(MODULES_PATH + MDifficulty.EASY),
                new File(MODULES_PATH + MDifficulty.MEDIUM),
                new File(MODULES_PATH + MDifficulty.HARD),
                new File(MODULES_PATH + MDifficulty.EXTREME)
        };

        Map<MDifficulty, List<Module>> modules = new HashMap<>();

        Arrays.stream(difficultyDirs)
                .forEach(difficultyDir -> {
                    List<Module> moduleList = Arrays.stream(Objects.requireNonNull(difficultyDir.list()))
                            .map(name -> {
                                try {
                                    return loadModule(name, difficultyDir.getName());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());
                    modules.put(MDifficulty.valueOf(difficultyDir.getName().toUpperCase()), moduleList);
                });
        return modules;
    }


    private void loadModules() {
        Map<MDifficulty, List<Module>> allModules = getAllModules();
        Map<MDifficulty, List<Module>> modules = new HashMap<>();

        allModules.forEach((difficulty, moduleList) -> Collections.shuffle(moduleList));

        modules.put(MDifficulty.EASY, allModules.get(MDifficulty.EASY).subList(0, 3));
        modules.put(MDifficulty.MEDIUM, allModules.get(MDifficulty.MEDIUM).subList(0, 3));
        modules.put(MDifficulty.HARD, allModules.get(MDifficulty.HARD).subList(0, 3));
        modules.put(MDifficulty.EXTREME, Collections.singletonList(allModules.get(MDifficulty.EXTREME).get(0)));

        this.modules = modules;
    }


}
