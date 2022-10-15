plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.resilience4j:resilience4j-ratelimiter:1.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

application {
    mainClass.set("fr.tawane.demo.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}


