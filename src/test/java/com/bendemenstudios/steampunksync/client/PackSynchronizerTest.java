package com.bendemenstudios.steampunksync.client;

import com.bendemenstudios.steampunksync.model.VersionManifest;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.List;
import java.util.zip.*;
import static org.junit.jupiter.api.Assertions.*;

class PackSynchronizerTest {
    @Test void rejectsNonHttpsManifest() {
        VersionManifest m = new VersionManifest("v1.0","HVMC","1.21.1","NeoForge","21.1.250","http://example.invalid/pack.zip","a".repeat(64),10,List.of());
        assertThrows(IllegalArgumentException.class, () -> PackSynchronizer.validateManifest(m));
    }

    @Test void blocksZipPathTraversal() throws Exception {
        Path dir=Files.createTempDirectory("steampunksync-test");
        Path zip=dir.resolve("bad.zip"), root=dir.resolve("stage");
        try(ZipOutputStream out=new ZipOutputStream(Files.newOutputStream(zip))){
            out.putNextEntry(new ZipEntry("../outside.txt")); out.write("blocked".getBytes()); out.closeEntry();
        }
        assertThrows(IOException.class, () -> PackSynchronizer.extractSafe(zip,root,(d,t,s,b)->{}));
        assertFalse(Files.exists(dir.getParent().resolve("outside.txt")));
        PackSynchronizer.deleteTree(dir);
    }

    @Test void rejectsUnsupportedTopLevelDirectory() throws Exception {
        Path dir=Files.createTempDirectory("steampunksync-test"), stage=dir.resolve("stage"), game=dir.resolve("game"), backup=dir.resolve("backup");
        Files.createDirectories(stage.resolve("world"));
        assertThrows(IOException.class, () -> PackSynchronizer.applyPack(stage,game,backup));
        PackSynchronizer.deleteTree(dir);
    }
}
