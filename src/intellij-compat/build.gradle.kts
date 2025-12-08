// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

val ijVersion = BuildConfiguration.IntelliJ

version = ijVersion.buildVersion.toString()

sourceSets.main {
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

    if (ijVersion.buildVersion >= 253) {
        java.srcDir("src/251-253/native")
    } else if (ijVersion.buildVersion >= 251) {
        java.srcDir("src/251-253/251")
    } else {
        java.srcDir("src/251-253/compat")
    }

    if (ijVersion.buildVersion >= 253) {
        java.srcDir("src/233-253/native")
    } else if (ijVersion.buildVersion >= 252) {
        java.srcDir("src/233-253/252")
    } else if (ijVersion.buildVersion >= 251) {
        java.srcDir("src/233-253/251")
    } else if (ijVersion.buildVersion >= 233) {
        java.srcDir("src/233-253/233")
    } else {
        java.srcDir("src/233-253/232")
    }

    if (ijVersion.buildVersion >= 252) {
        java.srcDir("src/232-252/native")
    } else {
        java.srcDir("src/232-252/232")
    }

    if (ijVersion.buildVersion >= 253) {
        java.srcDir("src/241-253/native")
    } else if (ijVersion.buildVersion >= 241) {
        java.srcDir("src/241-253/241")
    } else {
        java.srcDir("src/241-253/233")
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
