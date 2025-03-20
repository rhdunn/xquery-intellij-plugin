// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

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
}
