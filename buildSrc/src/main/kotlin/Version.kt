// Copyright (C) 2024-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

import io.github.rhdunn.intellij.IntelliJVersion
import org.gradle.api.GradleException

object Version {
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
     * Versions of the various libraries used by the project.
     */
    object Dependency {
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
