#!/usr/bin/env bash
set -euo pipefail

requested_namespace="${1:-${CENTRAL_NAMESPACE:-}}"
publishing_type="${CENTRAL_PUBLISHING_TYPE:-automatic}"
staging_api_base_url="${CENTRAL_STAGING_API_BASE_URL:-https://ossrh-staging-api.central.sonatype.com}"

central_portal_username="${CENTRAL_PORTAL_USERNAME:-${SONATYPE_USERNAME:-${OSSRH_USERNAME:-}}}"
central_portal_password="${CENTRAL_PORTAL_PASSWORD:-${SONATYPE_PASSWORD:-${OSSRH_PASSWORD:-}}}"

if [[ -z "${requested_namespace}" ]]; then
	echo "Usage: $0 <namespace>" >&2
	echo "Or set CENTRAL_NAMESPACE in the environment." >&2
	exit 1
fi

if [[ -z "${central_portal_username}" || -z "${central_portal_password}" ]]; then
	echo "Central Portal credentials are required." >&2
	echo "Set CENTRAL_PORTAL_USERNAME / CENTRAL_PORTAL_PASSWORD (OSSRH_* is also accepted as a fallback)." >&2
	exit 1
fi

authorization_token="$(printf '%s:%s' "${central_portal_username}" "${central_portal_password}" | base64 | tr -d '\n')"
upload_url="${staging_api_base_url}/manual/upload/defaultRepository/${requested_namespace}?publishing_type=${publishing_type}"

response="$(
	curl \
		--fail \
		--show-error \
		--silent \
		--request POST \
		--header "Authorization: Bearer ${authorization_token}" \
		"${upload_url}"
)"

if [[ -n "${response}" ]]; then
	echo "${response}"
fi

echo "Uploaded default staging repository for ${requested_namespace} to Sonatype Central Portal (${publishing_type})."
