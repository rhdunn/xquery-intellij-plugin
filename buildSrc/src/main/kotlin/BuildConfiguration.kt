// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

import io.github.rhdunn.intellij.IntelliJVersion
import org.gradle.api.GradleException
import org.gradle.api.Project

object BuildConfiguration {
    /**
     * The version of IntelliJ platform to target.
     */
    fun getPlatformVersion(project: Project): IntelliJVersion {
        val version = getProperty(project, "platform.version", "IDEA_VERSION")
            ?: throw GradleException("The platform.version property is not set.")
        return IntelliJVersion(version)
    }

    private fun getProperty(project: Project, name: String, envName: String? = null): String? {
        val projectValue = project.findProperty(name)?.toString()
            ?.takeIf { value -> value.isNotBlank() }
        val systemValue = System.getProperty(name)
            ?.takeIf { value -> value.isNotBlank() }
        val envValue = envName?.let { System.getenv(it) }
            ?.takeIf { value -> value.isNotBlank() }
        return envValue ?: systemValue ?: projectValue
    }
}
