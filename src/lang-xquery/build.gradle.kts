// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

val ijVersion = BuildConfiguration.getPlatformVersion(project)

sourceSets.main {
    java.srcDir("main")
    resources.srcDir("main/resources")
}

sourceSets.test {
    java.srcDir("test")
    resources.srcDir("test/resources")
}

tasks {
    processResources {
        from("main") {
            include("**/*.dic")
        }
    }
}

dependencies {
    intellijPlatform {
        if (ijVersion.buildVersion >= 252) {
            bundledModule("intellij.spellchecker")
        }
    }

    implementation(project(":src:kotlin-intellij"))
    implementation(project(":src:lang-core"))
    implementation(project(":src:lang-xpath"))
    implementation(project(":src:lang-java"))
    implementation(project(":src:lang-xdm"))
    implementation(project(":src:lang-xpm"))
    implementation(project(":src:lang-xqdoc"))
    implementation(project(":src:plugin-api"))

    testImplementation(project(":src:intellij-compat"))
    testImplementation(project(":src:intellij-test"))

    testImplementation(project(":src:plugin-basex"))
    testImplementation(project(":src:plugin-marklogic"))
    testImplementation(project(":src:plugin-saxon"))
    testImplementation(project(":src:plugin-w3"))
}