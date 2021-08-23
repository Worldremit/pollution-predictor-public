# Infrastructure

One Ring to rule them all:

    ./gradlew run

## Gradle tasks

- composePull - pull docker images
- start|stop|restart - setup|shutdown|renew environment (containers, topics, schemas, data)

## Requirements

- 7zip
- mongodb-tools

## CLI

    ./kafka_adm_cli.sh topics
    ./kafka_adm_cli.sh consumer-groups --list
    ./kafka_adm_cli.sh consumer-groups --group pollution-deduplication --describe
    ./kafka_adm_cli.sh consumer-groups --group pollution-deduplication --topic pollutions-raw --reset-offsets --to-earliest --execute
    ./kafka_adm_cli.sh topics --list
