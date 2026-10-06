package com.bendemenstudios.steampunksync;

import com.bendemenstudios.steampunksync.config.SteampunkSyncConfig;
import com.bendemenstudios.steampunksync.client.UpdateCoordinator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(value = SteampunkSync.MOD_ID, dist = Dist.CLIENT)
public final class SteampunkSync {
    public static final String MOD_ID = "steampunksync";

    public SteampunkSync(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, SteampunkSyncConfig.SPEC);
        UpdateCoordinator.register();
    }
}
