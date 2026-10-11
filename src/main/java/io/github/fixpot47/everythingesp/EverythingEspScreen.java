package io.github.fixpot47.everythingesp;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class EverythingEspScreen extends Screen {
    private EditBox search;
    private String status = "Enter a registry ID, e.g. diamond_ore";
    public EverythingEspScreen() { super(Component.literal("EverythingESP")); }

    @Override
    protected void init() {
        int x = width / 2 - 110;
        int y = height / 2 - 60;
        search = new EditBox(font, x, y, 220, 20, Component.literal("Target ID"));
        search.setHint(Component.literal("minecraft:diamond_ore"));
        addRenderableWidget(search);
        addRenderableWidget(Button.builder(Component.literal("Add target"), b -> {
            if (TargetList.add(search.getValue())) {
                status = "Added: " + search.getValue();
                search.setValue("");
            } else status = "Invalid ID or already added";
        }).bounds(x, y + 28, 105, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Clear list"), b -> {
            TargetList.TARGETS.clear();
            status = "Target list cleared";
        }).bounds(x + 115, y + 28, 105, 20).build());
        addRenderableWidget(Button.builder(
            Component.literal(TargetList.enabled ? "ESP: ON" : "ESP: OFF"), b -> {
                TargetList.enabled = !TargetList.enabled;
                b.setMessage(Component.literal(TargetList.enabled ? "ESP: ON" : "ESP: OFF"));
            }).bounds(x, y + 55, 105, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
            .bounds(x + 115, y + 55, 105, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int x = width / 2 - 110;
        int y = height / 2 - 60;
        graphics.text(font, "EverythingESP 0.1.0-alpha", x, y - 32, 0xFFFFFFFF, true);
        graphics.text(font, status, x, y + 85, 0xFFAAAAAA, false);
        String targets = String.join(", ", TargetList.TARGETS);
        if (targets.length() > 42) targets = targets.substring(0, 39) + "...";
        graphics.text(font, "Targets: " + (targets.isEmpty() ? "(none)" : targets),
            x, y + 102, 0xFFFFFFFF, false);
        graphics.text(font, "ESP renderer not implemented yet", x, y + 118, 0xFFFFAA55, false);
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(null);
    }
}
