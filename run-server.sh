#!/usr/bin/env sh
# Starts the server in backend/ on http://localhost:8080 (PORT changes it).
# CN1_PROFILE picks its settings; the default, dev, needs nothing installed.
cd "$(dirname "$0")" || exit 1
CN1_PROFILE="${CN1_PROFILE:-dev}" exec ./mvnw -B -pl backend -am -Dcodename1.platform=backend compile cn1:backend "$@"
