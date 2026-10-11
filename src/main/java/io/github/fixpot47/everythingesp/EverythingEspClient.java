package io.github.fixpot47.everythingesp;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

public final class EverythingEspClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        ResourceLocation.fromNamespaceAndPath("everythingesp", "controls"));
    private static final KeyMapping OPEN_MENU = KeyMappingHelper.registerKeyMapping(
        new KeyMapping("key.everythingesp.open", InputConstants.Type.KEYSYM,
            InputConstants.KEY_COMMA, CATEGORY));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MENU.consumeClick()) {
                if (client.player != null && client.gui.getScreen() == null) {
                    client.gui.setScreen(new EverythingEspScreen());
                }
            }
        });
    }
}
