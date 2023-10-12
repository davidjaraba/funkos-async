plugins {
    id("java")
    id("io.freefair.lombok") version "8.3"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.mybatis:mybatis:3.5.13")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}