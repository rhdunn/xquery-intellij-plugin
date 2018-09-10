/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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

package uk.co.reecedunn.intellij.plugin.xpath.tests.model

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.XsAnyURI
import uk.co.reecedunn.intellij.plugin.xdm.XsNCName
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginQuantifiedExprBinding
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableBinding
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableReference
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("UNCHECKED_CAST")
private class XPathModelTest : ParserTestCase() {
    // region Variables
    // region Param (XPathVariableBinding)

    @Test
    fun testParam_NCName() {
        val expr = parse<XPathParam>("function (\$x) {}")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testParam_QName() {
        val expr = parse<XPathParam>("function (\$a:x) {}")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testParam_URIQualifiedName() {
        val expr = parse<XPathParam>(
                "function (\$Q{http://www.example.com}x) {}")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testParam_MissingVarName() {
        val expr = parse<XPathParam>("function (\$) {}")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region QuantifiedExprBinding (XPathVariableBinding)

    @Test
    fun testQuantifiedExprBinding_NCName() {
        val expr = parse<PluginQuantifiedExprBinding>("some \$x in \$y satisfies \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testQuantifiedExprBinding_QName() {
        val expr = parse<PluginQuantifiedExprBinding>("some \$a:x in \$a:y satisfies \$a:z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testQuantifiedExprBinding_URIQualifiedName() {
        val expr = parse<PluginQuantifiedExprBinding>(
                "some \$Q{http://www.example.com}x in  \$Q{http://www.example.com}y satisfies \$Q{http://www.example.com}z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testQuantifiedExprBinding_MissingVarName() {
        val expr = parse<PluginQuantifiedExprBinding>("some \$")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region VarName (XPathVariableName)

    @Test
    fun testVarName_NCName() {
        val expr = parse<XPathVarName>("let \$x := 2 return \$y")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testVarName_QName() {
        val expr = parse<XPathVarName>("let \$a:x := 2 return \$a:y")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testVarName_URIQualifiedName() {
        val expr = parse<XPathVarName>("let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region VarRef (XPathVariableReference)

    @Test
    fun testVarRef_NCName() {
        val expr = parse<XPathVarRef>("let \$x := 2 return \$y")[0] as XPathVariableReference
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testVarRef_QName() {
        val expr = parse<XPathVarRef>("let \$a:x := 2 return \$a:y")[0] as XPathVariableReference
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.prefix?.staticType, `is`(XsNCName))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testVarRef_URIQualifiedName() {
        val expr = parse<XPathVarRef>("let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y")[0] as XPathVariableReference
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    @Test
    fun testVarRef_MissingVarName() {
        val expr = parse<XPathVarRef>("let \$x := 2 return \$")[0] as XPathVariableReference
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
}
