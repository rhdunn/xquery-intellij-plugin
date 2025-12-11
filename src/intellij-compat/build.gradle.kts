// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

val ijVersion = BuildConfiguration.IntelliJ

version = ijVersion.buildVersion.toString()

sourceSets.main {
    if (ijVersion.buildVersion >= 242) {
        java.srcDir("src/242/native")
    } else {
        java.srcDir("src/242/compat")
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

    // Microservices

    if (ijVersion.buildVersion >= 251 || ijVersion.platformType == "IU") {
        java.srcDir("src/microservices/IU-231")
    } else {
        java.srcDir("src/microservices/IC-203")
    }
}
