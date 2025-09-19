plugins {
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "tutorials"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainModule.set("tutorial.tutorial")
    mainClass.set("tutorial.tutorial.TutorialApp")
}

javafx {
    version = "25"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.media")
}

