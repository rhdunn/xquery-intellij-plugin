// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

sourceSets.main {
    java.srcDir("main")
    resources.srcDir("main/resources")
}

dependencies {
    implementation(project(":src:intellij-compat"))
    implementation(project(":src:kotlin-intellij"))
    implementation(project(":src:lang-xdm"))
    implementation(project(":src:lang-xpm"))
    implementation(project(":src:lang-xquery"))
}
