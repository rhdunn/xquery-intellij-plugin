/*
 * Copyright (C) 2019 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.xdm.tests.java

import com.intellij.mock.MockProjectEx
import com.intellij.testFramework.UsefulTestCase
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.java.JavaModulePath

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("Modules - Java Paths")
private class JavaModulePathTest : UsefulTestCase() {
    private val project = MockProjectEx(testRootDisposable)

    @Test
    @DisplayName("HTTP scheme URL")
    fun httpScheme() {
        val path = JavaModulePath.create(project, "http://www.example.com/lorem/ipsum")
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("HTTPS scheme URL")
    fun httpsScheme() {
        val path = JavaModulePath.create(project, "https://www.example.com/lorem/ipsum")
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("file scheme URL")
    fun fileScheme() {
        val path = JavaModulePath.create(project, "file:///C:/lorem/ipsum")
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("URN scheme")
    fun urnScheme() {
        val path = JavaModulePath.create(project, "urn:lorem:ipsum")
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("Java scheme")
    fun javaScheme() {
        val path = JavaModulePath.create(project, "java:java.lang.String")!!
        assertThat(path.classPath, `is`("java.lang.String"))
    }

    @Test
    @DisplayName("relative path")
    fun relativePath() {
        val path = JavaModulePath.create(project, "lorem/ipsum")
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("Java class path")
    fun javaClassPath() {
        val path = JavaModulePath.create(project, "java.lang.String")
        assertThat(path, `is`(nullValue()))
    }
}
