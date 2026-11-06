plugins {
	java
	idea
}

val mainPackage = "net.sourceforge.pdfjumbler"
val mainClass = "$mainPackage.PdfJumbler"

defaultTasks("jar")

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.tinylog:tinylog-api:2.8.1")
	implementation("org.tinylog:tinylog-impl:2.8.1")
	implementation("org.apache.pdfbox:pdfbox:2.0.21")
	implementation("org.apache.xmlgraphics:batik-transcoder:1.13")
	implementation("com.formdev:flatlaf:3.8")
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
