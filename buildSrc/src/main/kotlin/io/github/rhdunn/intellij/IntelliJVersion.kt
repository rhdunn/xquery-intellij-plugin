// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package io.github.rhdunn.intellij

import org.gradle.api.GradleException
import java.io.File
import java.net.URI

sealed interface IntelliJVersion {
    val platformType: String
    val platformVersion: String
    val buildVersion: Int
}

fun IntelliJVersion(type: String, version: String): IntelliJVersion {
    return IntelliJVersionNumber.parse(type, version)
        ?: IntelliJBuildNumber.parse(type, version)
        ?: IntelliJSnapshot.parse(type, version)
        ?: throw GradleException("Unsupported IntelliJ version: $version")
}

/**
 * An IntelliJ version number.
 *
 * Supported formats:
 * - `2022.1.1`
 * - `2024.1`
 * - `IC-2022.1.1`
 * - `IC-2024.1`
 */
data class IntelliJVersionNumber(
    val value: String,
    override val platformType: String,
    override val platformVersion: String,
    override val buildVersion: Int,
    val year: Int,
    val release: Int,
    val build: Int?,
) : IntelliJVersion {
    override fun toString(): String = value

    companion object {
        val FORMAT = "(([A-Z]+)-)?((20([0-9][0-9])).([1-3])(.([0-9]+))?)".toRegex()

        fun parse(type: String, version: String): IntelliJVersionNumber? {
            FORMAT.matchEntire(version)?.let { match ->
                return IntelliJVersionNumber(
                    value = version,
                    platformType = match.groupValues[2].takeIf { it.isNotEmpty() } ?: type,
                    platformVersion = match.groupValues[3],
                    buildVersion = "${match.groupValues[5]}${match.groupValues[6]}".toInt(),
                    year = match.groupValues[4].toInt(),
                    release = match.groupValues[6].toInt(),
                    build = match.groupValues[8].takeIf { it.isNotEmpty() }?.toInt(),
                )
            }
            return null
        }
    }
}

/**
 * An IntelliJ build number.
 *
 * Supported formats:
 * - `221.5080.210`
 * - `IC-221.5080.210`
 */
data class IntelliJBuildNumber(
    val value: String,
    override val platformType: String,
    override val platformVersion: String,
    val major: Int,
    val minor: Int,
    val patch: Int,
) : IntelliJVersion {
    override val buildVersion: Int get() = major

    override fun toString(): String = value

    companion object {
        val FORMAT = "(([A-Z]+)-)?((([0-9][0-9])([0-9])).([0-9]+).([0-9]+))".toRegex()

        fun parse(type: String, version: String): IntelliJBuildNumber? {
            FORMAT.matchEntire(version)?.let { match ->
                return IntelliJBuildNumber(
                    value = version,
                    platformType = match.groupValues[2].takeIf { it.isNotEmpty() } ?: type,
                    platformVersion = "20${match.groupValues[5]}.${match.groupValues[6]}",
                    major = match.groupValues[4].toInt(),
                    minor = match.groupValues[7].toInt(),
                    patch = match.groupValues[8].toInt(),
                )
            }
            return null
        }
    }
}

/**
 * An IntelliJ snapshot.
 *
 * Supported formats:
 * - `192-EAP-SNAPSHOT`
 * - `LATEST-EAP-SNAPSHOT`
 */
data class IntelliJSnapshot(
    val value: String,
    val build: IntelliJBuildNumber,
) : IntelliJVersion {
    override val platformType: String get() = build.platformType
    override val platformVersion: String get() = "${build.platformVersion} EAP"
    override val buildVersion: Int get() = build.buildVersion

    override fun toString(): String = value

    companion object {
        private const val REPOSITORY_PATH =
            "https://www.jetbrains.com/intellij-repository/snapshots/com/jetbrains/intellij/idea/BUILD"

        val FORMAT = "([0-9][0-9][0-9]|LATEST)-EAP-SNAPSHOT".toRegex()

        fun parse(type: String, version: String): IntelliJSnapshot? {
            if (FORMAT.matchEntire(version) == null) return null

            val build = File("build/BUILD-$version.txt")
            if (!build.exists()) {
                build.parentFile.mkdirs()

                val downloadUrl = URI("$REPOSITORY_PATH/$version/BUILD-$version.txt").toURL()
                println("Downloading build version file '$downloadUrl' to '${build.absolutePath}'")

                downloadUrl.openStream().use { input ->
                    build.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            return IntelliJSnapshot(version, IntelliJBuildNumber.parse(type, build.readLines()[0])!!)
        }
    }
}
