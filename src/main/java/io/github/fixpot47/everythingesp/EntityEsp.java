package io.github.fixpot47.everythingesp;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * Client-side entity highlighting using the game's vanilla glowing outline.
 * Server state can override glowing flags; previously glowing entities are preserved.
 */
public final class EntityEsp {
    private static final Set<Integer> OWNED = new HashSet<>();
    private EntityEsp() {}

    public static void tick(Minecraft client) {
        if (client.level == null || client.player == null) {
            OWNED.clear();
            return;
        }
        Set<Integer> active = new HashSet<>();
        for (Entity entity : client.level.entitiesForRendering()) {
            String type = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
            String item = entity instanceof ItemEntity drop
                ? BuiltInRegistries.ITEM.getKey(drop.getItem().getItem()).toString() : "";
            boolean match = TargetList.enabled && (TargetList.TARGETS.contains(type)
                || (!item.isEmpty() && TargetList.TARGETS.contains(item)));
            if (match) {
                if (!entity.isCurrentlyGlowing() && !OWNED.contains(entity.getId())) {
                    entity.setGlowingTag(true);
                    OWNED.add(entity.getId());
                }
                active.add(entity.getId());
            } else if (OWNED.remove(entity.getId())) {
                entity.setGlowingTag(false);
            }
        }
        OWNED.removeIf(id -> !active.contains(id));
    }
}
