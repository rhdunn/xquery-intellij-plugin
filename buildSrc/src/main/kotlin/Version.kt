// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

import io.github.rhdunn.intellij.IntelliJVersion
import org.gradle.api.GradleException

object Version {
    /**
     * The version of the IntelliJ IDE the plugin supports.
     *
     * `SPDX-License-Identifier: Apache-2.0`
     */
    val IntelliJ: IntelliJVersion = IntelliJVersion(System.getenv("IDEA_VERSION") ?: "2024.2")

    /**
     * The version of the Kotlin compiler and runtime.
     *
     * `SPDX-License-Identifier: Apache-2.0`
     *
     * @see <a href="https://github.com/JetBrains/kotlin">https://github.com/JetBrains/kotlin</a>
     */
    val Kotlin: String = when {
        IntelliJ.buildVersion >= 242 -> "1.9.22"
        IntelliJ.buildVersion >= 223 -> "1.8.22"
        else -> throw GradleException("Unsupported version of IntelliJ: $IntelliJ")
    }

    /**
     * The version of Java.
     */
    val Java: Int = when {
        IntelliJ.buildVersion >= 242 -> 21
        IntelliJ.buildVersion >= 223 -> 17
        else -> throw GradleException("Unsupported version of IntelliJ: $IntelliJ")
    }
}
