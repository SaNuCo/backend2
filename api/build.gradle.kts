import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
	id("org.springframework.boot") version "2.4.1"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.4.21"
	kotlin("plugin.spring") version "1.4.30-M1"
	id("org.jetbrains.kotlin.plugin.noarg") version "1.4.20"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.4.20"
	id("jacoco")
	id("org.jetbrains.dokka") version "1.4.20"
}

group = "de.unistuttgart.iste.sopraws20"
version = "1.3.16"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	jcenter()
	maven("https://dl.bintray.com/kotlin/kotlin-eap")
	maven("https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.7")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.3.7")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.mariadb.jdbc:mariadb-java-client:2.7.0")
	implementation("org.apache.commons:commons-dbcp2:2.8.0")
	implementation("com.opencsv:opencsv:4.5")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.h2database:h2:1.3.148")

	dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.20")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
