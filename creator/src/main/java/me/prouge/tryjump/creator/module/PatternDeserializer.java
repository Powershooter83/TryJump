package me.prouge.tryjump.creator.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

import java.io.IOException;

public class PatternDeserializer extends StdDeserializer<Pattern> {

    public PatternDeserializer() {
        this(null);
    }

    public PatternDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Pattern deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);

        DyeColor color = DyeColor.valueOf(node.get("color").asText());
        PatternType pattern = PatternType.valueOf(node.get("pattern").asText());

        return new Pattern(color, pattern);
    }
}