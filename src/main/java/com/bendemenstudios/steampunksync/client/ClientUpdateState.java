package com.bendemenstudios.steampunksync.client;

import com.bendemenstudios.steampunksync.model.VersionManifest;

public final class ClientUpdateState {
    private static volatile VersionManifest available;

    private ClientUpdateState() {}

    public static void setAvailable(VersionManifest manifest) { available = manifest; }
    public static VersionManifest available() { return available; }
}
