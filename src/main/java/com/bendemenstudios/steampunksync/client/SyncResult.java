package com.bendemenstudios.steampunksync.client;
public record SyncResult(boolean success, String message) { public static SyncResult ok(String m){return new SyncResult(true,m);} public static SyncResult fail(String m){return new SyncResult(false,m);} }
