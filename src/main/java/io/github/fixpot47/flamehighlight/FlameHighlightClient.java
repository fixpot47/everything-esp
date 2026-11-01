package io.github.fixpot47.flamehighlight;

import net.fabricmc.api.ClientModInitializer;

public final class FlameHighlightClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FlameConfig.load();
    }
}
