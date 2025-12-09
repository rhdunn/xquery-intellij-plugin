// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

val ijVersion = BuildConfiguration.IntelliJ

version = ijVersion.buildVersion.toString()

sourceSets.main {
    java.srcDir("main")

    if (ijVersion.buildVersion >= 242) {
        java.srcDir("compat/242/native")
    } else {
        java.srcDir("compat/242/compat")
    }

    if (ijVersion.buildVersion >= 243) {
        java.srcDir("compat/243/native")
    } else {
        java.srcDir("compat/243/compat")
    }

    if (ijVersion.buildVersion >= 252) {
        java.srcDir("compat/252/native")
    } else {
        java.srcDir("compat/252/compat")
    }

    if (ijVersion.buildVersion >= 253) {
        java.srcDir("compat/253/native")
    } else {
        java.srcDir("compat/253/compat")
    }

    if (ijVersion.buildVersion >= 253) {
        java.srcDir("compat/233-253/253")
    } else if (ijVersion.buildVersion >= 252) {
        java.srcDir("compat/233-253/252")
    } else if (ijVersion.buildVersion >= 251) {
        java.srcDir("compat/233-253/251")
    } else {
        java.srcDir("compat/233-253/233")
    }
}

sourceSets.test {
    java.srcDir("test")
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:${Version.Dependency.JUnit5}")
    implementation("org.hamcrest:hamcrest:${Version.Dependency.Hamcrest}")

    implementation(project(":src:intellij-compat"))

    implementation(project(":src:kotlin-intellij"))
}

// Include the testFramework dependency in the main build dependencies.
configurations.getByName("intellijPlatformTestDependencies").dependencies.forEach { dependency ->
    dependencies {
        implementation("${dependency.group}:${dependency.name}:${dependency.version}")
    }
}
