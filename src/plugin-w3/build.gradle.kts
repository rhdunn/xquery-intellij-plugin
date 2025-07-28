val jsoupVersion = project.property("jsoup_version")

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
    implementation("org.jsoup:jsoup:$jsoupVersion")
}
