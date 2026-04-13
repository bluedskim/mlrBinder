# Releasing to Maven Central

Releases go to **Maven Central** through the [Sonatype Central Portal](https://central.sonatype.com/). OSSRH was shut down in 2025, so this project now uploads with Gradle's built-in `maven-publish` plugin to Sonatype's **OSSRH Staging API compatibility service** and then transfers that staging repository into the Central Portal. The GitHub Action `.github/workflows/publish.yml` runs on tags matching `v*` (for example `v0.1.0`) after `./gradlew :lib:test` succeeds, and it leaves the deployment in **user-managed** mode so validation can be reviewed in the Portal before publishing.

The **`io.github.bluedskim`** namespace is already verified in Central Portal, so the remaining preparation is just credentials, signing, and the transfer step into the Portal.

## GitHub repository secrets

| Secret | Purpose |
|--------|---------|
| `CENTRAL_PORTAL_USERNAME` | Sonatype Central Portal user token username |
| `CENTRAL_PORTAL_PASSWORD` | Sonatype Central Portal user token password |
| `SIGNING_KEY` | ASCII-armored OpenPGP **private** key (full block including `BEGIN` / `END` lines) |
| `SIGNING_PASSWORD` | Passphrase for that key |

The **public** key that matches `SIGNING_KEY` must be published to a keyserver that [Sonatype Maven Central](https://central.sonatype.org/) can query (for example [keys.openpgp.org](https://keys.openpgp.org/) or Ubuntu’s keyserver). If it is missing, Central reports *Invalid signature* / *Could not find a public key by the key fingerprint* for the `.asc` files. After generating or rotating a key, export the public key and upload it before tagging a release. The publish workflow runs `./scripts/verify-signing-public-key.sh` so a missing public key fails **before** Gradle uploads artifacts.

`OSSRH_USERNAME` / `OSSRH_PASSWORD` are still accepted as a temporary fallback in the Gradle build and workflow, but new configuration should use the `CENTRAL_PORTAL_*` names above.

## OpenPGP signing and keyservers (detailed)

### Why Central complains about signatures

Gradle signs each published file with the **private** key from `SIGNING_KEY`. Maven Central’s validator downloads the matching **public** key from keyservers it trusts. If that public key is missing, stale, or on an unsupported pool, validation fails even when the `.asc` files are cryptographically correct. The error text usually mentions the **primary key fingerprint** (40 hex characters).

### Confirm which key GitHub Actions will use

1. The fingerprint in Central’s error message must match the key in `SIGNING_KEY`.
2. On a machine where you imported the same private key material as in the secret:

   ```bash
   gpg --list-secret-keys --with-subkey-fingerprints
   ```

   Note the **primary** fingerprint (often shown on a `sec` line with `ed25519` or `rsa4096`, and again as `fpr` in `--with-colons` output).

3. Optional: compare with what CI would see locally (same format as the secret):

   ```bash
   export SIGNING_KEY="$(cat path/to-private-key.asc)"
   ./scripts/verify-signing-public-key.sh
   ```

   Exit code `0` means the public key was found on at least one of: `hkps://keys.openpgp.org`, `hkps://keyserver.ubuntu.com`. These mirror what the workflow checks before `./gradlew :lib:publish`.

### Publish the public key (pick one or more)

Export only the **public** key (never commit or paste the private key into issues or chats):

```bash
gpg --armor --export YOUR_KEY_ID_OR_EMAIL > public-release.asc
```

**Option A — keys.openpgp.org (recommended for new keys)**

1. Upload (replace `FINGERPRINT` with your 40-character primary fingerprint, no spaces):

   ```bash
   gpg --armor --export FINGERPRINT | curl -fsS -T - https://keys.openpgp.org
   ```

2. If the key has an email address, the site may require **email verification** before the key is visible to others. Complete the flow at [keys.openpgp.org/manage](https://keys.openpgp.org/manage). Until verification finishes, `gpg --recv-keys` from another host can still fail.

3. Confirm from a **clean** temporary directory (optional but good practice):

   ```bash
   GNUPGHOME="$(mktemp -d)" gpg --batch --keyserver hkps://keys.openpgp.org --recv-keys FINGERPRINT
   ```

**Option B — Ubuntu keyserver**

```bash
gpg --keyserver hkps://keyserver.ubuntu.com --send-keys FINGERPRINT
```

Propagation can take from seconds to many minutes. Re-run `./scripts/verify-signing-public-key.sh` until it succeeds, then re-run the GitHub Action or publish locally.

### After a failed deployment (for example `0.2`)

1. **Do not** assume the problem is Gradle: fix keyserver visibility first.
2. Publish or fix the public key as above; wait until `./scripts/verify-signing-public-key.sh` passes with your real `SIGNING_KEY`.
3. In [Central Portal → Publishing → Deployments](https://central.sonatype.com/publishing/deployments), **drop** or ignore the failed deployment if the UI allows it.
4. Re-upload artifacts: either push a tag again to re-trigger [Publish to Maven Central](https://github.com/bluedskim/mlrBinder/actions/workflows/publish.yml), or run locally:

   ```bash
   export CENTRAL_PORTAL_USERNAME=...
   export CENTRAL_PORTAL_PASSWORD=...
   export SIGNING_KEY="$(cat path/to-private-key.asc)"
   export SIGNING_PASSWORD=...
   ./scripts/verify-signing-public-key.sh
   ./gradlew :lib:publish --no-daemon
   ./scripts/upload-central-deployment.sh io.github.bluedskim
   ```

5. If Maven Central already reserved the same coordinates for a broken release, you may need Sonatype support or a patch version—coordinate with what the Portal shows for that namespace.

### Rotating the signing key

1. Generate a new key pair; export the new **private** key ASCII-armored block into GitHub secret `SIGNING_KEY` and set `SIGNING_PASSWORD`.
2. Publish the new **public** key to a supported keyserver and confirm `./scripts/verify-signing-public-key.sh` passes.
3. Old releases remain signed with the old key; that is normal. Only ensure the key used for **each** release is on a keyserver when that release is published.

### Troubleshooting checklist

| Symptom | Things to check |
|--------|-------------------|
| `Could not find a public key by the key fingerprint` | Public key not published, wrong key published, or keys.openpgp.org email not verified. |
| Workflow fails at “Verify signing public key…” | Same as above; fix keyserver before retrying. |
| Local `verify-signing-public-key.sh` OK but Central still fails | Rare propagation delay; wait and retry. Confirm Central’s error fingerprint matches your key. |
| Multiple secret keys in `SIGNING_KEY` | Avoid bundling several private keys in one armored file; export only the signing key you intend. |

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

Transfer the uploaded staging repository into the Central Portal for validation review:

```bash
./scripts/upload-central-deployment.sh io.github.bluedskim
```

That creates a deployment in [Sonatype Central Portal publishing](https://central.sonatype.com/publishing/deployments). By default the script now uses `user_managed`, so review the validation results there and click publish from the Portal UI when ready. If you ever want to auto-release a specific deployment instead, override it with `CENTRAL_PUBLISHING_TYPE=automatic`.

After a successful deployment is published to Central, bump `version` in the root `build.gradle` for the next release.
