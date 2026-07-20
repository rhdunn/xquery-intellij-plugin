// Copyright (C) 2016-2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

val ijVersion = BuildConfiguration.IntelliJ

sourceSets.main {
    java.srcDir("main")
    resources.srcDir("main/resources")
}

sourceSets.test {
    java.srcDir("test")
}

dependencies {
    intellijPlatform {
        if (ijVersion.buildVersion >= 262) {
            bundledModule("intellij.platform.smRunner")
        }
    }

    implementation(project(":src:intellij-compat"))
    implementation(project(":src:kotlin-intellij"))
    implementation(project(":src:lang-core"))
    implementation(project(":src:lang-xdm"))
    implementation(project(":src:lang-xpm"))

    testImplementation(project(":src:intellij-test"))
    testImplementation(project(":src:lang-xpath"))
}
