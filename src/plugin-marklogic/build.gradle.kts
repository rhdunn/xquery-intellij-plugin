val ijVersion = BuildConfiguration.getPlatformVersion(project)

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
        bundledPlugin("com.intellij.properties")
        if (ijVersion.buildVersion >= 243) {
            bundledPlugin("com.intellij.modules.json")
        }
    }

    implementation(project(":src:intellij-compat"))
    implementation(project(":src:kotlin-intellij"))
    implementation(project(":src:lang-core"))
    implementation(project(":src:lang-xpath"))
    implementation(project(":src:lang-xquery"))
    implementation(project(":src:lang-xslt"))
    implementation(project(":src:lang-xdm"))
    implementation(project(":src:lang-xpm"))
    implementation(project(":src:lang-xqdoc"))
    implementation(project(":src:plugin-api"))

    testImplementation(project(":src:intellij-test"))

    // Saxon HE
    runtimeOnly("net.sf.saxon:Saxon-HE:${Version.Dependency.SaxonHE}")

    // JSoup
    implementation("org.jsoup:jsoup:${Version.Dependency.JSoup}")
}
