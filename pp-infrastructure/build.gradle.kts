import com.github.davidmc24.gradle.plugin.avro.GenerateAvroProtocolTask
import com.github.davidmc24.gradle.plugin.avro.GenerateAvroSchemaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.20"
    kotlin("plugin.spring") version "1.5.20"

    //    id("com.github.oasalonen.kafka-schema-registry-gradle-plugin") version "0.4.0-SNAPSHOT"
    id("com.github.imflog.kafka-schema-registry-gradle-plugin") version "1.4.0"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.2.0"
    id("com.github.davidmc24.gradle.plugin.avro-base") version "1.2.0"
    id("com.avast.gradle.docker-compose") version "0.14.3" apply true
}

group = "com.worldremit"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    implementation("io.github.microutils:kotlin-logging:1.7.6")

    compileOnly("org.apache.avro:avro:1.10.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

task<Exec>("restoreDump") {
    this.group = "app"
    workingDir("mongo-dump")
    commandLine("sh", "restore.sh")
}

tasks.create<Delete>("removeTmp") {
    group = "app"
    delete = setOf("/tmp/kafka-streams")
}

avro {
    fieldVisibility.set("PRIVATE")
}

dockerCompose {
    useComposeFiles = listOf("${project.rootDir}/docker-compose.yml")
    captureContainersOutput = false
    stopContainers = true
    removeContainers = true
    removeVolumes = true
    removeOrphans = true
    forceRecreate = true
    projectName = project.name

}

val avroSrcDir = "src/main/avro"
val avroAvprDir = "build/generated-avro-avpr"
val avroAvscDir = "build/generated-avro-avsc"
val avroDownload = "build/downloaded-avro"
val wrPackage = "com.worldremit.avro"

val generateProtocolTask = tasks.register<GenerateAvroProtocolTask>("generateProtocol") {
    source = fileTree(avroSrcDir)
    include("**/*.avdl")
    setOutputDir(file(avroAvprDir))
}

val generateSchemaTask = tasks.register<GenerateAvroSchemaTask>("generateSchema") {
    dependsOn(generateProtocolTask)
    source = fileTree(avroSrcDir)
    source = fileTree(avroAvprDir)
    include("**/*.avpr")
    setOutputDir(file(avroAvscDir))
}

val avdlToAvscTask = tasks.register("avdlToAvsc") {
    group = "build"
    val generateSchemaTask = tasks.getByName("generateSchema")
    dependsOn(generateProtocolTask, generateSchemaTask)
    generateSchemaTask.mustRunAfter(generateProtocolTask)
}

tasks.register("registerAvdl") {
    group = "app"
    val register = tasks.getByName("registerSchemasTask")
    val download = tasks.getByName("downloadSchemasTask")
    dependsOn(avdlToAvscTask, register) // download
//    dependsOn(avdlToAvscTask, download) // download
}

tasks.register("createTopics") {
    this.group = "app"
    dependsOn(tasks.getByName("bootRun"))
}

tasks.register("start") {
    this.group = "app"
    val removeTmp = tasks.getByName("removeTmp")
    val brokerUp = tasks.getByName("composeUp")
    val restoreDump = tasks.getByName("restoreDump")
    val createTopics = tasks.getByName("createTopics")
    val registerAvdl = tasks.getByName("registerAvdl")

    dependsOn(removeTmp, brokerUp, restoreDump, createTopics, registerAvdl)
    brokerUp.mustRunAfter(removeTmp)
    restoreDump.mustRunAfter(brokerUp)
    createTopics.mustRunAfter(brokerUp)
    registerAvdl.mustRunAfter(createTopics)
}

tasks.register("restart") {
    this.group = "app"
    val brokerDown = tasks.getByName("composeDownForced")
    val start = tasks.getByName("start")
    dependsOn(brokerDown, start)
    start.mustRunAfter(brokerDown)
}

tasks.register("stop") {
    this.group = "app"
    val composeDown = tasks.getByName("composeDownForced")
    dependsOn(composeDown)
}

schemaRegistry {

    download {
        subjectPattern(".*", avroDownload)
    }

    register {

        fun schemaPath(className: String) =
            "$wrPackage.$className".let { "${avroAvscDir}/${it.replace(".", "/")}.avsc" }

        fun registerKvSchemas(subjectPrefix: String, classNameKey: String, classNameValue: String) {
            val fileNameKey = schemaPath(classNameKey)
            val fileNameValue = schemaPath(classNameValue)
            subject("$subjectPrefix-key", fileNameKey)
            subject("$subjectPrefix-value", fileNameValue)
        }

        fun registerRepartition(topology: String, store: String, classValue: String) {
            val subjectPrefix = "$topology-$store"
            val fileName = schemaPath(classValue)
            subject("$subjectPrefix-repartition", fileName)
        }

        fun registerChangelog(topology: String, store: String, classKey: String, classValue: String) {
            val subjectPrefix = "$topology-$store-changelog"
            registerKvSchemas(subjectPrefix, classKey, classValue)
        }

        fun registerTopic(topic: String, classKey: String, classValue: String) {
            registerKvSchemas(topic, classKey, classValue)
        }

        fun registerJoin(
            topology: String,
            store: String,
            classKey: String,
            classValueThis: String,
            classValueOther: String,
        ) {
            val subjectPrefix = "$topology-$store"
            val subjectSuffix = "join-store-changelog"
            registerKvSchemas("$subjectPrefix-this-$subjectSuffix", classKey, classValueThis)
            registerKvSchemas("$subjectPrefix-other-$subjectSuffix", classKey, classValueOther)
        }

        registerTopic(topic = "pollutions-raw", classKey = "Location", classValue = "PollutionRaw")
        registerTopic(topic = "weathers-raw", classKey = "Location", classValue = "WeatherRaw")
        registerTopic(topic = "pollutions-deduplicated", classKey = "Location", classValue = "PollutionRaw")
        registerTopic(topic = "weathers-deduplicated", classKey = "Location", classValue = "WeatherRaw")
        registerTopic(topic = "pollutions", classKey = "Location", classValue = "PollutionRecord")
        registerTopic(topic = "weathers", classKey = "Location", classValue = "WeatherRecord")

        registerTopic(topic = "pollutions-mean-stddev", classKey = "Location", classValue = "StatsMapResult")
        registerTopic(topic = "weathers-mean-stddev", classKey = "Location", classValue = "StatsMapResult")
        registerTopic(topic = "accuracies", classKey = "Location", classValue = "StatsMapResult")
        registerTopic(topic = "delay-model-stats", classKey = "Location", classValue = "StatsMapResult")

        registerTopic(topic = "measurement-stats", classKey = "Location", classValue = "MeasurementStatsResult")
        registerTopic(topic = "global-stats", classKey = "Location", classValue = "MeasurementStatsResult")

        registerTopic(topic = "measurements", classKey = "Location", classValue = "Measurement")
        registerTopic(topic = "measurements-normalized", classKey = "Location", classValue = "MeasurementNormalized")
        registerTopic(topic = "predictions", classKey = "Location", classValue = "Predicted")
        registerTopic(topic = "confusions", classKey = "Location", classValue = "ConfusionRecord")
        registerTopic(topic = "predictions-vs-actuals", classKey = "Location", classValue = "MatchedRecord")
        registerTopic(topic = "models", classKey = "Location", classValue = "KmeansModels")
        registerTopic(topic = "forecasts", classKey = "Location", classValue = "Forecast")

        registerTopic(topic = "correlations-pm25-pm10", classKey = "Location", classValue = "CorrelationResult")
        registerTopic(topic = "correlations-temp-pressure", classKey = "Location", classValue = "CorrelationResult")
        registerTopic(topic = "correlations-pm25-pressure", classKey = "Location", classValue = "CorrelationResult")

        registerChangelog(topology = "pollution-deduplication", store = "pollution-deduplication-store", classKey = "LocationTime", classValue = "DeduplicationRecord")
        registerChangelog(topology = "weather-deduplication", store = "weather-deduplication-store", classKey = "LocationTime", classValue = "DeduplicationRecord")
        registerChangelog(topology = "predictor", store = "models-store", classKey = "Location", classValue = "KmeansModels")

        registerChangelog(topology = "delay-model-stats", store = "delay-model-stats-store", classKey = "Location", classValue = "StatsMapAggregate")
        registerChangelog(topology = "accuracy", store = "accuracy-stats-store", classKey = "Location", classValue = "StatsMapAggregate")

        registerChangelog(topology = "pollution-mean-stddev", store = "mean-stddev-store", classKey = "Location", classValue = "StatsMapAggregate")
        registerChangelog(topology = "weather-mean-stddev", store = "mean-stddev-store", classKey = "Location", classValue = "StatsMapAggregate")

        registerChangelog(topology = "normalizer", store = "normalizer-stats-store", classKey = "Location", classValue = "NormalizerStats")
        registerChangelog(topology = "normalizer", store = "measurement-training-store", classKey = "Location", classValue = "Measurement")

        registerChangelog(topology = "measurement-stats", store = "measurement-stats-store", classKey = "Location", classValue = "MeasurementStatsAggregate")
        registerChangelog(topology = "global-stats", store = "global-stats-store", classKey = "Location", classValue = "MeasurementStatsResult")
        registerChangelog(topology = "confusion", store = "confusion-stats-store", classKey = "Location", classValue = "MeasurementStatsAggregate")

        registerChangelog(topology = "confusion", store = "confusion-filter-store", classKey = "Location", classValue = "MatchedRecord")

        registerChangelog(topology = "correlation-pm25-pm10", store = "correlation-store", classKey = "Location", classValue = "CorrelationAggregate")
        registerChangelog(topology = "correlation-pm25-pressure", store = "correlation-store", classKey = "Location", classValue = "CorrelationAggregate")
        registerChangelog(topology = "correlation-temp-pressure", store = "correlation-store", classKey = "Location", classValue = "CorrelationAggregate")
        registerChangelog(topology = "clusterer", store = "clusterer-models-store", classKey = "Location", classValue = "KmeansModels")
        registerChangelog(topology = "predictor", store = "predictor-models-store", classKey = "Location", classValue = "KmeansModels")

        registerRepartition(topology = "matcher", store = "matcher-actual-rekey-store", classValue = "MeasurementNormalized")
        registerRepartition(topology = "matcher", store = "matcher-predictions-forwarded-rekey-store", classValue = "Predicted")
        registerRepartition(topology = "matcher", store = "matcher-rekey-store", classValue = "MatchedRecord")
        registerRepartition(topology = "measurement", store = "pollution-rekey-store", classValue = "PollutionRecord")
        registerRepartition(topology = "measurement", store = "weather-rekey-store", classValue = "WeatherRecord")
        registerRepartition(topology = "measurement", store = "measurement-rekey-store", classValue = "Measurement")

        registerJoin(topology = "confusion", store = "confusion-join-store", classKey = "Location", classValueThis = "MatchedRecord", classValueOther = "MeasurementStatsResult")
        // TODO: measurement-store or measurement-join-store
        registerJoin(topology = "measurement", store = "measurement-join-store", classKey = "LocationTime", classValueThis = "WeatherRecord", classValueOther = "PollutionRecord")
        registerJoin(topology = "matcher", store = "matcher-join-store", classKey = "LocationTime", classValueThis = "Predicted", classValueOther = "MeasurementNormalized")


    }
}