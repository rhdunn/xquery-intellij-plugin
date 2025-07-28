sourceSets.main {
    java.srcDir("main")
    resources.srcDir("main/resources")
}

sourceSets.test {
    java.srcDir("test")
}

dependencies {
    implementation(project(":src:lang-xpath"))
    implementation(project(":src:lang-xquery"))
    implementation(project(":src:lang-xslt"))
    implementation(project(":src:lang-xdm"))
    implementation(project(":src:lang-xpm"))

    testImplementation(project(":src:kotlin-intellij"))
    testImplementation(project(":src:intellij-test"))
}
