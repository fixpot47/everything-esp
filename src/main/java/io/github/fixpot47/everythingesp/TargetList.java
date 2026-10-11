package io.github.fixpot47.everythingesp;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.resources.Identifier;

public final class TargetList {
    public static final Set<String> TARGETS = new LinkedHashSet<>();
    public static boolean enabled = true;
    private TargetList() {}

    public static boolean add(String text) {
        String name = text.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        if (name.isEmpty() || Identifier.tryParse(name) == null) return false;
        if (!name.contains(":")) name = "minecraft:" + name;
        return TARGETS.add(name);
    }
}
