# Releasing to Maven Central

Releases go to **Maven Central** via Sonatype OSSRH (`s01.oss.sonatype.org`). The GitHub Action `.github/workflows/publish.yml` runs on tags matching `v*` (for example `v0.1.0`) after `./gradlew :lib:test` succeeds.

Before the first upload, verify the **`io.github.bluedskim`** groupId on [Sonatype Central Portal](https://central.sonatype.com/) (GitHub namespace), if not already done.

## GitHub repository secrets

| Secret | Purpose |
|--------|---------|
| `OSSRH_USERNAME` | Sonatype user token username |
| `OSSRH_PASSWORD` | Sonatype user token password |
| `SIGNING_KEY` | ASCII-armored OpenPGP **private** key (full block including `BEGIN` / `END` lines) |
| `SIGNING_PASSWORD` | Passphrase for that key |

## Local commands

Dry run (installs into `~/.m2/repository` only):

```bash
./gradlew :lib:publishToMavenLocal
```

Upload to the OSSRH staging repository:

```bash
export OSSRH_USERNAME=...
export OSSRH_PASSWORD=...
export SIGNING_KEY="$(cat path/to-private-key.asc)"
export SIGNING_PASSWORD=...
./gradlew :lib:publish
```

After a successful deployment, complete the release in the [Sonatype Central Portal](https://central.sonatype.com/) (or the legacy staging UI if your namespace still uses it), then bump `version` in the root `build.gradle` for the next release.
