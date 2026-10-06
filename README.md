# SteampunkSync

SteampunkSync is a 100% original NeoForge 1.21.1 client-side mod for synchronizing Minecraft modpacks.

## Target
- Minecraft 1.21.1
- NeoForge 21.1.250+
- Java 21

## Distribution model
GitHub hosts a lightweight `version.json` manifest. The actual modpack ZIP is hosted on MinIO/S3-compatible storage.

Example manifest:

```json
{
  "version": "v1.2",
  "modpack": "HVMC",
  "minecraft": "1.21.1",
  "loader": "NeoForge",
  "loaderVersion": "21.1.250",
  "download": "https://minio.example.com/steampunk/hvmc/pack.zip",
  "sha256": "…",
  "size": 583294123,
  "releaseNotes": [
    "Updated Create",
    "Added new resourcepacks"
  ]
}
```

No GitHub token or MinIO credentials are required by the client when both endpoints are public.

## Status
Development build. Not a release.
