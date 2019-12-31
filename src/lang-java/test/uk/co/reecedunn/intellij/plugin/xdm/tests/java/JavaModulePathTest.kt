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
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import uk.co.reecedunn.compat.testFramework.PlatformLiteFixture
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.java.JavaModulePath
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("Modules - Java Paths")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class JavaModulePathTest : PlatformLiteFixture() {
    private fun anyURI(path: String, context: XdmUriContext): XsAnyUriValue {
        return XsAnyUri(path, context, XdmModuleType.NONE, null as PsiElement?)
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
            val path = JavaModulePath.create(myProject, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("HTTP scheme")
    internal inner class HttpScheme {
        @Test
        @DisplayName("scheme only")
        fun schemeOnly() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("http://", context)
                val path = JavaModulePath.create(myProject, uri)
                assertThat(path, `is`(nullValue()))
            }
        }

        @Test
        @DisplayName("with path")
        fun withPath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("http://www.example.com/lorem/ipsum", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("com.example.www.lorem.Ipsum"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("with dots in path")
        fun withDotsInPath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("http://www.example.com/lorem.ipsum", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("com.example.www.lorem.Ipsum"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("with replacement characters in path")
        fun withReplacementCharactersInPath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("http://www.example.com/lorem^ipsum\$dolor12sed*emit", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("com.example.www.LoremIpsumDolor12sedEmit"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("with path, trailing slash")
        fun withPathTrailingSlash() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("http://www.example.com/lorem/ipsum/", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("com.example.www.lorem.ipsum.Index"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("without path")
        fun withoutPath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("http://www.example.com", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("com.example.www.Index"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }
    }

    @Nested
    @DisplayName("HTTPS scheme")
    internal inner class HttpsScheme {
        @Test
        @DisplayName("scheme only")
        fun schemeOnly() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("https://", context)
                val path = JavaModulePath.create(myProject, uri)
                assertThat(path, `is`(nullValue()))
            }
        }

        @Test
        @DisplayName("with path")
        fun withPath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("https://www.example.com/lorem/ipsum", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("com.example.www.lorem.Ipsum"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("with dots in path")
        fun withDotsInPath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("https://www.example.com/lorem.ipsum", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("com.example.www.lorem.Ipsum"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("with replacement characters in path")
        fun withReplacementCharactersInPath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("https://www.example.com/lorem^ipsum\$dolor12sed*emit", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("com.example.www.LoremIpsumDolor12sedEmit"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("with path, trailing slash")
        fun withPathTrailingSlash() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("https://www.example.com/lorem/ipsum/", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("com.example.www.lorem.ipsum.Index"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("without path")
        fun withoutPath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("https://www.example.com", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("com.example.www.Index"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }
    }

    @Test
    @DisplayName("file scheme URL")
    fun fileScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("file:///C:/lorem/ipsum", context)
            val path = JavaModulePath.create(myProject, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("URN scheme")
    internal inner class UrnScheme {
        @Test
        @DisplayName("urn")
        fun urn() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("urn:lorem:ipsum", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("urn.lorem.Ipsum"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("with replacement characters in path")
        fun withReplacementCharactersInPath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("urn:a:b:lorem^ipsum\$dolor12sed*emit", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("urn.a.b.LoremIpsumDolor12sedEmit"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }
    }

    @Test
    @DisplayName("resource scheme")
    fun resourceScheme() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("resource:org/lorem/ipsum.xqm", context)
            val path = JavaModulePath.create(myProject, uri)
            when (context) {
                XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                    assertThat(path.classPath, `is`("resource.org.lorem.ipsum.Xqm"))
                    assertThat(path.voidThis, `is`(false))
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
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("java.lang.String"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("classpath with void=this")
        fun voidThis() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("java:java.lang.String?void=this", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("java.lang.String"))
                        assertThat(path.voidThis, `is`(true))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }
    }

    @Nested
    @DisplayName("relative path")
    internal inner class RelativePath {
        @Test
        @DisplayName("relative path")
        fun relativePath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("lorem/ipsum", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("lorem.Ipsum"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("trailing slash")
        fun trailingSlash() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("lorem/ipsum/", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("lorem.ipsum.Index"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("with replacement characters in path")
        fun withReplacementCharactersInPath() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("a/b/lorem^ipsum\$dolor12sed*emit", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("a.b.LoremIpsumDolor12sedEmit"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }

        @Test
        @DisplayName("local file")
        fun localFile() {
            XdmUriContext.values().forEach { context ->
                val uri = anyURI("lorem.xqy", context)
                val path = JavaModulePath.create(myProject, uri)
                when (context) {
                    XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                        assertThat(path, `is`(notNullValue()))
                        assertThat(path!!.project, `is`(sameInstance(myProject)))
                        assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                        assertThat(path.classPath, `is`("lorem.xqy"))
                        assertThat(path.voidThis, `is`(false))
                    }
                    else -> assertThat(path, `is`(nullValue()))
                }
            }
        }
    }

    @Test
    @DisplayName("MarkLogic database path")
    fun markLogicDatabasePath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("/lorem/ipsum.xqy", context)
            val path = JavaModulePath.create(myProject, uri)
            when (context) {
                XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                    assertThat(path.classPath, `is`("lorem.ipsum.Xqy"))
                    assertThat(path.voidThis, `is`(false))
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
            val path = JavaModulePath.create(myProject, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("Java class path")
    fun javaClassPath() {
        XdmUriContext.values().forEach { context ->
            val uri = anyURI("java.lang.String", context)
            val path = JavaModulePath.create(myProject, uri)
            when (context) {
                XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                    assertThat(path.classPath, `is`("java.lang.String"))
                    assertThat(path.voidThis, `is`(false))
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
            val path = JavaModulePath.create(myProject, uri)
            when (context) {
                XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                    assertThat(path, `is`(notNullValue()))
                    assertThat(path!!.project, `is`(sameInstance(myProject)))
                    assertThat(path.moduleTypes, `is`(sameInstance(XdmModuleType.JAVA)))
                    assertThat(path.classPath, `is`("net.sf.saxon.JavaType"))
                    assertThat(path.voidThis, `is`(false))
                }
                else -> assertThat(path, `is`(nullValue()))
            }
        }
    }
}
