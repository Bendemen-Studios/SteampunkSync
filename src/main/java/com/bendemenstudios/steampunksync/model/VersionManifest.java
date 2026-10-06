package com.bendemenstudios.steampunksync.model;
import java.util.List;
public record VersionManifest(String version,String modpack,String minecraft,String loader,String loaderVersion,String download,String sha256,long size,List<String> releaseNotes){
 public VersionManifest{releaseNotes=releaseNotes==null?List.of():List.copyOf(releaseNotes);}
}
