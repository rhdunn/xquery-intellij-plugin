val intellijVersion = project.property("idea_since_build") as Int
val intellijType = project.property("idea_type")

version = intellijVersion

sourceSets.main {
    if (intellijVersion >= 231) {
        java.srcDir("src/231/native")
    } else {
        java.srcDir("src/231/compat")
    }

    if (intellijVersion >= 232) {
        java.srcDir("src/232/native")
    } else {
        java.srcDir("src/232/compat")
    }

    if (intellijVersion >= 233) {
        java.srcDir("src/233/native")
    } else {
        java.srcDir("src/233/compat")
    }

    if (intellijVersion >= 242) {
        java.srcDir("src/242/native")
    } else {
        java.srcDir("src/242/compat")
    }

    if (intellijVersion >= 242) {
        java.srcDir("src/233-242/native")
    } else if (intellijVersion >= 233) {
        java.srcDir("src/233-242/233")
    } else {
        java.srcDir("src/233-242/232")
    }

    if (intellijVersion >= 243) {
        java.srcDir("src/243/native")
    } else {
        java.srcDir("src/243/compat")
    }

    if (intellijVersion >= 251) {
        java.srcDir("src/251/native")
    } else {
        java.srcDir("src/251/compat")
    }

    if (intellijVersion >= 251) {
        java.srcDir("src/233-251/native")
    } else if (intellijVersion >= 233) {
        java.srcDir("src/233-251/233")
    } else {
        java.srcDir("src/233-251/232")
    }

    // Microservices

    if (intellijType == "IU") {
        if (intellijVersion >= 231) {
            java.srcDir("src/microservices/IU-231")
        } else {
            java.srcDir("src/microservices/IU-203")
        }
    } else {
        java.srcDir("src/microservices/IC-203")
    }
}
