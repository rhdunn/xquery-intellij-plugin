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
    implementation(project(":src:intellij-compat"))
    implementation(project(":src:lang-xdm"))

    testImplementation(project(":src:intellij-test"))
}
