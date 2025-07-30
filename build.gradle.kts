// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
import io.github.rhdunn.intellij.IntelliJSnapshot
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.net.URI

val junit5_version = "5.9.1"
val junit_platform_version = "1.9.1"

var plugin_version = "1.9.4"
// Suffix ordering:
//    `"-snapshot"` -- for development builds
//    `"-eap-###"`  -- for early access preview builds (`-eap-1`, `-eap-2`, `-eap-3`, etc.)
//    `""`          -- for release builds
var suffix = ""

plugins {
    id("org.jetbrains.intellij.platform") version "2.7.0"
    id("org.jetbrains.kotlin.jvm") version Version.Kotlin(BuildConfiguration.IntelliJ)
}

val ijVersion = BuildConfiguration.IntelliJ

configure(allprojects - project(":src")) {
    apply(plugin = "org.jetbrains.intellij.platform")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "uk.co.reecedunn.intellij.plugin.xquery"
    version = plugin_version + "-" + ijVersion.buildVersion.toString() + suffix

    repositories {
        mavenCentral()
        intellijPlatform {
            defaultRepositories()
            jetbrainsRuntime()
        }
        maven { url = URI("https://packages.jetbrains.team/maven/p/grazi/grazie-platform-public") } // grazie
        maven { url = URI("https://download.jetbrains.com/teamcity-repository") } // teamcity
        maven { url = URI("https://packages.jetbrains.team/maven/p/dpgpv/maven") } // download-pgp-verifier
    }

    kotlin {
        jvmToolchain(Version.Java(ijVersion))
    }

    tasks.runIde {
        maxHeapSize = "2g"
    }

    dependencies {
        intellijPlatform {
            if (ijVersion is IntelliJSnapshot) {
                create(ijVersion.platformType, ijVersion.value) {
                    useInstaller = false
                }
                jetbrainsRuntime()
            } else {
                create(ijVersion.platformType, ijVersion.platformVersion) {
                    useInstaller = true
                }
            }

            testFramework(TestFrameworkType.Platform)
        }

        testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5_version")
        testImplementation("org.hamcrest:hamcrest-core:1.3")

        testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junit_platform_version")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5_version")
        testRuntimeOnly("junit:junit:4.13")
    }

    tasks.test {
        useJUnitPlatform()

        // IntelliJ requires the tests to be run headless when loading icons, etc.
        systemProperty("java.awt.headless", "true")
    }
}

dependencies {
    intellijPlatform {
        bundledPlugin("org.intellij.intelliLang")
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.properties")
        if (ijVersion.buildVersion >= 243) {
            bundledPlugin("com.intellij.modules.json")
        }
    }

    implementation(project(":src:kotlin-intellij"))
    implementation(project(":src:lang-core"))
    implementation(project(":src:plugin-api"))

    implementation(project(":src:plugin-basex"))
    implementation(project(":src:plugin-existdb"))
    implementation(project(":src:plugin-marklogic"))
    implementation(project(":src:plugin-saxon"))
    implementation(project(":src:plugin-xijp"))

    implementation(project(":src:lang-xdm"))
    implementation(project(":src:lang-xpm"))

    implementation(project(":src:lang-xpath"))
    implementation(project(":src:lang-xquery"))
    implementation(project(":src:lang-xslt"))
    implementation(project(":src:lang-xproc"))

    implementation(project(":src:lang-java"))
    implementation(project(":src:lang-xqdoc"))

    implementation(project(":src:plugin-expath"))
    implementation(project(":src:plugin-exquery"))
    implementation(project(":src:plugin-w3"))

    testImplementation(project(":src:intellij-test"))
    testImplementation("org.hamcrest:hamcrest-core:1.3")
}

sourceSets.main {
    if (ijVersion.platformType == "IU") {
        if (ijVersion.buildVersion >= 193) {
            resources.srcDir("src/main/resources-microservices/193")
        } else {
            resources.srcDir("src/main/resources-microservices/compat")
        }
    } else {
        resources.srcDir("src/main/resources-microservices/compat")
    }
}

println("Building for IntelliJ ${ijVersion.platformType} version '${ijVersion.platformVersion}', since build '${ijVersion.buildVersion}'")

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "${ijVersion.buildVersion}"
            untilBuild = "${ijVersion.buildVersion}.*"
        }
    }
}
