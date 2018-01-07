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
package uk.co.reecedunn.intellij.plugin.xpath.tests.xdm

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableReference
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathTypeDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XPathXdmTest : ParserTestCase() {
    private inline fun <reified T> parse(xquery: String): List<T> {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().toList()
    }

    private inline fun <reified T> parseLiteral(xquery: String): XdmLexicalValue {
        return parseText(xquery)!!
                .walkTree().filterIsInstance<T>()
                .first() as XdmLexicalValue
    }

    private inline fun <reified T> parseSimpleExpression(xquery: String): List<XdmStaticValue> {
        return parseText(xquery)!!
                .walkTree().filterIsInstance<T>()
                .map { type -> type as XdmStaticValue }
                .toList()
    }

    // region Lexical Values
    // region BracedUriLiteral (XdmLexicalValue)

    fun testBracedUriLiteral() {
        val literal = parseLiteral<XPathBracedURILiteral>("Q{http://www.example.com\uFFFF}")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com\uFFFF"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testBracedUriLiteral_Unclosed() {
        val literal = parseLiteral<XPathBracedURILiteral>("Q{http://www.example.com")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region DecimalLiteral (XdmLexicalValue)

    fun testDecimalLiteral() {
        val literal = parseLiteral<XPathDecimalLiteral>("12.34")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("12.34"))
        assertThat(literal.staticType, `is`(XsDecimal as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region DoubleLiteral (XdmLexicalValue)

    fun testDoubleLiteral() {
        val literal = parseLiteral<XPathDoubleLiteral>("1e3")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("1e3"))
        assertThat(literal.staticType, `is`(XsDouble as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region IntegerLiteral (XdmLexicalValue)

    fun testIntegerLiteral() {
        val literal = parseLiteral<XPathIntegerLiteral>("123")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("123"))
        assertThat(literal.staticType, `is`(XsInteger as XdmSequenceType))
        assertThat(literal.toInt(), `is`(123))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region Literal (XdmLexicalValue)

    fun testLiteral_DoubleLiteral() {
        val literal = parseLiteral<XPathLiteral>("1e3")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("1e3"))
        assertThat(literal.staticType, `is`(XsDouble as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testLiteral_DecimalLiteral() {
        val literal = parseLiteral<XPathLiteral>("12.34")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("12.34"))
        assertThat(literal.staticType, `is`(XsDecimal as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testLiteral_IntegerLiteral() {
        val literal = parseLiteral<XPathLiteral>("123")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("123"))
        assertThat(literal.staticType, `is`(XsInteger as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testLiteral_StringLiteral() {
        val literal = parseLiteral<XPathLiteral>("\"Lorem ipsum.\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum."))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region StringLiteral (XdmLexicalValue)

    fun testStringLiteral() {
        val literal = parseLiteral<XPathStringLiteral>("\"Lorem ipsum.\uFFFF\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testStringLiteral_Unclosed() {
        val literal = parseLiteral<XPathStringLiteral>("\"Lorem ipsum.")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum."))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testStringLiteral_EscapeApos() {
        val literal = parseLiteral<XPathStringLiteral>("'''\"\"'")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("'\"\""))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testStringLiteral_EscapeQuot() {
        val literal = parseLiteral<XPathStringLiteral>("\"''\"\"\"")
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.lexicalRepresentation, `is`("''\""))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Lexical and Expanded QNames
    // region NCName (XdmStaticValue)

    fun testNCName() {
        val expr = parseSimpleExpression<XPathNCName>("test")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))

        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.isLexicalQName, `is`(true))
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(qname.toString(), `is`("test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testNCName_Keyword() {
        val expr = parseSimpleExpression<XPathNCName>("option")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))

        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.isLexicalQName, `is`(true))
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("option"))

        assertThat(qname.toString(), `is`("option"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    // endregion
    // region QName (XdmStaticValue)

    fun testQName() {
        val expr = parseSimpleExpression<XPathQName>("fn:true")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.isLexicalQName, `is`(true))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("fn"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("true"))

        assertThat(qname.toString(), `is`("fn:true"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testQName_KeywordPrefix() {
        val expr = parseSimpleExpression<XPathQName>("option:test")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.isLexicalQName, `is`(true))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("option"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(qname.toString(), `is`("option:test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testQName_KeywordLocalName() {
        val expr = parseSimpleExpression<XPathQName>("test:case")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.isLexicalQName, `is`(true))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("test"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("case"))

        assertThat(qname.toString(), `is`("test:case"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testQName_NoLocalName() {
        val expr = parseSimpleExpression<XPathQName>("xs:")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.constantValue, `is`(nullValue()))
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    // endregion
    // region URIQualifiedName (XdmStaticValue)

    fun testURIQualifiedName() {
        val expr = parseSimpleExpression<XPathURIQualifiedName>("Q{http://www.example.com}test")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.isLexicalQName, `is`(false))
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(qname.toString(), `is`("Q{http://www.example.com}test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testURIQualifiedName_EmptyNamespace() {
        val expr = parseSimpleExpression<XPathURIQualifiedName>("Q{}test")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.isLexicalQName, `is`(false))
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`(""))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(qname.toString(), `is`("Q{}test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testURIQualifiedName_KeywordLocalName() {
        val expr = parseSimpleExpression<XPathURIQualifiedName>("Q{http://www.example.com}option")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.isLexicalQName, `is`(false))
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("option"))

        assertThat(qname.toString(), `is`("Q{http://www.example.com}option"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testURIQualifiedName_NoLocalName() {
        val expr = parseSimpleExpression<XPathURIQualifiedName>("Q{http://www.example.com}")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.constantValue, `is`(nullValue()))
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Sequence Types
    // region AtomicOrUnionType (XPathTypeDeclaration)

    fun testAtomicOrUnionType_NCName() {
        val expr = parse<XPathAtomicOrUnionType>("\$x instance of test")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathNCName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testAtomicOrUnionType_QName() {
        val expr = parse<XPathAtomicOrUnionType>("\$x instance of a:type")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathQName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("type"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testAtomicOrUnionType_URIQualifiedName() {
        val expr = parse<XPathAtomicOrUnionType>("\$x instance of Q{http://www.example.com}test")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testAtomicOrUnionType_BuiltinAtomicType() {
        val expr = parse<XPathAtomicOrUnionType>("\$x instance of Q{http://www.w3.org/2001/XMLSchema}boolean")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsBoolean as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnyAtomicType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("boolean"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testAtomicOrUnionType_BuiltinUnionType() {
        val expr = parse<XPathAtomicOrUnionType>("\$x instance of Q{http://www.w3.org/2001/XMLSchema}numeric")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsNumeric as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("numeric"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testAtomicOrUnionType_BuiltinListType() {
        // NOTE: XQuery processors (e.g. BaseX and MarkLogic) allow these in cast expressions,
        // due to them being referenced in XMLSchema, but report errors elsewhere.

        val expr = parse<XPathAtomicOrUnionType>("\$x instance of Q{http://www.w3.org/2001/XMLSchema}NMTOKENS")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsNMTOKENS as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.MANY))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("NMTOKENS"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testAtomicOrUnionType_BuiltinAbstractType() {
        // NOTE: Errors when using these types are detected and reported elsewhere.

        val expr = parse<XPathAtomicOrUnionType>("\$x instance of Q{http://www.w3.org/2001/XMLSchema}anyType")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsAnyType as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.MANY))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("anyType"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region TypeName (XPathTypeDeclaration)

    fun testTypeName_NCName() {
        val expr = parse<XPathTypeName>("\$x instance of element(*, test)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathNCName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testTypeName_QName() {
        val expr = parse<XPathTypeName>("\$x instance of element(*, a:type)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathQName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("type"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testTypeName_URIQualifiedName() {
        val expr = parse<XPathTypeName>("\$x instance of element(*, Q{http://www.example.com}test)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testTypeName_BuiltinAtomicType() {
        val expr = parse<XPathTypeName>("\$x instance of element(*, Q{http://www.w3.org/2001/XMLSchema}boolean)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsBoolean as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnyAtomicType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("boolean"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testTypeName_BuiltinUnionType() {
        val expr = parse<XPathTypeName>("\$x instance of element(*, Q{http://www.w3.org/2001/XMLSchema}numeric)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsNumeric as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("numeric"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testTypeName_BuiltinListType() {
        // NOTE: XQuery processors (e.g. BaseX and MarkLogic) allow these in cast expressions,
        // due to them being referenced in XMLSchema, but report errors elsewhere.

        val expr = parse<XPathTypeName>("\$x instance of element(*, Q{http://www.w3.org/2001/XMLSchema}NMTOKENS)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsNMTOKENS as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.MANY))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("NMTOKENS"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testTypeName_BuiltinAbstractType() {
        val expr = parse<XPathTypeName>("\$x instance of element(*, Q{http://www.w3.org/2001/XMLSchema}anyType)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsAnyType as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.MANY))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("anyType"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Single Types
    // region SimpleTypeName (XPathTypeDeclaration)

    fun testSimpleTypeName_NCName() {
        val expr = parse<XPathSimpleTypeName>("\$x cast as test")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathNCName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testSimpleTypeName_QName() {
        val expr = parse<XPathSimpleTypeName>("\$x cast as a:type")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathQName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("type"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testSimpleTypeName_URIQualifiedName() {
        val expr = parse<XPathSimpleTypeName>("\$x cast as Q{http://www.example.com}test")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testSimpleTypeName_BuiltinAtomicType() {
        val expr = parse<XPathSimpleTypeName>("\$x cast as Q{http://www.w3.org/2001/XMLSchema}boolean")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsBoolean as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnyAtomicType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("boolean"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testSimpleTypeName_BuiltinUnionType() {
        val expr = parse<XPathSimpleTypeName>("\$x cast as Q{http://www.w3.org/2001/XMLSchema}numeric")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsNumeric as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("numeric"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testSimpleTypeName_BuiltinListType() {
        // NOTE: XQuery processors (e.g. BaseX and MarkLogic) allow these in cast expressions,
        // due to them being referenced in XMLSchema, but report errors elsewhere.

        val expr = parse<XPathSimpleTypeName>("\$x cast as Q{http://www.w3.org/2001/XMLSchema}NMTOKENS")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsNMTOKENS as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.MANY))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("NMTOKENS"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testSimpleTypeName_BuiltinAbstractType() {
        val expr = parse<XPathSimpleTypeName>("\$x cast as Q{http://www.w3.org/2001/XMLSchema}anyType")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsAnyType as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.MANY))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("anyType"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region SingleType (XPathTypeDeclaration)

    fun testSingleType_NCName() {
        val expr = parse<XPathSingleType>("\$x cast as test")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild.firstChild as XPathNCName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testSingleType_QName() {
        val expr = parse<XPathSingleType>("\$x cast as a:type")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild.firstChild as XPathQName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("type"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testSingleType_URIQualifiedName() {
        val expr = parse<XPathSingleType>("\$x cast as Q{http://www.example.com}test")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild.firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        val type = expr.declaredType as XdmSimpleType
        assertThat(type.typeName, `is`(notNullValue()))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.itemType, `is`(type as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testSingleType_BuiltinAtomicType() {
        val expr = parse<XPathSingleType>("\$x cast as Q{http://www.w3.org/2001/XMLSchema}boolean")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild.firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsBoolean as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnyAtomicType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("boolean"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testSingleType_BuiltinUnionType() {
        val expr = parse<XPathSingleType>("\$x cast as Q{http://www.w3.org/2001/XMLSchema}numeric")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild.firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsNumeric as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("numeric"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testSingleType_BuiltinListType() {
        // NOTE: XQuery processors (e.g. BaseX and MarkLogic) allow these in cast expressions,
        // due to them being referenced in XMLSchema, but report errors elsewhere.

        val expr = parse<XPathSingleType>("\$x cast as Q{http://www.w3.org/2001/XMLSchema}NMTOKENS")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild.firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsNMTOKENS as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.MANY))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("NMTOKENS"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testSingleType_BuiltinAbstractType() {
        val expr = parse<XPathSingleType>("\$x cast as Q{http://www.w3.org/2001/XMLSchema}anyType")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild.firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmSimpleType::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmSimpleType
        assertThat(type.itemType, `is`(XsAnyType as XdmSequenceType))
        assertThat(type.baseType, `is`(XsAnySimpleType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.MANY))

        val qname = type.typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("anyType"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testSingleType_Optional() {
        val expr = parse<XPathSingleType>("\$x cast as Q{http://www.w3.org/2001/XMLSchema}boolean?")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        val name = (expr as PsiElement).firstChild.firstChild as XPathURIQualifiedName
        assertThat(expr.declaredType, `is`(instanceOf(XdmOptional::class.java)))

        // NOTE: itemType is not `this`, but is the mapped builtin type object.
        val type = expr.declaredType as XdmOptional
        assertThat(type.itemType, `is`(instanceOf(XdmSimpleType::class.java)))
        assertThat(type.itemType.itemType, `is`(XsBoolean as XdmSequenceType))
        assertThat((type.itemType as XdmSimpleType).baseType, `is`(XsAnyAtomicType as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))

        val qname = (type.itemType as XdmSimpleType).typeName!!
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("boolean"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Constant Expressions
    // region PostfixExpr (XdmStaticValue)

    fun testPostfixExpr_LiteralValue() {
        val expr = parseSimpleExpression<XPathPostfixExpr>("1e3")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.constantValue, `is`(notNullValue()))
        assertThat(expr.constantValue, `is`(instanceOf(String::class.java)))
        assertThat(expr.constantValue as String, `is`("1e3"))
        assertThat(expr.staticType, `is`(XsDouble as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPostfixExpr_LiteralValue_ComplexExpression() {
        val expr = parseSimpleExpression<XPathPostfixExpr>("1?1")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.constantValue, `is`(nullValue())) // Expression is invalid, and cannot be resolved.
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPostfixExpr_NonLiteralValue() {
        val expr = parseSimpleExpression<XPathPostfixExpr>("test()")[0]
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.constantValue, `is`(nullValue())) // Cannot evaluate non-literal expression.
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Variables
    // region Param (XPathVariableDeclaration)

    fun testParam_NCName() {
        val expr = parse<XPathParam>("function (\$x) {}")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testParam_QName() {
        val expr = parse<XPathParam>("function (\$a:x) {}")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testParam_URIQualifiedName() {
        val expr = parse<XPathParam>(
                "function (\$Q{http://www.example.com}x) {}")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testParam_MissingVarName() {
        val expr = parse<XPathParam>("function (\$) {}")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region QuantifiedExprBinding (XPathVariableDeclaration)

    fun testQuantifiedExprBinding_NCName() {
        val expr = parse<XPathQuantifiedExprBinding>("some \$x in \$y satisfies \$z")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testQuantifiedExprBinding_QName() {
        val expr = parse<XPathQuantifiedExprBinding>("some \$a:x in \$a:y satisfies \$a:z")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testQuantifiedExprBinding_URIQualifiedName() {
        val expr = parse<XPathQuantifiedExprBinding>(
                "some \$Q{http://www.example.com}x in  \$Q{http://www.example.com}y satisfies \$Q{http://www.example.com}z")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testQuantifiedExprBinding_MissingVarName() {
        val expr = parse<XPathQuantifiedExprBinding>("some \$")[0] as XPathVariableDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region VarName (XPathVariableName)

    fun testVarName_NCName() {
        val expr = parse<XPathVarName>("let \$x := 2 return \$y")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testVarName_QName() {
        val expr = parse<XPathVarName>("let \$a:x := 2 return \$a:y")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testVarName_URIQualifiedName() {
        val expr = parse<XPathVarName>("let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y")[0] as XPathVariableName
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region VarRef (XPathVariableReference)

    fun testVarRef_NCName() {
        val expr = parse<XPathVarRef>("let \$x := 2 return \$y")[0] as XPathVariableReference
        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testVarRef_QName() {
        val expr = parse<XPathVarRef>("let \$a:x := 2 return \$a:y")[0] as XPathVariableReference
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.DoNotCache))
    }

    fun testVarRef_URIQualifiedName() {
        val expr = parse<XPathVarRef>("let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y")[0] as XPathVariableReference
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.lexicalRepresentation, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testVarRef_MissingVarName() {
        val expr = parse<XPathVarRef>("let \$x := 2 return \$")[0] as XPathVariableReference
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
}
