plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.4"
	kotlin("plugin.jpa") version "1.9.25"
	id("com.google.cloud.tools.jib") version "3.4.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("mysql:mysql-connector-java:8.0.33")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	runtimeOnly("org.flywaydb:flyway-mysql")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jib {
	from {
		image = "amazoncorretto:17"
		platforms {
			platform {
				architecture = "arm64"
				os = "linux"
			}
		}
	}
	to {
		image = "tmddlf9730/lottery"
		tags = setOf("0.0.1")
		auth {
			username = project.findProperty("docker.username")?.toString() ?: System.getenv("DOCKER_USERNAME")
			password = project.findProperty("docker.password")?.toString() ?: System.getenv("DOCKER_PASSWORD")
		}
	}
	container {
		jvmFlags = listOf(
			"-Dspring.profiles.active=local",
			"-Dfile.encoding=UTF-8",
		)
		ports = listOf("8080")
	}
}
