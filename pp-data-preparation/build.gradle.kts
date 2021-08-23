import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.commercehub.gradle.plugin.avro") version "0.19.1"
	id("com.commercehub.gradle.plugin.avro-base") version "0.19.1"
	kotlin("jvm") version "1.5.20"
	kotlin("plugin.spring") version "1.5.20"
//	application
}

group = "com.worldremit"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://packages.confluent.io/maven/") }
}

extra["springCloudVersion"] = "2020.0.3"

dependencies {
	implementation("org.apache.kafka:kafka-streams")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.cloud:spring-cloud-stream")
	implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")
	testImplementation("org.apache.kafka:kafka-streams-test-utils:2.7.1")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

	implementation("org.apache.commons:commons-math3:3.6.1")
	implementation("io.github.microutils:kotlin-logging:1.7.6")
	implementation("io.confluent:kafka-streams-avro-serde:5.5.1")
	implementation("org.apache.avro:avro:1.10.1")
//	implementation("com.google.guava:guava:30.1.1-jre")

	testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23")


}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

avro {
	fieldVisibility.set("PRIVATE")
}

//application {
//	applicationDefaultJvmArgs = listOf("-Xmx2048m") // "-XX:+PrintFlagsFinal"
//}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
