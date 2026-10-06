package com.bendemenstudios.steampunksync.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SteampunkSyncConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<String> PACK_ID;
    public static final ModConfigSpec.ConfigValue<String> VERSION_URL;
    public static final ModConfigSpec.ConfigValue<String> UPDATE_DIR;
    public static final ModConfigSpec.BooleanValue ENABLE_AUTO_UPDATE;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("steampunksync");
        PACK_ID = b.define("packId", "hvmc");
        VERSION_URL = b.define("versionUrl", "https://raw.githubusercontent.com/Bendemen-Studios/SteampunkSync-Packs/main/hvmc/version.json");
        UPDATE_DIR = b.define("updateDirectory", "steampunksync-packs/hvmc");
        ENABLE_AUTO_UPDATE = b.define("autoUpdate", true);
        b.pop();
        SPEC = b.build();
    }

    private SteampunkSyncConfig() {}
}
