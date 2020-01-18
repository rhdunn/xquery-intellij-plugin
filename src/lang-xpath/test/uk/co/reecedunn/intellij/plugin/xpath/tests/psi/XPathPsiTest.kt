/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.psi

import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.util.Range
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lang.findUsages.XPathFindUsagesProvider
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginAnyItemType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginAnyTextTest
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginQuantifiedExprBinding
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableBinding
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableName
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableReference
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase
import java.math.BigDecimal
import java.math.BigInteger

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XPath 3.1 - IntelliJ Program Structure Interface (PSI)")
private class XPathPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XPath 3.1 (2) Basics")
    internal inner class Basics {
        @Nested
        @DisplayName("XPath 3.1 EBNF (117) URIQualifiedName")
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
            @DisplayName("PsiNameIdentifierOwner")
            fun psiNameIdentifierOwner() {
                val name = parse<XPathURIQualifiedName>(
                    "(: :) Q{http://www.example.com}test"
                )[0] as PsiNameIdentifierOwner

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(31))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (118) BracedURILiteral")
        internal inner class BracedURILiteral {
            @Test
            @DisplayName("braced uri literal content")
            fun bracedUriLiteral() {
                val literal = parse<XPathBracedURILiteral>("Q{Lorem ipsum.\uFFFF}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.context, `is`(XdmUriContext.Namespace))
                assertThat(literal.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("unclosed braced uri literal content")
            fun unclosedBracedUriLiteral() {
                val literal = parse<XPathBracedURILiteral>("Q{Lorem ipsum.")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum."))
                assertThat(literal.context, `is`(XdmUriContext.Namespace))
                assertThat(literal.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (122) QName")
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

                val renamed = name.setName("lorem-ipsum")
                assertThat(renamed, `is`(instanceOf(XPathQName::class.java)))
                assertThat(renamed.text, `is`("a:lorem-ipsum"))
                assertThat((renamed as PsiNameIdentifierOwner).name, `is`("lorem-ipsum"))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (123) NCName")
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
            @DisplayName("PsiNameIdentifierOwner")
            fun psiNameIdentifierOwner() {
                val name = parse<XPathNCName>("(: :) test")[0] as PsiNameIdentifierOwner

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(6))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))

                val renamed = name.setName("lorem-ipsum")
                assertThat(renamed, `is`(instanceOf(XPathNCName::class.java)))
                assertThat(renamed.text, `is`("lorem-ipsum"))
                assertThat((renamed as PsiNameIdentifierOwner).name, `is`("lorem-ipsum"))
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
    @DisplayName("XPath 3.1 (2.5.4) SequenceType Syntax")
    internal inner class SequenceTypeSyntax {
        @Nested
        @DisplayName("XPath 3.1 EBNF (79) SequenceType")
        internal inner class SequenceType {
            @Test
            @DisplayName("empty sequence")
            fun emptySequence() {
                val type = parse<XPathSequenceType>("() instance of empty-sequence ( (::) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("empty-sequence()"))
                assertThat(type.itemType, `is`(nullValue()))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(0))
            }

            @Test
            @DisplayName("optional item")
            fun optionalItem() {
                val type = parse<XPathSequenceType>("() instance of xs:string ?")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("xs:string?"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("optional sequence")
            fun optionalSequence() {
                val type = parse<XPathSequenceType>("() instance of xs:string *")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("xs:string*"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }

            @Test
            @DisplayName("sequence")
            fun sequence() {
                val type = parse<XPathSequenceType>("() instance of xs:string +")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("xs:string+"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }

            @Test
            @DisplayName("parenthesized item type")
            fun parenthesizedItemType() {
                val type = parse<XPathSequenceType>("() instance of ( xs:string ) ?")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(xs:string)?"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("parenthesized sequence type")
            fun parenthesizedSequenceType() {
                val type = parse<XPathSequenceType>("() instance of ( xs:string + ) ?")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(xs:string+)?"))

                // NOTE: For the purposes of the plugin w.r.t. keeping consistent
                // analysis/logic given that mixing SequenceTypes like this is an
                // error, the outer-most SequenceType overrides the inner one.
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (81) ItemType")
        fun itemType() {
            val type = parse<PluginAnyItemType>("() instance of item ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("item()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmItem::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (82) AtomicOrUnionType")
        internal inner class AtomicOrUnionType {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("() instance of test")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("type"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("item type")
            fun itemType() {
                val test = parse<XPathAtomicOrUnionType>("() instance of xs:string")[0]
                assertThat(test.type, `is`(sameInstance(test.children().filterIsInstance<XsQNameValue>().first())))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("xs:string"))
                assertThat(type.typeClass, `is`(sameInstance(XsAnySimpleType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (84) AnyKindTest")
        fun anyKindTest() {
            val type = parse<XPathAnyKindTest>("() instance of node ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("node()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (85) DocumentTest")
        internal inner class DocumentTest {
            @Test
            @DisplayName("any")
            fun any() {
                val test = parse<XPathDocumentTest>("() instance of document-node ( (::) )")[0]
                assertThat(test.rootNodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("document-node()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmDocumentNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("element test")
            fun elementTest() {
                val test = parse<XPathDocumentTest>("() instance of document-node ( element ( (::) ) )")[0]
                assertThat(test.rootNodeType, `is`(instanceOf(XPathElementTest::class.java)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("document-node(element())"))
                assertThat(type.typeClass, `is`(sameInstance(XdmDocumentNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("schema element test")
            fun schemaElementTest() {
                val test = parse<XPathDocumentTest>("() instance of document-node ( schema-element ( test ) )")[0]
                assertThat(test.rootNodeType, `is`(instanceOf(XPathSchemaElementTest::class.java)))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("document-node(schema-element(test))"))
                assertThat(type.typeClass, `is`(sameInstance(XdmDocumentNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (86) TextTest")
        fun textTest() {
            val type = parse<PluginAnyTextTest>("() instance of text ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("text()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmTextNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (87) CommentTest")
        fun commentTest() {
            val type = parse<XPathCommentTest>("() instance of comment ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("comment()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmCommentNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (88) NamespaceNodeTest")
        fun namespaceNodeTest() {
            val type = parse<XPathNamespaceNodeTest>("() instance of namespace-node ( (::) )")[0] as XdmItemType
            assertThat(type.typeName, `is`("namespace-node()"))
            assertThat(type.typeClass, `is`(sameInstance(XdmNamespaceNode::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (89) PITest")
        internal inner class PITest {
            @Test
            @DisplayName("any")
            fun any() {
                val test = parse<XPathPITest>("() instance of processing-instruction ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("processing-instruction()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmProcessingInstructionNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("NCName")
            fun ncname() {
                val test = parse<XPathPITest>("() instance of processing-instruction ( test )")[0]
                assertThat(test.nodeName, `is`(instanceOf(XsNCNameValue::class.java)))
                assertThat((test.nodeName as XsNCNameValue).data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("processing-instruction(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmProcessingInstructionNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("StringLiteral")
            fun stringLiteral() {
                val test = parse<XPathPITest>("() instance of processing-instruction ( \" test \" )")[0]
                assertThat(test.nodeName, `is`(instanceOf(XsNCNameValue::class.java)))

                val nodeName = test.nodeName as XsNCNameValue
                assertThat(nodeName.data, `is`("test"))
                assertThat(
                    nodeName.element,
                    `is`(sameInstance(test.children().filterIsInstance<XPathStringLiteral>().firstOrNull()))
                )

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("processing-instruction(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmProcessingInstructionNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (101) TypeName")
        internal inner class TypeName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("() instance of element(*, test)")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("type"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("item type")
            fun itemType() {
                val test = parse<XPathTypeName>("() instance of element( *, xs:string )")[0]
                assertThat(test.type, `is`(sameInstance(test.children().filterIsInstance<XsQNameValue>().first())))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("xs:string"))
                assertThat(type.typeClass, `is`(sameInstance(XsAnyType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (111) ParenthesizedItemType")
        internal inner class ParenthesizedItemType {
            @Test
            @DisplayName("item type")
            fun itemType() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( text ( (::) ) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(text())"))
                assertThat(type.itemType?.typeName, `is`("text()"))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("error recovery: missing type")
            fun missingType() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( (::) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(empty-sequence())"))
                assertThat(type.itemType, `is`(nullValue()))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(0))
            }

            @Test
            @DisplayName("error recovery: empty sequence")
            fun emptySequence() {
                val type = parse<XPathParenthesizedItemType>(
                    "() instance of ( empty-sequence ( (::) ) )"
                )[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(empty-sequence())"))
                assertThat(type.itemType, `is`(nullValue()))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(0))
            }

            @Test
            @DisplayName("error recovery: optional item")
            fun optionalItem() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( xs:string ? ) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(xs:string?)"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("error recovery: optional sequence")
            fun optionalSequence() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( xs:string * ) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(xs:string*)"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(0))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }

            @Test
            @DisplayName("error recovery: optional item")
            fun sequence() {
                val type = parse<XPathParenthesizedItemType>("() instance of ( xs:string + ) )")[0] as XdmSequenceType
                assertThat(type.typeName, `is`("(xs:string+)"))
                assertThat(type.itemType?.typeName, `is`("xs:string"))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (2.5.5.3) Element Test")
    internal inner class ElementTest {
        @Nested
        @DisplayName("XPath 3.1 EBNF (94) ElementTest")
        internal inner class ElementTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<XPathElementTest>("() instance of element ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<XPathElementTest>("() instance of element ( * )")[0]
                assertThat(test.nodeName, `is`(nullValue()))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name only")
            fun nameOnly() {
                val test = parse<XPathElementTest>("() instance of element ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("type only")
            fun typeOnly() {
                val test = parse<XPathElementTest>("() instance of element ( * , elem-type )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val nodeType = test.nodeType!!
                assertThat(nodeType.typeName, `is`("elem-type"))
                assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                assertThat(nodeType.lowerBound, `is`(1))
                assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(*,elem-type)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name and type")
            fun nameAndType() {
                val test = parse<XPathElementTest>("() instance of element ( test , elem-type )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val nodeType = test.nodeType!!
                assertThat(nodeType.typeName, `is`("elem-type"))
                assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                assertThat(nodeType.lowerBound, `is`(1))
                assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(test,elem-type)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (99) ElementName")
        internal inner class ElementName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("() instance of element(test)")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("element"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (2.5.5.4) Schema Element Test")
    internal inner class SchemaElementTest {
        @Nested
        @DisplayName("XPath 3.1 EBNF (96) SchemaElementTest")
        internal inner class SchemaElementTest {
            @Test
            @DisplayName("missing element declaration")
            fun missingElementDeclaration() {
                val test = parse<XPathSchemaElementTest>("() instance of schema-element ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-element(<unknown>)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("element declaration")
            fun elementDeclaration() {
                val test = parse<XPathSchemaElementTest>("() instance of schema-element ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-element(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (97) ElementDeclaration")
        internal inner class ElementDeclaration {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("() instance of schema-element(test)")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("element"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (2.5.5.5) Attribute Test")
    internal inner class AttributeTest {
        @Nested
        @DisplayName("XPath 3.1 EBNF (90) AttributeTest")
        internal inner class AttributeTest {
            @Test
            @DisplayName("any; empty")
            fun anyEmpty() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("any; wildcard")
            fun anyWildcard() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( * )")[0]
                assertThat(test.nodeName, `is`(nullValue()))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name only")
            fun nameOnly() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("type only")
            fun typeOnly() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( * , attr-type )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val nodeType = test.nodeType!!
                assertThat(nodeType.typeName, `is`("attr-type"))
                assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                assertThat(nodeType.lowerBound, `is`(1))
                assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute(*,attr-type)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("name and type")
            fun nameAndType() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( test , attr-type )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val nodeType = test.nodeType!!
                assertThat(nodeType.typeName, `is`("attr-type"))
                assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                assertThat(nodeType.lowerBound, `is`(1))
                assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute(test,attr-type)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (98) AttributeName")
        internal inner class AttributeName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("() instance of attribute(test)")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("attribute"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (2.5.5.6) Schema Attribute Test")
    internal inner class SchemaAttributeTest {
        @Nested
        @DisplayName("XPath 3.1 EBNF (92) SchemaAttributeTest")
        internal inner class SchemaAttributeTest {
            @Test
            @DisplayName("missing attribute declaration")
            fun missingAttributeDeclaration() {
                val test = parse<XPathSchemaAttributeTest>("() instance of schema-attribute ( (::) )")[0]
                assertThat(test.nodeName, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-attribute(<unknown>)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("attribute declaration")
            fun attributeDeclaration() {
                val test = parse<XPathSchemaAttributeTest>("() instance of schema-attribute ( test )")[0]
                assertThat(test.nodeName?.localName!!.data, `is`("test"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("schema-attribute(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (93) AttributeDeclaration")
        internal inner class AttributeDeclaration {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathNCName>("() instance of schema-attribute(test)")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("attribute"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (2.5.5.7) Function Test")
    internal inner class FunctionTest {
        @Test
        @DisplayName("XPath 3.1 EBNF (103) AnyFunctionTest")
        fun anyFunctionTest() {
            val test = parse<XPathAnyFunctionTest>("() instance of function ( * )")[0]

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("function(*)"))
            assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (104) TypedFunctionTest")
        internal inner class TypedFunctionTest {
            @Test
            @DisplayName("no parameters")
            fun noParameters() {
                val test = parse<XPathTypedFunctionTest>("() instance of function ( ) as empty-sequence ( (::) )")[0]
                assertThat(test.returnType?.typeName, `is`("empty-sequence()"))

                val paramTypes = test.paramTypes.toList()
                assertThat(paramTypes.size, `is`(0))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("function() as empty-sequence()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("one parameter")
            fun oneParameter() {
                val test = parse<XPathTypedFunctionTest>("() instance of function ( xs:float ) as xs:integer")[0]
                assertThat(test.returnType?.typeName, `is`("xs:integer"))

                val paramTypes = test.paramTypes.toList()
                assertThat(paramTypes.size, `is`(1))
                assertThat(paramTypes[0].typeName, `is`("xs:float"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("function(xs:float) as xs:integer"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("multiple parameters")
            fun multipleParameters() {
                val test = parse<XPathTypedFunctionTest>(
                    "() instance of function ( xs:long , array ( * ) ) as xs:double +"
                )[0]
                assertThat(test.returnType?.typeName, `is`("xs:double+"))

                val paramTypes = test.paramTypes.toList()
                assertThat(paramTypes.size, `is`(2))
                assertThat(paramTypes[0].typeName, `is`("xs:long"))
                assertThat(paramTypes[1].typeName, `is`("array(*)"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("function(xs:long, array(*)) as xs:double+"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("missing return type")
            fun missingReturnType() {
                val test = parse<XPathTypedFunctionTest>("() instance of function ( map ( * ) )")[0]
                assertThat(test.returnType, `is`(nullValue()))

                val paramTypes = test.paramTypes.toList()
                assertThat(paramTypes.size, `is`(1))
                assertThat(paramTypes[0].typeName, `is`("map(*)"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("function(map(*)) as item()*"))
                assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (2.5.5.8) Map Test")
    internal inner class MapTest {
        @Test
        @DisplayName("XPath 3.1 EBNF (106) AnyMapTest")
        fun anyMapTest() {
            val type = parse<XPathAnyMapTest>("() instance of map ( * )")[0] as XdmItemType
            assertThat(type.typeName, `is`("map(*)"))
            assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (107) TypedMapTest")
        internal inner class TypedMapTest {
            @Test
            @DisplayName("key and value type")
            fun keyAndValueType() {
                val test = parse<XPathTypedMapTest>("() instance of map ( xs:string , xs:int )")[0]
                assertThat(test.keyType?.typeName, `is`("xs:string"))
                assertThat(test.valueType?.typeName, `is`("xs:int"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(xs:string, xs:int)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("missing key type")
            fun missingKeyType() {
                val test = parse<XPathTypedMapTest>("() instance of map ( , xs:int )")[0]
                assertThat(test.keyType, `is`(nullValue()))
                assertThat(test.valueType?.typeName, `is`("xs:int"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(xs:anyAtomicType, xs:int)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("missing value type")
            fun missingValueType() {
                val test = parse<XPathTypedMapTest>("() instance of map ( xs:string , )")[0]
                assertThat(test.keyType?.typeName, `is`("xs:string"))
                assertThat(test.valueType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(xs:string, item()*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("missing key and value type")
            fun missingKeyAndValueType() {
                val test = parse<XPathTypedMapTest>("() instance of map ( , )")[0]
                assertThat(test.keyType, `is`(nullValue()))
                assertThat(test.valueType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (2.5.5.9) Array Test")
    internal inner class ArrayTest {
        @Nested
        @DisplayName("XPath 3.1 EBNF (109) AnyArrayTest")
        internal inner class AnyArrayTest {
            @Test
            @DisplayName("any array test")
            fun anyArrayTest() {
                val type = parse<XPathAnyArrayTest>("() instance of array ( * )")[0] as XdmItemType
                assertThat(type.typeName, `is`("array(*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmArray::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("missing star or sequence type")
            fun missingStarOrSequenceType() {
                val type = parse<XPathAnyArrayTest>("() instance of array ( )")[0] as XdmItemType
                assertThat(type.typeName, `is`("array(*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmArray::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (110) TypedArrayTest")
        fun typedArrayTest() {
            val test = parse<XPathTypedArrayTest>("() instance of array ( node ( ) )")[0]
            assertThat(test.memberType.typeName, `is`("node()"))

            val type = test as XdmItemType
            assertThat(type.typeName, `is`("array(node())"))
            assertThat(type.typeClass, `is`(sameInstance(XdmArray::class.java)))

            assertThat(type.itemType, `is`(sameInstance(type)))
            assertThat(type.lowerBound, `is`(1))
            assertThat(type.upperBound, `is`(1))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.1.1) Literals")
    internal inner class Literals {
        @Test
        @DisplayName("XPath 3.1 EBNF (113) IntegerLiteral")
        fun integerLiteral() {
            val literal = parse<XPathIntegerLiteral>("123")[0] as XsIntegerValue
            assertThat(literal.data, `is`(BigInteger.valueOf(123)))
            assertThat(literal.toInt(), `is`(123))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (114) DecimalLiteral")
        fun decimalLiteral() {
            val literal = parse<XPathDecimalLiteral>("12.34")[0] as XsDecimalValue
            assertThat(literal.data, `is`(BigDecimal(BigInteger.valueOf(1234), 2)))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (115) DoubleLiteral")
        fun doubleLiteral() {
            val literal = parse<XPathDoubleLiteral>("1e3")[0] as XsDoubleValue
            assertThat(literal.data, `is`(1e3))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (116) StringLiteral")
        internal inner class StringLiteral {
            @Test
            @DisplayName("string literal content")
            fun stringLiteral() {
                val literal = parse<XPathStringLiteral>("\"Lorem ipsum.\uFFFF\"")[0] as XsStringValue
                assertThat(literal.data, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("unclosed string literal content")
            fun unclosedStringLiteral() {
                val literal = parse<XPathStringLiteral>("\"Lorem ipsum.")[0] as XsStringValue
                assertThat(literal.data, `is`("Lorem ipsum."))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val literal = parse<XPathStringLiteral>("'''\"\"'")[0] as XsStringValue
                assertThat(literal.data, `is`("'\"\""))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val literal = parse<XPathStringLiteral>("\"''\"\"\"")[0] as XsStringValue
                assertThat(literal.data, `is`("''\""))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.1.2) Variable References")
    internal inner class VariableReferences {
        @Nested
        @DisplayName("XPath 3.1 EBNF (59) VarRef")
        internal inner class VarRef {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XPathVarRef>("let \$x := 2 return \$y")[0] as XdmVariableReference

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XPathVarRef>("let \$a:x := 2 return \$a:y")[0] as XdmVariableReference

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
                )[0] as XdmVariableReference

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("y"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<XPathVarRef>("let \$x := 2 return \$")[0] as XdmVariableReference
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (60) VarName")
        internal inner class VarName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XPathVarName>("let \$x := 2 return \$y")[0] as XdmVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XPathVarName>("let \$a:x := 2 return \$a:y")[0] as XdmVariableName

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
                )[0] as XdmVariableName

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncnameNamespaceResolution() {
                val qname = parse<XPathNCName>("\$test")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("variable"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.1.5) Static Function Calls")
    internal inner class StaticFunctionCalls {
        @Nested
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("non-empty ArgumentList")
            fun nonEmptyArguments() {
                val f = parse<XPathFunctionCall>("math:pow(2, 8)")[0] as XdmFunctionReference
                assertThat(f.arity, `is`(2))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("math"))
                assertThat(qname.localName!!.data, `is`("pow"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(2))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("empty ArgumentList")
            fun emptyArguments() {
                val f = parse<XPathFunctionCall>("fn:true()")[0] as XdmFunctionReference
                assertThat(f.arity, `is`(0))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(0))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("ArgumentPlaceholder")
            fun argumentPlaceholder() {
                val f = parse<XPathFunctionCall>("math:sin(?)")[0] as XdmFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("math"))
                assertThat(qname.localName!!.data, `is`("sin"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(1))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathFunctionCall>(":true(1)")[0] as XdmFunctionReference
                assertThat(f.arity, `is`(1))
                assertThat(f.functionName, `is`(nullValue()))

                val args = (f as XPathFunctionCall).argumentList
                assertThat(args.arity, `is`(1))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("true()")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("function"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (50) ArgumentList")
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
    }

    @Nested
    @DisplayName("XPath 3.1 (3.1.6) Named Function References")
    internal inner class NamedFunctionReferences {
        @Nested
        @DisplayName("XPath 3.1 EBNF (67) NamedFunctionRef")
        internal inner class NamedFunctionRef {
            @Test
            @DisplayName("named function reference")
            fun namedFunctionRef() {
                val f = parse<XPathNamedFunctionRef>("true#3")[0] as XdmFunctionReference
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
                val f = parse<XPathNamedFunctionRef>("true#")[0] as XdmFunctionReference
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
                val f = parse<XPathNamedFunctionRef>(":true#0")[0] as XdmFunctionReference
                assertThat(f.arity, `is`(0))
                assertThat(f.functionName, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("true#0")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("function"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.1.7) Inline Function Expression")
    internal inner class InlineFunctionExpressions {
        @Nested
        @DisplayName("XPath 3.1 EBNF (3) Param")
        internal inner class Param {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XPathParam>("function (\$x) {}")[0] as XdmVariableBinding
                assertThat((expr as XdmVariableType).variableType?.typeName, `is`(nullValue()))

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (expr as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.presentableText, `is`("\$x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XPathParam>("function (\$a:x) {}")[0] as XdmVariableBinding
                assertThat((expr as XdmVariableType).variableType?.typeName, `is`(nullValue()))

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (expr as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.presentableText, `is`("\$a:x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XPathParam>("function (\$Q{http://www.example.com}x) {}")[0] as XdmVariableBinding
                assertThat((expr as XdmVariableType).variableType?.typeName, `is`(nullValue()))

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (expr as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.presentableText, `is`("\$Q{http://www.example.com}x"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<XPathParam>("function (\$) {}")[0] as XdmVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
                assertThat((expr as XdmVariableType).variableType?.typeName, `is`(nullValue()))

                val presentation = (expr as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.presentableText, `is`(nullValue()))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("with type")
            fun withType() {
                val expr = parse<XPathParam>("function ( \$x  as  element() ) {}")[0] as XdmVariableBinding
                assertThat((expr as XdmVariableType).variableType?.typeName, `is`("element()"))

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val presentation = (expr as NavigatablePsiElement).presentation!!
                assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                assertThat(presentation.presentableText, `is`("\$x as element()"))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncnameNamespaceResolution() {
                val qname = parse<XPathEQName>("function (\$test) {}")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("parameter"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (68) InlineFunctionExpr")
        internal inner class InlineFunctionExpr {
            @Test
            @DisplayName("empty ParamList")
            fun emptyParamList() {
                val decl = parse<XdmFunctionDeclaration>("function () {}")[0]
                assertThat(decl.functionName, `is`(nullValue()))
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(0, 0)))
                assertThat(decl.params.size, `is`(0))
                assertThat(decl.isVariadic, `is`(false))
            }

            @Test
            @DisplayName("non-empty ParamList")
            fun nonEmptyParamList() {
                val decl = parse<XdmFunctionDeclaration>("function (\$one, \$two) {}")[0]
                assertThat(decl.functionName, `is`(nullValue()))
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(2, 2)))
                assertThat(decl.isVariadic, `is`(false))

                assertThat(decl.params.size, `is`(2))
                assertThat(op_qname_presentation(decl.params[0].variableName!!), `is`("one"))
                assertThat(op_qname_presentation(decl.params[1].variableName!!), `is`("two"))
            }

            @Test
            @DisplayName("non-empty ParamList with types")
            fun nonEmptyParamListWithTypes() {
                val decl =
                    parse<XdmFunctionDeclaration>("function (\$one  as  array ( * ), \$two  as  node((::))) {}")[0]
                assertThat(decl.functionName, `is`(nullValue()))
                assertThat(decl.returnType, `is`(nullValue()))
                assertThat(decl.arity, `is`(Range(2, 2)))
                assertThat(decl.isVariadic, `is`(false))

                assertThat(decl.params.size, `is`(2))
                assertThat(op_qname_presentation(decl.params[0].variableName!!), `is`("one"))
                assertThat(op_qname_presentation(decl.params[1].variableName!!), `is`("two"))
            }

            @Test
            @DisplayName("return type")
            fun returnType() {
                val decl = parse<XdmFunctionDeclaration>("function ()  as  xs:boolean  {}")[0]
                assertThat(decl.functionName, `is`(nullValue()))
                assertThat(decl.returnType?.typeName, `is`("xs:boolean"))
                assertThat(decl.arity, `is`(Range(0, 0)))
                assertThat(decl.params.size, `is`(0))
                assertThat(decl.isVariadic, `is`(false))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.3.2.1) Axes")
    internal inner class Axes {
        @Nested
        @DisplayName("XPath 3.1 EBNF (41) ForwardAxis")
        internal inner class ForwardAxis {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>(
                    """
                    child::one, descendant::two, attribute::three, self::four, descendant-or-self::five,
                    following-sibling::six, following::seven, namespace::eight
                    """
                )
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

            @Test
            @DisplayName("find usages type name")
            fun findUsagesTypeName() {
                val steps = parse<XPathNodeTest>(
                    """
                    child::one, descendant::two, attribute::three, self::four, descendant-or-self::five,
                    following-sibling::six, following::seven, namespace::eight
                    """
                ).map { it.walkTree().filterIsInstance<XsQNameValue>().first().element!! }
                assertThat(steps.size, `is`(8))
                assertThat(XPathFindUsagesProvider.getType(steps[0]), `is`("element")) // child
                assertThat(XPathFindUsagesProvider.getType(steps[1]), `is`("element")) // descendant
                assertThat(XPathFindUsagesProvider.getType(steps[2]), `is`("attribute")) // attribute
                assertThat(XPathFindUsagesProvider.getType(steps[3]), `is`("element")) // self
                assertThat(XPathFindUsagesProvider.getType(steps[4]), `is`("element")) // descendant-or-self
                assertThat(XPathFindUsagesProvider.getType(steps[5]), `is`("element")) // following-sibling
                assertThat(XPathFindUsagesProvider.getType(steps[6]), `is`("element")) // following
                assertThat(XPathFindUsagesProvider.getType(steps[7]), `is`("namespace")) // namespace
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (44) ReverseAxis")
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

            @Test
            @DisplayName("find usages type name")
            fun findUsagesTypeName() {
                val steps = parse<XPathNodeTest>(
                    "parent::one, ancestor::two, preceding-sibling::three, preceding::four, ancestor-or-self::five"
                ).map { it.walkTree().filterIsInstance<XsQNameValue>().first().element!! }
                assertThat(steps.size, `is`(5))
                assertThat(XPathFindUsagesProvider.getType(steps[0]), `is`("element")) // parent
                assertThat(XPathFindUsagesProvider.getType(steps[1]), `is`("element")) // ancestor
                assertThat(XPathFindUsagesProvider.getType(steps[2]), `is`("element")) // preceding-sibling
                assertThat(XPathFindUsagesProvider.getType(steps[3]), `is`("element")) // preceding
                assertThat(XPathFindUsagesProvider.getType(steps[4]), `is`("element")) // ancestor-or-self
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.3.2.2) Node Tests")
    internal inner class NodeTests {
        @Nested
        @DisplayName("XPath 3.1 EBNF (47) NameTest")
        internal inner class NameTest {
            @Test
            @DisplayName("NCName namespace resolution; element principal node kind")
            fun elementNcname() {
                val qname = parse<XPathEQName>("ancestor::test")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("element"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName namespace resolution; attribute principal node kind")
            fun attributeNcname() {
                val qname = parse<XPathEQName>("attribute::test")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("attribute"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("NCName namespace resolution; namespace principal node kind")
            fun namespaceNcname() {
                val qname = parse<XPathEQName>("namespace::test")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("namespace"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (48) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("any")
            fun any() {
                val qname = parse<XPathWildcard>("*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

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

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
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
    @DisplayName("XPath 3.1 (3.3.5) Abbreviated Syntax")
    internal inner class AbbreviatedSyntax {
        @Nested
        @DisplayName("XPath 3.1 EBNF (42) AbbrevForwardStep")
        internal inner class AbbrevForwardStep {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>("one, @two")
                assertThat(steps.size, `is`(2))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element))
                assertThat(steps[1].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Attribute))
            }

            @Test
            @DisplayName("find usages type name")
            fun findUsagesTypeName() {
                val steps = parse<XPathNodeTest>("one, @two").map {
                    it.walkTree().filterIsInstance<XsQNameValue>().first().element!!
                }
                assertThat(steps.size, `is`(2))
                assertThat(XPathFindUsagesProvider.getType(steps[0]), `is`("element"))
                assertThat(XPathFindUsagesProvider.getType(steps[1]), `is`("attribute"))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.11.1) Maps")
    internal inner class Maps {
        @Nested
        @DisplayName("XPath 3.1 EBNF (70) MapConstructorEntry")
        internal inner class MapConstructorEntry {
            @Test
            @DisplayName("key, value")
            fun keyValue() {
                val entry = parse<XPathMapConstructorEntry>("map { \"1\" : \"one\" }")[0]
                assertThat(entry.separator.node.elementType, `is`(XPathTokenType.QNAME_SEPARATOR))
            }

            @Test
            @DisplayName("key, no value")
            fun saxon() {
                val entry = parse<XPathMapConstructorEntry>("map { \$ a }")[0]
                assertThat(entry.separator.node.elementType, `is`(XPathElementType.MAP_KEY_EXPR))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.13) Quantified Expressions")
    internal inner class QuantifiedExpressions {
        @Nested
        @DisplayName("XPath 3.1 EBNF (14) QuantifiedExpr")
        internal inner class QuantifiedExpr {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<PluginQuantifiedExprBinding>(
                    "some \$x in \$y satisfies \$z"
                )[0] as XdmVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<PluginQuantifiedExprBinding>(
                    "some \$a:x in \$a:y satisfies \$a:z"
                )[0] as XdmVariableBinding

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
                )[0] as XdmVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<PluginQuantifiedExprBinding>("some \$")[0] as XdmVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.18.3) Cast")
    internal inner class Cast {
        @Nested
        @DisplayName("XPath 3.1 EBNF (100) SimpleTypeName")
        internal inner class SimpleTypeName {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("() cast as test")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("type"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("item type")
            fun itemType() {
                val test = parse<XPathSimpleTypeName>("() cast as xs:string")[0]
                assertThat(op_qname_presentation(test.type), `is`("xs:string"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("xs:string"))
                assertThat(type.typeClass, `is`(sameInstance(XsAnyType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(Int.MAX_VALUE))
            }
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (77) SingleType")
        fun singleType() {
            val type = parse<XPathSingleType>("() cast as xs:string ?")[0] as XdmItemType
            assertThat(type.typeName, `is`("xs:string?"))
            assertThat(type.typeClass, `is`(sameInstance(XsAnyType::class.java)))

            assertThat(type.itemType, `is`(sameInstance((type as PsiElement).firstChild)))
            assertThat(type.lowerBound, `is`(0))
            assertThat(type.upperBound, `is`(Int.MAX_VALUE))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.20) Arrow Operator")
    internal inner class ArrowOperator {
        @Nested
        @DisplayName("XPath 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowFunctionSpecifier {
            @Test
            @DisplayName("EQName specifier, non-empty ArgumentList")
            fun nonEmptyArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => format-date(1, 2, 3,  4)")[0] as XdmFunctionReference
                assertThat(f.arity, `is`(5))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("format-date"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathArrowFunctionSpecifier).argumentList!!
                assertThat(args.arity, `is`(4))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("EQName specifier, empty ArgumentList")
            fun emptyArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => upper-case()")[0] as XdmFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("upper-case"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathArrowFunctionSpecifier).argumentList!!
                assertThat(args.arity, `is`(0))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("EQName specifier, empty ArgumentList, second call in the chain")
            fun secondFunctionSpecifier() {
                val f = parse<XPathArrowFunctionSpecifier>(
                    "\$x => upper-case() => string-to-codepoints()"
                )[1] as XdmFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("string-to-codepoints"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                val args = (f as XPathArrowFunctionSpecifier).argumentList!!
                assertThat(args.arity, `is`(0))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("EQName specifier, missing ArgumentList")
            fun missingArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => upper-case")[0] as XdmFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("upper-case"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat((f as XPathArrowFunctionSpecifier).argumentList, `is`(nullValue()))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => :upper-case()")[0] as XdmFunctionReference
                assertThat(f.arity, `is`(1))
                assertThat(f.functionName, `is`(nullValue()))

                val args = (f as XPathArrowFunctionSpecifier).argumentList!!
                assertThat(args.arity, `is`(0))
                assertThat(args.functionReference, `is`(sameInstance(f)))

                val bindings = args.bindings
                assertThat(bindings.size, `is`(0))
            }

            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("() => test()")[0] as XsQNameValue
                assertThat(XPathFindUsagesProvider.getType(qname.element!!), `is`("function"))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }
        }
    }
}
