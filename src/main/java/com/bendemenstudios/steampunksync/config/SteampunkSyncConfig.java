package com.bendemenstudios.steampunksync.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SteampunkSyncConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<String> PACK_ID;
    public static final ModConfigSpec.ConfigValue<String> VERSION_URL;
    public static final ModConfigSpec.BooleanValue ENABLE_AUTO_UPDATE;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("steampunksync");
        PACK_ID = b.comment("The SteampunkSync pack ID supplied by the modpack.")
                .define("packId", "hvmc");
        VERSION_URL = b.comment("The GitHub raw version.json URL supplied by the modpack.")
                .define("versionUrl", "https://raw.githubusercontent.com/Bendemen-Studios/SteampunkSync-Packs/main/hvmc/version.json");
        ENABLE_AUTO_UPDATE = b.comment("Check for modpack updates when Minecraft starts.")
                .define("autoUpdate", true);
        b.pop();
        SPEC = b.build();
    }

    private SteampunkSyncConfig() {}
}
