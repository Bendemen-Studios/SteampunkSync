package com.bendemenstudios.steampunksync.client;

import com.bendemenstudios.steampunksync.config.SteampunkSyncConfig;
import com.bendemenstudios.steampunksync.model.VersionManifest;
import com.google.gson.Gson;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class UpdateCoordinator {
    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private static boolean started;

    private UpdateCoordinator() {}

    public static void register() {
        NeoForge.EVENT_BUS.register(UpdateCoordinator.class);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (started || !SteampunkSyncConfig.ENABLE_AUTO_UPDATE.get()) return;
        started = true;
        Thread.startVirtualThread(UpdateCoordinator::checkNow);
    }

    private static void checkNow() {
        try {
            String body = HTTP.send(
                    HttpRequest.newBuilder(URI.create(SteampunkSyncConfig.VERSION_URL.get()))
                            .timeout(Duration.ofSeconds(15))
                            .header("Accept", "application/json")
                            .GET().build(),
                    HttpResponse.BodyHandlers.ofString()
            ).body();
            VersionManifest manifest = GSON.fromJson(body, VersionManifest.class);
            if (manifest == null || manifest.version() == null || manifest.download() == null) return;
            ClientUpdateState.setAvailable(manifest);
        } catch (Exception ignored) {
            // Offline/update endpoint errors must never prevent Minecraft from launching.
        }
    }
}
