sourceSets.main {
    java.srcDir("main")
    resources.srcDir("main/resources")
}

sourceSets.test {
    java.srcDir("test")
}

dependencies {
    implementation(project(":src:kotlin-intellij"))
    implementation(project(":src:lang-core"))
    implementation(project(":src:lang-xquery"))
    implementation(project(":src:lang-xslt"))
    implementation(project(":src:lang-xdm"))
    implementation(project(":src:lang-xpm"))
    implementation(project(":src:plugin-api"))

    testImplementation(project(":src:intellij-test"))

    // JSoup
    implementation("org.jsoup:jsoup:${Version.Dependency.JSoup}")
}
