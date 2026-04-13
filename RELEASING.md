# Releasing to Maven Central

Releases go to **Maven Central** through the [Sonatype Central Portal](https://central.sonatype.com/). OSSRH was shut down in 2025, so this project now uploads with Gradle's built-in `maven-publish` plugin to Sonatype's **OSSRH Staging API compatibility service** and then transfers that staging repository into the Central Portal. The GitHub Action `.github/workflows/publish.yml` runs on tags matching `v*` (for example `v0.1.0`) after `./gradlew :lib:test` succeeds, and it requests **automatic publish** once Sonatype validation passes.

The **`io.github.bluedskim`** namespace is already verified in Central Portal, so the remaining preparation is just credentials, signing, and the transfer step into the Portal.

## GitHub repository secrets

| Secret | Purpose |
|--------|---------|
| `CENTRAL_PORTAL_USERNAME` | Sonatype Central Portal user token username |
| `CENTRAL_PORTAL_PASSWORD` | Sonatype Central Portal user token password |
| `SIGNING_KEY` | ASCII-armored OpenPGP **private** key (full block including `BEGIN` / `END` lines) |
| `SIGNING_PASSWORD` | Passphrase for that key |

`OSSRH_USERNAME` / `OSSRH_PASSWORD` are still accepted as a temporary fallback in the Gradle build and workflow, but new configuration should use the `CENTRAL_PORTAL_*` names above.

## Local commands

Dry run (installs into `~/.m2/repository` only):

```bash
./gradlew :lib:publishToMavenLocal
```

Upload release artifacts to the Sonatype staging compatibility service:

```bash
export CENTRAL_PORTAL_USERNAME=...
export CENTRAL_PORTAL_PASSWORD=...
export SIGNING_KEY="$(cat path/to-private-key.asc)"
export SIGNING_PASSWORD=...
./gradlew :lib:publish
```

Transfer the uploaded staging repository into the Central Portal and request automatic release:

```bash
./scripts/upload-central-deployment.sh io.github.bluedskim
```

That creates a deployment in [Sonatype Central Portal publishing](https://central.sonatype.com/publishing/deployments). By default the script now uses `automatic`, so Sonatype will publish to Maven Central automatically after validation succeeds. If you need a manual hold for a specific release, override it with `CENTRAL_PUBLISHING_TYPE=user_managed`.

After a successful deployment is published to Central, bump `version` in the root `build.gradle` for the next release.
