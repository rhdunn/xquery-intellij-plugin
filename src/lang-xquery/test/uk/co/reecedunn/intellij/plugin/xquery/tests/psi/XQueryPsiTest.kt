/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.util.Range
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginAnyItemType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginQuantifiedExprBinding
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.model.expand
import uk.co.reecedunn.intellij.plugin.xquery.model.getNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase
import java.math.BigDecimal
import java.math.BigInteger

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery 3.1 - IntelliJ Program Structure Interface (PSI)")
private class XQueryPsiTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile(XQueryPsiTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    override fun registerModules(manager: MockModuleManager) {
        manager.addModule(ResourceVirtualFile(XQueryPsiTest::class.java.classLoader, "tests/module-xquery"))
    }

    @Nested
    @DisplayName("XQuery 3.1 (2) Basics")
    internal inner class Basics {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (217) URILiteral")
        internal inner class URILiteral {
            @Test
            @DisplayName("uri literal content")
            fun uriLiteral() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"Lorem ipsum.\uFFFF\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("unclosed uri literal content")
            fun unclosedUriLiteral() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"Lorem ipsum.")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum."))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val psi = parse<XQueryUriLiteral>("module namespace test = '''\"\"'")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("'\"\""))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"''\"\"\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("''\""))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val psi = parse<XQueryUriLiteral>("module namespace test = \"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val psi = parse<XQueryUriLiteral>("module namespace test = \"&#xA0;&#160;&#x20;\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (223) URIQualifiedName")
        internal inner class URIQualifiedName {
            @Test
            @DisplayName("non-keyword local name")
            fun identifier() {
                val qname = parse<XPathURIQualifiedName>("Q{http://www.example.com}test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("keyword local name")
            fun keyword() {
                val qname = parse<XPathURIQualifiedName>("Q{http://www.example.com}option")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("option"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("empty namespace")
            fun emptyNamespace() {
                val qname = parse<XPathURIQualifiedName>("Q{}test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`(""))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("missing local name")
            fun noLocalName() {
                val qname = parse<XPathURIQualifiedName>("Q{http://www.example.com}")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val qname = parse<XPathURIQualifiedName>("declare option Q{http://www.example.com}* \"\";")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("PsiNameIdentifierOwner")
            fun psiNameIdentifierOwner() {
                val name = parse<XPathURIQualifiedName>("(: :) Q{http://www.example.com}test")[0] as PsiNameIdentifierOwner

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(31))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))
            }

            @Test
            @DisplayName("expand; namespace prefix in statically-known namespaces")
            fun expandToExistingNamespace() {
                val qname = parse<XPathURIQualifiedName>("Q{http://www.w3.org/2001/XMLSchema}test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(2))

                // The URIQualifiedName bound to the 'xs' NamespaceDecl.
                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))

                // The URIQualifiedName itself, not bound to a namespace.
                assertThat(expanded[1].isLexicalQName, `is`(false))
                assertThat(expanded[1].namespace!!.data, `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(expanded[1].prefix, `is`(nullValue()))
                assertThat(expanded[1].localName!!.data, `is`("test"))
                assertThat(expanded[1].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("expand; namespace prefix not in statically-known namespaces")
            fun expandToMissingNamespace() {
                val qname = parse<XPathURIQualifiedName>("Q{http://www.example.com}test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                // The URIQualifiedName itself, not bound to a namespace.
                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.com"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (224) BracedURILiteral")
        internal inner class BracedURILiteral {
            @Test
            @DisplayName("braced uri literal content")
            fun bracedUriLiteral() {
                val literal = parse<XPathBracedURILiteral>("Q{Lorem ipsum.\uFFFF}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("unclosed braced uri literal content")
            fun unclosedBracedUriLiteral() {
                val literal = parse<XPathBracedURILiteral>("Q{Lorem ipsum.")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum."))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val literal = parse<XPathBracedURILiteral>("Q{&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val literal = parse<XPathBracedURILiteral>("Q{&#xA0;&#160;&#x20;}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (234) QName")
        internal inner class QName {
            @Test
            @DisplayName("non-keyword prefix; non-keyword local name")
            fun identifier() {
                val qname = parse<XPathQName>("fn:true")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("keyword prefix; non-keyword local name")
            fun keywordPrefix() {
                val qname = parse<XPathQName>("option:test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("option"))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("non-keyword prefix; keyword local name")
            fun keywordLocalName() {
                val qname = parse<XPathQName>("test:case")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("test"))
                assertThat(qname.localName!!.data, `is`("case"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("missing local name")
            fun noLocalName() {
                val qname = parse<XPathQName>("xs:")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xs"))
                assertThat(qname.localName, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("whitespace in QName; before ':'")
            fun whitespaceInQName_beforeColon() {
                val qname = parse<XPathQName>("xs :string")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xs"))
                assertThat(qname.localName!!.data, `is`("string"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("whitespace in QName; after ':'")
            fun whitespaceInQName_afterColon() {
                val qname = parse<XPathQName>("xs: string")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xs"))
                assertThat(qname.localName!!.data, `is`("string"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("whitespace in QName; before and after ':'")
            fun whitespaceInQName_beforeAndAfterColon() {
                val qname = parse<XPathQName>("xs : string")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xs"))
                assertThat(qname.localName!!.data, `is`("string"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("PsiNameIdentifierOwner")
            fun psiNameIdentifierOwner() {
                val name = parse<XPathQName>("(: :) a:test")[0] as PsiNameIdentifierOwner

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(8))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))
            }

            @Test
            @DisplayName("expand; namespace prefix in statically-known namespaces")
            fun expandToExistingNamespace() {
                val qname = parse<XPathQName>("xs:test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xs"))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(expanded[0].prefix!!.data, `is`("xs"))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("expand; namespace prefix not in statically-known namespaces")
            fun expandToMissingNamespace() {
                val qname = parse<XPathQName>("xsd:test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("xsd"))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(0))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (235) NCName")
        internal inner class NCName {
            @Test
            @DisplayName("identifier")
            fun identifier() {
                val qname = parse<XPathNCName>("test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("keyword")
            fun keyword() {
                val qname = parse<XPathNCName>("order")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("order"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val qname = parse<XPathNCName>("declare option * \"\";")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("PsiNameIdentifierOwner")
            fun psiNameIdentifierOwner() {
                val name = parse<XPathNCName>("(: :) test")[0] as PsiNameIdentifierOwner

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(6))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))
            }

            @Test
            @DisplayName("expand")
            fun expand() {
                val qname = parse<XPathNCName>("test")[0] as XsQNameValue

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Test
        @DisplayName("Namespaces in XML 1.0 (3) Declaring Namespaces : EBNF (4) NCName")
        fun xmlNCName() {
            val literal = parse<XmlNCNameImpl>("test")[0] as XsNCNameValue
            assertThat(literal.data, `is`("test"))
            assertThat(literal.element, sameInstance(literal as PsiElement))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.4) SequenceType Syntax")
    internal inner class SequenceTypeSyntax {
        @Test
        @DisplayName("XQuery 3.1 EBNF (186) ItemType")
        fun itemType() {
            val type = parse<PluginAnyItemType>("() instance of item ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("item()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmItem::class.java)))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (187) AtomicOrUnionType")
        internal inner class AtomicOrUnionType {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () instance of test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (189) AnyKindTest")
        fun anyKindTest() {
            val type = parse<XPathAnyKindTest>("() instance of node ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("node()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmNode::class.java)))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (192) CommentTest")
        fun commentTest() {
            val type = parse<XPathCommentTest>("() instance of comment ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("comment()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmComment::class.java)))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (206) TypeName")
        internal inner class TypeName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () instance of element(*, test)
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.3) Element Test")
    internal inner class ElementTest {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (204) ElementName")
        internal inner class ElementName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () instance of element(test)
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.4) Schema Element Test")
    internal inner class SchemaElementTest {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (202) ElementDeclaration")
        internal inner class ElementDeclaration {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () instance of schema-element(test)
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.5) Attribute Test")
    internal inner class AttributeTest {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (203) AttributeName")
        internal inner class AttributeName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("() instance of attribute(test)")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (2.5.5.6) Schema Attribute Test")
    internal inner class SchemaAttributeTest {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (198) AttributeDeclaration")
        internal inner class AttributeDeclaration {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("() instance of schema-attribute(test)")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.1.1) Literals")
    internal inner class Literals {
        @Test
        @DisplayName("XQuery 3.1 EBNF (219) IntegerLiteral")
        fun integerLiteral() {
            val literal = parse<XPathIntegerLiteral>("123")[0] as XsIntegerValue
            assertThat(literal.data, `is`(BigInteger.valueOf(123)))
            assertThat(literal.toInt(), `is`(123))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (220) DecimalLiteral")
        fun decimalLiteral() {
            val literal = parse<XPathDecimalLiteral>("12.34")[0] as XsDecimalValue
            assertThat(literal.data, `is`(BigDecimal(BigInteger.valueOf(1234), 2)))
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (221) DoubleLiteral")
        fun doubleLiteral() {
            val literal = parse<XPathDoubleLiteral>("1e3")[0] as XsDoubleValue
            assertThat(literal.data, `is`(1e3))
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
        internal inner class StringLiteral {
            @Test
            @DisplayName("string literal content")
            fun stringLiteral() {
                val psi = parse<XPathStringLiteral>("\"Lorem ipsum.\uFFFF\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("unclosed string literal content")
            fun unclosedStringLiteral() {
                val psi = parse<XPathStringLiteral>("\"Lorem ipsum.")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("Lorem ipsum."))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val psi = parse<XPathStringLiteral>("'''\"\"'")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("'\"\""))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val psi = parse<XPathStringLiteral>("\"''\"\"\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("''\""))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val psi = parse<XPathStringLiteral>("\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val psi = parse<XPathStringLiteral>("\"&#xA0;&#160;&#x20;\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.1.2) Variable References")
    internal inner class VariableReferences {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (131) VarRef")
        internal inner class VarRef {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XPathVarRef>("let \$x := 2 return \$y")[0] as XPathVariableReference

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XPathVarRef>("let \$a:x := 2 return \$a:y")[0] as XPathVariableReference

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XPathVarRef>(
                    "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y"
                )[0] as XPathVariableReference

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<XPathVarRef>("let \$x := 2 return \$")[0] as XPathVariableReference
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (132) VarName")
        internal inner class VarName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XPathVarName>("let \$x := 2 return \$y")[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XPathVarName>("let \$a:x := 2 return \$a:y")[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XPathVarName>(
                    "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y"
                )[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncnameNamespaceResolution() {
                val qname = parse<XPathNCName>("\$test")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.1.5) Static Function Calls")
    internal inner class StaticFunctionCalls {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (122) ArgumentList")
        internal inner class ArgumentList {
            @Test
            @DisplayName("empty parameters")
            fun empty() {
                val args = parse<XPathArgumentList>("fn:true()")[0]
                assertThat(args.arity, `is`(0))
            }

            @Test
            @DisplayName("multiple ExprSingle parameters")
            fun multiple() {
                val args = parse<XPathArgumentList>("math:pow(2, 8)")[0]
                assertThat(args.arity, `is`(2))
            }

            @Test
            @DisplayName("ArgumentPlaceholder parameter")
            fun argumentPlaceholder() {
                val args = parse<XPathArgumentList>("math:sin(?)")[0]
                assertThat(args.arity, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("non-empty ArgumentList")
            fun nonEmptyArguments() {
                val f = parse<XPathFunctionCall>("math:pow(2, 8)")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(2))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("math"))
                assertThat(qname.localName!!.data, `is`("pow"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("empty ArgumentList")
            fun emptyArguments() {
                val f = parse<XPathFunctionCall>("fn:true()")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("ArgumentPlaceholder")
            fun argumentPlaceholder() {
                val f = parse<XPathFunctionCall>("math:sin(?)")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("math"))
                assertThat(qname.localName!!.data, `is`("sin"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathFunctionCall>(":true()")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))
                assertThat(f.functionName, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    test()
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultFunction))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/function"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.1.6) Named Function References")
    internal inner class NamedFunctionReferences {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
        internal inner class NamedFunctionRef {
            @Test
            @DisplayName("named function reference")
            fun namedFunctionRef() {
                val f = parse<XPathNamedFunctionRef>("true#3")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(3))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("missing arity")
            fun missingArity() {
                val f = parse<XPathNamedFunctionRef>("true#")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathNamedFunctionRef>(":true#0")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))
                assertThat(f.functionName, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    test#1
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultFunction))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/function"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.3.2.1) Axes")
    internal inner class Axes {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (113) ForwardAxis")
        internal inner class ForwardAxis {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>("""
                    child::one, descendant::two, attribute::three, self::four, descendant-or-self::five,
                    following-sibling::six, following::seven, namespace::eight
                """)
                assertThat(steps.size, `is`(8))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // child
                assertThat(steps[1].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // descendant
                assertThat(steps[2].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Attribute)) // attribute
                assertThat(steps[3].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // self
                assertThat(steps[4].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // descendant-or-self
                assertThat(steps[5].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // following-sibling
                assertThat(steps[6].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // following
                assertThat(steps[7].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Namespace)) // namespace
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (116) ReverseAxis")
        internal inner class ReverseAxis {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>(
                    "parent::one, ancestor::two, preceding-sibling::three, preceding::four, ancestor-or-self::five"
                )
                assertThat(steps.size, `is`(5))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // parent
                assertThat(steps[1].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // ancestor
                assertThat(steps[2].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // preceding-sibling
                assertThat(steps[3].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // preceding
                assertThat(steps[4].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // ancestor-or-self
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.3.2.2) Node Tests")
    internal inner class NodeTests {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (119) NameTest")
        internal inner class NameTest {
            @Test
            @DisplayName("NCName namespace resolution; element principal node kind")
            fun elementNcname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    ancestor::test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName namespace resolution; attribute principal node kind")
            fun attributeNcname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    attribute::test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName namespace resolution; namespace principal node kind")
            fun namespaceNcname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    namespace::test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (120) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("wildcard prefix; wildcard local name")
            fun bothWildcard() {
                val qname = parse<XPathWildcard>("*:*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("wildcard prefix; non-wildcard local name")
            fun wildcardPrefix() {
                val qname = parse<XPathWildcard>("*:test")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(not(instanceOf(XdmWildcardValue::class.java))))
                assertThat(qname.localName!!.data, `is`("test"))
            }

            @Test
            @DisplayName("non-wildcard prefix; wildcard local name")
            fun wildcardLocalName() {
                val qname = parse<XPathWildcard>("test:*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(not(instanceOf(XdmWildcardValue::class.java))))
                assertThat(qname.prefix!!.data, `is`("test"))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("missing local name")
            fun noLocalName() {
                val qname = parse<XPathWildcard>("*:")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(nullValue()))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun keyword() {
                val qname = parse<XPathWildcard>("Q{http://www.example.com}*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("URIQualifiedName with an empty namespace")
            fun emptyNamespace() {
                val qname = parse<XPathWildcard>("Q{}*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`(""))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.3.5) Abbreviated Syntax")
    internal inner class AbbreviatedSyntax {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (114) AbbrevForwardStep")
        internal inner class AbbrevForwardStep {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>("one, @two")
                assertThat(steps.size, `is`(2))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element))
                assertThat(steps[1].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Attribute))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.9.1) Direct Element Constructors")
    internal inner class DirectElementConstructors {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
        internal inner class DirElemConstructor {
            @Test
            @DisplayName("open and close tags")
            fun openAndCloseTags() {
                val element = parse<XQueryDirElemConstructor>("<a:b></a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag!!
                assertThat(close.prefix!!.data, `is`("a"))
                assertThat(close.localName!!.data, `is`("b"))
            }

            @Test
            @DisplayName("self-closing")
            fun selfClosing() {
                val element = parse<XQueryDirElemConstructor>("<h:br/>")[0]
                assertThat(element.isSelfClosing, `is`(true))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("h"))
                assertThat(open.localName!!.data, `is`("br"))

                val close = element.closeTag
                assertThat(close, `is`(nullValue()))
            }

            @Test
            @DisplayName("error recovery: missing close tag")
            fun missingClosingTag() {
                val element = parse<XQueryDirElemConstructor>("<a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag
                assertThat(close, `is`(nullValue()))
            }

            @Test
            @DisplayName("error recovery: incomplete open tag")
            fun incompleteOpenTag() {
                val element = parse<XQueryDirElemConstructor>("<a:></a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName, `is`(nullValue()))

                val close = element.closeTag!!
                assertThat(close.prefix!!.data, `is`("a"))
                assertThat(close.localName!!.data, `is`("b"))
            }

            @Test
            @DisplayName("error recovery: incomplete close tag")
            fun incompleteCloseTag() {
                val element = parse<XQueryDirElemConstructor>("<a:b></a:>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag!!
                assertThat(close.prefix!!.data, `is`("a"))
                assertThat(close.localName, `is`(nullValue()))
            }

            @Test
            @DisplayName("error recovery: partial close tag only")
            fun partialCloseTagOnly() {
                val element = parse<XQueryDirElemConstructor>("</<test>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag
                assertThat(open, `is`(nullValue()))

                val close = element.closeTag
                assertThat(close, `is`(nullValue()))
            }

            @Test
            @DisplayName("error recovery: close tag only")
            fun soloCloseTag() {
                val element = parse<XQueryDirElemConstructor>("</a:b>")[0]
                assertThat(element.isSelfClosing, `is`(false))

                val open = element.openTag!!
                assertThat(open.prefix!!.data, `is`("a"))
                assertThat(open.localName!!.data, `is`("b"))

                val close = element.closeTag
                assertThat(close, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    <test/>
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("<elem test=\"\"/>")[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue")
        internal inner class DirAttributeValue {
            @Test
            @DisplayName("attribute value content")
            fun attributeValue() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com\uFFFF\"/>")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("unclosed attribute value content")
            fun unclosedAttributeValue() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"http://www.example.com")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("http://www.example.com"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val psi = parse<XQueryDirAttributeValue>("<a b='''\"\"{{}}'")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("'\"\"{}"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"''\"\"{{}}\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("''\"{}"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val psi = parse<XQueryDirAttributeValue>("<a b=\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"&#xA0;&#160;&#x20;\"")[0]
                assertThat(psi.value, `is`(instanceOf(XsStringValue::class.java)))

                val literal = psi.value as XsStringValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("EnclosedExpr tokens")
            fun enclosedExpr() {
                val psi = parse<XQueryDirAttributeValue>("<a b=\"x{\$y}z\"")[0]
                assertThat(psi.value, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (3.9.1.2) Namespace Declaration Attributes")
        internal inner class NamespaceDeclarationAttributes {
            @Test
            @DisplayName("namespace prefix")
            fun namespacePrefix() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns:b=\"http://www.example.com")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("http://www.example.com"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("namespace prefix containing an EnclosedExpr")
            fun namespacePrefixWithEnclosedExpr() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns:b=\"http://www.{\"example\"}.com\"/>")[0]
                assertThat(psi.value, `is`(nullValue()))
            }

            @Test
            @DisplayName("default element/type namespace")
            fun defaultElementTypeNamespace() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns=\"http://www.example.com")[0]
                assertThat(psi.value, `is`(instanceOf(XsAnyUriValue::class.java)))

                val literal = psi.value as XsAnyUriValue
                assertThat(literal.data, `is`("http://www.example.com"))
                assertThat(literal.element, sameInstance(psi as PsiElement))
            }

            @Test
            @DisplayName("default element/type namespace containing an EnclosedExpr")
            fun defaultElementTypeNamespaceEnclosedExpr() {
                val psi = parse<XQueryDirAttributeValue>("<a xmlns:b=\"http://www.{\"example\"}.com\"/>")[0]
                assertThat(psi.value, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.9.3.1) Computed Element Constructors")
    internal inner class ComputedElementConstructors {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
        internal inner class CompElemConstructor {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    element test {}
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.9.3.2) Computed Attribute Constructors")
    internal inner class ComputedAttributeConstructors {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
        internal inner class CompAttrConstructor {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("attribute test {}")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.11.1) Maps")
    internal inner class Maps {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (171) MapConstructorEntry")
        internal inner class MapConstructorEntry {
            @Test
            @DisplayName("key, value")
            fun keyValue() {
                val entry = parse<XPathMapConstructorEntry>("map { \"1\" : \"one\" }")[0]
                assertThat(entry.separator.node.elementType, `is`(XPathTokenType.QNAME_SEPARATOR))
            }

            @Test
            @DisplayName("key, no value")
            fun noValue() {
                val entry = parse<XPathMapConstructorEntry>("map { \$ a }")[0]
                assertThat(entry.separator.node.elementType, `is`(XPathElementType.MAP_KEY_EXPR))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12) FLWORExpressions")
    internal inner class FLWORExpressions {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
        internal inner class PositionalVar {
            @Test
            @DisplayName("NCName")
            fun testPositionalVar_NCName() {
                val expr = parse<XQueryPositionalVar>("for \$x at \$y in \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("QName")
            fun testPositionalVar_QName() {
                val expr = parse<XQueryPositionalVar>("for \$a:x at \$a:y in \$a:z return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testPositionalVar_URIQualifiedName() {
                val expr = parse<XQueryPositionalVar>(
                    "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testPositionalVar_MissingVarName() {
                val expr = parse<XQueryPositionalVar>("for \$x at \$ \$z return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.2) For Clause")
    internal inner class ForClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (45) ForBinding")
        internal inner class ForBinding {
            @Test
            @DisplayName("NCName")
            fun testForBinding_NCName() {
                val expr = parse<XQueryForBinding>("for \$x at \$y in \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testForBinding_QName() {
                val expr = parse<XQueryForBinding>("for \$a:x at \$a:y in \$a:z return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testForBinding_URIQualifiedName() {
                val expr = parse<XQueryForBinding>(
                    "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testForBinding_MissingVarName() {
                val expr = parse<XQueryForBinding>("for \$ \$y return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.3) Let Clause")
    internal inner class LetClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (49) LetBinding")
        internal inner class LetBinding {
            @Test
            @DisplayName("NCName")
            fun testLetBinding_NCName() {
                val expr = parse<XQueryLetBinding>("let \$x := 2 return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testLetBinding_QName() {
                val expr = parse<XQueryLetBinding>("let \$a:x := 2 return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testLetBinding_URIQualifiedName() {
                val expr = parse<XQueryLetBinding>(
                    "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testLetBinding_MissingVarName() {
                val expr = parse<XQueryLetBinding>("let \$ := 2 return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.4) Window Clause")
    internal inner class WindowClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (51) TumblingWindowClause")
        internal inner class TumblingWindowClause {
            @Test
            @DisplayName("NCName")
            fun testTumblingWindowClause_NCName() {
                val expr = parse<XQueryTumblingWindowClause>(
                    "for tumbling window \$x in \$y return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testTumblingWindowClause_QName() {
                val expr = parse<XQueryTumblingWindowClause>(
                    "for tumbling window \$a:x in \$a:y return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testTumblingWindowClause_URIQualifiedName() {
                val expr = parse<XQueryTumblingWindowClause>(
                    "for tumbling window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testTumblingWindowClause_MissingVarName() {
                val expr = parse<XQueryTumblingWindowClause>("for tumbling window \$ \$y return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (52) SlidingWindowClause")
        internal inner class SlidingWindowClause {
            @Test
            @DisplayName("NCName")
            fun testSlidingWindowClause_NCName() {
                val expr = parse<XQuerySlidingWindowClause>(
                    "for sliding window \$x in \$y return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun testSlidingWindowClause_QName() {
                val expr = parse<XQuerySlidingWindowClause>(
                    "for sliding window \$a:x in \$a:y return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testSlidingWindowClause_URIQualifiedName() {
                val expr = parse<XQuerySlidingWindowClause>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testSlidingWindowClause_MissingVarName() {
                val expr = parse<XQuerySlidingWindowClause>("for sliding window \$ \$y return \$w")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
        internal inner class CurrentItem {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    "for sliding window \$x in () start \$test when () end when () return ()"
                )[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName")
            fun testCurrentItem_NCName() {
                val expr = parse<XQueryCurrentItem>("for sliding window \$x in \$y start \$w when true() return \$z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("QName")
            fun testCurrentItem_QName() {
                val expr = parse<XQueryCurrentItem>("for sliding window \$a:x in \$a:y start \$a:w when true() return \$a:z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testCurrentItem_URIQualifiedName() {
                val expr = parse<XQueryCurrentItem>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("w"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
        internal inner class PreviousItem {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    "for sliding window \$x in () start previous \$test when () end when () return ()"
                )[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName")
            fun testPreviousItem_NCName() {
                val expr = parse<XQueryPreviousItem>(
                    "for sliding window \$x in \$y start \$v previous \$w when true() return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("QName")
            fun testPreviousItem_QName() {
                val expr = parse<XQueryPreviousItem>(
                    "for sliding window \$a:x in \$a:y start \$a:v previous \$a:w when true() return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testPreviousItem_URIQualifiedName() {
                val expr = parse<XQueryPreviousItem>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}v previous \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("w"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (58) NextItem")
        internal inner class NextItem {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    "for sliding window \$x in () start next \$test when () end when () return ()"
                )[1] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName")
            fun testNextItem_NCName() {
                val expr = parse<XQueryNextItem>("for sliding window \$x in \$y start \$v next \$w when true() return \$z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("QName")
            fun testNextItem_QName() {
                val expr = parse<XQueryNextItem>(
                    "for sliding window \$a:x in \$a:y start \$a:v next \$a:w when true() return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("w"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testNextItem_URIQualifiedName() {
                val expr = parse<XQueryNextItem>(
                    "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}v next \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("w"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.6) Count Clause")
    internal inner class CountClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (59) CountClause")
        internal inner class CountClauseTest {
            @Test
            @DisplayName("NCName")
            fun testCountClause_NCName() {
                val expr = parse<XQueryCountClause>("for \$x in \$y count \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("QName")
            fun testCountClause_QName() {
                val expr = parse<XQueryCountClause>("for \$a:x in \$a:y count \$a:z return \$a:w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testCountClause_URIQualifiedName() {
                val expr = parse<XQueryCountClause>(
                    "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y count \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testCountClause_MissingVarName() {
                val expr = parse<XQueryCountClause>("for \$x in \$y count \$")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.12.7) Group By Clause")
    internal inner class GroupByClause {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (63) GroupingSpec")
        internal inner class GroupingSpec {
            @Test
            @DisplayName("NCName")
            fun testGroupingSpec_NCName() {
                val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$z return \$w")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("QName")
            fun testGroupingSpec_QName() {
                val expr = parse<XQueryGroupingSpec>(
                    "for \$a:x in \$a:y group by \$a:z return \$a:w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testGroupingSpec_URIQualifiedName() {
                val expr = parse<XQueryGroupingSpec>(
                    "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "group by \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testGroupingSpec_MissingVarName() {
                val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (64) GroupingVariable")
        internal inner class GroupingVariable {
            @Test
            @DisplayName("NCName")
            fun testGroupingVariable_NCName() {
                val expr = parse<XQueryGroupingVariable>("for \$x in \$y group by \$z return \$w")[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("QName")
            fun testGroupingVariable_QName() {
                val expr = parse<XQueryGroupingVariable>(
                    "for \$a:x in \$a:y group by \$a:z return \$a:w"
                )[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testGroupingVariable_URIQualifiedName() {
                val expr = parse<XQueryGroupingVariable>(
                    "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "group by \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                )[0] as XPathVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("z"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testGroupingVariable_MissingVarName() {
                val expr = parse<XQueryGroupingVariable>("for \$x in \$y group by \$")[0] as XPathVariableName
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.16) Quantified Expressions")
    internal inner class QuantifiedExpressions {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (70) QuantifiedExpr")
        internal inner class QuantifiedExpr {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<PluginQuantifiedExprBinding>("some \$x in \$y satisfies \$z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<PluginQuantifiedExprBinding>("some \$a:x in \$a:y satisfies \$a:z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<PluginQuantifiedExprBinding>(
                    "some \$Q{http://www.example.com}x in \$Q{http://www.example.com}y satisfies \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<PluginQuantifiedExprBinding>("some \$")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.18.2) Typeswitch")
    internal inner class Typeswitch {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (75) CaseClause")
        internal inner class CaseClause {
            @Test
            @DisplayName("NCName")
            fun testCaseClause_NCName() {
                val expr = parse<XQueryCaseClause>(
                    "typeswitch (\$x) case \$y as xs:string return \$z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("QName")
            fun testCaseClause_QName() {
                val expr = parse<XQueryCaseClause>(
                    "typeswitch (\$a:x) case \$a:y as xs:string return \$a:z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun testCaseClause_URIQualifiedName() {
                val expr = parse<XQueryCaseClause>(
                    "typeswitch (\$Q{http://www.example.com}x) " +
                            "case \$Q{http://www.example.com}y as xs:string " +
                            "return \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("missing VarName")
            fun testCaseClause_NoVarName() {
                val expr = parse<XQueryCaseClause>("typeswitch (\$x) case xs:string return \$z")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.18.3) Cast")
    internal inner class Cast {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (205) SimpleTypeName")
        internal inner class SimpleTypeName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () cast as test
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultElementOrType))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/element"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.20) Arrow Operator")
    internal inner class ArrowOperator {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowFunctionSpecifier {
            @Test
            @DisplayName("EQName specifier, non-empty ArgumentList")
            fun nonEmptyArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => f(1, 2,  3)")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(4))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("f"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("EQName specifier, empty ArgumentList")
            fun emptyArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => upper-case()")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("upper-case"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("EQName specifier, missing ArgumentList")
            fun missingArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => upper-case")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("upper-case"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => :upper-case")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))
                assertThat(f.functionName, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () => test()
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultFunction))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/function"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.22) Extension Expressions")
    internal inner class ExtensionExpressions {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (105) Pragma")
        internal inner class Pragma {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("(# test #)")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4) Modules and Prologs")
    internal inner class ModulesAndPrologs {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (1) Module")
        internal inner class Module {
            @Test
            @DisplayName("empty file")
            fun emptyFile() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("")[0]

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl missing")
            fun noVersionDecl() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("1234")[0]

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with version")
            fun versionDeclWithVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery version \"1.0\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(XQuerySpec.REC_1_0_20070123))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_1_0_20070123))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(XQuerySpec.REC_1_0_20070123))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_1_0_20070123))
            }

            @Test
            @DisplayName("VersionDecl with unsupported version")
            fun versionDeclWithUnsupportedVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery version \"9.4\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with empty version")
            fun versionDeclWithEmptyVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery version \"\"; 1234")[0]
                val decl = file.descendants().filterIsInstance<XQueryVersionDecl>().first()

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(decl.version))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }

            @Test
            @DisplayName("VersionDecl with missing version")
            fun versionDeclWithMissingVersion() {
                settings.XQueryVersion = XQuerySpec.REC_3_0_20140408.versionId

                val file = parse<XQueryModule>("xquery; 1234")[0]

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_0_20140408))

                settings.XQueryVersion = XQuerySpec.REC_3_1_20170321.versionId

                assertThat(file.XQueryVersion.version, `is`(nullValue()))
                assertThat(file.XQueryVersion.declaration, `is`(nullValue()))
                assertThat(file.XQueryVersion.getVersionOrDefault(file.project), `is`(XQuerySpec.REC_3_1_20170321))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (3) MainModule")
        internal inner class MainModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val module = parse<XQueryMainModule>("()")[0]

                assertThat((module as XQueryPrologResolver).prolog.count(), `is`(0))
            }

            @Test
            @DisplayName("prolog")
            fun prolog() {
                val module = parse<XQueryMainModule>("declare function local:func() {}; ()")[0]

                val prologs = (module as XQueryPrologResolver).prolog.toList()
                assertThat(prologs.size, `is`(1))

                val name = prologs[0].walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("local:func"))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (4) LibraryModule")
        internal inner class LibraryModule {
            @Test
            @DisplayName("no prolog")
            fun noProlog() {
                val module = parse<XQueryLibraryModule>("module namespace test = \"http://www.example.com\";")[0]

                assertThat((module as XQueryPrologResolver).prolog.count(), `is`(0))
            }

            @Test
            @DisplayName("prolog")
            fun prolog() {
                val module = parse<XQueryLibraryModule>(
                    """
                        module namespace test = "http://www.example.com";
                        declare function test:func() {};
                    """
                )[0]

                val prologs = (module as XQueryPrologResolver).prolog.toList()
                assertThat(prologs.size, `is`(1))

                val name = prologs[0].walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("test:func"))
            }
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (38) QueryBody")
        fun queryBody() {
            val decl = parse<XQueryQueryBody>("1 div 2")[0]

            val presentation = decl.presentation!!
            assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.QueryBody)))
            assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.QueryBody)))
            assertThat(presentation.presentableText, `is`("query body"))
            assertThat(presentation.locationString, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.1) Version Declaration")
    internal inner class VersionDecl {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (2) VersionDecl")
        internal inner class VersionDecl {
            @Test
            @DisplayName("no version, no encoding")
            fun noVersionOrEncoding() {
                val decl = parse<XQueryVersionDecl>("xquery;")[0]
                assertThat(decl.version, `is`(nullValue()))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("version, no encoding")
            fun versionOnly() {
                val decl = parse<XQueryVersionDecl>("xquery version \"1.0\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("no version, encoding")
            fun encodingOnly() {
                val decl = parse<XQueryVersionDecl>("xquery encoding \"latin1\";")[0]
                assertThat(decl.version, `is`(nullValue()))
                assertThat((decl.encoding!!.value as XsStringValue).data, `is`("latin1"))
            }

            @Test
            @DisplayName("empty version, no encoding")
            fun emptyVersion() {
                val decl = parse<XQueryVersionDecl>("xquery version \"\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`(""))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("no version, empty encoding")
            fun emptyEncoding() {
                val decl = parse<XQueryVersionDecl>("xquery encoding \"\";")[0]
                assertThat(decl.version, `is`(nullValue()))
                assertThat((decl.encoding!!.value as XsStringValue).data, `is`(""))
            }

            @Test
            @DisplayName("version, encoding")
            fun versionAndEncoding() {
                val decl = parse<XQueryVersionDecl>("xquery version \"1.0\" encoding \"latin1\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
                assertThat((decl.encoding!!.value as XsStringValue).data, `is`("latin1"))
            }

            @Test
            @DisplayName("version, empty encoding")
            fun emptyEncodingWithVersion() {
                val decl = parse<XQueryVersionDecl>("xquery version \"1.0\" encoding \"\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
                assertThat((decl.encoding!!.value as XsStringValue).data, `is`(""))
            }

            @Test
            @DisplayName("comment before declaration")
            fun commentBefore() {
                val decl = parse<XQueryVersionDecl>("(: test :)\nxquery version \"1.0\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
                assertThat(decl.encoding, `is`(nullValue()))
            }

            @Test
            @DisplayName("comment as whitespace")
            fun commentAsWhitespace() {
                val decl =
                    parse<XQueryVersionDecl>("xquery(: A :)version(: B :)\"1.0\"(: C :)encoding(: D :)\"latin1\";")[0]
                assertThat((decl.version!!.value as XsStringValue).data, `is`("1.0"))
                assertThat((decl.encoding!!.value as XsStringValue).data, `is`("latin1"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.2) Module Declaration")
    internal inner class ModuleDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 [4.2] Module Declaration : [5] ModuleDecl")
        internal inner class ModuleDecl {
            @Test
            @DisplayName("without prolog")
            fun withoutProlog() {
                val decl = parse<XQueryModuleDecl>("module namespace test = \"http://www.example.com\";")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix!!.data, `is`("test"))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))

                assertThat((decl as XQueryPrologResolver).prolog.count(), `is`(0))
            }

            @Test
            @DisplayName("with prolog")
            fun withProlog() {
                val decl = parse<XQueryModuleDecl>("""
                    module namespace test = "http://www.example.com";
                    declare function test:func() {};
                """)[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix!!.data, `is`("test"))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))

                val prologs = (decl as XQueryPrologResolver).prolog.toList()
                assertThat(prologs.size, `is`(1))

                val name = prologs[0].walkTree().filterIsInstance<XPathEQName>().first()
                assertThat(name.text, `is`("test:func"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val decl = parse<XQueryModuleDecl>("module namespace = \"http://www.example.com\";")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val decl = parse<XQueryModuleDecl>("module namespace test = ;")[0] as XPathNamespaceDeclaration
                assertThat(decl.namespacePrefix!!.data, `is`("test"))
                assertThat(decl.namespaceUri, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.10) Decimal Format Declaration")
    internal inner class DecimalFormatDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (18) DecimalFormatDecl")
        internal inner class DecimalFormatDecl {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("declare decimal-format test;")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.11) Schema Import")
    internal inner class SchemaImport {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("specified namespace prefix and uri")
            fun namespacePrefixAndUri() {
                val expr = parse<XQuerySchemaImport>("import schema namespace test = 'http://www.example.com';")[0] as XPathDefaultNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("test"))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.Prefixed))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val expr = parse<XQuerySchemaImport>("import schema namespace = 'http://www.example.com';")[0] as XPathDefaultNamespaceDeclaration
                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.Prefixed))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val expr = parse<XQuerySchemaImport>("import schema namespace test = ;")[0] as XPathDefaultNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("test"))
                assertThat(expr.namespaceUri, `is`(nullValue()))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.Prefixed))
            }

            @Test
            @DisplayName("default element namespace")
            fun defaultElementNamespace() {
                val expr = parse<XQuerySchemaImport>("import schema default element namespace 'http://www.example.com';")[0] as XPathDefaultNamespaceDeclaration
                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                assertThat(expr.namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.12) Module Import")
    internal inner class ModuleImport {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (23) ModuleImport")
        internal inner class ModuleImport {
            @Test
            @DisplayName("specified namespace prefix and uri")
            fun namespacePrefixAndUri() {
                val import = parse<XQueryModuleImport>("import module namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix!!.data, `is`("test"))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))

                val uris = (import as XQueryModuleImport).locationUris.toList()
                assertThat(uris.size, `is`(1))
                assertThat(uris[0].data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace prefix NCName")
            fun noNamespacePrefixNCName() {
                val import = parse<XQueryModuleImport>("import module namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix, `is`(nullValue()))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))

                val uris = (import as XQueryModuleImport).locationUris.toList()
                assertThat(uris.size, `is`(1))
                assertThat(uris[0].data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val import = parse<XQueryModuleImport>("import module 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix, `is`(nullValue()))
                assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))

                val uris = (import as XQueryModuleImport).locationUris.toList()
                assertThat(uris.size, `is`(1))
                assertThat(uris[0].data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val import = parse<XQueryModuleImport>("import module namespace test = ;")[0] as XPathNamespaceDeclaration
                assertThat(import.namespacePrefix!!.data, `is`("test"))
                assertThat(import.namespaceUri, `is`(nullValue()))

                val uris = (import as XQueryModuleImport).locationUris.toList()
                assertThat(uris.size, `is`(0))
            }

            @Test
            @DisplayName("location uris; single uri")
            fun singleLocationUri() {
                val import = parse<XQueryModuleImport>("import module namespace test = 'http://www.example.com' at 'test1.xqy';")[0]

                val uris = import.locationUris.toList()
                assertThat(uris.size, `is`(1))
                assertThat(uris[0].data, `is`("test1.xqy"))
            }

            @Test
            @DisplayName("location uris; multiple uris")
            fun multipleLocationUris() {
                val import = parse<XQueryModuleImport>("import module namespace test = 'http://www.example.com' at 'test1.xqy' , 'test2.xqy';")[0]

                val uris = import.locationUris.toList()
                assertThat(uris.size, `is`(2))
                assertThat(uris[0].data, `is`("test1.xqy"))
                assertThat(uris[1].data, `is`("test2.xqy"))
            }

            @Nested
            @DisplayName("resolve uri")
            internal inner class ResolveUri {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_Empty.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("same directory")
                fun sameDirectory() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_SameDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/resolve-xquery/files/test.xq"))
                }

                @Test
                @DisplayName("parent directory")
                fun parentDirectory() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_ParentDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(0))
                }

                @Test
                @DisplayName("module in relative directory")
                fun moduleInRelativeDirectory() {
                    val file = parseResource("tests/resolve-xquery/ModuleImport_URILiteral_InDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/resolve-xquery/namespaces/ModuleDecl.xq"))
                }

                @Test
                @DisplayName("http:// file matching")
                fun httpProtocol() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_HttpProtocol.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// file missing")
                fun httpProtocolMissing() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_HttpProtocol_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// (import namespace) file matching")
                fun httpProtocolOnNamespace() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_NamespaceOnly.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// (import namespace) file missing")
                fun httpProtocolOnNamespaceMissing() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_NamespaceOnly_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("multiple location URIs")
                fun multipleLocationUris() {
                    val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_MultipleLocationUris.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(2))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/resolve-xquery/files/test.xq"))
                    assertThat(prologs[1].resourcePath(), endsWith("/tests/resolve-xquery/files/test2.xq"))
                }

                @Test
                @DisplayName("module root")
                fun moduleRoot() {
                    val psi = parse<XQueryModuleImport>(
                        """
                        import module "http://example.com/test" at "/files/test.xq";
                        ()
                        """
                    )[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/tests/module-xquery/files/test.xq"))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.13) Namespace Declaration")
    internal inner class NamespaceDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (24) NamespaceDecl")
        internal inner class NamespaceDecl {
            @Test
            @DisplayName("specified namespace prefix and uri")
            fun namespacePrefixAndUri() {
                val expr = parse<XQueryNamespaceDecl>("declare namespace test = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("test"))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace prefix")
            fun noNamespacePrefix() {
                val expr = parse<XQueryNamespaceDecl>("declare namespace = 'http://www.example.com';")[0] as XPathNamespaceDeclaration
                assertThat(expr.namespacePrefix, `is`(nullValue()))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("missing namespace uri")
            fun noNamespaceUri() {
                val expr = parse<XQueryNamespaceDecl>("declare namespace test = ;")[0] as XPathNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("test"))
                assertThat(expr.namespaceUri, `is`(nullValue()))
            }

            @Nested
            @DisplayName("resolve uri")
            internal inner class ResolveUri {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val file = parseResource("tests/resolve-xquery/files/NamespaceDecl_Empty.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("same directory")
                fun sameDirectory() {
                    val file = parseResource("tests/resolve-xquery/files/NamespaceDecl_SameDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// file matching")
                fun httpProtocol() {
                    val file = parseResource("tests/resolve-xquery/files/NamespaceDecl_HttpProtocol.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// file missing")
                fun httpProtocolMissing() {
                    val file = parseResource("tests/resolve-xquery/files/NamespaceDecl_HttpProtocol_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.14) Default Namespace Declaration")
    internal inner class DefaultNamespaceDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
        internal inner class DefaultNamespaceDecl {
            @Test
            @DisplayName("default element/type namespace declaration")
            fun element() {
                val decl = parse<XPathDefaultNamespaceDeclaration>(
                    "declare default element namespace 'http://www.w3.org/1999/xhtml';"
                )[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri?.data, `is`("http://www.w3.org/1999/xhtml"))
                assertThat(decl.namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
            }

            @Test
            @DisplayName("default function namespace declaration")
            fun function() {
                val decl = parse<XPathDefaultNamespaceDeclaration>(
                    "declare default function namespace 'http://www.w3.org/2005/xpath-functions/math';"
                )[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri?.data, `is`("http://www.w3.org/2005/xpath-functions/math"))
                assertThat(decl.namespaceType, `is`(XPathNamespaceType.DefaultFunction))
            }

            @Test
            @DisplayName("empty namespace")
            fun emptyNamespace() {
                val decl = parse<XPathDefaultNamespaceDeclaration>("declare default element namespace '';")[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri!!.data, `is`(""))
                assertThat(decl.namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
            }

            @Test
            @DisplayName("missing namespace")
            fun missingNamespace() {
                val decl = parse<XPathDefaultNamespaceDeclaration>("declare default element namespace;")[0]

                assertThat(decl.namespacePrefix, `is`(nullValue()))
                assertThat(decl.namespaceUri, `is`(nullValue()))
                assertThat(decl.namespaceType, `is`(XPathNamespaceType.DefaultElementOrType))
            }

            @Nested
            @DisplayName("resolve uri")
            internal inner class ResolveUri {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val file = parseResource("tests/resolve-xquery/files/DefaultNamespaceDecl_Empty.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("same directory")
                fun sameDirectory() {
                    val file = parseResource("tests/resolve-xquery/files/DefaultNamespaceDecl_SameDirectory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }

                @Test
                @DisplayName("http:// file matching; ending in name")
                fun httpProtocol() {
                    val file = parseResource("tests/resolve-xquery/files/DefaultNamespaceDecl_HttpProtocol.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/2005/xpath-functions/array.xqy"))
                }

                @Test
                @DisplayName("http:// file matching; ending in name with '#'")
                fun httpProtocolWithHash() {
                    val file = parseResource("tests/resolve-xquery/files/DefaultNamespaceDecl_HttpProtocol_Hash.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/www.w3.org/1999/02/22-rdf-syntax-ns.xqy"))
                }

                @Test
                @DisplayName("http:// file matching; as a directory (ending in '/')")
                fun httpProtocolForDirectory() {
                    val file = parseResource("tests/resolve-xquery/files/DefaultNamespaceDecl_HttpProtocol_Directory.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    val prologs = (psi as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    assertThat(prologs[0].resourcePath(), endsWith("/builtin/saxon.sf.net/default.xqy"))
                }

                @Test
                @DisplayName("http:// file missing")
                fun httpProtocolMissing() {
                    val file = parseResource("tests/resolve-xquery/files/DefaultNamespaceDecl_HttpProtocol_FileNotFound.xq")
                    val psi = file.walkTree().filterIsInstance<XQueryDefaultNamespaceDecl>().toList()[0]

                    assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.15) Annotations")
    internal inner class Annotations {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (27) Annotation")
        internal inner class Annotation {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("declare function %test f() {};")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.XQuery))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2012/xquery"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (3.16) Variable Declaration")
    internal inner class VariableDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (28) VarDecl")
        internal inner class VarDecl {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XQueryVarDecl>("declare variable \$x := \$y;")[0]

                val qname = (expr as XPathVariableDeclaration).variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = expr.presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`("\$x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XQueryVarDecl>("declare variable \$a:x := \$a:y;")[0]

                val qname = (expr as XPathVariableDeclaration).variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = expr.presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`("\$a:x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XQueryVarDecl>(
                    "declare variable \$Q{http://www.example.com}x := \$Q{http://www.example.com}y;"
                )[0]

                val qname = (expr as XPathVariableDeclaration).variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = expr.presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`("\$Q{http://www.example.com}x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<XQueryVarDecl>("declare variable \$ := \$y;")[0]
                assertThat((expr as XPathVariableDeclaration).variableName, `is`(nullValue()))

                val presentation = expr.presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`(nullValue()))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("invalid VarName")
            fun invalidVarName() {
                val expr = parse<XQueryVarDecl>("declare variable \$: := \$y;")[0]
                assertThat((expr as XPathVariableDeclaration).variableName, `is`(nullValue()))

                val presentation = expr.presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.VarDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.VarDecl)))
                assertThat(presentation.presentableText, `is`(nullValue()))
                assertThat(presentation.locationString, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.18) Function Declaration")
    internal inner class FunctionDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
        internal inner class FunctionDecl {
            @Test
            @DisplayName("empty ParamList")
            fun emptyParamList() {
                val decl = parse<XQueryFunctionDecl>("declare function fn:true() external;")[0]
                assertThat(decl.arity, `is`(Range(0, 0)))

                val qname = decl.functionName!!
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val presentation = decl.presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.FunctionDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.FunctionDecl)))
                assertThat(presentation.presentableText, `is`("fn:true#0"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("non-empty ParamList")
            fun nonEmptyParamList() {
                val decl = parse<XQueryFunctionDecl>("declare function test(\$one, \$two) external;")[0]
                assertThat(decl.arity, `is`(Range(2, 2)))

                val qname = decl.functionName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val presentation = decl.presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.FunctionDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.FunctionDecl)))
                assertThat(presentation.presentableText, `is`("test#2"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val decl = parse<XQueryFunctionDecl>("declare function :true() external;")[0]
                assertThat(decl.arity, `is`(Range(0, 0)))
                assertThat(decl.functionName, `is`(nullValue()))

                val presentation = decl.presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.FunctionDecl)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.FunctionDecl)))
                assertThat(presentation.presentableText, `is`(nullValue()))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>(
                    """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    declare function test() {};
                    """
                )[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.DefaultFunction))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.example.co.uk/function"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (34) Param")
        internal inner class Param {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XPathParam>("function (\$x) {}")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XPathParam>("function (\$a:x) {}")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XPathParam>("function (\$Q{http://www.example.com}x) {}")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<XPathParam>("function (\$) {}")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncnameNamespaceResolution() {
                val qname = parse<XPathEQName>("function (\$test) {}")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.None))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`(""))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 (4.19) Option Declaration")
    internal inner class OptionDeclaration {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (37) OptionDecl")
        internal inner class OptionDecl {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("declare option test \"lorem ipsum\";")[0] as XsQNameValue
                assertThat(qname.getNamespaceType(), `is`(XPathNamespaceType.XQuery))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val expanded = qname.expand().toList()
                assertThat(expanded.size, `is`(1))

                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2012/xquery"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
            }
        }
    }
}
