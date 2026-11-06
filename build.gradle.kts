plugins {
	java
	idea
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
}

tasks.compileJava {
	options.encoding = "UTF-8"
	options.release = 21
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
