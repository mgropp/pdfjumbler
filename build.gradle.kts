plugins {
	java
	idea
	alias(libs.plugins.spotless)
}

val mainPackage = "io.gropp.pdfjumbler"
val mainClass = "$mainPackage.PdfJumbler"

defaultTasks("jar")

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.tinylog.api)
	implementation(libs.tinylog.impl)
	implementation(libs.pdfbox)
	implementation(libs.batik.transcoder)
	implementation(libs.flatlaf)
	implementation(libs.jspecify)

	testImplementation(platform(libs.junit.bom))
	testImplementation(libs.junit.jupiter)
	testImplementation(libs.assertj.core)
	testRuntimeOnly(libs.junit.platform.launcher)
}

spotless {
	java {
		palantirJavaFormat("2.50.0")
	}
}

tasks.register("installGitHooks") {
	doLast {
		val hook = file("${rootDir}/.git/hooks/pre-push")
		hook.writeText("#!/bin/sh\n./gradlew spotlessCheck\n")
		hook.setExecutable(true)
	}
}

tasks.compileJava {
	options.encoding = "UTF-8"
	options.release = 21
}

tasks.test {
	useJUnitPlatform()
	systemProperty("java.awt.headless", "true")
}

tasks.jar {
	archiveFileName = "pdfjumbler.jar"
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE

	manifest {
		attributes("Main-Class" to mainClass)
	}

	from({
		configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
	})

	exclude("META-INF/*.SF")
	exclude("META-INF/*.DSA")
	exclude("META-INF/*.RSA")
}
