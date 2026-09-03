#!/bin/sh
set -e

until mongosh --host "${MONGO_HOST:-mongo}" --quiet --eval "db.runCommand({ ping: 1 })" >/dev/null 2>&1; do
  sleep 1
done

mongoimport --host "${MONGO_HOST:-mongo}" --db "${MONGO_DATABASE:-agasalha}" \
  --collection itens --jsonArray --drop --file /seed/itens.json
