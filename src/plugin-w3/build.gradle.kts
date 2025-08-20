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
    implementation(project(":src:kotlin-intellij"))
    implementation(project(":src:lang-core"))
    implementation(project(":src:lang-xdm"))
    implementation(project(":src:lang-xpath"))
    implementation(project(":src:lang-xquery"))
    implementation(project(":src:lang-xslt"))
    implementation(project(":src:lang-xpm"))
    implementation(project(":src:lang-xqdoc"))

    testImplementation(project(":src:intellij-test"))

    // JSoup
    implementation("org.jsoup:jsoup:${Version.Dependency.JSoup}")
}
