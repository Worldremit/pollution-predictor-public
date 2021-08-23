#!/usr/bin/env bash

docker exec -it broker kafka-topics --bootstrap-server localhost:9092 --delete --topic ".*"
docker exec -it broker kafka-topics --bootstrap-server localhost:9092 --list