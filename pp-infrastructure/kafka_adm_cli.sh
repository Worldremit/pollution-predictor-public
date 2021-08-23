#!/usr/bin/env sh

CMD_PREFIX="docker-compose exec broker"
BROKER_CONFIG="--bootstrap-server localhost:9092"

case $1 in
  consumer-groups)
    $CMD_PREFIX kafka-consumer-groups $BROKER_CONFIG "${@:2}" ;;
  topics)
    $CMD_PREFIX kafka-topics $BROKER_CONFIG "${@:2}" ;;
  *)
    echo "Unknown command"; exit 1 ;;
esac

exit 0