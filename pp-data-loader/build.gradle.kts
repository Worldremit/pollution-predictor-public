import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	id("com.commercehub.gradle.plugin.avro") version "0.19.1"
	id("com.commercehub.gradle.plugin.avro-base") version "0.19.1"
	kotlin("jvm") version "1.4.20"
	kotlin("plugin.spring") version "1.4.20"
}

group = "com.worldremit"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	jcenter()
	maven { url = uri("https://packages.confluent.io/maven/") }
}

dependencies {
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.3.6.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("io.github.microutils:kotlin-logging:1.7.6") // 1.12.0

	implementation("io.confluent:kafka-avro-serializer:6.0.0")
	implementation("io.confluent:kafka-streams-avro-serde:6.0.0")
	implementation("org.apache.avro:avro:1.10.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
}

avro {
	fieldVisibility.set("PRIVATE")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
