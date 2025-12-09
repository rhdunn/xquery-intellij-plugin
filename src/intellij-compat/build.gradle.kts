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

    if (ijVersion.buildVersion >= 253) {
        java.srcDir("src/251-253/native")
    } else if (ijVersion.buildVersion >= 251) {
        java.srcDir("src/251-253/251")
    } else {
        java.srcDir("src/251-253/compat")
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
