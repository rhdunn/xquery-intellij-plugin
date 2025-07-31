// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package io.github.rhdunn.intellij

/**
 * The build type associated with the project version.
 *
 * @param suffix The suffix applied to the version tag.
 */
enum class BuildType(val suffix: String) {
    /**
     * A development snapshot build. Takes precedence over `EAPn` and `Release` builds.
     */
    Snapshot("-snapshot"),

    /**
     * An early access preview build. Takes precedence over `Release` builds.
     */
    EAP1("-eap-1"),

    /**
     * An early access preview build. Takes precedence over `EAP1` and `Release` builds.
     */
    EAP2("-eap-2"),

    /**
     * A release build.
     */
    Release(""),
}
