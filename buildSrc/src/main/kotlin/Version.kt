// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0

import io.github.rhdunn.intellij.IntelliJVersion

object Version {
    /**
     * The version of the IntelliJ IDE the plugin supports.
     *
     * `SPDX-License-Identifier: Apache-2.0`
     */
    val IntelliJ: IntelliJVersion = IntelliJVersion(System.getenv("IDEA_VERSION") ?: "2024.2")
}
