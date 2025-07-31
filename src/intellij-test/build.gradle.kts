// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

sourceSets.main {
    java.srcDir("main")
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
