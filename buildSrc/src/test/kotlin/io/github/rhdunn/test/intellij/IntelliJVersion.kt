// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package io.github.rhdunn.test.intellij

import io.github.rhdunn.intellij.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DisplayName("The IntelliJVersionNumber class")
class IntelliJVersionNumberTest {
    @Test
    fun `should parse year-release version numbers`() {
        val build = IntelliJVersionNumber.parse("2021.3")!!
        assertEquals(IntelliJVersionNumber("2021.3", null, "2021.3", 2021, 3, null), build)
        assertEquals("2021.3", build.toString())

        assertNull(build.platformType)
        assertEquals("2021.3", build.platformVersion)
    }

    @Test
    fun `should parse year-release-build version numbers`() {
        val build = IntelliJVersionNumber.parse("2017.2.4")!!
        assertEquals(IntelliJVersionNumber("2017.2.4", null, "2017.2.4", 2017, 2, 4), build)
        assertEquals("2017.2.4", build.toString())

        assertNull(build.platformType)
        assertEquals("2017.2.4", build.platformVersion)
    }

    @Test
    fun `should parse product-year-release version numbers`() {
        val build = IntelliJVersionNumber.parse("IC-2021.3")!!
        assertEquals(IntelliJVersionNumber("IC-2021.3", "IC", "2021.3", 2021, 3, null), build)
        assertEquals("IC-2021.3", build.toString())

        assertEquals("IC", build.platformType)
        assertEquals("2021.3", build.platformVersion)
    }

    @Test
    fun `should parse product-year-release-build version numbers`() {
        val build = IntelliJVersionNumber.parse("IC-2017.3.4")!!
        assertEquals(IntelliJVersionNumber("IC-2017.3.4", "IC", "2017.3.4", 2017, 3, 4), build)
        assertEquals("IC-2017.3.4", build.toString())

        assertEquals("IC", build.platformType)
        assertEquals("2017.3.4", build.platformVersion)
    }
}

@DisplayName("The IntelliJBuildNumber class")
class IntelliJBuildNumberTest {
    @Test
    fun `should parse version numbers without platform type`() {
        val build = IntelliJBuildNumber.parse("221.5080.210")!!
        assertEquals(IntelliJBuildNumber("221.5080.210", null, "221.5080.210", 221, 5080, 210), build)
        assertEquals("221.5080.210", build.toString())

        assertNull(build.platformType)
        assertEquals("221.5080.210", build.platformVersion)
    }

    @Test
    fun `should parse version numbers with platform type`() {
        val build = IntelliJBuildNumber.parse("IC-221.5080.210")!!
        assertEquals(IntelliJBuildNumber("IC-221.5080.210", "IC", "221.5080.210", 221, 5080, 210), build)
        assertEquals("IC-221.5080.210", build.toString())

        assertEquals("IC", build.platformType)
        assertEquals("221.5080.210", build.platformVersion)
    }
}

@DisplayName("The IntelliJSnapshot class")
class IntelliJSnapshotTest {
    @Test
    fun `should parse versioned eap snapshots`() {
        val build = IntelliJSnapshot.parse("242-EAP-SNAPSHOT")!!
        assertEquals(build.value, "242-EAP-SNAPSHOT")
        assertEquals("242-EAP-SNAPSHOT", build.toString())

        val versionFile = File("build/BUILD-242-EAP-SNAPSHOT.txt")
        assertTrue(versionFile.exists(), "The ${versionFile.path} file exists.")

        val buildVersion = versionFile.readLines()[0]
        assertEquals(buildVersion, build.build.toString())

        assertNull(build.platformType)
        assertEquals(buildVersion, build.platformVersion)
    }

    @Test
    fun `should parse latest eap snapshots`() {
        val build = IntelliJSnapshot.parse("LATEST-EAP-SNAPSHOT")!!
        assertEquals(build.value, "LATEST-EAP-SNAPSHOT")
        assertEquals("LATEST-EAP-SNAPSHOT", build.toString())

        val versionFile = File("build/BUILD-LATEST-EAP-SNAPSHOT.txt")
        assertTrue(versionFile.exists(), "The ${versionFile.path} file exists.")

        val buildVersion = versionFile.readLines()[0]
        assertEquals(buildVersion, build.build.toString())

        assertNull(build.platformType)
        assertEquals(buildVersion, build.platformVersion)
    }
}
