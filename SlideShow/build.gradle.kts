plugins {
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.1.1"
}

group = "SlideShow"
version = "1.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("slideshow.slideshow")
    mainClass.set("slideshow.slideshow.SliderShowApplication")

    applicationDefaultJvmArgs = listOf(
        "-XX:NativeMemoryTracking=summary",
        "-XX:+UseZGC",
        "-Xmx400m",
        "-Xms400m",
        "-Xss90k",
        "-XX:MaxGCPauseMillis=200"
    )
}

javafx {
    version = "23"
    modules = listOf("javafx.controls", "javafx.fxml")
}

jlink {

}
