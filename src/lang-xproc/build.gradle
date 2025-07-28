sourceSets.main {
    java.srcDir("main")
    resources.srcDir("main/resources")
}

sourceSets.test {
    java.srcDir("test")
    resources.srcDir("test/resources")
}

dependencies {
    implementation(project(":src:intellij-compat"))
    implementation(project(":src:kotlin-intellij"))
    implementation(project(":src:lang-xdm"))
    implementation(project(":src:lang-xslt"))

    testImplementation(project(":src:intellij-test"))
}
