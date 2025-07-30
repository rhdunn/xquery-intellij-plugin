// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

sourceSets.main {
    java.srcDir("main")
}

sourceSets.test {
    java.srcDir("test")
    resources.srcDir("test/resources")
}

dependencies {
    implementation(project(":src:intellij-compat"))
}
