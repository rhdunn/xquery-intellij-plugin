/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lang.highlighter

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarRef
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference
import uk.co.reecedunn.intellij.plugin.xquery.lang.highlighter.XQueryReadWriteAccessDetector
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryVariableProvider
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - Read/Write Usage Detector - XQuery")
class XQueryReadWriteAccessDetectorTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryReadWriteAccessDetectorTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
        XpmVariableProvider.register(this, XQueryVariableProvider)
    }

    val detector: ReadWriteAccessDetector = XQueryReadWriteAccessDetector()

    fun variable(text: String): Pair<PsiElement, PsiReference> {
        val module = parseText(text)
        val call = module.walkTree().filterIsInstance<XPathVarRef>().first() as XpmVariableReference
        val element = call.variableName?.element!! as XPathEQName
        val references = element.references
        return when (references.size) {
            1 -> element to references[0] // NCName
            2 -> element to references[1] // QName
            else -> throw ArrayIndexOutOfBoundsException()
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (28) VarDecl")
    internal inner class VarDecl {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val ref = variable("declare variable \$x := 2; \$x")
            val decl = ref.second.resolve()!!

            assertThat(detector.isReadWriteAccessible(decl), `is`(true))
            assertThat(detector.isDeclarationWriteAccess(decl), `is`(true))

            assertThat(detector.isReadWriteAccessible(decl.parent), `is`(false))
            assertThat(detector.isDeclarationWriteAccess(decl.parent), `is`(true))

            assertThat(detector.getReferenceAccess(decl, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(decl), `is`(Access.Write))

            assertThat(detector.getReferenceAccess(decl.parent, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(decl.parent), `is`(Access.Read))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val ref = variable("declare variable \$local:x := 2; \$local:x")
            val decl = ref.second.resolve()!!

            assertThat(detector.isReadWriteAccessible(decl), `is`(true))
            assertThat(detector.isDeclarationWriteAccess(decl), `is`(true))

            assertThat(detector.isReadWriteAccessible(decl.parent), `is`(false))
            assertThat(detector.isDeclarationWriteAccess(decl.parent), `is`(true))

            assertThat(detector.getReferenceAccess(decl, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(decl), `is`(Access.Write))

            assertThat(detector.getReferenceAccess(decl.parent, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(decl.parent), `is`(Access.Read))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (34) Param")
    internal inner class Param {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val ref = variable("declare function f(\$x){ \$x }; f(2)")
            val decl = ref.second.resolve()!!

            assertThat(detector.isReadWriteAccessible(decl), `is`(true))
            assertThat(detector.isDeclarationWriteAccess(decl), `is`(true))

            assertThat(detector.isReadWriteAccessible(decl.parent), `is`(false))
            assertThat(detector.isDeclarationWriteAccess(decl.parent), `is`(true))

            assertThat(detector.getReferenceAccess(decl, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(decl), `is`(Access.Write))

            assertThat(detector.getReferenceAccess(decl.parent, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(decl.parent), `is`(Access.Read))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val ref = variable("declare function f(\$local:x){ \$local:x }; f(2)")
            val decl = ref.second.resolve()!!

            assertThat(detector.isReadWriteAccessible(decl), `is`(true))
            assertThat(detector.isDeclarationWriteAccess(decl), `is`(true))

            assertThat(detector.isReadWriteAccessible(decl.parent), `is`(false))
            assertThat(detector.isDeclarationWriteAccess(decl.parent), `is`(true))

            assertThat(detector.getReferenceAccess(decl, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(decl), `is`(Access.Write))

            assertThat(detector.getReferenceAccess(decl.parent, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(decl.parent), `is`(Access.Read))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (131) VarRef")
    internal inner class VarRef {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val ref = variable("declare variable \$x := 2; \$x")

            assertThat(detector.isReadWriteAccessible(ref.first), `is`(true))
            assertThat(detector.isDeclarationWriteAccess(ref.first), `is`(true))

            assertThat(detector.isReadWriteAccessible(ref.first.parent), `is`(false))
            assertThat(detector.isDeclarationWriteAccess(ref.first.parent), `is`(true))

            assertThat(detector.getReferenceAccess(ref.first, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(ref.first), `is`(Access.Read))

            assertThat(detector.getReferenceAccess(ref.first.parent, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(ref.first.parent), `is`(Access.Read))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val ref = variable("declare variable \$local:x := 2; \$local:x")

            assertThat(detector.isReadWriteAccessible(ref.first), `is`(true))
            assertThat(detector.isDeclarationWriteAccess(ref.first), `is`(true))

            assertThat(detector.isReadWriteAccessible(ref.first.parent), `is`(false))
            assertThat(detector.isDeclarationWriteAccess(ref.first.parent), `is`(true))

            assertThat(detector.getReferenceAccess(ref.first, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(ref.first), `is`(Access.Read))

            assertThat(detector.getReferenceAccess(ref.first.parent, ref.second), `is`(Access.Read))
            assertThat(detector.getExpressionAccess(ref.first.parent), `is`(Access.Read))
        }
    }
}
