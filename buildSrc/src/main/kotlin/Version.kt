// Copyright (C) 2024-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

import io.github.rhdunn.intellij.IntelliJVersion
import org.gradle.api.GradleException

@Suppress("ConstPropertyName")
object Version {
    /**
     * The IntelliJ platform type to default to unless overridden. See BuildConfiguration.
     */
    const val PlatformType = "IC"

    /**
     * The IntelliJ platform version to default to unless overridden. See BuildConfiguration.
     */
    const val PlatformVersion = "2025.1"

    /**
     * The version of the Kotlin compiler and runtime.
     *
     * `SPDX-License-Identifier: Apache-2.0`
     *
     * @see <a href="https://github.com/JetBrains/kotlin">https://github.com/JetBrains/kotlin</a>
     */
    @Suppress("FunctionName")
    fun Kotlin(intellij: IntelliJVersion): String = when {
        intellij.buildVersion >= 251 -> "2.1.10"
        intellij.buildVersion >= 242 -> "1.9.22"
        intellij.buildVersion >= 223 -> "1.8.22"
        else -> throw GradleException("Unsupported version of IntelliJ: $intellij")
    }

    /**
     * The version of Java.
     */
    @Suppress("FunctionName")
    fun Java(intellij: IntelliJVersion): Int = when {
        intellij.buildVersion >= 242 -> 21
        intellij.buildVersion >= 223 -> 17
        else -> throw GradleException("Unsupported version of IntelliJ: $intellij")
    }

    /**
     * Versions of the various plugins used by the project.
     */
    object Plugin {
        /**
         * The version of the `id("org.jetbrains.intellij.platform")` plugin.
         *
         * `SPDX-License-Identifier: Apache-2.0`
         *
         * @see <a href="https://github.com/JetBrains/intellij-platform-gradle-plugin">https://github.com/JetBrains/intellij-platform-gradle-plugin</a>
         */
        const val IntelliJPlatform = "2.7.0"
    }

    /**
     * Versions of the various libraries used by the project.
     */
    object Dependency {
        /**
         * The version of the `hamcrest` library.
         *
         * `SPDX-License-Identifier: BSD-3`
         *
         * @see <a href="https://github.com/hamcrest/JavaHamcrest">https://github.com/hamcrest/JavaHamcrest</a>
         */
        const val Hamcrest = "3.0"

        /**
         * A Java-based HTML 5/LS parser.
         *
         * `SPDX-License-Identifier: MPL-2.0`
         *
         * @see <a href="https://github.com/jhy/jsoup">https://github.com/jhy/jsoup</a>
         */
        const val JSoup = "1.15.4"

        /**
         * The version of the `junit` library.
         *
         * `SPDX-License-Identifier: EPL-1.0` (Eclipse Public License 1.0)
         *
         * @see <a href="https://github.com/junit-team/junit4">https://github.com/junit-team/junit4</a>
         */
        const val JUnit4 = "4.13"

        /**
         * The version of the `junit` library.
         *
         * `SPDX-License-Identifier: EPL-2.0` (Eclipse Public License 2.0)
         *
         * @see <a href="https://github.com/junit-team/junit5">https://github.com/junit-team/junit5</a>
         */
        const val JUnit5 = "5.9.1"

        /**
         * The version of the `junit` library.
         *
         * `SPDX-License-Identifier: EPL-2.0` (Eclipse Public License 2.0)
         *
         * @see <a href="https://github.com/junit-team/junit5">https://github.com/junit-team/junit5</a>
         */
        const val JUnitPlatform = "1.9.1"

        /**
         * The version of the Saxon HE XQuery/XSLT processor.
         *
         * `SPDX-License-Identifier: MPL-2.0`
         *
         * @see <a href="https://www.saxonica.com/download/java.xml">https://www.saxonica.com/download/java.xml</a>
         */
        const val SaxonHE = "9.9.1-7"
    }
}
