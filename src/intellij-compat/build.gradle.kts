val ijVersion = BuildConfiguration.getPlatformVersion(project)

version = ijVersion.buildVersion.toString()

sourceSets.main {
    if (ijVersion.buildVersion >= 231) {
        java.srcDir("src/231/native")
    } else {
        java.srcDir("src/231/compat")
    }

    if (ijVersion.buildVersion >= 232) {
        java.srcDir("src/232/native")
    } else {
        java.srcDir("src/232/compat")
    }

    if (ijVersion.buildVersion >= 233) {
        java.srcDir("src/233/native")
    } else {
        java.srcDir("src/233/compat")
    }

    if (ijVersion.buildVersion >= 242) {
        java.srcDir("src/242/native")
    } else {
        java.srcDir("src/242/compat")
    }

    if (ijVersion.buildVersion >= 242) {
        java.srcDir("src/233-242/native")
    } else if (ijVersion.buildVersion >= 233) {
        java.srcDir("src/233-242/233")
    } else {
        java.srcDir("src/233-242/232")
    }

    if (ijVersion.buildVersion >= 243) {
        java.srcDir("src/243/native")
    } else {
        java.srcDir("src/243/compat")
    }

    if (ijVersion.buildVersion >= 251) {
        java.srcDir("src/251/native")
    } else {
        java.srcDir("src/251/compat")
    }

    if (ijVersion.buildVersion >= 251) {
        java.srcDir("src/233-251/native")
    } else if (ijVersion.buildVersion >= 233) {
        java.srcDir("src/233-251/233")
    } else {
        java.srcDir("src/233-251/232")
    }

    // Microservices

    if (ijVersion.platformType == "IU") {
        if (ijVersion.buildVersion >= 231) {
            java.srcDir("src/microservices/IU-231")
        } else {
            java.srcDir("src/microservices/IU-203")
        }
    } else {
        java.srcDir("src/microservices/IC-203")
    }
}
