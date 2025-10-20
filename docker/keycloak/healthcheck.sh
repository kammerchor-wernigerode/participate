#!/bin/bash
set -euo pipefail

exec 3<>/dev/tcp/localhost/${KC_HTTP_MANAGEMENT_PORT:-9000}

{
    printf "GET %s/health/ready HTTP/1.1\r\n" "${KC_HTTP_MANAGEMENT_RELATIVE_PATH:-${KC_HTTP_RELATIVE_PATH:-}}";
    printf "Host: localhost:%s\r\n" "${KC_HTTP_MANAGEMENT_PORT:-9000}";
    printf "User-Agent: healthcheck\r\n";
    printf "Connection: close\r\n";
    printf "\r\n";
} >&3

timeout --preserve-status 2 cat <&3 | grep -m1 -E '"status"[[:space:]]*:[[:space:]]*"UP"'
error=$?

exec 3<&- 3>&-

exit $error
