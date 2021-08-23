#!/usr/bin/env sh

BASE_URL="http://localhost:30300"

case $1 in
  init) curl ${BASE_URL}/init ;;
  regular) curl ${BASE_URL}/regular ;;
  *) echo "Unknown command"; exit 1 ;;
esac

exit 0

