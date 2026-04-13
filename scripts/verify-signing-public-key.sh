#!/usr/bin/env bash
# Ensure the OpenPGP public key that matches SIGNING_KEY is discoverable from a
# keyserver that Sonatype Maven Central queries. Central rejects deployments when
# .asc signatures reference a key that is not published to a supported server.
set -euo pipefail

if [[ -z "${SIGNING_KEY:-}" ]]; then
	echo "SIGNING_KEY must be set (ASCII-armored private key used for Gradle signing)." >&2
	exit 1
fi

GNUPGHOME="$(mktemp -d)"
export GNUPGHOME
chmod 700 "${GNUPGHOME}"
keyring="${GNUPGHOME}/signing-key.asc"
cleanup() {
	rm -rf "${GNUPGHOME}"
}
trap cleanup EXIT

printf '%s\n' "${SIGNING_KEY}" >"${keyring}"
gpg --batch --import --quiet "${keyring}"

fpr="$(gpg --batch --list-secret-keys --with-colons 2>/dev/null | awk -F: '$1 == "fpr" {print $10; exit}')"
if [[ -z "${fpr}" ]]; then
	echo "Could not read a key fingerprint from SIGNING_KEY after import." >&2
	exit 1
fi

echo "Checking public key ${fpr} on keyservers used by Maven Central validation..."

keyservers=(
	'hkps://keys.openpgp.org'
	'hkps://keyserver.ubuntu.com'
)

for ks in "${keyservers[@]}"; do
	if gpg --batch --keyserver "${ks}" --recv-keys "${fpr}" &>/dev/null; then
		echo "OK: public key is available from ${ks}"
		exit 0
	fi
done

echo "" >&2
echo "ERROR: Public key ${fpr} was not found on keys.openpgp.org or keyserver.ubuntu.com." >&2
echo "Sonatype Central validates signatures against public keys on supported keyservers." >&2
echo "" >&2
echo "Publish the matching public key, for example:" >&2
echo "  gpg --armor --export ${fpr} | curl -fsS -T - https://keys.openpgp.org" >&2
echo "  gpg --keyserver hkps://keyserver.ubuntu.com --send-keys ${fpr}" >&2
echo "" >&2
echo "If you use keys.openpgp.org, confirm the upload at https://keys.openpgp.org/manage" >&2
exit 1
