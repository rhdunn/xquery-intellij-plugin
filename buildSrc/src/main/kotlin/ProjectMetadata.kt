// Copyright (C) 2024-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
import io.github.rhdunn.intellij.BuildType
import io.github.rhdunn.intellij.IntelliJVersion

/**
 * Information about the project.
 */
@Suppress("ConstPropertyName", "FunctionName")
object ProjectMetadata {
    /**
     * Version information about the current build of the project.
     */
    object Build {
        /**
         * The semantic version of the current version.
         */
        const val VersionTag = "1.9.5"

        /**
         * The build type of this project.
         */
        val Type = BuildType.Release

        /**
         * The artifact version ID.
         */
        fun Version(ijVersion: IntelliJVersion) = "${VersionTag}-${ijVersion.buildVersion}${Type.suffix}"

        /**
         * The plugin group ID.
         */
        const val GroupId = "uk.co.reecedunn.intellij.plugin.xquery"
    }
}
