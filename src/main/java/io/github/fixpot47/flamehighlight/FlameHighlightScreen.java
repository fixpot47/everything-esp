package io.github.fixpot47.flamehighlight;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class FlameHighlightScreen extends Screen {
    private final Screen parent;

    public FlameHighlightScreen(Screen parent) {
        super(Component.translatable("flamehighlight.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = width / 2 - 105;
        int y = height / 2 - 10;
        addRenderableWidget(Button.builder(
            Component.translatable(FlameConfig.enabled ? "flamehighlight.enabled" : "flamehighlight.disabled"),
            b -> {
                FlameConfig.setEnabled(!FlameConfig.enabled);
                b.setMessage(Component.translatable(FlameConfig.enabled
                    ? "flamehighlight.enabled" : "flamehighlight.disabled"));
            }).bounds(x, y, 210, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("flamehighlight.done"),
            b -> onClose()).bounds(x, y + 31, 210, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.text(font, this.title, width / 2 - font.width(this.title) / 2,
            height / 2 - 42, 0xFFFFFFFF, true);
        Component detail = Component.translatable("flamehighlight.explanation");
        graphics.text(font, detail, width / 2 - font.width(detail) / 2,
            height / 2 + 64, 0xFFBBBBBB, false);
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }
}
