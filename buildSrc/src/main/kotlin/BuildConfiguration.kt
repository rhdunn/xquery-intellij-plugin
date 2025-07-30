// Copyright (C) 2024-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

import io.github.rhdunn.intellij.IntelliJVersion

object BuildConfiguration {
    /**
     * The IntelliJ platform type to build for.
     */
    val PlatformType: String
        get() = getProperty("platform.type", "IDEA_TYPE") ?: Version.PlatformType

    /**
     * The version of IntelliJ platform to target.
     */
    fun getPlatformVersion(): IntelliJVersion {
        val version = getProperty("platform.version", "IDEA_VERSION") ?: "2025.1"
        return IntelliJVersion(PlatformType, version)
    }

    private fun getProperty(name: String, envName: String? = null): String? {
        val systemValue = System.getProperty(name)
            ?.takeIf { value -> value.isNotBlank() }
        val envValue = envName?.let { System.getenv(it) }
            ?.takeIf { value -> value.isNotBlank() }
        return envValue ?: systemValue
    }
}
