#!/bin/sh

echo "[Entrypoint] Waiting for database connection..."
echo

for i in `seq 10` ; do
  nc -z "${DATABASE_HOST:-localhost}" "${DATABASE_PORT:-3306}" > /dev/null 2>&1

  result=$?
  if [ $result -eq 0 ]; then
    echo "[Entrypoint] Database connection established"

    if [ $# -gt 0 ]; then
      echo "[Entrypoint] Starting application..."
      echo

      exec "$@"
    fi

    exit 0
  fi

  sleep 2
done

echo "[Entrypoint] Timeout. Database connection could not be established."

exit 1
