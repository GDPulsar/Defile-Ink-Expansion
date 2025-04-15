package com.pulsar.inkexpansion.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class InkExpansionClient implements ClientModInitializer {
    public static boolean disableInkRenderer = false;

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("figura")) {
            disableInkRenderer = true;
        }
    }
}
