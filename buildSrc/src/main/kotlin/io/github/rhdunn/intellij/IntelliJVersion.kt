// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package io.github.rhdunn.intellij

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
    val platformType: String?,
    val platformVersion: String,
    val year: Int,
    val release: Int,
    val build: Int?,
) {
    override fun toString(): String = value

    companion object {
        val FORMAT = "(([A-Z]+)-)?((20[0-9][0-9]).([1-3])(.([0-9]+))?)".toRegex()

        fun parse(value: String): IntelliJVersionNumber? {
            FORMAT.matchEntire(value)?.let { match ->
                return IntelliJVersionNumber(
                    value = value,
                    platformType = match.groupValues[2].takeIf { it.isNotEmpty() },
                    platformVersion = match.groupValues[3],
                    year = match.groupValues[4].toInt(),
                    release = match.groupValues[5].toInt(),
                    build = match.groupValues[7].takeIf { it.isNotEmpty() }?.toInt(),
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
    val platformType: String?,
    val platformVersion: String,
    val major: Int,
    val minor: Int,
    val patch: Int,
) {
    override fun toString(): String = value

    companion object {
        val FORMAT = "(([A-Z]+)-)?(([0-9][0-9][0-9]).([0-9]+).([0-9]+))".toRegex()

        fun parse(value: String): IntelliJBuildNumber? {
            FORMAT.matchEntire(value)?.let { match ->
                return IntelliJBuildNumber(
                    value = value,
                    platformType = match.groupValues[2].takeIf { it.isNotEmpty() },
                    platformVersion = match.groupValues[3],
                    major = match.groupValues[4].toInt(),
                    minor = match.groupValues[5].toInt(),
                    patch = match.groupValues[6].toInt(),
                )
            }
            return null
        }
    }
}
