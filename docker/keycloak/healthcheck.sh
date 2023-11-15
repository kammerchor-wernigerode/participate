#!/bin/bash

exec 3<>/dev/tcp/localhost/${KC_HTTP_PORT:-8080}

echo -e "GET /health/ready HTTP/1.1\nhost: localhost:${KC_HTTP_PORT:-8080}\n" >&3

timeout --preserve-status 1 cat <&3 | grep -m 1 status | grep -m 1 UP
error=$?

exec 3<&-
exec 3>&-

exit $error
