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
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import uk.co.reecedunn.compat.testFramework.PlatformLiteFixture
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.java.JavaModulePath
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUri

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("Modules - Java Paths")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class JavaModulePathTest : PlatformLiteFixture() {
    private var project: Project? = null

    @BeforeAll
    override fun setUp() {
        super.setUp()
        initApplication()
        project = MockProjectEx(testRootDisposable)
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    @DisplayName("empty")
    fun empty() {
        var uri = XsAnyUri("", XdmUriContext.TargetNamespace, null as PsiElement?)
        var path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("", XdmUriContext.NamespaceDeclaration, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("", XdmUriContext.Namespace, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("HTTP scheme URL")
    fun httpScheme() {
        var uri = XsAnyUri("http://www.example.com/lorem/ipsum", XdmUriContext.TargetNamespace, null as PsiElement?)
        var path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("http://www.example.com/lorem/ipsum", XdmUriContext.NamespaceDeclaration, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("http://www.example.com/lorem/ipsum", XdmUriContext.Namespace, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("HTTPS scheme URL")
    fun httpsScheme() {
        var uri = XsAnyUri("https://www.example.com/lorem/ipsum", XdmUriContext.TargetNamespace, null as PsiElement?)
        var path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("https://www.example.com/lorem/ipsum", XdmUriContext.NamespaceDeclaration, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("https://www.example.com/lorem/ipsum", XdmUriContext.Namespace, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("file scheme URL")
    fun fileScheme() {
        var uri = XsAnyUri("file:///C:/lorem/ipsum", XdmUriContext.TargetNamespace, null as PsiElement?)
        var path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("file:///C:/lorem/ipsum", XdmUriContext.NamespaceDeclaration, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("file:///C:/lorem/ipsum", XdmUriContext.Namespace, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Test
    @DisplayName("URN scheme")
    fun urnScheme() {
        var uri = XsAnyUri("urn:lorem:ipsum", XdmUriContext.TargetNamespace, null as PsiElement?)
        var path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("urn:lorem:ipsum", XdmUriContext.NamespaceDeclaration, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("urn:lorem:ipsum", XdmUriContext.Namespace, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Nested
    @DisplayName("Java scheme")
    internal inner class JavaScheme {
        @Test
        @DisplayName("classpath")
        fun classpath() {
            var uri = XsAnyUri("java:java.lang.String", XdmUriContext.TargetNamespace, null as PsiElement?)
            var path = JavaModulePath.create(project!!, uri)!!
            assertThat(path.project, `is`(sameInstance(project)))
            assertThat(path.classPath, `is`("java.lang.String"))
            assertThat(path.voidThis, `is`(false))

            uri = XsAnyUri("java:java.lang.String", XdmUriContext.NamespaceDeclaration, null as PsiElement?)
            path = JavaModulePath.create(project!!, uri)!!
            assertThat(path.project, `is`(sameInstance(project)))
            assertThat(path.classPath, `is`("java.lang.String"))
            assertThat(path.voidThis, `is`(false))

            uri = XsAnyUri("java:java.lang.String", XdmUriContext.Namespace, null as PsiElement?)
            path = JavaModulePath.create(project!!, uri)!!
            assertThat(path.project, `is`(sameInstance(project)))
            assertThat(path.classPath, `is`("java.lang.String"))
            assertThat(path.voidThis, `is`(false))
        }

        @Test
        @DisplayName("classpath with void=this")
        fun voidThis() {
            var uri = XsAnyUri("java:java.lang.String?void=this", XdmUriContext.TargetNamespace, null as PsiElement?)
            var path = JavaModulePath.create(project!!, uri)!!
            assertThat(path.project, `is`(sameInstance(project)))
            assertThat(path.classPath, `is`("java.lang.String"))
            assertThat(path.voidThis, `is`(true))

            uri = XsAnyUri("java:java.lang.String?void=this", XdmUriContext.NamespaceDeclaration, null as PsiElement?)
            path = JavaModulePath.create(project!!, uri)!!
            assertThat(path.project, `is`(sameInstance(project)))
            assertThat(path.classPath, `is`("java.lang.String"))
            assertThat(path.voidThis, `is`(true))

            uri = XsAnyUri("java:java.lang.String?void=this", XdmUriContext.Namespace, null as PsiElement?)
            path = JavaModulePath.create(project!!, uri)!!
            assertThat(path.project, `is`(sameInstance(project)))
            assertThat(path.classPath, `is`("java.lang.String"))
            assertThat(path.voidThis, `is`(true))
        }
    }

    @Test
    @DisplayName("relative path")
    fun relativePath() {
        var uri = XsAnyUri("lorem/ipsum", XdmUriContext.TargetNamespace, null as PsiElement?)
        var path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("lorem/ipsum", XdmUriContext.NamespaceDeclaration, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))

        uri = XsAnyUri("lorem/ipsum", XdmUriContext.Namespace, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)
        assertThat(path, `is`(nullValue()))
    }

    @Nested
    @DisplayName("location paths")
    internal inner class Location {
        @Test
        @DisplayName("XQuery file, 'xq' extension")
        fun xq() {
            val uri = XsAnyUri("test.xq", XdmUriContext.Location, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xqy' extension")
        fun xqy() {
            val uri = XsAnyUri("test.xqy", XdmUriContext.Location, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xquery' extension")
        fun xquery() {
            val uri = XsAnyUri("test.xquery", XdmUriContext.Location, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xqu' extension")
        fun xqu() {
            val uri = XsAnyUri("test.xqu", XdmUriContext.Location, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xql' extension")
        fun xql() {
            val uri = XsAnyUri("test.xql", XdmUriContext.Location, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xqm' extension")
        fun xqm() {
            val uri = XsAnyUri("test.xqm", XdmUriContext.Location, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XQuery file, 'xqws' extension")
        fun xqws() {
            val uri = XsAnyUri("test.xqws", XdmUriContext.Location, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XML Stylesheet file, 'xsl' extension")
        fun xsl() {
            val uri = XsAnyUri("test.xsl", XdmUriContext.Location, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XML Stylesheet file, 'xslt' extension")
        fun xslt() {
            val uri = XsAnyUri("test.xslt", XdmUriContext.Location, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }

        @Test
        @DisplayName("XML Schema file, 'xsd' extension")
        fun xsd() {
            val uri = XsAnyUri("test.xsd", XdmUriContext.Location, null as PsiElement?)
            val path = JavaModulePath.create(project!!, uri)
            assertThat(path, `is`(nullValue()))
        }
    }

    @Test
    @DisplayName("Java class path")
    fun javaClassPath() {
        var uri = XsAnyUri("java.lang.String", XdmUriContext.TargetNamespace, null as PsiElement?)
        var path = JavaModulePath.create(project!!, uri)!!
        assertThat(path.project, `is`(sameInstance(project)))
        assertThat(path.classPath, `is`("java.lang.String"))
        assertThat(path.voidThis, `is`(false))

        uri = XsAnyUri("java.lang.String", XdmUriContext.NamespaceDeclaration, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)!!
        assertThat(path.project, `is`(sameInstance(project)))
        assertThat(path.classPath, `is`("java.lang.String"))
        assertThat(path.voidThis, `is`(false))

        uri = XsAnyUri("java.lang.String", XdmUriContext.Namespace, null as PsiElement?)
        path = JavaModulePath.create(project!!, uri)!!
        assertThat(path.project, `is`(sameInstance(project)))
        assertThat(path.classPath, `is`("java.lang.String"))
        assertThat(path.voidThis, `is`(false))
    }
}
