package io.github.fixpot47.flamehighlight;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FlameConfig {
    public static volatile boolean enabled = true;
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("flame-highlight.json");

    private FlameConfig() {}

    public static void load() {
        try {
            if (Files.isRegularFile(FILE)) {
                JsonObject json = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8)).getAsJsonObject();
                if (json.has("enabled")) {
                    enabled = json.get("enabled").getAsBoolean();
                }
            }
        } catch (Exception e) {
            System.err.println("[Flame Highlight] Cannot read config: " + e);
        }
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        save();
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, "{\n  \"enabled\": " + enabled + "\n}\n", StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[Flame Highlight] Cannot save config: " + e);
        }
    }
}
