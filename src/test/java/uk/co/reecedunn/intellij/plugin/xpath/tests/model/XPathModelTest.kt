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
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathTypeDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase
import java.math.BigDecimal
import java.math.BigInteger

@Suppress("UNCHECKED_CAST")
class XPathModelTest : ParserTestCase() {
    // region Lexical Values
    // region BracedUriLiteral (XdmStaticValue)

    fun testBracedUriLiteral() {
        val literal = parse<XPathBracedURILiteral>("Q{http://www.example.com\uFFFF}")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("http://www.example.com\uFFFF"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testBracedUriLiteral_Unclosed() {
        val literal = parse<XPathBracedURILiteral>("Q{http://www.example.com")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("http://www.example.com"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region DecimalLiteral (XdmStaticValue)

    fun testDecimalLiteral() {
        val literal = parse<XPathDecimalLiteral>("12.34")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as BigDecimal, `is`(BigDecimal(BigInteger.valueOf(1234), 2)))
        assertThat(literal.staticType, `is`(XsDecimal as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region DoubleLiteral (XdmStaticValue)

    fun testDoubleLiteral() {
        val literal = parse<XPathDoubleLiteral>("1e3")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as Double, `is`(1e3))
        assertThat(literal.staticType, `is`(XsDouble as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region IntegerLiteral (XdmStaticValue)

    fun testIntegerLiteral() {
        val literal = parse<XPathIntegerLiteral>("123")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as BigInteger, `is`(BigInteger.valueOf(123)))
        assertThat(literal.staticType, `is`(XsInteger as XdmSequenceType))
        assertThat(literal.toInt(), `is`(123))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region Literal (XdmStaticValue)

    fun testLiteral_DoubleLiteral() {
        val literal = parse<XPathLiteral>("1e3")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as Double, `is`(1e3))
        assertThat(literal.staticType, `is`(XsDouble as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testLiteral_DecimalLiteral() {
        val literal = parse<XPathLiteral>("12.34")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as BigDecimal, `is`(BigDecimal(BigInteger.valueOf(1234), 2)))
        assertThat(literal.staticType, `is`(XsDecimal as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testLiteral_IntegerLiteral() {
        val literal = parse<XPathLiteral>("123")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as BigInteger, `is`(BigInteger.valueOf(123)))
        assertThat(literal.staticType, `is`(XsInteger as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testLiteral_StringLiteral() {
        val literal = parse<XPathLiteral>("\"Lorem ipsum.\"")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("Lorem ipsum."))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region StringLiteral (XdmStaticValue)

    fun testStringLiteral() {
        val literal = parse<XPathStringLiteral>("\"Lorem ipsum.\uFFFF\"")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testStringLiteral_Unclosed() {
        val literal = parse<XPathStringLiteral>("\"Lorem ipsum.")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("Lorem ipsum."))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testStringLiteral_EscapeApos() {
        val literal = parse<XPathStringLiteral>("'''\"\"'")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("'\"\""))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testStringLiteral_EscapeQuot() {
        val literal = parse<XPathStringLiteral>("\"''\"\"\"")[0] as XdmStaticValue
        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(literal.staticValue as String, `is`("''\""))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))

        assertThat(literal.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Lexical and Expanded QNames
    // region NCName (XdmStaticValue)

    fun testNCName() {
        val expr = parse<XPathNCName>("test")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.staticValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.staticValue as QName
        assertThat(qname.isLexicalQName, `is`(true))
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("test"))

        assertThat(qname.toString(), `is`("test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testNCName_Keyword() {
        val expr = parse<XPathNCName>("option")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.staticValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.staticValue as QName
        assertThat(qname.isLexicalQName, `is`(true))
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("option"))

        assertThat(qname.toString(), `is`("option"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testNCName_Wildcard() {
        val expr = parse<XPathNCName>("declare option * \"\";")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.staticValue, `is`(nullValue()))
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region QName (XdmStaticValue)

    fun testQName() {
        val expr = parse<XPathQName>("fn:true")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.staticValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.staticValue as QName
        assertThat(qname.isLexicalQName, `is`(true))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("fn"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("true"))

        assertThat(qname.toString(), `is`("fn:true"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testQName_KeywordPrefix() {
        val expr = parse<XPathQName>("option:test")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.staticValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.staticValue as QName
        assertThat(qname.isLexicalQName, `is`(true))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("option"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("test"))

        assertThat(qname.toString(), `is`("option:test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testQName_KeywordLocalName() {
        val expr = parse<XPathQName>("test:case")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.staticValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.staticValue as QName
        assertThat(qname.isLexicalQName, `is`(true))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("test"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("case"))

        assertThat(qname.toString(), `is`("test:case"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testQName_NoLocalName() {
        val expr = parse<XPathQName>("xs:")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.staticValue, `is`(nullValue()))
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region URIQualifiedName (XdmStaticValue)

    fun testURIQualifiedName() {
        val expr = parse<XPathURIQualifiedName>("Q{http://www.example.com}test")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.staticValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.staticValue as QName
        assertThat(qname.isLexicalQName, `is`(false))
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("test"))

        assertThat(qname.toString(), `is`("Q{http://www.example.com}test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testURIQualifiedName_EmptyNamespace() {
        val expr = parse<XPathURIQualifiedName>("Q{}test")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.staticValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.staticValue as QName
        assertThat(qname.isLexicalQName, `is`(false))
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`(""))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("test"))

        assertThat(qname.toString(), `is`("Q{}test"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testURIQualifiedName_KeywordLocalName() {
        val expr = parse<XPathURIQualifiedName>("Q{http://www.example.com}option")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.staticValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.staticValue as QName
        assertThat(qname.isLexicalQName, `is`(false))
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("option"))

        assertThat(qname.toString(), `is`("Q{http://www.example.com}option"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testURIQualifiedName_NoLocalName() {
        val expr = parse<XPathURIQualifiedName>("Q{http://www.example.com}")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.staticValue, `is`(nullValue()))
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
        assertThat(qname.localName.staticValue as String, `is`("test"))

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
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("type"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("test"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("boolean"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("numeric"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("NMTOKENS"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("anyType"))

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
        assertThat(qname.localName.staticValue as String, `is`("test"))

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
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("type"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("test"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("boolean"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("numeric"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("NMTOKENS"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("anyType"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region PITest (XPathTypeDeclaration)

    fun testPITest() {
        val expr = parse<XPathPITest>("\$x instance of processing-instruction()")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        val type = expr.declaredType as? XdmProcessingInstruction
        assertThat(type, `is`(notNullValue()))
        assertThat(type!!.nodeName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPITest_NCNameTarget_NCName() {
        val expr = parse<XPathPITest>("\$x instance of processing-instruction(test)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        val type = expr.declaredType as? XdmProcessingInstruction
        assertThat(type, `is`(notNullValue()))

        assertThat(type!!.nodeName!!.prefix, `is`(nullValue()))
        assertThat(type!!.nodeName!!.namespace, `is`(nullValue()))
        assertThat(type!!.nodeName!!.localName.staticValue as String, `is`("test"))
        assertThat(type!!.nodeName!!.declaration!!.get(), `is`(instanceOf(XPathNCName::class.java)))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPITest_NCNameTarget_Keyword() {
        val expr = parse<XPathPITest>("\$x instance of processing-instruction(option)")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        val type = expr.declaredType as? XdmProcessingInstruction
        assertThat(type, `is`(notNullValue()))

        assertThat(type!!.nodeName!!.prefix, `is`(nullValue()))
        assertThat(type!!.nodeName!!.namespace, `is`(nullValue()))
        assertThat(type!!.nodeName!!.localName.staticValue as String, `is`("option"))
        assertThat(type!!.nodeName!!.declaration!!.get(), `is`(instanceOf(XPathNCName::class.java)))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPITest_StringLiteralTarget_NCName() {
        val expr = parse<XPathPITest>("\$x instance of processing-instruction('test')")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        val type = expr.declaredType as? XdmProcessingInstruction
        assertThat(type, `is`(notNullValue()))

        // TODO: Support StringLiteral encoded NCName PITarget specifiers.
        assertThat(type!!.nodeName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPITest_StringLiteralTarget_Keyword() {
        val expr = parse<XPathPITest>("\$x instance of processing-instruction('option')")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        val type = expr.declaredType as? XdmProcessingInstruction
        assertThat(type, `is`(notNullValue()))

        // TODO: Support StringLiteral encoded NCName PITarget specifiers.
        assertThat(type!!.nodeName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPITest_StringLiteralTarget_NormalizeSpace() {
        val expr = parse<XPathPITest>("\$x instance of processing-instruction('  test\t\n\r\r  ')")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        val type = expr.declaredType as? XdmProcessingInstruction
        assertThat(type, `is`(notNullValue()))

        // TODO: Support StringLiteral encoded NCName PITarget specifiers.
        assertThat(type!!.nodeName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPITest_StringLiteralTarget_InvalidNCName() {
        val expr = parse<XPathPITest>("\$x instance of processing-instruction('*')")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        val type = expr.declaredType as? XdmProcessingInstruction
        assertThat(type, `is`(notNullValue()))

        // TODO: Support StringLiteral encoded NCName PITarget specifiers.
        assertThat(type!!.nodeName, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region CommentTest (XPathTypeDeclaration)

    fun testCommentTest() {
        val expr = parse<XPathCommentTest>("\$x instance of comment()")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.declaredType, `is`(XdmComment as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region TextTest (XPathTypeDeclaration)

    fun testTextTest() {
        val expr = parse<XPathTextTest>("\$x instance of text()")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.declaredType, `is`(XdmText as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region NamespaceNodeTest (XPathTypeDeclaration)

    fun testNamespaceNodeTest() {
        val expr = parse<XPathNamespaceNodeTest>("\$x instance of namespace-node()")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.declaredType, `is`(XdmNamespace as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region AnyKindTest (XPathTypeDeclaration)

    fun testAnyKindTest() {
        val expr = parse<XPathAnyKindTest>("\$x instance of node()")[0] as XPathTypeDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.declaredType, `is`(XdmNode as XdmSequenceType))

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
        assertThat(qname.localName.staticValue as String, `is`("test"))

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
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("type"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("test"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("boolean"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("numeric"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("NMTOKENS"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("anyType"))

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
        assertThat(qname.localName.staticValue as String, `is`("test"))

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
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("type"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("test"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("boolean"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("numeric"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("NMTOKENS"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("anyType"))

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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("boolean"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Variables
    // region Param (XPathVariableBinding)

    fun testParam_NCName() {
        val expr = parse<XPathParam>("function (\$x) {}")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testParam_QName() {
        val expr = parse<XPathParam>("function (\$a:x) {}")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling as XPathQName

        val qname = expr.variableName as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testParam_URIQualifiedName() {
        val expr = parse<XPathParam>(
                "function (\$Q{http://www.example.com}x) {}")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        val name = (expr as PsiElement).firstChild.nextSibling as XPathURIQualifiedName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testParam_MissingVarName() {
        val expr = parse<XPathParam>("function (\$) {}")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(nullValue()))
        assertThat(expr.variableType, `is`(nullValue()))
        assertThat(expr.variableValue, `is`(nullValue()))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region QuantifiedExprBinding (XPathVariableBinding)

    fun testQuantifiedExprBinding_NCName() {
        val expr = parse<XPathQuantifiedExprBinding>("some \$x in \$y satisfies \$z")[0] as XPathVariableBinding
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
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
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testQuantifiedExprBinding_QName() {
        val expr = parse<XPathQuantifiedExprBinding>("some \$a:x in \$a:y satisfies \$a:z")[0] as XPathVariableBinding
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
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testQuantifiedExprBinding_URIQualifiedName() {
        val expr = parse<XPathQuantifiedExprBinding>(
                "some \$Q{http://www.example.com}x in  \$Q{http://www.example.com}y satisfies \$Q{http://www.example.com}z")[0] as XPathVariableBinding
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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testQuantifiedExprBinding_MissingVarName() {
        val expr = parse<XPathQuantifiedExprBinding>("some \$")[0] as XPathVariableBinding
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
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val name = (expr as PsiElement).firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
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
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region VarRef (XPathVariableReference)

    fun testVarRef_NCName() {
        val expr = parse<XPathVarRef>("let \$x := 2 return \$y")[0] as XPathVariableReference
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
        assertThat(expr.variableName, `is`(notNullValue()))

        val varname = (expr as PsiElement).children().filterIsInstance<XPathVarName>().first()
        val name = varname.firstChild as XPathNCName

        val qname = expr.variableName as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(name as XdmStaticValue))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
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
        assertThat(qname.prefix?.staticValue as String, `is`("a"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
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
        assertThat(qname.namespace?.staticValue as String, `is`("http://www.example.com"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.staticValue as String, `is`("y"))

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
    // region Functions
    // region ParamList (XPathFunctionArguments)

    fun testParamList_SingleArguments() {
        val expr = parse<XPathParamList>("function (\$x) {}")[0] as XPathFunctionArguments<XPathVariableBinding>
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.arity, `is`(1))
        assertThat(expr.arguments.size, `is`(1))

        assertThat(expr.arguments[0].variableType, `is`(nullValue()))
        assertThat(expr.arguments[0].variableValue, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.namespace, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.prefix, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testParamList_MultipleArguments() {
        val expr = parse<XPathParamList>("function (\$x, \$y) {}")[0] as XPathFunctionArguments<XPathVariableBinding>
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.arity, `is`(2))
        assertThat(expr.arguments.size, `is`(2))

        assertThat(expr.arguments[0].variableType, `is`(nullValue()))
        assertThat(expr.arguments[0].variableValue, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.namespace, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.prefix, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.localName.staticValue as String, `is`("x"))

        assertThat(expr.arguments[1].variableType, `is`(nullValue()))
        assertThat(expr.arguments[1].variableValue, `is`(nullValue()))
        assertThat(expr.arguments[1].variableName!!.namespace, `is`(nullValue()))
        assertThat(expr.arguments[1].variableName!!.prefix, `is`(nullValue()))
        assertThat(expr.arguments[1].variableName!!.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // region InlineFunctionExpr (XPathFunctionDeclaration)

    fun testInlineFunctionExpr_NoArguments() {
        val expr = parse<XPathInlineFunctionExpr>("function () {}")[0] as XPathFunctionDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))

        assertThat(expr.functionName, `is`(nullValue()))
        assertThat(expr.returnType, `is`(nullValue()))
        assertThat(expr.arity, `is`(0))
        assertThat(expr.arguments.size, `is`(0))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testInlineFunctionExpr_SingleArguments() {
        val expr = parse<XPathInlineFunctionExpr>("function (\$x) {}")[0] as XPathFunctionDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.functionName, `is`(nullValue()))
        assertThat(expr.returnType, `is`(nullValue()))
        assertThat(expr.arity, `is`(1))
        assertThat(expr.arguments.size, `is`(1))

        assertThat(expr.arguments[0].variableType, `is`(nullValue()))
        assertThat(expr.arguments[0].variableValue, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.namespace, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.prefix, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.localName.staticValue as String, `is`("x"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testInlineFunctionExpr_MultipleArguments() {
        val expr = parse<XPathInlineFunctionExpr>("function (\$x, \$y) {}")[0] as XPathFunctionDeclaration
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.functionName, `is`(nullValue()))
        assertThat(expr.returnType, `is`(nullValue()))
        assertThat(expr.arity, `is`(2))
        assertThat(expr.arguments.size, `is`(2))

        assertThat(expr.arguments[0].variableType, `is`(nullValue()))
        assertThat(expr.arguments[0].variableValue, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.namespace, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.prefix, `is`(nullValue()))
        assertThat(expr.arguments[0].variableName!!.localName.staticValue as String, `is`("x"))

        assertThat(expr.arguments[1].variableType, `is`(nullValue()))
        assertThat(expr.arguments[1].variableValue, `is`(nullValue()))
        assertThat(expr.arguments[1].variableName!!.namespace, `is`(nullValue()))
        assertThat(expr.arguments[1].variableName!!.prefix, `is`(nullValue()))
        assertThat(expr.arguments[1].variableName!!.localName.staticValue as String, `is`("y"))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
    // region Expressions
    // region PostfixExpr (XdmStaticValue)

    fun testPostfixExpr_LiteralValue() {
        val expr = parse<XPathPostfixExpr>("1e3")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.staticValue, `is`(notNullValue()))
        assertThat(expr.staticValue, `is`(instanceOf(Double::class.java)))
        assertThat(expr.staticValue as Double, `is`(1e3))
        assertThat(expr.staticType, `is`(XsDouble as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPostfixExpr_LiteralValue_ComplexExpression() {
        val expr = parse<XPathPostfixExpr>("1?1")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.staticValue, `is`(nullValue())) // Expression is invalid, and cannot be resolved.
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    fun testPostfixExpr_NonLiteralValue() {
        val expr = parse<XPathPostfixExpr>("test()")[0] as XdmStaticValue
        assertThat(expr.cacheable, `is`(CachingBehaviour.Undecided))

        assertThat(expr.staticValue, `is`(nullValue())) // Cannot evaluate non-literal expression.
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))

        assertThat(expr.cacheable, `is`(CachingBehaviour.Cache))
    }

    // endregion
    // endregion
}
