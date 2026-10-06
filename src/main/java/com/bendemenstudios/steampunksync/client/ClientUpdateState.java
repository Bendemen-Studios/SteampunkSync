package com.bendemenstudios.steampunksync.client;

import com.bendemenstudios.steampunksync.model.VersionManifest;

public final class ClientUpdateState {
    private static volatile VersionManifest available;
    private static volatile String installedVersion;

    private ClientUpdateState() {}

    public static void setAvailable(VersionManifest manifest) {
        available = manifest;
    }

    public static VersionManifest available() {
        return available;
    }

    public static String installedVersion() {
        return installedVersion;
    }

    public static void setInstalledVersion(String version) {
        installedVersion = version;
    }
}
