#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if (($#)); then
  exec mvn -q compile exec:java "-Dexec.args=$*"
fi
exec mvn -q compile exec:java
