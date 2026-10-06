package com.bendemenstudios.steampunksync.client;

import com.bendemenstudios.steampunksync.model.VersionManifest;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.*;

public final class PackSynchronizer {
    private static final HttpClient HTTP = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(15)).build();
    private static final Set<String> ALLOWED = Set.of("mods","config","resourcepacks","shaderpacks","defaultconfigs","essential","fancymenu_data");
    private static final long MAX_ARCHIVE_BYTES = 2L * 1024 * 1024 * 1024;
    private static final long MAX_EXTRACTED_BYTES = 6L * 1024 * 1024 * 1024;
    private static final int MAX_ENTRIES = 100_000;
    private PackSynchronizer() {}

    public static SyncResult downloadAndInstall(VersionManifest m, Path gameDir, ProgressListener l) {
        Path zip=gameDir.resolve(".steampunksync-download.tmp"), stage=gameDir.resolve(".steampunksync-staging");
        try {
            validateManifest(m); Files.createDirectories(gameDir); Files.deleteIfExists(zip); deleteTree(stage); Files.createDirectories(stage);
            HttpResponse<InputStream> r=HTTP.send(HttpRequest.newBuilder(URI.create(m.download())).timeout(Duration.ofMinutes(20)).header("Accept","application/zip, application/octet-stream").GET().build(),HttpResponse.BodyHandlers.ofInputStream());
            if(r.statusCode()/100!=2)return SyncResult.fail("Download failed: HTTP "+r.statusCode());
            long total=m.size()>0?m.size():r.headers().firstValueAsLong("Content-Length").orElse(-1); if(total>MAX_ARCHIVE_BYTES)return SyncResult.fail("Modpack archive is too large.");
            MessageDigest d=MessageDigest.getInstance("SHA-256"); long done=0,started=System.nanoTime();
            try(InputStream in=r.body();OutputStream out=Files.newOutputStream(zip)){
                byte[] b=new byte[1024*1024]; for(int n;(n=in.read(b))!=-1;){done+=n;if(done>MAX_ARCHIVE_BYTES)throw new IOException("Modpack archive exceeds the maximum allowed size.");d.update(b,0,n);out.write(b,0,n);long ms=Math.max(1,(System.nanoTime()-started)/1_000_000);l.update(done,total,"Downloading modpack",done*1000d/ms);}
            }
            if(!HexFormat.of().formatHex(d.digest()).equalsIgnoreCase(m.sha256().trim()))return SyncResult.fail("SHA-256 verification failed.");
            l.update(done,total,"Verifying download",0); extractSafe(zip,stage,l); applyPack(stage,gameDir); Files.deleteIfExists(zip); deleteTree(stage);
            return SyncResult.ok("Installed "+m.version()+". Restart Minecraft to load the updated modpack.");
        } catch(Exception e){try{Files.deleteIfExists(zip);deleteTree(stage);}catch(Exception ignored){}return SyncResult.fail(e.getClass().getSimpleName()+": "+String.valueOf(e.getMessage()));}
    }

    private static void validateManifest(VersionManifest m){
        if(m==null||blank(m.version())||blank(m.modpack())||blank(m.minecraft())||blank(m.loader())||blank(m.loaderVersion())||blank(m.download())||blank(m.sha256()))throw new IllegalArgumentException("Manifest is incomplete.");
        URI u=URI.create(m.download());if(!"https".equalsIgnoreCase(u.getScheme()))throw new IllegalArgumentException("Download URL must use HTTPS.");
        if(!m.sha256().trim().matches("(?i)[0-9a-f]{64}"))throw new IllegalArgumentException("Invalid SHA-256 in manifest.");
        if(m.size()<0||m.size()>MAX_ARCHIVE_BYTES)throw new IllegalArgumentException("Invalid archive size.");
    }
    private static boolean blank(String s){return s==null||s.isBlank();}

    private static void extractSafe(Path zip,Path root,ProgressListener l)throws IOException{
        root=root.toAbsolutePath().normalize();long extracted=0;int entries=0;
        try(ZipInputStream z=new ZipInputStream(Files.newInputStream(zip))){
            for(ZipEntry e;(e=z.getNextEntry())!=null;){
                if(++entries>MAX_ENTRIES)throw new IOException("ZIP contains too many entries.");
                String n=e.getName().replace('\\','/');
                if(n.isBlank()||n.startsWith("/")||n.matches("^[A-Za-z]:.*")||n.contains("../")||n.contains("/.."))throw new IOException("Unsafe ZIP entry: "+n);
                Path t=root.resolve(n).normalize();if(!t.startsWith(root))throw new IOException("ZIP path traversal blocked.");
                if(e.isDirectory())Files.createDirectories(t);else{Files.createDirectories(t.getParent());try(OutputStream o=Files.newOutputStream(t)){byte[] b=new byte[1024*1024];for(int nread;(nread=z.read(b))!=-1;){extracted+=nread;if(extracted>MAX_EXTRACTED_BYTES)throw new IOException("Extracted modpack exceeds the maximum allowed size.");o.write(b,0,nread);}}l.update(extracted,MAX_EXTRACTED_BYTES,"Preparing modpack",0);}z.closeEntry();
            }
        }
    }
    private static void applyPack(Path stage,Path gameDir)throws IOException{
        try(Stream<Path> roots=Files.list(stage)){Iterator<Path> it=roots.iterator();while(it.hasNext()){Path root=it.next();String name=root.getFileName().toString();if(!ALLOWED.contains(name))throw new IOException("Pack contains unsupported top-level directory: "+name);Path target=gameDir.resolve(name).normalize();if(!target.getParent().equals(gameDir.toAbsolutePath().normalize()))throw new IOException("Invalid target path.");deleteTree(target);Files.move(root,target,StandardCopyOption.REPLACE_EXISTING);}}
    }
    private static void deleteTree(Path p)throws IOException{if(!Files.exists(p))return;try(Stream<Path>w=Files.walk(p)){w.sorted(Comparator.reverseOrder()).forEach(x->{try{Files.deleteIfExists(x);}catch(IOException e){throw new UncheckedIOException(e);}});}catch(UncheckedIOException e){throw e.getCause();}}
    @FunctionalInterface public interface ProgressListener{void update(long done,long total,String stage,double bytesPerSecond);}
}