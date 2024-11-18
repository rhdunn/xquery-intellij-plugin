// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package io.github.rhdunn.test.intellij

import io.github.rhdunn.intellij.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("The IntelliJVersionNumber class")
class IntelliJVersionNumberTest {
    @Test
    fun `should parse year-release version numbers`() {
        val build = IntelliJVersionNumber.parse("2021.3")
        assertEquals(IntelliJVersionNumber("2021.3", null, "2021.3", 2021, 3, null), build)
        assertEquals("2021.3", build.toString())
    }

    @Test
    fun `should parse year-release-build version numbers`() {
        val build = IntelliJVersionNumber.parse("2017.2.4")
        assertEquals(IntelliJVersionNumber("2017.2.4", null, "2017.2.4", 2017, 2, 4), build)
        assertEquals("2017.2.4", build.toString())
    }

    @Test
    fun `should parse product-year-release version numbers`() {
        val build = IntelliJVersionNumber.parse("IC-2021.3")
        assertEquals(IntelliJVersionNumber("IC-2021.3", "IC", "2021.3", 2021, 3, null), build)
        assertEquals("IC-2021.3", build.toString())
    }

    @Test
    fun `should parse product-year-release-build version numbers`() {
        val build = IntelliJVersionNumber.parse("IC-2017.3.4")
        assertEquals(IntelliJVersionNumber("IC-2017.3.4", "IC", "2017.3.4", 2017, 3, 4), build)
        assertEquals("IC-2017.3.4", build.toString())
    }
}

@DisplayName("The IntelliJBuildNumber class")
class IntelliJBuildNumberTest {
    @Test
    fun `should parse version numbers without platform type`() {
        val build = IntelliJBuildNumber.parse("221.5080.210")
        assertEquals(IntelliJBuildNumber("221.5080.210", null, "221.5080.210", 221, 5080, 210), build)
        assertEquals("221.5080.210", build.toString())
    }

    @Test
    fun `should parse version numbers with platform type`() {
        val build = IntelliJBuildNumber.parse("IC-221.5080.210")
        assertEquals(IntelliJBuildNumber("IC-221.5080.210", "IC", "221.5080.210", 221, 5080, 210), build)
        assertEquals("IC-221.5080.210", build.toString())
    }
}
