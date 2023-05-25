/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.tests.module.path

import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath

@DisplayName("Modules - Location Paths")
class XpmModuleLocationPathTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("XpmModuleLocationPathTest")

    @Suppress("PrivatePropertyName")
    private val TEST_MODULE_TYPES = arrayOf(XdmModuleType.DotNet) // A unique object to this test.

    private fun anyURI(path: String, context: XdmUriContext): XsAnyUriValue {
        return XsAnyUri(path, context, TEST_MODULE_TYPES)
    }

    @Test
    @DisplayName("empty")
    fun empty() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("", context)
            val path = XpmModuleLocationPath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("HTTP scheme URL")
    fun httpScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("http://www.example.com/lorem/ipsum", context)
            val path = XpmModuleLocationPath.create(project, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(project)))
                    assertThat(path.path, `is`("http://www.example.com/lorem/ipsum"))
                    assertThat(path.moduleTypes, `is`(TEST_MODULE_TYPES))
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
            val path = XpmModuleLocationPath.create(project, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(project)))
                    assertThat(path.path, `is`("https://www.example.com/lorem/ipsum"))
                    assertThat(path.moduleTypes, `is`(TEST_MODULE_TYPES))
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
            val path = XpmModuleLocationPath.create(project, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(project)))
                    assertThat(path.path, `is`("file:///C:/lorem/ipsum"))
                    assertThat(path.moduleTypes, `is`(TEST_MODULE_TYPES))
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
            val path = XpmModuleLocationPath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("resource scheme")
    fun resourceScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("resource:org/lorem/ipsum.xqm", context)
            val path = XpmModuleLocationPath.create(project, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(project)))
                    assertThat(path.path, `is`("org/lorem/ipsum.xqm"))
                    assertThat(path.moduleTypes, `is`(TEST_MODULE_TYPES))
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
                val path = XpmModuleLocationPath.create(project, uri)
                assertThat(path, `is`(nullValue()))
            }
        }

        @Test
        @DisplayName("classpath with void=this")
        fun voidThis() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("java:java.lang.String?void=this", context)
                val path = XpmModuleLocationPath.create(project, uri)
                assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Test
    @DisplayName("relative path")
    fun relativePath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("lorem/ipsum", context)
            val path = XpmModuleLocationPath.create(project, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(project)))
                    assertThat(path.path, `is`("lorem/ipsum"))
                    assertThat(path.moduleTypes, `is`(TEST_MODULE_TYPES))
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
            val path = XpmModuleLocationPath.create(project, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(project)))
                    assertThat(path.path, `is`("/lorem/ipsum.xqy"))
                    assertThat(path.moduleTypes, `is`(TEST_MODULE_TYPES))
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
            val path = XpmModuleLocationPath.create(project, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(project)))
                    assertThat(path.path, `is`("/db/modules/lorem/ipsum.xqm"))
                    assertThat(path.moduleTypes, `is`(TEST_MODULE_TYPES))
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
            val path = XpmModuleLocationPath.create(project, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(project)))
                    assertThat(path.path, `is`("java.lang.String"))
                    assertThat(path.moduleTypes, `is`(TEST_MODULE_TYPES))
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
            val path = XpmModuleLocationPath.create(project, uri)
            when (context) {
                XdmUriContext.Location -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(project)))
                    assertThat(path.path, `is`("http://saxon.sf.net/java-type"))
                    assertThat(path.moduleTypes, `is`(TEST_MODULE_TYPES))
                    assertThat(path.isResource, `is`(false))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }
}
