// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

val ijVersion = BuildConfiguration.IntelliJ

version = ijVersion.buildVersion.toString()

sourceSets.main {
    java.srcDir("main")

    if (ijVersion.buildVersion >= 252) {
        java.srcDir("compat/252/native")
    } else {
        java.srcDir("compat/252/compat")
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
