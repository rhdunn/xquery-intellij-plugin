/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.xdm

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmConstantExpression
import uk.co.reecedunn.intellij.plugin.xdm.model.toInt
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XPathXdmTest : ParserTestCase() {
    private inline fun <reified T> parseLiteral(xquery: String): XdmLexicalValue {
        return parseText(xquery)!!
                .walkTree().filterIsInstance<T>()
                .first() as XdmLexicalValue
    }

    private inline fun <reified T> parseSimpleExpression(xquery: String): List<XdmConstantExpression> {
        return parseText(xquery)!!
                .walkTree().filterIsInstance<T>()
                .map { type -> type as XdmConstantExpression }
                .toList()
    }

    // region Lexical Values
    // region BracedUriLiteral (XdmLexicalValue)

    fun testBracedUriLiteral() {
        val literal = parseLiteral<XPathBracedURILiteral>("Q{http://www.example.com\uFFFF}")
        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com\uFFFF"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testBracedUriLiteral_Unclosed() {
        val literal = parseLiteral<XPathBracedURILiteral>("Q{http://www.example.com")
        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    // endregion
    // region DecimalLiteral (XdmLexicalValue)

    fun testDecimalLiteral() {
        val literal = parseLiteral<XPathDecimalLiteral>("12.34")
        assertThat(literal.lexicalRepresentation, `is`("12.34"))
        assertThat(literal.staticType, `is`(XsDecimal as XdmSequenceType))
    }

    // endregion
    // region DoubleLiteral (XdmLexicalValue)

    fun testDoubleLiteral() {
        val literal = parseLiteral<XPathDoubleLiteral>("1e3")
        assertThat(literal.lexicalRepresentation, `is`("1e3"))
        assertThat(literal.staticType, `is`(XsDouble as XdmSequenceType))
    }

    // endregion
    // region IntegerLiteral (XdmLexicalValue)

    fun testIntegerLiteral() {
        val literal = parseLiteral<XPathIntegerLiteral>("123")
        assertThat(literal.lexicalRepresentation, `is`("123"))
        assertThat(literal.staticType, `is`(XsInteger as XdmSequenceType))
        assertThat(literal.toInt(), `is`(123))
    }

    // endregion
    // region Literal (XdmLexicalValue)

    fun testLiteral_DoubleLiteral() {
        val literal = parseLiteral<XPathLiteral>("1e3")
        assertThat(literal.lexicalRepresentation, `is`("1e3"))
        assertThat(literal.staticType, `is`(XsDouble as XdmSequenceType))
    }

    fun testLiteral_DecimalLiteral() {
        val literal = parseLiteral<XPathLiteral>("12.34")
        assertThat(literal.lexicalRepresentation, `is`("12.34"))
        assertThat(literal.staticType, `is`(XsDecimal as XdmSequenceType))
    }

    fun testLiteral_IntegerLiteral() {
        val literal = parseLiteral<XPathLiteral>("123")
        assertThat(literal.lexicalRepresentation, `is`("123"))
        assertThat(literal.staticType, `is`(XsInteger as XdmSequenceType))
    }

    fun testLiteral_StringLiteral() {
        val literal = parseLiteral<XPathLiteral>("\"Lorem ipsum.\"")
        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum."))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    // endregion
    // region StringLiteral (XdmLexicalValue)

    fun testStringLiteral() {
        val literal = parseLiteral<XPathStringLiteral>("\"Lorem ipsum.\uFFFF\"")
        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    fun testStringLiteral_Unclosed() {
        val literal = parseLiteral<XPathStringLiteral>("\"Lorem ipsum.")
        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum."))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    fun testStringLiteral_EscapeApos() {
        val literal = parseLiteral<XPathStringLiteral>("'''\"\"'")
        assertThat(literal.lexicalRepresentation, `is`("'\"\""))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    fun testStringLiteral_EscapeQuot() {
        val literal = parseLiteral<XPathStringLiteral>("\"''\"\"\"")
        assertThat(literal.lexicalRepresentation, `is`("''\""))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    // endregion
    // endregion
    // region Lexical and Expanded QNames
    // region NCName (XdmConstantExpression)

    fun testNCName() {
        val expr = parseSimpleExpression<XPathNCName>("test")[0]
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(qname.toString(), `is`("test"))
    }

    fun testNCName_Keyword() {
        val expr = parseSimpleExpression<XPathNCName>("option")[0]
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("option"))

        assertThat(qname.toString(), `is`("option"))
    }

    // endregion
    // region QName (XdmConstantExpression)

    fun testQName() {
        val expr = parseSimpleExpression<XPathQName>("fn:true")[0]
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("fn"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("true"))

        assertThat(qname.toString(), `is`("fn:true"))
    }

    fun testQName_KeywordPrefix() {
        val expr = parseSimpleExpression<XPathQName>("option:test")[0]
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("option"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(qname.toString(), `is`("option:test"))
    }

    fun testQName_KeywordLocalName() {
        val expr = parseSimpleExpression<XPathQName>("test:case")[0]
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("test"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("case"))

        assertThat(qname.toString(), `is`("test:case"))
    }

    fun testQName_NoLocalName() {
        val expr = parseSimpleExpression<XPathQName>("xs:")[0]
        assertThat(expr.constantValue, `is`(nullValue()))
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
    }

    // endregion
    // region URIQualifiedName (XdmConstantExpression)

    fun testURIQualifiedName() {
        val expr = parseSimpleExpression<XPathURIQualifiedName>("Q{http://www.example.com}test")[0]
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(qname.toString(), `is`("Q{http://www.example.com}test"))
    }

    fun testURIQualifiedName_EmptyNamespace() {
        val expr = parseSimpleExpression<XPathURIQualifiedName>("Q{}test")[0]
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`(""))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(qname.toString(), `is`("Q{}test"))
    }

    fun testURIQualifiedName_KeywordLocalName() {
        val expr = parseSimpleExpression<XPathURIQualifiedName>("Q{http://www.example.com}option")[0]
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("option"))

        assertThat(qname.toString(), `is`("Q{http://www.example.com}option"))
    }

    fun testURIQualifiedName_NoLocalName() {
        val expr = parseSimpleExpression<XPathURIQualifiedName>("Q{http://www.example.com}")[0]
        assertThat(expr.constantValue, `is`(nullValue()))
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
    }

    // endregion
    // region VarName (XdmConstantExpression)

    fun testVarName_NCName() {
        val expr = parseSimpleExpression<XPathVarName>("let \$x := 2 return \$y")[0]
        val name = (expr as PsiElement).firstChild as XPathNCName

        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmConstantExpression))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))
    }

    fun testVarName_QName() {
        val expr = parseSimpleExpression<XPathVarName>("let \$a:x := 2 return \$a:y")[0]
        val name = (expr as PsiElement).firstChild as XPathQName

        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))

        val qname = expr.constantValue as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmConstantExpression))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))
    }

    fun testVarName_URIQualifiedName() {
        val expr = parseSimpleExpression<XPathVarName>("let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y")[0]
        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName

        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmConstantExpression))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))
    }

    // endregion
    // endregion
    // region Variables
    // region VarRef (XdmConstantExpression)

    fun testVarRef_NCName() {
        val expr = parseSimpleExpression<XPathVarRef>("let \$x := 2 return \$y")[0]
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmConstantExpression))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("y"))
    }

    fun testVarRef_QName() {
        val expr = parseSimpleExpression<XPathVarRef>("let \$a:x := 2 return \$a:y")[0]
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.constantValue as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmConstantExpression))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("y"))
    }

    fun testVarRef_URIQualifiedName() {
        val expr = parseSimpleExpression<XPathVarRef>("let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y")[0]
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmConstantExpression))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("y"))
    }

    fun testVarRef_MissingVarName() {
        val expr = parseSimpleExpression<XPathVarRef>("let \$x := 2 return \$")[0]
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
        assertThat(expr.constantValue, `is`(nullValue()))
    }

    // endregion
    // endregion
    // region Constant Expressions
    // region PostfixExpr (XdmConstantExpression)

    fun testPostfixExpr_LiteralValue() {
        val expr = parseSimpleExpression<XPathPostfixExpr>("1e3")[0]
        assertThat(expr.constantValue, `is`(notNullValue()))
        assertThat(expr.constantValue, `is`(instanceOf(String::class.java)))
        assertThat(expr.constantValue as String, `is`("1e3"))
        assertThat(expr.staticType, `is`(XsDouble as XdmSequenceType))
    }

    fun testPostfixExpr_LiteralValue_ComplexExpression() {
        val expr = parseSimpleExpression<XPathPostfixExpr>("1?1")[0]
        assertThat(expr.constantValue, `is`(nullValue())) // Expression is invalid, and cannot be resolved.
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
    }

    fun testPostfixExpr_NonLiteralValue() {
        val expr = parseSimpleExpression<XPathPostfixExpr>("test()")[0]
        assertThat(expr.constantValue, `is`(nullValue())) // Cannot evaluate non-literal expression.
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
    }

    // endregion
    // endregion
}
