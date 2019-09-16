import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    val kotlinVersion = "1.3.41"
    val springBootVersion = "2.1.8.RELEASE"
    val springKotlinPluginVersion = "1.3.21"
    val testLoggerVersion = "1.6.0"

    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version springKotlinPluginVersion
    id("org.springframework.boot") version springBootVersion
    id("com.adarshr.test-logger") version testLoggerVersion
    jacoco
}
apply(plugin = "io.spring.dependency-management")

group = "com.yuranos"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    //tests dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        //exclude(module = "junit") spring-kafka-test does not compile without junit TestRules in current version of spring-kafka-test
    }
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testCompileOnly("org.junit.vintage:junit-vintage-engine")
}

configurations {
    all {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        resolutionStrategy.cacheDynamicVersionsFor(10, "minutes")
    }
}
