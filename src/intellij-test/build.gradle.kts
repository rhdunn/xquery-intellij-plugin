sourceSets.main {
    java.srcDir("main")
}

sourceSets.test {
    java.srcDir("test")
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    implementation("org.hamcrest:hamcrest:3.0")

    implementation(project(":src:intellij-compat"))

    implementation(project(":src:kotlin-intellij"))
}

// Include the testFramework dependency in the main build dependencies.
configurations.getByName("intellijPlatformTestDependencies").dependencies.forEach { dependency ->
    dependencies {
        implementation("${dependency.group}:${dependency.name}:${dependency.version}")
    }
}
