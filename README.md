# Online Machine Learning using Kafka Streams

## Keywords:

Machine Learning, Online, Streaming, Kafka Streams, Kotlin, JVM  

## Presentation

I encourage everyone to see a [presentation](https://github.com/Worldremit/pollution-predictor-public/blob/master/presentation-devoxx_2021-08-25.pdf) first.

## Project structure

- pp-docs - a solution blueprints and other presentation assets
- pp-infrastructure - a simple gradle build / docker-compose to spin up an infrastructure including dumped measurements
- pp-data-preparation - a set of streams to clean-up and prepare data
- pp-pollution-predictor - a set of streams for data clustering
- pp-data-loader - a simple tool that reads input (MongoDb) data and load it to kafka topics
- pp-generator - a simple tool to generate load and send it to kafka topics
- presentation-devoxx_2021-08-25.pdf - a presentation

## Project summary

A project was created to show a practical end-to-end example how to deal with online machine learning problems (using Kafka Streams). 
Its main purpose is to predict pollution level based on historical data and current weather conditions.

### Main concept

A main idea is to compare 'old' predictions (24h ahead) with current measurements. That is an input for clusterization.
Date models (one per each kafka key) are dynamic, and they change in a streaming way. 
The beauty of this solution is that we do not need to store all historical measurements.
As we may read in a paper: 'Fast and accurate k-means', it is enough to just persist coordinates of a cluster.
I used for that Kafka Streams' state stores, which are scalable and durable.
In order to achieve this I had to create a serializable custom implementation of that algorithm (a fork of Mahout's one).

![A main concept!](/pp-docs/main_concept.png)

A solution is quasi-online. 
We create statistics (e.g. average, standard deviation, correlation) in a streaming fashion, however after a training period passes we use their fixed snapshot for data normalization.
'A switch' is dynamic and was achieved using a custom Kafka Streams' window store.

![Normalization!](/pp-docs/normalization.png)

### Services

A code is organized around two main JVMs: pp-data-preparation and pp-pollution-predictor. 
Those are Spring Boot apps combined with Spring Cloud Stream framework. 
Each app consists of several Kafka Streams topologies.

![Streams!](/pp-docs/streams.png)

A high level diagram shows a data flow. Arrows represent producers and consumers:

![High Level!](/pp-docs/high_level.png)


## How to run
 
    pp-infrastructure>      ./gradlew start
    pp-generator>           ./gradlew bootRun     # or pp-data-loader>
    pp-data-preparation>    ./gradlew bootRun
    pp-pollution-predictor> ./gradlew bootRun

## Requirements

- 8+ GB RAM ()
- Linux platform (with small modifications should work on other platforms as well)
- 7zip, mongodb-tools on PATH

