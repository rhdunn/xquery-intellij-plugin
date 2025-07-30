// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

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
        bundledPlugin("com.intellij.java")
    }

    implementation(project(":src:kotlin-intellij"))
    implementation(project(":src:lang-xdm"))
    implementation(project(":src:lang-xpm"))

    testImplementation(project(":src:intellij-test"))
}
