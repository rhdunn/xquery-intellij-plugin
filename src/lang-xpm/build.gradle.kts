// Copyright (C) 2016-2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

val ijVersion = BuildConfiguration.IntelliJ

sourceSets.main {
    java.srcDir("main")
    resources.srcDir("main/resources")
}

sourceSets.test {
    java.srcDir("test")
    resources.srcDir("test/resources")
}

dependencies {
    intellijPlatform {
        if (ijVersion.buildVersion >= 262) {
            bundledModule("intellij.xml.structureView")
        }
    }

    implementation(project(":src:kotlin-intellij"))
    implementation(project(":src:intellij-compat"))
    implementation(project(":src:lang-xdm"))

    testImplementation(project(":src:intellij-test"))
}
