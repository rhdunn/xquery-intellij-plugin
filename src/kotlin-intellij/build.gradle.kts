// Copyright (C) 2016-2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

val ijVersion = BuildConfiguration.IntelliJ

sourceSets.main {
    java.srcDir("main")
}

sourceSets.test {
    java.srcDir("test")
    resources.srcDir("test/resources")
}

dependencies {
    intellijPlatform {
        if (ijVersion.buildVersion >= 262) {
            bundledModule("intellij.platform.smRunner")
        }
    }

    implementation(project(":src:intellij-compat"))
}
