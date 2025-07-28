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
