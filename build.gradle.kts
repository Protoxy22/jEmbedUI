plugins {
    kotlin("jvm") version "2.0.21"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val lwjglVersion = "3.4.0-SNAPSHOT"
val jomlVersion = "1.10.8"
val `joml-primitivesVersion` = "1.10.0"
val lwjglNatives = listOf("natives-linux", "natives-linux-arm64")

repositories {
    mavenLocal()          // <-- looks in ~/.m2/repository
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots")
}

// We ship BOTH linux-x64 and linux-arm64 natives so one jar runs on either arch.
dependencies {

    // LWJGL—core + GLFW + OpenGL ES + NanoVG + STB, plus BOTH native packs
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengles:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-nanovg:${lwjglVersion}")
    implementation("org.lwjgl:lwjgl-stb:${lwjglVersion}")
    implementation ("org.lwjgl:lwjgl-sdl:${lwjglVersion}")

    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-opengles:$lwjglVersion:natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-sdl:$lwjglVersion:natives-linux")


    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:natives-linux-arm64")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-linux-arm64")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-linux-arm64")
    runtimeOnly("org.lwjgl:lwjgl-opengles:$lwjglVersion:natives-linux-arm64")
    runtimeOnly("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-linux-arm64")
    runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-linux-arm64")
    runtimeOnly("org.lwjgl:lwjgl-sdl:$lwjglVersion:natives-linux-arm64")
}

application {
    mainClass.set("com.jembedui.examples.ComprehensiveExample")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.jembedui.examples.ComprehensiveExample"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.shadowJar {
    archiveBaseName.set("jEmbedUI")
    archiveClassifier.set("all")
    archiveVersion.set("")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

