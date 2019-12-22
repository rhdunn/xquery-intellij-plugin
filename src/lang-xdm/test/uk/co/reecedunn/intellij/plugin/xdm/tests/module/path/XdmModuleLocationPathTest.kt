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
package uk.co.reecedunn.intellij.plugin.xdm.tests.module.path

import com.intellij.mock.MockProjectEx
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import uk.co.reecedunn.compat.testFramework.PlatformLiteFixture
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleLocationPath

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("Modules - Location Paths")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class XdmModuleLocationPathTest : PlatformLiteFixture() {
    private fun anyURI(path: String, context: XdmUriContext): XsAnyUriValue {
        return XsAnyUri(path, context, null as PsiElement?)
    }

    @BeforeAll
    override fun setUp() {
        super.setUp()
        initApplication()
        myProjectEx = MockProjectEx(testRootDisposable)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    @DisplayName("empty")
    fun empty() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("HTTP scheme URL")
    fun httpScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("http://www.example.com/lorem/ipsum", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.path, `is`("http://www.example.com/lorem/ipsum"))
                    assertThat(path.isResource, `is`(false))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Test
    @DisplayName("HTTPS scheme URL")
    fun httpsScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("https://www.example.com/lorem/ipsum", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.path, `is`("https://www.example.com/lorem/ipsum"))
                    assertThat(path.isResource, `is`(false))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Test
    @DisplayName("file scheme URL")
    fun fileScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("file:///C:/lorem/ipsum", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.path, `is`("file:///C:/lorem/ipsum"))
                    assertThat(path.isResource, `is`(false))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Test
    @DisplayName("URN scheme")
    fun urnScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("urn:lorem:ipsum", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("resource scheme")
    fun resourceScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("resource:org/lorem/ipsum.xqm", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.path, `is`("org/lorem/ipsum.xqm"))
                    assertThat(path.isResource, `is`(true))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("Java scheme")
    internal inner class JavaScheme {
        @Test
        @DisplayName("classpath")
        fun classpath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("java:java.lang.String", context)
                val path = XdmModuleLocationPath.create(myProject, uri)
                assertThat(path, `is`(nullValue()))
            }
        }

        @Test
        @DisplayName("classpath with void=this")
        fun voidThis() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("java:java.lang.String?void=this", context)
                val path = XdmModuleLocationPath.create(myProject, uri)
                assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Test
    @DisplayName("relative path")
    fun relativePath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("lorem/ipsum", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.path, `is`("lorem/ipsum"))
                    assertThat(path.isResource, `is`(false))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Test
    @DisplayName("MarkLogic database path")
    fun markLogicDatabasePath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("/lorem/ipsum.xqy", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.path, `is`("/lorem/ipsum.xqy"))
                    assertThat(path.isResource, `is`(false))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Test
    @DisplayName("eXist-db database path")
    fun eXistDBDatabasePath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("xmldb:exist:///db/modules/lorem/ipsum.xqm", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.path, `is`("/db/modules/lorem/ipsum.xqm"))
                    assertThat(path.isResource, `is`(false))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Test
    @DisplayName("Java class path")
    fun javaClassPath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("java.lang.String", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.path, `is`("java.lang.String"))
                    assertThat(path.isResource, `is`(false))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Test
    @DisplayName("Saxon java-type namespace")
    fun javaType() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("http://saxon.sf.net/java-type", context)
            val path = XdmModuleLocationPath.create(myProject, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.path, `is`("http://saxon.sf.net/java-type"))
                    assertThat(path.isResource, `is`(false))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }
}
