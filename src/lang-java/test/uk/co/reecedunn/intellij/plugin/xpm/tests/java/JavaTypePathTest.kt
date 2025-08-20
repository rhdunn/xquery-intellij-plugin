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
package uk.co.reecedunn.intellij.plugin.xpm.tests.java

import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
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
import uk.co.reecedunn.intellij.plugin.xpm.java.JavaTypePath

@Suppress("RedundantVisibilityModifier")
@DisplayName("Modules - Java Paths")
class JavaTypePathTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("JavaTypePathTest")

    override fun registerServicesAndExtensions() {
        project.registerService(JavaTypePath(project))
    }

    private fun anyURI(path: String, context: XdmUriContext): XsAnyUriValue {
        return XsAnyUri(path, context, XdmModuleType.NONE)
    }

    @Test
    @DisplayName("empty")
    fun empty() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("", context)
            val path = JavaTypePath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("HTTP scheme URL")
    fun httpScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("http://www.example.com/lorem/ipsum", context)
            val path = JavaTypePath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("HTTPS scheme URL")
    fun httpsScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("https://www.example.com/lorem/ipsum", context)
            val path = JavaTypePath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("file scheme URL")
    fun fileScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("file:///C:/lorem/ipsum", context)
            val path = JavaTypePath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("URN scheme")
    fun urnScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("urn:lorem:ipsum", context)
            val path = JavaTypePath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("resource scheme")
    fun resourceScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("resource:org/lorem/ipsum.xqm", context)
            val path = JavaTypePath.create(project, uri)
            assertThat(path, `is`(nullValue()))
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
                val path = JavaTypePath.create(project, uri)
                assertThat(path, `is`(nullValue()))
            }
        }

        @Test
        @DisplayName("classpath with void=this")
        fun voidThis() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("java:java.lang.String?void=this", context)
                val path = JavaTypePath.create(project, uri)
                assertThat(path, `is`(nullValue()))
            }
        }
    }

    @Test
    @DisplayName("relative path")
    fun relativePath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("lorem/ipsum", context)
            val path = JavaTypePath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("MarkLogic database path")
    fun markLogicDatabasePath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("/lorem/ipsum.xqy", context)
            val path = JavaTypePath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("eXist-db database path")
    fun eXistDBDatabasePath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("xmldb:exist:///db/modules/lorem/ipsum.xqm", context)
            val path = JavaTypePath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("Java class path")
    fun javaClassPath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("java.lang.String", context)
            val path = JavaTypePath.create(project, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("Saxon java-type namespace")
    fun javaType() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("http://saxon.sf.net/java-type", context)
            val path = JavaTypePath.create(project, uri)
            when (context) {
                XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(project)))
                    assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }
}
