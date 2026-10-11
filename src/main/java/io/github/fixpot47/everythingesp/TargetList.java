package io.github.fixpot47.everythingesp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public final class TargetList {
    public static final Set<String> TARGETS = new LinkedHashSet<>();
    public static boolean enabled = true;
    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("everythingesp.properties"); }
    private TargetList() {}

    public static boolean add(String text) {
        String name = text.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        if (name.isEmpty() || Identifier.tryParse(name) == null) return false;
        if (!name.contains(":")) name = "minecraft:" + name;
        boolean added = TARGETS.add(name);
        if (added) save();
        return added;
    }

    public static void load() {
        try {
            if (!Files.exists(file())) return;
            Properties properties = new Properties();
            try (var in = Files.newInputStream(file())) { properties.load(in); }
            enabled = Boolean.parseBoolean(properties.getProperty("enabled", "true"));
            TARGETS.clear();
            for (String value : properties.getProperty("targets", "").split(",")) {
                String id = value.trim();
                if (!id.isEmpty() && Identifier.tryParse(id) != null) TARGETS.add(id);
            }
        } catch (IOException exception) {
            System.err.println("[EverythingESP] Couldn't load settings: " + exception.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(file().getParent());
            Properties properties = new Properties();
            properties.setProperty("enabled", String.valueOf(enabled));
            properties.setProperty("targets", String.join(",", TARGETS));
            try (var out = Files.newOutputStream(file())) { properties.store(out, "EverythingESP settings"); }
        } catch (IOException exception) {
            System.err.println("[EverythingESP] Couldn't save settings: " + exception.getMessage());
        }
    }
}
