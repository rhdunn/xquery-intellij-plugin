/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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

import com.intellij.navigation.NavigationItem
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.getPrincipalNodeKind
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.reference.XPathFunctionNameReference
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmVariadic
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.*
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.flwor.XpmBindingCollectionType
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.flwor.XpmFlworExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.flwor.XpmForClause
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.flwor.XpmLetClause
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmContextItem
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmEmptyExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.type.XpmSequenceTypeExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.type.XpmSequenceTypeOperation
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.*
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.XpmArrayExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.XpmMapExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.keyName
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.path.XpmAxisType
import uk.co.reecedunn.intellij.plugin.xpm.optree.path.XpmPathStep
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmAssignableVariable
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmCollectionBinding
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmParameter
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference
import java.math.BigDecimal
import java.math.BigInteger

@Suppress("ClassName", "RedundantVisibilityModifier", "Reformat")
@DisplayName("XPath 3.1 - IntelliJ Program Structure Interface (PSI)")
class XPathPsiTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XPathPsiTest")

    @Nested
    @DisplayName("XPath 4.0 ED (2) Basics ; XPath 3.1 (2) Basics")
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
                val file = name.containingFile

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(31))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))

                DebugUtil.performPsiModification<Throwable>("rename") {
                    val renamed = name.setName("lorem-ipsum")
                    assertThat(renamed, `is`(instanceOf(XPathURIQualifiedName::class.java)))
                    assertThat(renamed.text, `is`("Q{http://www.example.com}lorem-ipsum"))
                    assertThat((renamed as PsiNameIdentifierOwner).name, `is`("lorem-ipsum"))
                }

                assertThat(file.text, `is`("(: :) Q{http://www.example.com}lorem-ipsum"))
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
                val file = name.containingFile

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(8))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))

                DebugUtil.performPsiModification<Throwable>("rename") {
                    val renamed = name.setName("lorem-ipsum")
                    assertThat(renamed, `is`(instanceOf(XPathQName::class.java)))
                    assertThat(renamed.text, `is`("a:lorem-ipsum"))
                    assertThat((renamed as PsiNameIdentifierOwner).name, `is`("lorem-ipsum"))
                }

                assertThat(file.text, `is`("(: :) a:lorem-ipsum"))
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
                val file = name.containingFile

                assertThat(name.name, `is`("test"))
                assertThat(name.textOffset, `is`(6))

                assertThat(name.nameIdentifier, `is`(instanceOf(XmlNCNameImpl::class.java)))
                assertThat(name.nameIdentifier?.text, `is`("test"))

                DebugUtil.performPsiModification<Throwable>("rename") {
                    val renamed = name.setName("lorem-ipsum")
                    assertThat(renamed, `is`(instanceOf(XPathNCName::class.java)))
                    assertThat(renamed.text, `is`("lorem-ipsum"))
                    assertThat((renamed as PsiNameIdentifierOwner).name, `is`("lorem-ipsum"))
                }

                assertThat(file.text, `is`("(: :) lorem-ipsum"))
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
    @DisplayName("XPath 4.0 ED (3.4) Sequence Types ; XPath 3.1 (2.5.4) SequenceType Syntax")
    internal inner class SequenceTypes {
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
    }

    @Nested
    @DisplayName("XPath 4.0 ED (3.6) Item Types ; XPath 3.1 (2.5.5) SequenceType Matching")
    internal inner class ItemTypes {
        @Nested
        @DisplayName("XPath 4.0 ED (3.6.1) General Item Types")
        internal inner class GeneralItemTypes {
            @Test
            @DisplayName("XPath 3.1 EBNF (81) ItemType ; XPath 4.0 ED EBNF (97) AnyItemTest")
            fun itemType() {
                val type = parse<XPathAnyItemTest>("() instance of item ( (::) )")[0] as XdmItemType
                assertThat(type.typeName, `is`("item()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmItem::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
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
        @DisplayName("XPath 4.0 ED (3.6.2) Atomic and Union Types")
        internal inner class AtomicAndUnionTypes {
            @Nested
            @DisplayName("XPath 3.1 EBNF (82) AtomicOrUnionType")
            internal inner class AtomicOrUnionType {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathEQName>("() instance of test")[0] as XsQNameValue
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

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

            @Nested
            @DisplayName("XPath 3.1 EBNF (101) TypeName")
            internal inner class TypeName {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathEQName>("() instance of element(*, test)")[0] as XsQNameValue
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

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

                @Test
                @DisplayName("invalid QName")
                fun invalidQName() {
                    val test = parse<XPathTypeName>("() instance of element( *, xs: )")[0]
                    assertThat(test.type, `is`(sameInstance(test.children().filterIsInstance<XsQNameValue>().first())))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`(""))
                    assertThat(type.typeClass, `is`(sameInstance(XsAnyType::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(Int.MAX_VALUE))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (3.6.2.1) Local Union Types")
        internal inner class LocalUnionTypes {
            @Nested
            @DisplayName("XPath 4.0 ED EBNF (127) LocalUnionType")
            internal inner class LocalUnionType {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathEQName>("() instance of union(test)")[0] as XsQNameValue
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("test"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))
                }

                @Test
                @DisplayName("empty")
                fun empty() {
                    val test = parse<XPathLocalUnionType>("() instance of union ( (::) )")[0]

                    val memberTypes = test.memberTypes.toList()
                    assertThat(memberTypes.size, `is`(0))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("union()"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmAnyUnionType::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("one")
                fun one() {
                    val test = parse<XPathLocalUnionType>("() instance of union ( xs:string )")[0]

                    val memberTypes = test.memberTypes.toList()
                    assertThat(memberTypes.size, `is`(1))
                    assertThat(memberTypes[0].typeName, `is`("xs:string"))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("union(xs:string)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmAnyUnionType::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("many")
                fun many() {
                    val test = parse<XPathLocalUnionType>("() instance of union ( xs:string , xs:anyURI )")[0]

                    val memberTypes = test.memberTypes.toList()
                    assertThat(memberTypes.size, `is`(2))
                    assertThat(memberTypes[0].typeName, `is`("xs:string"))
                    assertThat(memberTypes[1].typeName, `is`("xs:anyURI"))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("union(xs:string, xs:anyURI)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmAnyUnionType::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (3.6.2.2) Enumeration Types")
        internal inner class EnumerationTypes {
            @Nested
            @DisplayName("XPath 4.0 ED EBNF (128) EnumerationType")
            internal inner class EnumerationType {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val test = parse<XPathEnumerationType>("() instance of enum ( (::) )")[0]

                    val values = test.values.toList()
                    assertThat(values.size, `is`(0))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("enum()"))
                    assertThat(type.typeClass, `is`(sameInstance(XsStringValue::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("one")
                fun one() {
                    val test = parse<XPathEnumerationType>("() instance of enum ( \"one\" )")[0]

                    val values = test.values.toList()
                    assertThat(values.size, `is`(1))
                    assertThat(values[0].data, `is`("one"))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("enum(\"one\")"))
                    assertThat(type.typeClass, `is`(sameInstance(XsStringValue::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("many")
                fun many() {
                    val test = parse<XPathEnumerationType>("() instance of enum ( \"one\" , \"two\" )")[0]

                    val values = test.values.toList()
                    assertThat(values.size, `is`(2))
                    assertThat(values[0].data, `is`("one"))
                    assertThat(values[1].data, `is`("two"))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("enum(\"one\", \"two\")"))
                    assertThat(type.typeClass, `is`(sameInstance(XsStringValue::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (3.6.3.1) Simple Node Tests")
        internal inner class SimpleNodeTests {
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
        }

        @Nested
        @DisplayName("XPath 4.0 ED (3.6.3.2) Element Test ; XPath 3.1 (2.5.5.3) Element Test")
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

                @Test
                @DisplayName("invalid TypeName")
                fun invalidTypeName() {
                    val test = parse<XPathElementTest>("() instance of element ( test , xs: )")[0]
                    assertThat(test.nodeName?.localName!!.data, `is`("test"))
                    assertThat(test.nodeType, `is`(nullValue()))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("element(test)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
            }

            @Test
            @DisplayName("XPath 4.0 ED EBNF (109) ElementTest ; XPath 4.0 ED EBNF (55) NameTest")
            fun elementTest_nameTest() {
                val test = parse<XPathElementTest>("() instance of element ( *:test )")[0]
                assertThat(qname_presentation(test.nodeName!!), `is`("*:test"))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(*:test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (99) ElementName")
            internal inner class ElementName {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathEQName>("() instance of element(test)")[0] as XsQNameValue
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Element))

                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("test"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (3.6.3.3) Schema Element Test ; XPath 3.1 (2.5.5.4) Schema Element Test")
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
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Element))

                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("test"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (3.6.3.4) Attribute Test ; XPath 3.1 (2.5.5.5) Attribute Test")
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

                @Test
                @DisplayName("invalid TypeName")
                fun invalidTypeName() {
                    val test = parse<XPathAttributeTest>("() instance of attribute ( test , xs: )")[0]
                    assertThat(test.nodeName?.localName!!.data, `is`("test"))
                    assertThat(test.nodeType, `is`(nullValue()))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("attribute(test)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
            }

            @Test
            @DisplayName("XPath 4.0 ED EBNF (109) AttributeTest ; XPath 4.0 ED EBNF (55) NameTest")
            fun attributeTest_nameTest() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( *:test )")[0]
                assertThat(qname_presentation(test.nodeName!!), `is`("*:test"))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute(*:test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (98) AttributeName")
            internal inner class AttributeName {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathNCName>("() instance of attribute(test)")[0] as XsQNameValue
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Attribute))

                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("test"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (3.6.3.5) Schema Attribute Test ; XPath 3.1 (2.5.5.6) Schema Attribute Test")
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
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Attribute))

                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("test"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (3.6.4.1) Function Test ; XPath 3.1 (2.5.5.7) Function Test")
        internal inner class FunctionTest {
            @Test
            @DisplayName("XPath 3.1 EBNF (103) AnyFunctionTest")
            fun anyFunctionTest() {
                val test = parse<XPathAnyFunctionTest>("() instance of function ( * )")[0]
                assertThat(test.annotations.count(), `is`(0))

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
                    val test = parse<XPathTypedFunctionTest>(
                        "() instance of function ( ) as empty-sequence ( (::) )"
                    )[0]
                    assertThat(test.returnType?.typeName, `is`("empty-sequence()"))
                    assertThat(test.annotations.count(), `is`(0))

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
                    assertThat(test.annotations.count(), `is`(0))

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
                    assertThat(test.annotations.count(), `is`(0))

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
                    assertThat(test.annotations.count(), `is`(0))

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
        @DisplayName("XPath 4.0 ED (3.6.4.2) Map Test ; XPath 3.1 (2.5.5.8) Map Test")
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

            @Nested
            @DisplayName("XPath 4.0 ED EBNF (121) TypedMapTest ; XPath 4.0 ED EBNF (127) LocalUnionType")
            internal inner class TypedMapTest_LocalUnionType {
                @Test
                @DisplayName("union key type")
                fun unionKeyType() {
                    val test = parse<XPathTypedMapTest>("() instance of map ( union ( xs:string , xs:float ) , xs:int )")[0]
                    assertThat(test.keyType?.typeName, `is`("union(xs:string, xs:float)"))
                    assertThat(test.valueType?.typeName, `is`("xs:int"))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("map(union(xs:string, xs:float), xs:int)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (3.6.4.3) Record Test")
        internal inner class RecordTest {
            @Nested
            @DisplayName("XPath 4.0 ED EBNF (122) RecordTest")
            internal inner class RecordTest {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val test = parse<XPathRecordTest>("() instance of record ( (::) )")[0]
                    assertThat(test.fields.count(), `is`(0))
                    assertThat(test.isExtensible, `is`(true))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("map(*)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("one field")
                fun one() {
                    val test = parse<XPathRecordTest>("() instance of record ( test )")[0]
                    assertThat(test.fields.count(), `is`(1))
                    assertThat(test.isExtensible, `is`(false))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("multiple fields")
                fun multiple() {
                    val test = parse<XPathRecordTest>("() instance of record ( x as xs:float , y as xs:float )")[0]
                    assertThat(test.fields.count(), `is`(2))
                    assertThat(test.isExtensible, `is`(false))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(x as xs:float, y as xs:float)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("empty; extensible")
                fun emptyExtensible() {
                    val test = parse<XPathRecordTest>("() instance of record ( * )")[0]
                    assertThat(test.fields.count(), `is`(0))
                    assertThat(test.isExtensible, `is`(true))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("map(*)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("multiple fields; extensible")
                fun multipleExtensible() {
                    val test = parse<XPathRecordTest>("() instance of record ( x as xs:float , y as xs:float , * )")[0]
                    assertThat(test.fields.count(), `is`(2))
                    assertThat(test.isExtensible, `is`(true))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(x as xs:float, y as xs:float, *)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
            }

            @Nested
            @DisplayName("XPath 4.0 ED EBNF (122) FieldDeclaration")
            internal inner class FieldDeclaration {
                @Test
                @DisplayName("required; unspecified type")
                fun nameOnlyRequired() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldType, `is`(nullValue()))
                    assertThat(field.fieldSeparator, `is`(nullValue()))
                    assertThat(field.isOptional, `is`(false))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("optional; unspecified type")
                fun nameOnlyOptional() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test ? )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldType, `is`(nullValue()))
                    assertThat(field.fieldSeparator, `is`(nullValue()))
                    assertThat(field.isOptional, `is`(true))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test?)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("StringLiteral name; no space in name")
                fun stringLiteralName_noSpace() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( 'test' )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldType, `is`(nullValue()))
                    assertThat(field.fieldSeparator, `is`(nullValue()))
                    assertThat(field.isOptional, `is`(false))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("StringLiteral name; space in name")
                fun stringLiteralName_withSpace() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( 'test key name' )")[0]
                    assertThat(field.fieldName.data, `is`("test key name"))
                    assertThat(field.fieldType, `is`(nullValue()))
                    assertThat(field.fieldSeparator, `is`(nullValue()))
                    assertThat(field.isOptional, `is`(false))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(\"test key name\")"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
            }

            @Nested
            @DisplayName("XPath 4.0 ED EBNF (122) FieldDeclaration ; XPath 4.0 ED EBNF (94) SequenceType")
            internal inner class FieldDeclaration_SequenceType {
                @Test
                @DisplayName("required field")
                fun requiredField() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test as xs:string )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldType?.typeName, `is`("xs:string"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                    assertThat(field.isOptional, `is`(false))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test as xs:string)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("optional field")
                fun optionalField() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test ? as xs:string )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldType?.typeName, `is`("xs:string"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                    assertThat(field.isOptional, `is`(true))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test? as xs:string)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("required field; colon type separator")
                fun requiredField_colon() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test : xs:string )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldType?.typeName, `is`("xs:string"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.QNAME_SEPARATOR))
                    assertThat(field.isOptional, `is`(false))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test as xs:string)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("optional field; colon type separator")
                fun optionalField_colon() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test ? : xs:string )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldType?.typeName, `is`("xs:string"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.QNAME_SEPARATOR))
                    assertThat(field.isOptional, `is`(true))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test? as xs:string)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("optional field; colon type separator; compact whitespace")
                fun optionalField_colon_compactWhitepace() {
                    val field = parse<XPathFieldDeclaration>("() instance of record(test?:xs:string)")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldType?.typeName, `is`("xs:string"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.ELVIS))
                    assertThat(field.isOptional, `is`(true))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test? as xs:string)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
            }

            @Nested
            @DisplayName("XPath 4.0 ED EBNF (122) FieldDeclaration ; XPath 4.0 ED EBNF (94) SelfReference")
            internal inner class FieldDeclaration_SelfReference {
                @Test
                @DisplayName("required field; item type (T)")
                fun requiredField_itemType() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test as .. )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                    assertThat(field.isOptional, `is`(false))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`(".."))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(1))
                    assertThat(fieldType.upperBound, `is`(1))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test as ..)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("required field; optional item type (T?)")
                fun requiredField_optionalItemType() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test as .. ? )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                    assertThat(field.isOptional, `is`(false))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`("..?"))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(0))
                    assertThat(fieldType.upperBound, `is`(1))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test as ..?)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("required field; sequence type (T*)")
                fun requiredField_sequenceType() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test as .. * )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                    assertThat(field.isOptional, `is`(false))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`("..*"))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(0))
                    assertThat(fieldType.upperBound, `is`(Int.MAX_VALUE))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test as ..*)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("required field; required sequence type (T+)")
                fun requiredField_requiredSequenceType() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test as .. + )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                    assertThat(field.isOptional, `is`(false))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`("..+"))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(1))
                    assertThat(fieldType.upperBound, `is`(Int.MAX_VALUE))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test as ..+)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("optional field; item type (T)")
                fun optionalField_itemType() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test ? as .. )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                    assertThat(field.isOptional, `is`(true))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`(".."))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(1))
                    assertThat(fieldType.upperBound, `is`(1))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test? as ..)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("optional field; optional item type (T?)")
                fun optionalField_optionalItemType() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test ? as .. ? )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                    assertThat(field.isOptional, `is`(true))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`("..?"))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(0))
                    assertThat(fieldType.upperBound, `is`(1))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test? as ..?)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("optional field; sequence type (T*)")
                fun optionalField_sequenceType() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test ? as .. * )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                    assertThat(field.isOptional, `is`(true))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`("..*"))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(0))
                    assertThat(fieldType.upperBound, `is`(Int.MAX_VALUE))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test? as ..*)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("optional field; required sequence type (T+)")
                fun optionalField_requiredSequenceType() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test ? as .. + )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.K_AS))
                    assertThat(field.isOptional, `is`(true))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`("..+"))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(1))
                    assertThat(fieldType.upperBound, `is`(Int.MAX_VALUE))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test? as ..+)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("required field; colon type separator")
                fun requiredField_colon() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test : .. )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.QNAME_SEPARATOR))
                    assertThat(field.isOptional, `is`(false))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`(".."))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(1))
                    assertThat(fieldType.upperBound, `is`(1))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test as ..)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("optional field; colon type separator")
                fun optionalField_colon() {
                    val field = parse<XPathFieldDeclaration>("() instance of record ( test ? : .. )")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.QNAME_SEPARATOR))
                    assertThat(field.isOptional, `is`(true))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`(".."))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(1))
                    assertThat(fieldType.upperBound, `is`(1))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test? as ..)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("optional field; colon type separator; compact whitespace")
                fun optionalField_colon_compactWhitespace() {
                    val field = parse<XPathFieldDeclaration>("() instance of record(test?:..)")[0]
                    assertThat(field.fieldName.data, `is`("test"))
                    assertThat(field.fieldSeparator, `is`(XPathTokenType.ELVIS))
                    assertThat(field.isOptional, `is`(true))

                    val test = field.parent as XPathRecordTest
                    assertThat(test.fields.first(), `is`(sameInstance(field)))

                    val fieldType = field.fieldType as XdmSequenceType
                    assertThat(fieldType.typeName, `is`(".."))
                    assertThat(fieldType.itemType, `is`(sameInstance(test)))
                    assertThat(fieldType.lowerBound, `is`(1))
                    assertThat(fieldType.upperBound, `is`(1))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("record(test? as ..)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (3.6.4.4) Array Test ; XPath 3.1 (2.5.5.9) Array Test")
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
    }

    @Nested
    @DisplayName("XPath 4.0 ED (4) Expressions ; XPath 3.1 (3) Expressions")
    internal inner class Expressions {
        @Nested
        @DisplayName("XPath 3.1 EBNF (1) XPath")
        internal inner class XPathTest {
            @Test
            @DisplayName("single")
            fun single() {
                val expr = parse<XPath>("1")[0] as XpmConcatenatingExpression
                assertThat(expr.expressionElement, `is`(nullValue()))

                val exprs = expr.expressions.toList()
                assertThat(exprs.size, `is`(1))
                assertThat(exprs[0].text, `is`("1"))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expr = parse<XPath>("1, 2 + 3, 4")[0] as XpmConcatenatingExpression
                assertThat(expr.expressionElement, `is`(nullValue()))

                val exprs = expr.expressions.toList()
                assertThat(exprs.size, `is`(3))
                assertThat(exprs[0].text, `is`("1"))
                assertThat(exprs[1].text, `is`("2 + 3"))
                assertThat(exprs[2].text, `is`("4"))
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.1) Setting Namespace Context")
        internal inner class SettingNamespaceContext {
            @Nested
            @DisplayName("XPath 4.0 ED EBNF (9) WithExpr")
            internal inner class WithExpr {
                @Test
                @DisplayName("empty")
                fun empty() {
                    val expr = parse<XPathWithExpr>("with xmlns=\"\" {}")[0] as XpmConcatenatingExpression
                    assertThat(expr.expressionElement, `is`(sameInstance(expr)))

                    val exprs = expr.expressions.toList()
                    assertThat(exprs.size, `is`(0))
                }

                @Test
                @DisplayName("single")
                fun single() {
                    val expr = parse<XPathWithExpr>("with xmlns=\"\" { 1 }")[0] as XpmConcatenatingExpression
                    assertThat(expr.expressionElement, `is`(sameInstance(expr)))

                    val exprs = expr.expressions.toList()
                    assertThat(exprs.size, `is`(1))
                    assertThat(exprs[0].text, `is`("1"))
                }

                @Test
                @DisplayName("multiplw")
                fun multiple() {
                    val expr = parse<XPathWithExpr>("with xmlns=\"\" { 1, 2 + 3, 4 }")[0] as XpmConcatenatingExpression
                    assertThat(expr.expressionElement, `is`(sameInstance(expr)))

                    val exprs = expr.expressions.toList()
                    assertThat(exprs.size, `is`(3))
                    assertThat(exprs[0].text, `is`("1"))
                    assertThat(exprs[1].text, `is`("2 + 3"))
                    assertThat(exprs[2].text, `is`("4"))
                }
            }

            @Nested
            @DisplayName("XPath 4.0 ED EBNF (10) NamespaceDeclaration")
            internal inner class NamespaceDeclaration {
                @Test
                @DisplayName("namespace prefix")
                fun namespacePrefix() {
                    val expr = parse<XPathNamespaceDeclaration>(
                        "with xmlns:b=\"http://www.example.com\" {}"
                    )[0] as XpmNamespaceDeclaration

                    assertThat(expr.namespacePrefix!!.data, `is`("b"))

                    val uri = expr.namespaceUri!!
                    assertThat(uri.data, `is`("http://www.example.com"))
                    assertThat(uri.context, `is`(XdmUriContext.NamespaceDeclaration))
                    assertThat(uri.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                    assertThat(uri.element, `is`(uri as PsiElement))

                    assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("default element/type namespace")
                fun defaultElementTypeNamespace() {
                    val expr = parse<XPathNamespaceDeclaration>(
                        "with xmlns=\"http://www.example.com\" {}"
                    )[0] as XpmNamespaceDeclaration

                    assertThat(expr.namespacePrefix?.data, `is`(""))

                    val uri = expr.namespaceUri!!
                    assertThat(uri.data, `is`("http://www.example.com"))
                    assertThat(uri.context, `is`(XdmUriContext.NamespaceDeclaration))
                    assertThat(uri.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                    assertThat(uri.element, `is`(uri as PsiElement))

                    assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(true))
                    assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("other (invalid) QName")
                fun otherQName() {
                    val expr = parse<XPathNamespaceDeclaration>(
                        "with b=\"http://www.example.com\" {}"
                    )[0] as XpmNamespaceDeclaration

                    assertThat(expr.namespacePrefix?.data, `is`(""))

                    val uri = expr.namespaceUri!!
                    assertThat(uri.data, `is`("http://www.example.com"))
                    assertThat(uri.context, `is`(XdmUriContext.NamespaceDeclaration))
                    assertThat(uri.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                    assertThat(uri.element, `is`(uri as PsiElement))

                    assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(false))
                    assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(true))
                    assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.3) Primary Expressions ; XPath 3.1 (3.1) Primary Expressions")
        internal inner class PrimaryExpressions {
            @Nested
            @DisplayName("XPath 3.1 (3.1.1) Literals")
            internal inner class Literals {
                @Test
                @DisplayName("XPath 3.1 EBNF (113) IntegerLiteral")
                fun integerLiteral() {
                    val literal = parse<XPathIntegerLiteral>("123")[0] as XsIntegerValue
                    assertThat(literal.data, `is`(BigInteger.valueOf(123)))
                    assertThat(literal.toInt(), `is`(123))

                    val expr = literal as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (114) DecimalLiteral")
                fun decimalLiteral() {
                    val literal = parse<XPathDecimalLiteral>("12.34")[0] as XsDecimalValue
                    assertThat(literal.data, `is`(BigDecimal(BigInteger.valueOf(1234), 2)))

                    val expr = literal as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (115) DoubleLiteral")
                fun doubleLiteral() {
                    val literal = parse<XPathDoubleLiteral>("1e3")[0] as XsDoubleValue
                    assertThat(literal.data, `is`(1e3))

                    val expr = literal as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
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

                        val expr = literal as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("unclosed string literal content")
                    fun unclosedStringLiteral() {
                        val literal = parse<XPathStringLiteral>("\"Lorem ipsum.")[0] as XsStringValue
                        assertThat(literal.data, `is`("Lorem ipsum."))
                        assertThat(literal.element, sameInstance(literal as PsiElement))

                        val expr = literal as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("EscapeApos tokens")
                    fun escapeApos() {
                        val literal = parse<XPathStringLiteral>("'''\"\"'")[0] as XsStringValue
                        assertThat(literal.data, `is`("'\"\""))
                        assertThat(literal.element, sameInstance(literal as PsiElement))

                        val expr = literal as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("EscapeQuot tokens")
                    fun escapeQuot() {
                        val literal = parse<XPathStringLiteral>("\"''\"\"\"")[0] as XsStringValue
                        assertThat(literal.data, `is`("''\""))
                        assertThat(literal.element, sameInstance(literal as PsiElement))

                        val expr = literal as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
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
                        val ref = parse<XPathVarRef>("let \$x := 2 return \$y")[0] as XpmVariableReference

                        val qname = ref.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("y"))

                        val expr = ref as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val ref = parse<XPathVarRef>("let \$a:x := 2 return \$a:y")[0] as XpmVariableReference

                        val qname = ref.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("y"))

                        val expr = ref as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val ref = parse<XPathVarRef>(
                            "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y"
                        )[0] as XpmVariableReference

                        val qname = ref.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("y"))

                        val expr = ref as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }
                }
            }

            @Nested
            @DisplayName("XPath 3.1 (3.1.3) Parenthesized Expressions")
            internal inner class ParenthesizedExpressions {
                @Test
                @DisplayName("XPath 3.1 EBNF (61) ParenthesizedExpr ; XQuery IntelliJ Plugin XPath EBNF (52) EmptyExpr")
                fun emptyExpr() {
                    val expr = parse<PluginEmptyExpr>("()")[0] as XpmConcatenatingExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                    assertThat(expr.expressions.count(), `is`(0))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 (3.1.4) Context Item Expression")
            internal inner class ContextItemExpression {
                @Test
                @DisplayName("XPath 3.1 EBNF (62) ContextItemExpr")
                fun contextItemExpr() {
                    val expr = parse<XPathContextItemExpr>("() ! .")[0] as XpmContextItemExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.4) Functions ; XPath 3.1 (3.1) Primary Expressions")
        internal inner class Functions {
            @Nested
            @DisplayName("XPath 4.0 ED (4.4.1) Static Functions ; XPath 3.1 (3.1.5) Static Function Calls")
            internal inner class StaticFunctions {
                @Nested
                @DisplayName("%variadic(\"no\"); empty parameters")
                internal inner class VariadicNo_EmptyParameters {
                    @Test
                    @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr")
                    fun inlineFunctionExpr() {
                        val decl = parse<XpmFunctionDeclaration>("function () {}")[0]
                        assertThat(decl.variadicType, `is`(XpmVariadic.No))
                        assertThat(decl.declaredArity, `is`(0))
                        assertThat(decl.requiredArity, `is`(0))
                    }
                }

                @Nested
                @DisplayName("%variadic(\"no\"); multiple parameters")
                internal inner class VariadicNo_MultipleParameters {
                    @Test
                    @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr")
                    fun inlineFunctionExpr() {
                        val decl = parse<XpmFunctionDeclaration>("function (\$one, \$two) {}")[0]
                        assertThat(decl.variadicType, `is`(XpmVariadic.No))
                        assertThat(decl.declaredArity, `is`(2))
                        assertThat(decl.requiredArity, `is`(2))
                    }
                }

                @Nested
                @DisplayName("%variadic(\"sequence\"); ellipsis")
                internal inner class VariadicSequence_Ellipsis {
                    @Test
                    @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr")
                    fun inlineFunctionExpr() {
                        val decl = parse<XpmFunctionDeclaration>("function (\$one, \$two ...) {}")[0]
                        assertThat(decl.variadicType, `is`(XpmVariadic.Ellipsis))
                        assertThat(decl.declaredArity, `is`(2))
                        assertThat(decl.requiredArity, `is`(1))
                    }
                }
            }

            @Nested
            @DisplayName("XPath 4.0 ED (4.4.1.1) Static Function Call Syntax ; XPath 3.1 (3.1.5) Static Function Calls")
            internal inner class StaticFunctionCallSyntax {
                @Nested
                @DisplayName("XPath 4.0 ED EBNF (62) KeywordArgument")
                internal inner class KeywordArgument {
                    @Test
                    @DisplayName("ncname")
                    fun ncname() {
                        val f = parse<XPathKeywordArgument>("fn:matches(input: \"test\", pattern: \".*\")")[0]
                        assertThat((f.keyExpression as XsNCNameValue).data, `is`("input"))
                        assertThat((f.valueExpression as XPathStringLiteral).data, `is`("test"))
                        assertThat(f.keyName, `is`("input"))
                    }

                    @Test
                    @DisplayName("missing value")
                    fun missingValue() {
                        val f = parse<XPathKeywordArgument>("fn:matches(input: , \".*\")")[0]
                        assertThat((f.keyExpression as XsNCNameValue).data, `is`("input"))
                        assertThat(f.valueExpression, `is`(nullValue()))
                        assertThat(f.keyName, `is`("input"))
                    }
                }

                @Nested
                @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
                internal inner class FunctionCall {
                    @Test
                    @DisplayName("positional arguments")
                    fun positionalArguments() {
                        val f = parse<XPathFunctionCall>("math:pow(2, 8)")[0] as XpmFunctionCall
                        assertThat(f.functionCallExpression, sameInstance(f))

                        val ref = f.functionReference!!
                        assertThat(ref, sameInstance(f))
                        assertThat(ref.positionalArity, `is`(2))
                        assertThat(ref.keywordArity, `is`(0))

                        val qname = ref.functionName!!
                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("math"))
                        assertThat(qname.localName!!.data, `is`("pow"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))

                        assertThat(f.positionalArguments.size, `is`(2))
                        assertThat(f.keywordArguments.size, `is`(0))

                        assertThat(f.positionalArguments[0].text, `is`("2"))
                        assertThat(f.positionalArguments[1].text, `is`("8"))

                        val expr = f as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.FUNCTION_CALL))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("keyword arguments")
                    fun keywordArguments() {
                        val f = parse<XPathFunctionCall>("math:pow(x: 2, y: 8)")[0] as XpmFunctionCall
                        assertThat(f.functionCallExpression, sameInstance(f))

                        val ref = f.functionReference!!
                        assertThat(ref, sameInstance(f))
                        assertThat(ref.positionalArity, `is`(0))
                        assertThat(ref.keywordArity, `is`(2))

                        val qname = ref.functionName!!
                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("math"))
                        assertThat(qname.localName!!.data, `is`("pow"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))

                        assertThat(f.positionalArguments.size, `is`(0))
                        assertThat(f.keywordArguments.size, `is`(2))

                        assertThat(f.keywordArguments[0].keyName, `is`("x"))
                        assertThat(f.keywordArguments[1].keyName, `is`("y"))

                        assertThat(f.keywordArguments[0].valueExpression?.text, `is`("2"))
                        assertThat(f.keywordArguments[1].valueExpression?.text, `is`("8"))

                        val expr = f as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.FUNCTION_CALL))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("positional and keyword arguments")
                    fun positionalAndKeywordArguments() {
                        val f = parse<XPathFunctionCall>("math:pow(2, y: 8)")[0] as XpmFunctionCall
                        assertThat(f.functionCallExpression, sameInstance(f))

                        val ref = f.functionReference!!
                        assertThat(ref, sameInstance(f))
                        assertThat(ref.positionalArity, `is`(1))
                        assertThat(ref.keywordArity, `is`(1))

                        val qname = ref.functionName!!
                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("math"))
                        assertThat(qname.localName!!.data, `is`("pow"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))

                        assertThat(f.positionalArguments.size, `is`(1))
                        assertThat(f.positionalArguments[0].text, `is`("2"))

                        assertThat(f.keywordArguments.size, `is`(1))
                        assertThat(f.keywordArguments[0].keyName, `is`("y"))
                        assertThat(f.keywordArguments[0].valueExpression?.text, `is`("8"))

                        val expr = f as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.FUNCTION_CALL))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("empty arguments")
                    fun emptyArguments() {
                        val f = parse<XPathFunctionCall>("fn:true()")[0] as XpmFunctionCall
                        assertThat(f.functionCallExpression, sameInstance(f))

                        val ref = f.functionReference!!
                        assertThat(ref, sameInstance(f))
                        assertThat(ref.positionalArity, `is`(0))
                        assertThat(ref.keywordArity, `is`(0))

                        val qname = ref.functionName!!
                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("fn"))
                        assertThat(qname.localName!!.data, `is`("true"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))

                        assertThat(f.positionalArguments.size, `is`(0))
                        assertThat(f.keywordArguments.size, `is`(0))

                        val expr = f as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.FUNCTION_CALL))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("partial function application")
                    fun partialFunctionApplication() {
                        val f = parse<XPathFunctionCall>("math:sin(?)")[0] as XpmFunctionCall
                        assertThat(f.functionCallExpression, sameInstance(f))

                        val ref = f.functionReference!!
                        assertThat(ref, sameInstance(f))
                        assertThat(ref.positionalArity, `is`(1))
                        assertThat(ref.keywordArity, `is`(0))

                        val qname = ref.functionName!!
                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("math"))
                        assertThat(qname.localName!!.data, `is`("sin"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))

                        assertThat(f.positionalArguments.size, `is`(1))
                        assertThat(f.keywordArguments.size, `is`(0))

                        assertThat(f.positionalArguments[0].text, `is`("?"))

                        val expr = f as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARGUMENT_LIST))
                        assertThat(expr.expressionElement?.textOffset, `is`(8))
                    }

                    @Test
                    @DisplayName("invalid EQName")
                    fun invalidEQName() {
                        val f = parse<XPathFunctionCall>(":true(1)")[0] as XpmFunctionCall
                        assertThat(f.functionCallExpression, sameInstance(f))

                        val ref = f.functionReference!!
                        assertThat(ref, sameInstance(f))
                        assertThat(ref.positionalArity, `is`(1))
                        assertThat(ref.keywordArity, `is`(0))
                        assertThat(ref.functionName, `is`(nullValue()))

                        assertThat(f.positionalArguments.size, `is`(1))
                        assertThat(f.keywordArguments.size, `is`(0))

                        assertThat(f.positionalArguments[0].text, `is`("1"))

                        val expr = f as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.FUNCTION_CALL))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("NCName namespace resolution")
                    fun ncname() {
                        val qname = parse<XPathEQName>("true()")[0] as XsQNameValue
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.FunctionRef))

                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("true"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))
                    }

                    @Test
                    @DisplayName("reference rename")
                    fun referenceRename() {
                        val expr = parse<XPathFunctionCall>("test()")[0] as XpmFunctionReference

                        val ref = (expr.functionName as PsiElement).reference!!
                        assertThat(ref, `is`(instanceOf(XPathFunctionNameReference::class.java)))

                        DebugUtil.performPsiModification<Throwable>("rename") {
                            val renamed = ref.handleElementRename("lorem-ipsum")
                            assertThat(renamed, `is`(instanceOf(XPathNCName::class.java)))
                            assertThat(renamed.text, `is`("lorem-ipsum"))
                            assertThat((renamed as PsiNameIdentifierOwner).name, `is`("lorem-ipsum"))
                        }

                        assertThat((expr.functionName as PsiElement).text, `is`("lorem-ipsum"))
                    }
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (65) ArgumentPlaceholder")
                fun argumentPlaceholder() {
                    val arg = parse<XPathArgumentPlaceholder>("math:sin(?)")[0] as XpmExpression
                    assertThat(arg.expressionElement?.elementType, `is`(XPathTokenType.OPTIONAL))
                }
            }

            @Nested
            @DisplayName("XPath 4.0 ED (4.4.2.3) Named Function References ; XPath 3.1 (3.1.6) Named Function References")
            internal inner class NamedFunctionReferences {
                @Nested
                @DisplayName("XPath 3.1 EBNF (67) NamedFunctionRef")
                internal inner class NamedFunctionRef {
                    @Test
                    @DisplayName("named function reference")
                    fun namedFunctionRef() {
                        val f = parse<XPathNamedFunctionRef>("true#3")[0] as XpmFunctionReference
                        assertThat(f.positionalArity, `is`(3))
                        assertThat(f.keywordArity, `is`(0))

                        val qname = f.functionName!!
                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("true"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))

                        val expr = f as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.NAMED_FUNCTION_REF))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("missing arity")
                    fun missingArity() {
                        val f = parse<XPathNamedFunctionRef>("true#")[0] as XpmFunctionReference
                        assertThat(f.positionalArity, `is`(0))
                        assertThat(f.keywordArity, `is`(0))

                        val qname = f.functionName!!
                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("true"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))

                        val expr = f as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.NAMED_FUNCTION_REF))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("invalid EQName")
                    fun invalidEQName() {
                        val f = parse<XPathNamedFunctionRef>(":true#0")[0] as XpmFunctionReference
                        assertThat(f.positionalArity, `is`(0))
                        assertThat(f.keywordArity, `is`(0))
                        assertThat(f.functionName, `is`(nullValue()))

                        val expr = f as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.NAMED_FUNCTION_REF))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("NCName namespace resolution")
                    fun ncname() {
                        val qname = parse<XPathEQName>("true#0")[0] as XsQNameValue
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.FunctionRef))

                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("true"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))
                    }

                    @Test
                    @DisplayName("reference rename")
                    fun referenceRename() {
                        val expr = parse<XPathNamedFunctionRef>("test#1")[0] as XpmFunctionReference

                        val ref = (expr.functionName as PsiElement).reference!!
                        assertThat(ref, `is`(instanceOf(XPathFunctionNameReference::class.java)))

                        DebugUtil.performPsiModification<Throwable>("rename") {
                            val renamed = ref.handleElementRename("lorem-ipsum")
                            assertThat(renamed, `is`(instanceOf(XPathNCName::class.java)))
                            assertThat(renamed.text, `is`("lorem-ipsum"))
                            assertThat((renamed as PsiNameIdentifierOwner).name, `is`("lorem-ipsum"))
                        }

                        assertThat((expr.functionName as PsiElement).text, `is`("lorem-ipsum"))
                    }
                }
            }

            @Nested
            @DisplayName("XPath 4.0 ED (4.4.2.4) Inline Function Expressions ; XPath 3.1 (3.1.7) Inline Function Expression")
            internal inner class InlineFunctionExpressions {
                @Nested
                @DisplayName("XPath 3.1 EBNF (3) Param")
                internal inner class Param {
                    @Test
                    @DisplayName("NCName")
                    fun ncname() {
                        val expr = parse<XPathParam>("function (\$x) {}")[0] as XpmParameter
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.defaultExpression, `is`(nullValue()))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val presentation = (expr as NavigationItem).presentation!!
                        assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                        assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                        assertThat(presentation.presentableText, `is`("\$x"))
                        assertThat(presentation.locationString, `is`(nullValue()))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XPathInlineFunctionExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XPathParam>("function (\$a:x) {}")[0] as XpmParameter
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.defaultExpression, `is`(nullValue()))

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val presentation = (expr as NavigationItem).presentation!!
                        assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                        assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                        assertThat(presentation.presentableText, `is`("\$a:x"))
                        assertThat(presentation.locationString, `is`(nullValue()))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XPathInlineFunctionExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XPathParam>("function (\$Q{http://www.example.com}x) {}")[0] as XpmParameter
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.defaultExpression, `is`(nullValue()))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val presentation = (expr as NavigationItem).presentation!!
                        assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                        assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                        assertThat(presentation.presentableText, `is`("\$Q{http://www.example.com}x"))
                        assertThat(presentation.locationString, `is`(nullValue()))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XPathInlineFunctionExpr::class.java)))
                    }

                    @Test
                    @DisplayName("missing VarName")
                    fun missingVarName() {
                        val expr = parse<XPathParam>("function (\$) {}")[0] as XpmParameter
                        assertThat(expr.variableName, `is`(nullValue()))
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.defaultExpression, `is`(nullValue()))

                        val presentation = (expr as NavigationItem).presentation!!
                        assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                        assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                        assertThat(presentation.presentableText, `is`(nullValue()))
                        assertThat(presentation.locationString, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("with type")
                    fun withType() {
                        val expr = parse<XPathParam>("function ( \$x  as  element() ) {}")[0] as XpmParameter
                        assertThat(expr.variableType?.typeName, `is`("element()"))
                        assertThat(expr.defaultExpression, `is`(nullValue()))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val presentation = (expr as NavigationItem).presentation!!
                        assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.Param)))
                        assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.Param)))
                        assertThat(presentation.presentableText, `is`("\$x as element()"))
                        assertThat(presentation.locationString, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("NCName namespace resolution")
                    fun ncnameNamespaceResolution() {
                        val qname = parse<XPathEQName>("function (\$test) {}")[0] as XsQNameValue
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Parameter))

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
                        val decl = parse<XpmFunctionDeclaration>("function () {}")[0]
                        assertThat(decl.functionName, `is`(nullValue()))
                        assertThat(decl.returnType, `is`(nullValue()))
                        assertThat(decl.parameters.size, `is`(0))
                        assertThat(decl.functionBody, sameInstance(XpmEmptyExpression))

                        val expr = decl as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("non-empty ParamList")
                    fun nonEmptyParamList() {
                        val decl = parse<XpmFunctionDeclaration>("function (\$one, \$two) {}")[0]
                        assertThat(decl.functionName, `is`(nullValue()))
                        assertThat(decl.returnType, `is`(nullValue()))
                        assertThat(decl.functionBody, sameInstance(XpmEmptyExpression))

                        assertThat(decl.parameters.size, `is`(2))
                        assertThat(qname_presentation(decl.parameters[0].variableName!!), `is`("one"))
                        assertThat(qname_presentation(decl.parameters[1].variableName!!), `is`("two"))

                        val expr = decl as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("non-empty ParamList with types")
                    fun nonEmptyParamListWithTypes() {
                        val decl = parse<XpmFunctionDeclaration>(
                            "function (\$one  as  array ( * ), \$two  as  node((::))) {}"
                        )[0]
                        assertThat(decl.functionName, `is`(nullValue()))
                        assertThat(decl.returnType, `is`(nullValue()))
                        assertThat(decl.functionBody, sameInstance(XpmEmptyExpression))

                        assertThat(decl.parameters.size, `is`(2))
                        assertThat(qname_presentation(decl.parameters[0].variableName!!), `is`("one"))
                        assertThat(qname_presentation(decl.parameters[1].variableName!!), `is`("two"))

                        val expr = decl as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("return type")
                    fun returnType() {
                        val decl = parse<XpmFunctionDeclaration>("function ()  as  xs:boolean  {}")[0]
                        assertThat(decl.functionName, `is`(nullValue()))
                        assertThat(decl.returnType?.typeName, `is`("xs:boolean"))
                        assertThat(decl.parameters.size, `is`(0))
                        assertThat(decl.functionBody, sameInstance(XpmEmptyExpression))

                        val expr = decl as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("function body expression")
                    fun functionBodyExpression() {
                        val decl = parse<XpmFunctionDeclaration>("function () { 2 + 3 }")[0]
                        assertThat(decl.functionName, `is`(nullValue()))
                        assertThat(decl.returnType, `is`(nullValue()))
                        assertThat(decl.parameters.size, `is`(0))
                        assertThat(decl.functionBody?.text, `is`("2 + 3 "))

                        val expr = decl as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.5) Postfix Expressions ; XPath 3.1 (3.2) Postfix Expressions")
        internal inner class PostfixExpressions {
            @Nested
            @DisplayName("XPath 3.1 EBNF (49) PostfixExpr")
            internal inner class PostfixExpr {
                @Test
                @DisplayName("initial step")
                fun initialStep() {
                    val step = parse<XPathPostfixExpr>("\$x/test")[0] as XpmPathStep
                    assertThat(step.axisType, `is`(XpmAxisType.Self))
                    assertThat(step.nodeName, `is`(nullValue()))
                    assertThat(step.nodeType, sameInstance(XdmNodeItem))
                    assertThat(step.predicateExpression, `is`(nullValue()))

                    val expr = step as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Test
                @DisplayName("intermediate step")
                fun intermediateStep() {
                    val step = parse<XPathPostfixExpr>("/test/./self::node()")[0] as XpmPathStep
                    assertThat(step.axisType, `is`(XpmAxisType.Self))
                    assertThat(step.nodeName, `is`(nullValue()))
                    assertThat(step.nodeType, sameInstance(XdmNodeItem))
                    assertThat(step.predicateExpression, `is`(nullValue()))

                    val expr = step as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Test
                @DisplayName("final step")
                fun finalStep() {
                    val step = parse<XPathPostfixExpr>("/test/string()")[0] as XpmPathStep
                    assertThat(step.axisType, `is`(XpmAxisType.Self))
                    assertThat(step.nodeName, `is`(nullValue()))
                    assertThat(step.nodeType, sameInstance(XdmNodeItem))
                    assertThat(step.predicateExpression, `is`(nullValue()))

                    val expr = step as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 (3.2.1) Filter Expressions")
            internal inner class FilterExpressions {
                @Nested
                @DisplayName("XQuery IntelliJ Plugin XPath EBNF (46) FilterExpr")
                internal inner class FilterExpressions {
                    @Test
                    @DisplayName("single predicate; null PrimaryExpr expressionElement")
                    fun singlePredicate_nullExpressionElement() {
                        val step = parse<XPathPostfixExpr>("\$x[1]")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Self))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(XdmNodeItem))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.ONE))

                        val expr = step as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.VAR_REF))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("single predicate; non-null PrimaryExpr expressionElement")
                    fun singlePredicate_nonNullExpressionElement() {
                        val step = parse<XPathPostfixExpr>("map { \"one\": 1 }[1]")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Self))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(XdmNodeItem))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.ONE))

                        val expr = step as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.MAP_CONSTRUCTOR_ENTRY))
                        assertThat(expr.expressionElement?.textOffset, `is`(6))
                    }

                    @Test
                    @DisplayName("multiple predicates; inner")
                    fun multiplePredicatesInner() {
                        val step = parse<XPathPostfixExpr>("\$x[1][2]")[1] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Self))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(XdmNodeItem))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.ONE))

                        val expr = step as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.VAR_REF))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("multiple predicates; outer")
                    fun multiplePredicatesOuter() {
                        val step = parse<XPathPostfixExpr>("\$x[1][2]")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Self))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(XdmNodeItem))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.valueOf(2)))

                        val expr = step as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.VAR_REF))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 (3.2.2) Dynamic Function Calls")
            internal inner class DynamicFunctionCalls {
                @Test
                @DisplayName("path step")
                fun pathStep() {
                    val step = parse<XPathPostfixExpr>("\$x(1)")[0] as XpmPathStep
                    assertThat(step.axisType, `is`(XpmAxisType.Self))
                    assertThat(step.nodeName, `is`(nullValue()))
                    assertThat(step.nodeType, sameInstance(XdmNodeItem))
                    assertThat(step.predicateExpression, `is`(nullValue()))

                    val expr = step as XpmExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.POSITIONAL_ARGUMENT_LIST))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (67) NamedFunctionRef")
                fun namedFunctionRef() {
                    val f = parse<PluginDynamicFunctionCall>("fn:abs#1(1)")[0] as XpmFunctionCall
                    assertThat(f.positionalArguments.size, `is`(1))
                    assertThat(f.positionalArguments[0].text, `is`("1"))

                    val ref = f.functionReference
                    assertThat(qname_presentation(ref?.functionName!!), `is`("fn:abs"))
                    assertThat(ref.positionalArity, `is`(1))
                }

                @Nested
                @DisplayName("XPath 3.1 EBNF (61) ParenthesizedExpr ; XPath 3.1 EBNF (67) NamedFunctionRef")
                internal inner class ParenthesizedExprWithNamedFunctionRef {
                    @Test
                    @DisplayName("single")
                    fun single() {
                        val f = parse<PluginDynamicFunctionCall>("(fn:abs#1)(1)")[0] as XpmFunctionCall
                        assertThat(f.positionalArguments.size, `is`(1))
                        assertThat(f.positionalArguments[0].text, `is`("1"))

                        val ref = f.functionReference
                        assertThat(qname_presentation(ref?.functionName!!), `is`("fn:abs"))
                        assertThat(ref.positionalArity, `is`(1))
                    }

                    @Test
                    @DisplayName("multiple")
                    fun multiple() {
                        val f = parse<PluginDynamicFunctionCall>("(fn:abs#1, fn:count#1)(1)")[0] as XpmFunctionCall
                        assertThat(f.functionReference, `is`(nullValue()))
                        assertThat(f.positionalArguments.size, `is`(1))
                        assertThat(f.positionalArguments[0].text, `is`("1"))
                    }
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.6) Path Expressions ; XPath 3.1 (3.3) Path Expressions")
        internal inner class PathExpressions {
            @Nested
            @DisplayName("XPath 3.1 EBNF (36) PathExpr")
            internal inner class PathExpr {
                @Test
                @DisplayName("last step is ForwardStep")
                fun lastStepIsForwardStep() {
                    val expr = parse<XPathPathExpr>("/lorem/ipsum/parent::dolor")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.REVERSE_STEP))
                    assertThat(expr.expressionElement?.textOffset, `is`(13))
                }

                @Test
                @DisplayName("last step is ReverseStep")
                fun lastStepIsReverseStep() {
                    val expr = parse<XPathPathExpr>("/lorem/ipsum/parent::dolor")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.REVERSE_STEP))
                    assertThat(expr.expressionElement?.textOffset, `is`(13))
                }

                @Test
                @DisplayName("last step is FilterStep")
                fun lastStepIsFilterStep() {
                    val expr = parse<XPathPathExpr>("/lorem/ipsum/dolor[1]")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.FILTER_STEP))
                    assertThat(expr.expressionElement?.textOffset, `is`(13))
                }

                @Test
                @DisplayName("last step is PrimaryExpr")
                fun lastStepIsPrimaryExpr() {
                    val expr = parse<XPathPathExpr>("/lorem/ipsum/dolor/.")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.NAME_TEST))
                    assertThat(expr.expressionElement?.textOffset, `is`(13))
                }

                @Test
                @DisplayName("last step is FilterExpr")
                fun lastStepIsFilterExpr() {
                    val expr = parse<XPathPathExpr>("/lorem/ipsum/dolor/.[1]")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.NAME_TEST))
                    assertThat(expr.expressionElement?.textOffset, `is`(13))
                }

                @Test
                @DisplayName("last step is DynamicFunctionCall")
                fun lastStepIsDynamicFunctionCall() {
                    val expr = parse<XPathPathExpr>("/lorem/ipsum/dolor/.(1)")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.NAME_TEST))
                    assertThat(expr.expressionElement?.textOffset, `is`(13))
                }

                @Test
                @DisplayName("last step is PostfixLookup")
                fun lastStepIsPostfixLookup() {
                    val expr = parse<XPathPathExpr>("/lorem/ipsum/dolor/.?test")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.NAME_TEST))
                    assertThat(expr.expressionElement?.textOffset, `is`(13))
                }
            }

            @Nested
            @DisplayName("A '//' at the beginning of a path expression")
            internal inner class LeadingDoubleForwardSlash {
                @Test
                @DisplayName("XQuery IntelliJ Plugin XPath EBNF (44) AbbrevDescendantOrSelfStep")
                fun abbrevDescendantOrSelfStep() {
                    val step = parse<PluginAbbrevDescendantOrSelfStep>("//test")[0] as XpmPathStep
                    assertThat(step.axisType, `is`(XpmAxisType.DescendantOrSelf))
                    assertThat(step.nodeName, `is`(nullValue()))
                    assertThat(step.nodeType, sameInstance(XdmNodeItem))
                    assertThat(step.predicateExpression, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 (3.3.2) Steps")
            internal inner class Steps {
                @Nested
                @DisplayName("XPath 3.1 EBNF (39) AxisStep")
                internal inner class AxisStep {
                    @Test
                    @DisplayName("multiple predicates; inner")
                    fun multiplePredicatesInner() {
                        val step = parse<XPathAxisStep>("child::test[1][2]")[1] as XpmPathStep
                        val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Child))
                        assertThat(step.nodeName, sameInstance(qname))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.ONE))
                    }

                    @Test
                    @DisplayName("multiple predicates; outer")
                    fun multiplePredicatesOuter() {
                        val step = parse<XPathAxisStep>("child::test[1][2]")[0] as XpmPathStep
                        val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Child))
                        assertThat(step.nodeName, sameInstance(qname))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.valueOf(2)))
                    }

                    @Test
                    @DisplayName("XPath 3.1 EBNF (45) AbbrevReverseStep")
                    fun abbrevReverseStep() {
                        val step = parse<XPathAxisStep>("..[1]")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Parent))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(XdmNodeItem))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.ONE))
                    }

                    @Test
                    @DisplayName("XPath 3.1 EBNF (47) NameTest")
                    fun nameTest() {
                        val step = parse<XPathAxisStep>("child::test[1]")[0] as XpmPathStep
                        val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Child))
                        assertThat(step.nodeName, sameInstance(qname))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.ONE))
                    }

                    @Test
                    @DisplayName("XPath 3.1 EBNF (83) KindTest")
                    fun kindTest() {
                        val step = parse<XPathAxisStep>("child::node()[1]")[0] as XpmPathStep
                        val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Child))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(itemType))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.ONE))
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
                    @DisplayName("XPath 3.1 EBNF (47) NameTest")
                    fun nameTest() {
                        val step = parse<XPathForwardStep>("child::test")[0] as XpmPathStep
                        val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Child))
                        assertThat(step.nodeName, sameInstance(qname))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                        assertThat(step.predicateExpression, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("XPath 3.1 EBNF (83) KindTest")
                    fun kindTest() {
                        val step = parse<XPathForwardStep>("child::node()")[0] as XpmPathStep
                        val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Child))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(itemType))
                        assertThat(step.predicateExpression, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("attribute axis")
                    fun attributeAxis() {
                        val step = parse<XPathForwardStep>("attribute::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Attribute))
                        assertThat(step.nodeType, sameInstance(XdmAttributeItem))
                    }

                    @Test
                    @DisplayName("child axis")
                    fun childAxis() {
                        val step = parse<XPathForwardStep>("child::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Child))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("descendant axis")
                    fun descendantAxis() {
                        val step = parse<XPathForwardStep>("descendant::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Descendant))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("descendant-or-self axis")
                    fun descendantOrSelfAxis() {
                        val step = parse<XPathForwardStep>("descendant-or-self::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.DescendantOrSelf))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("following axis")
                    fun followingAxis() {
                        val step = parse<XPathForwardStep>("following::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Following))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("following-sibling axis")
                    fun followingSiblingAxis() {
                        val step = parse<XPathForwardStep>("following-sibling::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.FollowingSibling))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("namespace axis")
                    fun namespaceAxis() {
                        val step = parse<XPathForwardStep>("namespace::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Namespace))
                        assertThat(step.nodeType, sameInstance(XdmNamespaceItem))
                    }

                    @Test
                    @DisplayName("self axis")
                    fun selfAxis() {
                        val step = parse<XPathForwardStep>("self::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Self))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("principal node kind")
                    fun principalNodeKind() {
                        val steps = parse<XPathNameTest>(
                            """
                        child::one, descendant::two, attribute::three, self::four, descendant-or-self::five,
                        following-sibling::six, following::seven, namespace::eight
                        """
                        )
                        assertThat(steps.size, `is`(8))
                        assertThat(steps[0].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // child
                        assertThat(steps[1].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // descendant
                        assertThat(steps[2].getPrincipalNodeKind(), `is`(XpmUsageType.Attribute)) // attribute
                        assertThat(steps[3].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // self
                        assertThat(steps[4].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // descendant-or-self
                        assertThat(steps[5].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // following-sibling
                        assertThat(steps[6].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // following
                        assertThat(steps[7].getPrincipalNodeKind(), `is`(XpmUsageType.Namespace)) // namespace
                    }

                    @Test
                    @DisplayName("usage type")
                    fun usageType() {
                        val steps = parse<XPathNameTest>(
                            """
                        child::one, descendant::two, attribute::three, self::four, descendant-or-self::five,
                        following-sibling::six, following::seven, namespace::eight
                        """
                        ).map { it.walkTree().filterIsInstance<XsQNameValue>().first().element!! }
                        assertThat(steps.size, `is`(8))
                        assertThat(steps[0].getUsageType(), `is`(XpmUsageType.Element)) // child
                        assertThat(steps[1].getUsageType(), `is`(XpmUsageType.Element)) // descendant
                        assertThat(steps[2].getUsageType(), `is`(XpmUsageType.Attribute)) // attribute
                        assertThat(steps[3].getUsageType(), `is`(XpmUsageType.Element)) // self
                        assertThat(steps[4].getUsageType(), `is`(XpmUsageType.Element)) // descendant-or-self
                        assertThat(steps[5].getUsageType(), `is`(XpmUsageType.Element)) // following-sibling
                        assertThat(steps[6].getUsageType(), `is`(XpmUsageType.Element)) // following
                        assertThat(steps[7].getUsageType(), `is`(XpmUsageType.Namespace)) // namespace
                    }
                }

                @Nested
                @DisplayName("XPath 3.1 EBNF (44) ReverseAxis")
                internal inner class ReverseAxis {
                    @Test
                    @DisplayName("XPath 3.1 EBNF (47) NameTest")
                    fun nameTest() {
                        val step = parse<XPathReverseStep>("parent::test")[0] as XpmPathStep
                        val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Parent))
                        assertThat(step.nodeName, sameInstance(qname))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                        assertThat(step.predicateExpression, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("XPath 3.1 EBNF (83) KindTest")
                    fun kindTest() {
                        val step = parse<XPathReverseStep>("parent::node()")[0] as XpmPathStep
                        val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Parent))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(itemType))
                        assertThat(step.predicateExpression, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("ancestor axis")
                    fun ancestorAxis() {
                        val step = parse<XPathReverseStep>("ancestor::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Ancestor))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("child axis")
                    fun ancestorOrSelfAxis() {
                        val step = parse<XPathReverseStep>("ancestor-or-self::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.AncestorOrSelf))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("parent axis")
                    fun parentAxis() {
                        val step = parse<XPathReverseStep>("parent::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Parent))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("preceding axis")
                    fun precedingAxis() {
                        val step = parse<XPathReverseStep>("preceding::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Preceding))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("preceding-sibling axis")
                    fun precedingSiblingAxis() {
                        val step = parse<XPathReverseStep>("preceding-sibling::test")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.PrecedingSibling))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                    }

                    @Test
                    @DisplayName("principal node kind")
                    fun principalNodeKind() {
                        val steps = parse<XPathNameTest>(
                            "parent::one, ancestor::two, preceding-sibling::three, preceding::four, ancestor-or-self::five"
                        )
                        assertThat(steps.size, `is`(5))
                        assertThat(steps[0].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // parent
                        assertThat(steps[1].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // ancestor
                        assertThat(steps[2].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // preceding-sibling
                        assertThat(steps[3].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // preceding
                        assertThat(steps[4].getPrincipalNodeKind(), `is`(XpmUsageType.Element)) // ancestor-or-self
                    }

                    @Test
                    @DisplayName("usage type")
                    fun usageType() {
                        val steps = parse<XPathNameTest>(
                            "parent::one, ancestor::two, preceding-sibling::three, preceding::four, ancestor-or-self::five"
                        ).map { it.walkTree().filterIsInstance<XsQNameValue>().first().element!! }
                        assertThat(steps.size, `is`(5))
                        assertThat(steps[0].getUsageType(), `is`(XpmUsageType.Element)) // parent
                        assertThat(steps[1].getUsageType(), `is`(XpmUsageType.Element)) // ancestor
                        assertThat(steps[2].getUsageType(), `is`(XpmUsageType.Element)) // preceding-sibling
                        assertThat(steps[3].getUsageType(), `is`(XpmUsageType.Element)) // preceding
                        assertThat(steps[4].getUsageType(), `is`(XpmUsageType.Element)) // ancestor-or-self
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
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Element))

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
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Attribute))

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
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Namespace))

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
            @DisplayName("XPath 3.1 (3.3.3) Predicates within Steps")
            internal inner class PredicatesWithinSteps {
                @Nested
                @DisplayName("XPath 3.1 EBNF (52) Predicate")
                internal inner class Predicate {
                    @Test
                    @DisplayName("single")
                    fun single() {
                        val expr = parse<PluginFilterStep>("/test[1]")[0] as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("multiple")
                    fun multiple() {
                        val exprs = parse<PluginFilterStep>("/test[1][2]")
                        assertThat(exprs[0].expressionElement, `is`(nullValue()))
                        assertThat(exprs[1].expressionElement, `is`(nullValue()))
                    }
                }
            }

            @Nested
            @DisplayName("XPath 3.1 (3.3.5) Abbreviated Syntax")
            internal inner class AbbreviatedSyntax {
                @Nested
                @DisplayName("1. The attribute axis attribute:: can be abbreviated by @.")
                internal inner class AbbreviatedAttributeAxis {
                    @Test
                    @DisplayName("XPath 3.1 EBNF (47) NameTest")
                    fun nameTest() {
                        val step = parse<XPathAbbrevForwardStep>("@test")[0] as XpmPathStep
                        val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Attribute))
                        assertThat(step.nodeName, sameInstance(qname))
                        assertThat(step.nodeType, sameInstance(XdmAttributeItem))
                        assertThat(step.predicateExpression, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("XPath 3.1 EBNF (83) KindTest ; XPath 3.1 EBNF (84) AnyKindTest")
                    fun anyKindTest() {
                        val step = parse<XPathAbbrevForwardStep>("@node()")[0] as XpmPathStep
                        val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Attribute))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(itemType))
                        assertThat(step.predicateExpression, `is`(nullValue()))
                    }
                }

                @Nested
                @DisplayName("2. If the axis name is omitted from an axis step")
                internal inner class AxisNameOmittedFromAxisStep {
                    @Nested
                    @DisplayName("the default axis is child")
                    internal inner class DefaultAxis {
                        @Test
                        @DisplayName("XPath 3.1 EBNF (47) NameTest")
                        fun nameTest() {
                            val step = parse<XPathNameTest>("test")[0] as XpmPathStep
                            val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, sameInstance(qname))
                            assertThat(step.nodeType, sameInstance(XdmElementItem))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XPath 3.1 EBNF (83) KindTest ; XPath 3.1 EBNF (84) AnyKindTest")
                        fun anyKindTest() {
                            val step = parse<XPathNodeTest>("node()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XPath 3.1 EBNF (83) KindTest ; XPath 3.1 EBNF (85) DocumentTest")
                        fun documentTest() {
                            val step = parse<XPathNodeTest>("document-node()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XPath 3.1 EBNF (83) KindTest ; XPath 3.1 EBNF (86) TextTest")
                        fun textTest() {
                            val step = parse<XPathNodeTest>("text()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XPath 3.1 EBNF (83) KindTest ; XPath 3.1 EBNF (87) CommentTest")
                        fun commentTest() {
                            val step = parse<XPathNodeTest>("comment()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XPath 3.1 EBNF (83) KindTest ; XPath 3.1 EBNF (89) PITest")
                        fun piTest() {
                            val step = parse<XPathNodeTest>("processing-instruction()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XPath 3.1 EBNF (83) KindTest ; XPath 3.1 EBNF (94) ElementTest")
                        fun elementTest() {
                            val step = parse<XPathNodeTest>("element()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XPath 3.1 EBNF (83) KindTest ; XPath 3.1 EBNF (96) SchemaElementTest")
                        fun schemaElementTest() {
                            val step = parse<XPathNodeTest>("schema-element(test)")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }
                    }

                    @Nested
                    @DisplayName("(1) if the NodeTest in an axis step contains an AttributeTest or SchemaAttributeTest then the default axis is attribute")
                    internal inner class AttributeAxis {
                        @Test
                        @DisplayName("XPath 3.1 EBNF (83) KindTest ; XPath 3.1 EBNF (94) AttributeTest")
                        fun attributeTest() {
                            val step = parse<XPathNodeTest>("attribute()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Attribute))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XPath 3.1 EBNF (83) KindTest ; XPath 3.1 EBNF (96) SchemaAttributeTest")
                        fun schemaAttributeTest() {
                            val step = parse<XPathNodeTest>("schema-attribute(test)")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Attribute))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }
                    }

                    @Test
                    @DisplayName("(2) if the NodeTest in an axis step is a NamespaceNodeTest then the default axis is namespace")
                    fun namespaceNodeTest() {
                        val step = parse<XPathNodeTest>("namespace-node()")[0] as XpmPathStep
                        val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Namespace))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(itemType))
                        assertThat(step.predicateExpression, `is`(nullValue()))
                    }
                }

                @Test
                @DisplayName("3. Each non-initial occurrence of // is effectively replaced by /descendant-or-self::node()/ during processing of a path expression.")
                fun abbrevDescendantOrSelfStep() {
                    val step = parse<PluginAbbrevDescendantOrSelfStep>("lorem//ipsum")[0] as XpmPathStep
                    assertThat(step.axisType, `is`(XpmAxisType.DescendantOrSelf))
                    assertThat(step.nodeName, `is`(nullValue()))
                    assertThat(step.nodeType, sameInstance(XdmNodeItem))
                    assertThat(step.predicateExpression, `is`(nullValue()))
                }

                @Test
                @DisplayName("4. A step consisting of .. is short for parent::node().")
                fun abbrevReverseStep() {
                    val step = parse<XPathAbbrevReverseStep>("..")[0] as XpmPathStep
                    assertThat(step.axisType, `is`(XpmAxisType.Parent))
                    assertThat(step.nodeName, `is`(nullValue()))
                    assertThat(step.nodeType, sameInstance(XdmNodeItem))
                    assertThat(step.predicateExpression, `is`(nullValue()))
                }

                @Nested
                @DisplayName("XPath 3.1 EBNF (42) AbbrevForwardStep")
                internal inner class AbbrevForwardStep {
                    @Test
                    @DisplayName("principal node kind")
                    fun principalNodeKind() {
                        val steps = parse<XPathNameTest>("one, @two")
                        assertThat(steps.size, `is`(2))
                        assertThat(steps[0].getPrincipalNodeKind(), `is`(XpmUsageType.Element))
                        assertThat(steps[1].getPrincipalNodeKind(), `is`(XpmUsageType.Attribute))
                    }

                    @Test
                    @DisplayName("usage type")
                    fun usageType() {
                        val steps = parse<XPathNameTest>("one, @two").map {
                            it.walkTree().filterIsInstance<XsQNameValue>().first().element!!
                        }
                        assertThat(steps.size, `is`(2))
                        assertThat(steps[0].getUsageType(), `is`(XpmUsageType.Element))
                        assertThat(steps[1].getUsageType(), `is`(XpmUsageType.Attribute))
                    }
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.7.1) Sequence Concatenation ; XPath 3.1 (3.4.1) Constructing Sequences")
        internal inner class SequenceConcatenation {
            @Test
            @DisplayName("XPath 3.1 EBNF (6) Expr")
            fun expr() {
                val expr = parse<XPathExpr>("(1, 2 + 3, 4)")[1] as XpmConcatenatingExpression
                assertThat(expr.expressionElement, `is`(nullValue()))

                val exprs = expr.expressions.toList()
                assertThat(exprs.size, `is`(3))
                assertThat(exprs[0].text, `is`("1"))
                assertThat(exprs[1].text, `is`("2 + 3"))
                assertThat(exprs[2].text, `is`("4"))
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.7.2) Range Expressions ; XPath 3.1 (3.4.1) Constructing Sequences")
        internal inner class RangeExpressions {
            @Test
            @DisplayName("XPath 3.1 EBNF (20) RangeExpr")
            fun rangeExpr() {
                val expr = parse<XPathRangeExpr>("1 to 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_TO))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.7.3) Combining Node Sequences ; XPath 3.1 (3.4.2) Combining Node Sequences")
        internal inner class CombiningNodeSequences {
            @Nested
            @DisplayName("XPath 3.1 EBNF (23) UnionExpr")
            internal inner class UnionExpr {
                @Test
                @DisplayName("keyword")
                fun keyword() {
                    val expr = parse<XPathUnionExpr>("1 union 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_UNION))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("symbol")
                fun symbol() {
                    val expr = parse<XPathUnionExpr>("1 | 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.UNION))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (24) IntersectExceptExpr")
            internal inner class IntersectExceptExpr {
                @Test
                @DisplayName("intersect")
                fun intersect() {
                    val expr = parse<XPathIntersectExceptExpr>("1 intersect 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_INTERSECT))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("except")
                fun except() {
                    val expr = parse<XPathIntersectExceptExpr>("1 except 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_EXCEPT))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.8) Arithmetic Expressions ; XPath 3.1 (3.5) Arithmetic Expressions")
        internal inner class ArithmeticExpressions {
            @Nested
            @DisplayName("XPath 3.1 EBNF (21) AdditiveExpr")
            internal inner class AdditiveExpr {
                @Test
                @DisplayName("plus")
                fun plus() {
                    val expr = parse<XPathAdditiveExpr>("1 + 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.PLUS))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("minus")
                fun minus() {
                    val expr = parse<XPathAdditiveExpr>("1 - 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.MINUS))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (22) MultiplicativeExpr")
            internal inner class MultiplicativeExpr {
                @Test
                @DisplayName("multiply")
                fun multiply() {
                    val expr = parse<XPathMultiplicativeExpr>("1 * 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.STAR))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("div")
                fun div() {
                    val expr = parse<XPathMultiplicativeExpr>("1 div 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_DIV))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("idiv")
                fun idiv() {
                    val expr = parse<XPathMultiplicativeExpr>("1 idiv 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_IDIV))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("mod")
                fun mod() {
                    val expr = parse<XPathMultiplicativeExpr>("1 mod 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_MOD))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (30) UnaryExpr")
            internal inner class UnaryExpr {
                @Test
                @DisplayName("plus")
                fun plus() {
                    val expr = parse<XPathUnaryExpr>("+3")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.PLUS))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }

                @Test
                @DisplayName("minus")
                fun minus() {
                    val expr = parse<XPathUnaryExpr>("-3")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.MINUS))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.9) String Concatenation Expressions ; XPath 3.1 (3.6) String Concatenation Expressions")
        internal inner class StringConcatenationExpressions {
            @Test
            @DisplayName("XPath 3.1 EBNF (19) StringConcatExpr")
            fun stringConcatExpr() {
                val expr = parse<XPathStringConcatExpr>("1 || 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.CONCATENATION))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.10) Comparison Expressions ; XPath 3.1 (3.7) Comparison Expressions")
        internal inner class ComparisonExpressions {
            @Nested
            @DisplayName("XPath 3.1 EBNF (18) ComparisonExpr ; XPath 3.1 EBNF (32) GeneralComp")
            internal inner class ComparisonExpr_GeneralComp {
                @Test
                @DisplayName("eq")
                fun eq() {
                    val expr = parse<XPathComparisonExpr>("1 = 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.EQUAL))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("ne")
                fun ne() {
                    val expr = parse<XPathComparisonExpr>("1 != 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.NOT_EQUAL))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("lt")
                fun lt() {
                    val expr = parse<XPathComparisonExpr>("1 < 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.LESS_THAN))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("le")
                fun le() {
                    val expr = parse<XPathComparisonExpr>("1 <= 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.LESS_THAN_OR_EQUAL))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("gt")
                fun gt() {
                    val expr = parse<XPathComparisonExpr>("1 > 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.GREATER_THAN))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("ge")
                fun ge() {
                    val expr = parse<XPathComparisonExpr>("1 >= 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.GREATER_THAN_OR_EQUAL))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (18) ComparisonExpr ; XPath 3.1 EBNF (33) ValueComp")
            internal inner class ComparisonExpr_ValueComp {
                @Test
                @DisplayName("eq")
                fun eq() {
                    val expr = parse<XPathComparisonExpr>("1 eq 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_EQ))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("ne")
                fun ne() {
                    val expr = parse<XPathComparisonExpr>("1 ne 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_NE))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("lt")
                fun lt() {
                    val expr = parse<XPathComparisonExpr>("1 lt 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_LT))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("le")
                fun le() {
                    val expr = parse<XPathComparisonExpr>("1 le 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_LE))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("gt")
                fun gt() {
                    val expr = parse<XPathComparisonExpr>("1 gt 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_GT))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("ge")
                fun ge() {
                    val expr = parse<XPathComparisonExpr>("1 ge 2")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_GE))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (18) ComparisonExpr ; XPath 3.1 EBNF (34) NodeComp")
            internal inner class ComparisonExpr_NodeComp {
                @Test
                @DisplayName("is")
                fun eq() {
                    val expr = parse<XPathComparisonExpr>("a is b")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_IS))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("<<")
                fun before() {
                    val expr = parse<XPathComparisonExpr>("a << b")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.NODE_BEFORE))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName(">>")
                fun after() {
                    val expr = parse<XPathComparisonExpr>("a >> b")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.NODE_AFTER))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.11) Logical Expressions ; XPath 3.1 (3.8) Logical Expressions")
        internal inner class LogicalExpressions {
            @Test
            @DisplayName("XPath 3.1 EBNF (16) OrExpr")
            fun orExpr() {
                val expr = parse<XPathOrExpr>("1 or 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_OR))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (17) AndExpr")
            fun andExpr() {
                val expr = parse<XPathAndExpr>("1 and 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_AND))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.12) For Expressions ; XPath 3.1 (3.9) For Expressions")
        internal inner class ForExpressions {
            @Nested
            @DisplayName("XPath 3.1 EBNF (8) ForExpr")
            internal inner class ForExpr {
                @Test
                @DisplayName("for expression")
                fun forExpr() {
                    val expr = parse<XPathForExpr>("for \$x in (1, 2, 3) return \$x")[0] as XpmFlworExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.FOR_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))

                    val clauses = expr.clauses.toList()
                    assertThat(clauses.size, `is`(1))

                    assertThat(clauses[0], instanceOf(XpmForClause::class.java))
                    assertThat((clauses[0] as PsiElement).text, `is`("for \$x in (1, 2, 3) "))

                    assertThat(expr.returnExpression?.text, `is`("\$x"))
                }

                @Test
                @DisplayName("missing return expression")
                fun missingReturnExpr() {
                    val expr = parse<XPathForExpr>("for \$x in (1, 2, 3) return")[0] as XpmFlworExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.FOR_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))

                    val clauses = expr.clauses.toList()
                    assertThat(clauses.size, `is`(1))

                    assertThat(clauses[0], instanceOf(XpmForClause::class.java))
                    assertThat((clauses[0] as PsiElement).text, `is`("for \$x in (1, 2, 3) "))

                    assertThat(expr.returnExpression, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (9) SimpleForClause")
            internal inner class SimpleForClause31 {
                @Test
                @DisplayName("single binding")
                fun singleBinding() {
                    val expr = parse<XPathSimpleForClause>("for \$x in (1, 2, 3) return \$x")[0] as XpmForClause

                    val bindings = expr.bindings.toList()
                    assertThat(bindings.size, `is`(1))

                    assertThat(qname_presentation(bindings[0].variableName!!), `is`("x"))
                    assertThat(bindings[0].bindingExpression?.text, `is`("1, 2, 3"))
                    assertThat(bindings[0].bindingCollectionType, `is`(XpmBindingCollectionType.SequenceItem))
                }

                @Test
                @DisplayName("multiple bindings")
                fun multipleBindings() {
                    val expr = parse<XPathSimpleForClause>(
                        "for \$x in (1, 2, 3), \$y in (4, 5, 6) return \$x"
                    )[0] as XpmForClause

                    val bindings = expr.bindings.toList()
                    assertThat(bindings.size, `is`(2))

                    assertThat(qname_presentation(bindings[0].variableName!!), `is`("x"))
                    assertThat(bindings[0].bindingExpression?.text, `is`("1, 2, 3"))
                    assertThat(bindings[0].bindingCollectionType, `is`(XpmBindingCollectionType.SequenceItem))

                    assertThat(qname_presentation(bindings[1].variableName!!), `is`("y"))
                    assertThat(bindings[1].bindingExpression?.text, `is`("4, 5, 6"))
                    assertThat(bindings[1].bindingCollectionType, `is`(XpmBindingCollectionType.SequenceItem))
                }
            }

            @Nested
            @DisplayName("XPath 4.0 ED EBNF (13) SimpleForClause")
            internal inner class SimpleForClause40 {
                @Test
                @DisplayName("single binding")
                fun singleBinding() {
                    val expr = parse<XPathSimpleForClause>("for member \$x in (1, 2, 3) return \$x")[0] as XpmForClause

                    val bindings = expr.bindings.toList()
                    assertThat(bindings.size, `is`(1))

                    assertThat(qname_presentation(bindings[0].variableName!!), `is`("x"))
                    assertThat(bindings[0].bindingExpression?.text, `is`("1, 2, 3"))
                    assertThat(bindings[0].bindingCollectionType, `is`(XpmBindingCollectionType.ArrayMember))
                }

                @Test
                @DisplayName("multiple bindings")
                fun multipleBindings() {
                    val expr = parse<XPathSimpleForClause>(
                        "for member \$x in (1, 2, 3), \$y in (4, 5, 6) return \$x"
                    )[0] as XpmForClause

                    val bindings = expr.bindings.toList()
                    assertThat(bindings.size, `is`(2))

                    assertThat(qname_presentation(bindings[0].variableName!!), `is`("x"))
                    assertThat(bindings[0].bindingExpression?.text, `is`("1, 2, 3"))
                    assertThat(bindings[0].bindingCollectionType, `is`(XpmBindingCollectionType.ArrayMember))

                    assertThat(qname_presentation(bindings[1].variableName!!), `is`("y"))
                    assertThat(bindings[1].bindingExpression?.text, `is`("4, 5, 6"))
                    assertThat(bindings[1].bindingCollectionType, `is`(XpmBindingCollectionType.ArrayMember))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (10) SimpleForBinding")
            internal inner class SimpleForBinding {
                @Test
                @DisplayName("NCName")
                fun ncname() {
                    val expr = parse<XPathSimpleForBinding>("for \$x in 2 return \$y")[0] as XpmCollectionBinding
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.bindingExpression?.text, `is`("2"))

                    val qname = expr.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XPathForExpr::class.java)))
                }

                @Test
                @DisplayName("QName")
                fun qname() {
                    val expr = parse<XPathSimpleForBinding>("for \$a:x in 2 return \$a:y")[0] as XpmCollectionBinding
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.bindingExpression?.text, `is`("2"))

                    val qname = expr.variableName!!
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix!!.data, `is`("a"))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XPathForExpr::class.java)))
                }

                @Test
                @DisplayName("URIQualifiedName")
                fun uriQualifiedName() {
                    val expr = parse<XPathSimpleForBinding>(
                        "for \$Q{http://www.example.com}x in 2 return \$Q{http://www.example.com}y"
                    )[0] as XpmCollectionBinding
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.bindingExpression?.text, `is`("2"))

                    val qname = expr.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XPathForExpr::class.java)))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.13) Let Expressions ; XPath 3.1 (3.10) Let Expressions")
        internal inner class LetExpressions {
            @Nested
            @DisplayName("XPath 3.1 EBNF (11) LetExpr")
            internal inner class LetExpr {
                @Test
                @DisplayName("let expression")
                fun letExpr() {
                    val expr = parse<XPathLetExpr>("let \$x := (1, 2, 3) return \$x")[0] as XpmFlworExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.LET_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))

                    val clauses = expr.clauses.toList()
                    assertThat(clauses.size, `is`(1))

                    assertThat(clauses[0], instanceOf(XpmLetClause::class.java))
                    assertThat((clauses[0] as PsiElement).text, `is`("let \$x := (1, 2, 3) "))

                    assertThat(expr.returnExpression?.text, `is`("\$x"))
                }

                @Test
                @DisplayName("missing return expression")
                fun missingReturnExpr() {
                    val expr = parse<XPathLetExpr>("let \$x := (1, 2, 3) return")[0] as XpmFlworExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.LET_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))

                    val clauses = expr.clauses.toList()
                    assertThat(clauses.size, `is`(1))

                    assertThat(clauses[0], instanceOf(XpmLetClause::class.java))
                    assertThat((clauses[0] as PsiElement).text, `is`("let \$x := (1, 2, 3) "))

                    assertThat(expr.returnExpression, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (12) SimpleLetClause")
            internal inner class SimpleLetClause {
                @Test
                @DisplayName("single binding")
                fun singleBinding() {
                    val expr = parse<XPathSimpleLetClause>("let \$x := (1, 2, 3) return \$x")[0] as XpmLetClause

                    val bindings = expr.bindings.toList()
                    assertThat(bindings.size, `is`(1))

                    assertThat(qname_presentation(bindings[0].variableName!!), `is`("x"))
                    assertThat(bindings[0].variableExpression?.text, `is`("1, 2, 3"))
                }

                @Test
                @DisplayName("multiple bindings")
                fun multipleBindings() {
                    val expr = parse<XPathSimpleLetClause>(
                        "let \$x := (1, 2, 3), \$y := (4, 5, 6) return \$x"
                    )[0] as XpmLetClause

                    val bindings = expr.bindings.toList()
                    assertThat(bindings.size, `is`(2))

                    assertThat(qname_presentation(bindings[0].variableName!!), `is`("x"))
                    assertThat(bindings[0].variableExpression?.text, `is`("1, 2, 3"))

                    assertThat(qname_presentation(bindings[1].variableName!!), `is`("y"))
                    assertThat(bindings[1].variableExpression?.text, `is`("4, 5, 6"))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (13) SimpleLetBinding")
            internal inner class SimpleLetBinding {
                @Test
                @DisplayName("NCName")
                fun ncname() {
                    val expr = parse<XPathSimpleLetBinding>("let \$x := 2 return \$y")[0] as XpmAssignableVariable
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.variableExpression?.text, `is`("2"))

                    val qname = expr.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XPathLetExpr::class.java)))
                }

                @Test
                @DisplayName("QName")
                fun qname() {
                    val expr = parse<XPathSimpleLetBinding>("let \$a:x := 2 return \$a:y")[0] as XpmAssignableVariable
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.variableExpression?.text, `is`("2"))

                    val qname = expr.variableName!!
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix!!.data, `is`("a"))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XPathLetExpr::class.java)))
                }

                @Test
                @DisplayName("URIQualifiedName")
                fun uriQualifiedName() {
                    val expr = parse<XPathSimpleLetBinding>(
                        "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}y"
                    )[0] as XpmAssignableVariable
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.variableExpression?.text, `is`("2"))

                    val qname = expr.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XPathLetExpr::class.java)))
                }

                @Test
                @DisplayName("missing VarName")
                fun missingVarName() {
                    val expr = parse<XPathSimpleLetBinding>("let \$ := 2 return \$w")[0] as XpmAssignableVariable
                    assertThat(expr.variableName, `is`(nullValue()))
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.variableExpression?.text, `is`("2"))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.14) Maps and Arrays ; XPath 3.1 (3.11) Maps and Arrays")
        internal inner class MapsAndArrays {
            @Nested
            @DisplayName("XPath 3.1 (3.11.1) Maps")
            internal inner class Maps {
                @Nested
                @DisplayName("XPath 3.1 EBNF (69) MapConstructor")
                internal inner class MapConstructor {
                    @Test
                    @DisplayName("empty")
                    fun empty() {
                        val expr = parse<XPathMapConstructor>("map {}")[0] as XpmMapExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.MAP_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))

                        assertThat(expr.itemTypeClass, sameInstance(XdmMap::class.java))
                        assertThat(expr.itemExpression, sameInstance(expr))

                        val entries = expr.entries.toList()
                        assertThat(entries.size, `is`(0))
                    }

                    @Test
                    @DisplayName("with entries")
                    fun withEntries() {
                        val expr = parse<XPathMapConstructor>("map { \"1\" : \"one\", \"2\" : \"two\" }")[0] as XpmMapExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.MAP_CONSTRUCTOR_ENTRY))
                        assertThat(expr.expressionElement?.textOffset, `is`(6))

                        assertThat(expr.itemTypeClass, sameInstance(XdmMap::class.java))
                        assertThat(expr.itemExpression, sameInstance(expr))

                        val entries = expr.entries.toList()
                        assertThat(entries.size, `is`(2))

                        assertThat((entries[0].keyExpression as XPathStringLiteral).data, `is`("1"))
                        assertThat((entries[0].valueExpression as XPathStringLiteral).data, `is`("one"))

                        assertThat((entries[1].keyExpression as XPathStringLiteral).data, `is`("2"))
                        assertThat((entries[1].valueExpression as XPathStringLiteral).data, `is`("two"))
                    }
                }

                @Nested
                @DisplayName("XPath 3.1 EBNF (70) MapConstructorEntry")
                internal inner class MapConstructorEntry {
                    @Test
                    @DisplayName("string key, value")
                    fun stringKey() {
                        val entry = parse<XPathMapConstructorEntry>("map { \"1\" : \"one\" }")[0]
                        assertThat(entry.separator.elementType, `is`(XPathTokenType.QNAME_SEPARATOR))

                        assertThat((entry.keyExpression as XPathStringLiteral).data, `is`("1"))
                        assertThat((entry.valueExpression as XPathStringLiteral).data, `is`("one"))
                        assertThat(entry.keyName, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("integer key, value")
                    fun integerKey() {
                        val entry = parse<XPathMapConstructorEntry>("map { 1 : \"one\" }")[0]
                        assertThat(entry.separator.elementType, `is`(XPathTokenType.QNAME_SEPARATOR))

                        assertThat((entry.keyExpression as XPathIntegerLiteral).data, `is`(BigInteger.ONE))
                        assertThat((entry.valueExpression as XPathStringLiteral).data, `is`("one"))
                        assertThat(entry.keyName, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("key, no value")
                    fun missingValue() {
                        val entry = parse<XPathMapConstructorEntry>("map { \$ a }")[0]
                        assertThat(entry.separator.elementType, `is`(XPathElementType.VAR_REF))

                        assertThat((entry.keyExpression as XpmVariableReference).variableName?.localName?.data, `is`("a"))
                        assertThat(entry.valueExpression, `is`(nullValue()))
                        assertThat(entry.keyName, `is`(nullValue()))
                    }
                }
            }

            @Nested
            @DisplayName("XPath 3.1 (3.11.2) Arrays")
            internal inner class Arrays {
                @Nested
                @DisplayName("XPath 3.1 EBNF (74) SquareArrayConstructor")
                internal inner class SquareArrayConstructor {
                    @Test
                    @DisplayName("empty")
                    fun empty() {
                        val expr = parse<XPathSquareArrayConstructor>("[]")[0] as XpmArrayExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.SQUARE_ARRAY_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))

                        assertThat(expr.itemTypeClass, sameInstance(XdmArray::class.java))
                        assertThat(expr.itemExpression, sameInstance(expr))

                        val entries = expr.memberExpressions.toList()
                        assertThat(entries.size, `is`(0))
                    }

                    @Test
                    @DisplayName("single member")
                    fun singleMember() {
                        val expr = parse<XPathSquareArrayConstructor>("[ 1 ]")[0] as XpmArrayExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.SQUARE_ARRAY_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))

                        assertThat(expr.itemTypeClass, sameInstance(XdmArray::class.java))
                        assertThat(expr.itemExpression, sameInstance(expr))

                        val entries = expr.memberExpressions.toList()
                        assertThat(entries.size, `is`(1))
                        assertThat(entries[0].text, `is`("1"))
                    }

                    @Test
                    @DisplayName("multiple members")
                    fun multipleMembers() {
                        val expr = parse<XPathSquareArrayConstructor>("[ 1, 2 + 3, 4 ]")[0] as XpmArrayExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.SQUARE_ARRAY_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))

                        assertThat(expr.itemTypeClass, sameInstance(XdmArray::class.java))
                        assertThat(expr.itemExpression, sameInstance(expr))

                        val entries = expr.memberExpressions.toList()
                        assertThat(entries.size, `is`(3))
                        assertThat(entries[0].text, `is`("1"))
                        assertThat(entries[1].text, `is`("2 + 3"))
                        assertThat(entries[2].text, `is`("4"))
                    }
                }

                @Nested
                @DisplayName("XPath 3.1 EBNF (75) CurlyArrayConstructor")
                internal inner class CurlyArrayConstructor {
                    @Test
                    @DisplayName("empty")
                    fun empty() {
                        val expr = parse<XPathCurlyArrayConstructor>("array {}")[0] as XpmArrayExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CURLY_ARRAY_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))

                        assertThat(expr.itemTypeClass, sameInstance(XdmArray::class.java))
                        assertThat(expr.itemExpression, sameInstance(expr))

                        val entries = expr.memberExpressions.toList()
                        assertThat(entries.size, `is`(0))
                    }

                    @Test
                    @DisplayName("single member")
                    fun singleMember() {
                        val expr = parse<XPathCurlyArrayConstructor>("array { 1 }")[0] as XpmArrayExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CURLY_ARRAY_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))

                        assertThat(expr.itemTypeClass, sameInstance(XdmArray::class.java))
                        assertThat(expr.itemExpression, sameInstance(expr))

                        val entries = expr.memberExpressions.toList()
                        assertThat(entries.size, `is`(1))
                        assertThat(entries[0].text, `is`("1"))
                    }

                    @Test
                    @DisplayName("multiple members")
                    fun multipleMembers() {
                        val expr = parse<XPathCurlyArrayConstructor>("array { 1, 2 + 3, 4 }")[0] as XpmArrayExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CURLY_ARRAY_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))

                        assertThat(expr.itemTypeClass, sameInstance(XdmArray::class.java))
                        assertThat(expr.itemExpression, sameInstance(expr))

                        val entries = expr.memberExpressions.toList()
                        assertThat(entries.size, `is`(3))
                        assertThat(entries[0].text, `is`("1"))
                        assertThat(entries[1].text, `is`("2 + 3"))
                        assertThat(entries[2].text, `is`("4"))
                    }
                }
            }

            @Nested
            @DisplayName("XPath 3.1 (3.11.3.1) Unary Lookup")
            internal inner class UnaryLookup {
                @Test
                @DisplayName("XPath 3.1 EBNF (76) UnaryLookup")
                fun unaryLookup() {
                    val expr = parse<XPathUnaryLookup>("map{} ! ?name")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.OPTIONAL))
                    assertThat(expr.expressionElement?.textOffset, `is`(8))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (54) KeySpecifier ; XPath 3.1 EBNF (113) IntegerLiteral")
                fun keySpecifier_expression() {
                    val expr = parse<XPathUnaryLookup>("map{} ! ?2")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression, sameInstance(XpmContextItem))

                    val key = expr.keyExpression as XsIntegerValue
                    assertThat(key.data, `is`(BigInteger.valueOf(2)))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (54) KeySpecifier ; XPath 3.1 EBNF (123) NCName")
                fun keySpecifier_ncname() {
                    val expr = parse<XPathUnaryLookup>("map{} ! ?name")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression, sameInstance(XpmContextItem))

                    val key = expr.keyExpression as XsNCNameValue
                    assertThat(key.data, `is`("name"))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (54) KeySpecifier ; wildcard")
                fun keySpecifier_wildcard() {
                    val expr = parse<XPathUnaryLookup>("map{} ! ?*")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression, sameInstance(XpmContextItem))

                    val key = expr.keyExpression as XdmWildcardValue
                    assertThat(key.data, `is`("*"))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 (3.11.3.2) Postfix Lookup")
            internal inner class PostfixLookup {
                @Test
                @DisplayName("XQuery IntelliJ Plugin XPath EBNF (48) PostfixLookup")
                fun postfixLookup() {
                    val step = parse<XPathPostfixExpr>("\$x?name")[0] as XpmPathStep
                    assertThat(step.axisType, `is`(XpmAxisType.Self))
                    assertThat(step.nodeName, `is`(nullValue()))
                    assertThat(step.nodeType, sameInstance(XdmNodeItem))
                    assertThat(step.predicateExpression, `is`(nullValue()))

                    val expr = step as XpmExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.OPTIONAL))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (54) KeySpecifier ; XPath 3.1 EBNF (113) IntegerLiteral")
                fun keySpecifier_expression() {
                    val expr = parse<XPathPostfixExpr>("\$x?2")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression.text, `is`("\$x"))

                    val key = expr.keyExpression as XsIntegerValue
                    assertThat(key.data, `is`(BigInteger.valueOf(2)))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (54) KeySpecifier ; XPath 3.1 EBNF (123) NCName")
                fun keySpecifier_ncname() {
                    val expr = parse<XPathPostfixExpr>("\$x?name")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression.text, `is`("\$x"))

                    val key = expr.keyExpression as XsNCNameValue
                    assertThat(key.data, `is`("name"))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (54) KeySpecifier ; wildcard")
                fun keySpecifier_wildcard() {
                    val expr = parse<XPathPostfixExpr>("\$x?*")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression.text, `is`("\$x"))

                    val key = expr.keyExpression as XdmWildcardValue
                    assertThat(key.data, `is`("*"))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.15) Conditional Expressions ; XPath 3.1 (3.12) Conditional Expressions")
        internal inner class ConditionalExpressions {
            @Test
            @DisplayName("XPath 4.0 ED EBNF (11) TernaryConditionalExpr")
            fun ternaryConditionalExpr() {
                val expr = parse<XPathTernaryConditionalExpr>("true() ?? 1 !! 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.TERNARY_IF))
                assertThat(expr.expressionElement?.textOffset, `is`(7))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (15) IfExpr")
            fun ifExpr() {
                val expr = parse<XPathIfExpr>("if (true()) then 1 else 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.IF_EXPR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.16) Otherwise Expressions")
        internal inner class OtherwiseExpressions {
            @Test
            @DisplayName("XPath 4.0 ED EBNF (28) OtherwiseExpr")
            fun otherwiseExpr() {
                val expr = parse<XPathOtherwiseExpr>("1 otherwise 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_OTHERWISE))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.17) Quantified Expressions ; XPath 3.1 (3.13) Quantified Expressions")
        internal inner class QuantifiedExpressions {
            @Nested
            @DisplayName("XPath 3.1 EBNF (14) QuantifiedExpr")
            internal inner class QuantifiedExpr {
                @Test
                @DisplayName("some")
                fun some() {
                    val expr = parse<XPathQuantifiedExpr>("some \$x in (1, 2, 3) satisfies \$x = 1")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.QUANTIFIED_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }

                @Test
                @DisplayName("every")
                fun every() {
                    val expr = parse<XPathQuantifiedExpr>("every \$x in (1, 2, 3) satisfies \$x = 1")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.QUANTIFIED_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (14) QuantifiedExpr ; XPath 4.0 ED EBNF (19) QuantifierBinding")
            internal inner class QuantifierBinding {
                @Test
                @DisplayName("NCName")
                fun ncname() {
                    val expr = parse<XPathQuantifierBinding>(
                        "some \$x in \$y satisfies \$z"
                    )[0] as XpmCollectionBinding
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.bindingExpression?.text, `is`("\$y"))

                    val qname = expr.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XPathQuantifiedExpr::class.java)))
                }

                @Test
                @DisplayName("QName")
                fun qname() {
                    val expr = parse<XPathQuantifierBinding>(
                        "some \$a:x in \$a:y satisfies \$a:z"
                    )[0] as XpmCollectionBinding
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.bindingExpression?.text, `is`("\$a:y"))

                    val qname = expr.variableName!!
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix!!.data, `is`("a"))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XPathQuantifiedExpr::class.java)))
                }

                @Test
                @DisplayName("URIQualifiedName")
                fun uriQualifiedName() {
                    val expr = parse<XPathQuantifierBinding>(
                        "some \$Q{http://www.example.com}x in \$Q{http://www.example.com}y satisfies \$Q{http://www.example.com}z"
                    )[0] as XpmCollectionBinding
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.bindingExpression?.text, `is`("\$Q{http://www.example.com}y"))

                    val qname = expr.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XPathQuantifiedExpr::class.java)))
                }

                @Test
                @DisplayName("missing VarName")
                fun missingVarName() {
                    val expr = parse<XPathQuantifierBinding>("some \$")[0] as XpmCollectionBinding
                    assertThat(expr.variableName, `is`(nullValue()))
                    assertThat(expr.variableType?.typeName, `is`(nullValue()))
                    assertThat(expr.bindingExpression?.text, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.18) Expressions on Sequence Types ; XPath 3.1 (3.14) Expressions on SequenceTypes")
        internal inner class ExpressionsOnSequenceTypes {
            @Test
            @DisplayName("XPath 3.1 EBNF (25) InstanceofExpr")
            fun instanceOfExpr() {
                val expr = parse<XPathInstanceofExpr>("1 instance of xs:string")[0] as XpmSequenceTypeExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_INSTANCE))
                assertThat(expr.expressionElement?.textOffset, `is`(2))

                assertThat(expr.operation, `is`(XpmSequenceTypeOperation.InstanceOf))
                assertThat((expr.expression as XsIntegerValue).data, `is`(BigInteger.ONE))
                assertThat(expr.type.typeName, `is`("xs:string"))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (26) TreatExpr")
            fun treatExpr() {
                val expr = parse<XPathTreatExpr>("1 treat as xs:string")[0] as XpmSequenceTypeExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_TREAT))
                assertThat(expr.expressionElement?.textOffset, `is`(2))

                assertThat(expr.operation, `is`(XpmSequenceTypeOperation.TreatAs))
                assertThat((expr.expression as XsIntegerValue).data, `is`(BigInteger.ONE))
                assertThat(expr.type.typeName, `is`("xs:string"))
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (27) CastableExpr")
            fun castableExpr() {
                val expr = parse<XPathCastableExpr>("1 castable as xs:string")[0] as XpmSequenceTypeExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_CASTABLE))
                assertThat(expr.expressionElement?.textOffset, `is`(2))

                assertThat(expr.operation, `is`(XpmSequenceTypeOperation.CastableAs))
                assertThat((expr.expression as XsIntegerValue).data, `is`(BigInteger.ONE))
                assertThat(expr.type.typeName, `is`("xs:string"))
            }

            @Nested
            @DisplayName("XPath 3.1 (3.14.2) Cast")
            internal inner class Cast {
                @Test
                @DisplayName("XPath 3.1 EBNF (28) CastExpr")
                fun castExpr() {
                    val expr = parse<XPathCastExpr>("1 cast as xs:string")[0] as XpmSequenceTypeExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_CAST))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))

                    assertThat(expr.operation, `is`(XpmSequenceTypeOperation.CastAs))
                    assertThat((expr.expression as XsIntegerValue).data, `is`(BigInteger.ONE))
                    assertThat(expr.type.typeName, `is`("xs:string"))
                }

                @Nested
                @DisplayName("XPath 3.1 EBNF (100) SimpleTypeName")
                internal inner class SimpleTypeName {
                    @Test
                    @DisplayName("NCName namespace resolution")
                    fun ncname() {
                        val qname = parse<XPathEQName>("() cast as test")[0] as XsQNameValue
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

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
                        assertThat(qname_presentation(test.type), `is`("xs:string"))

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
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.19) Simple Map Operator ; XPath 3.1 (3.15) Simple Map Operator")
        internal inner class SimpleMapOperator {
            @Test
            @DisplayName("XPath 3.1 EBNF (35) SimpleMapExpr")
            fun simpleMapExpr() {
                val expr = parse<XPathSimpleMapExpr>("/lorem ! fn:abs(.)")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.MAP_OPERATOR))
                assertThat(expr.expressionElement?.textOffset, `is`(7))
            }
        }

        @Nested
        @DisplayName("XPath 4.0 ED (4.17) Arrow Expressions ; XPath 3.1 (3.16) Arrow Operator")
        internal inner class ArrowExpressions {
            @Nested
            @DisplayName("XPath 3.1 EBNF (29) ArrowExpr")
            internal inner class ArrowExpr {
                @Test
                @DisplayName("XPath 3.1 EBNF (112) EQName")
                fun eqname() {
                    val expr = parse<XPathArrowExpr>("1 => fn:abs()")[0] as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (59) VarRef")
                fun varRef() {
                    val expr = parse<XPathArrowExpr>("let \$x := fn:abs#1 return 1 => \$x()")[0] as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Test
                @DisplayName("XPath 3.1 EBNF (61) ParenthesizedExpr")
                fun parenthesizedExpr() {
                    val expr = parse<XPathArrowExpr>("1 => (fn:abs#1)()")[0] as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XPath 3.1 EBNF (55) ArrowFunctionSpecifier; XPath 3.1 EBNF (112) EQName")
            internal inner class ArrowFunctionSpecifier_EQName {
                @Test
                @DisplayName("positional arguments")
                fun positionalArguments() {
                    val f = parse<PluginArrowFunctionCall>("\$x => format-date(1, 2, 3,  4)")[0] as XpmFunctionCall
                    assertThat(f.functionCallExpression, sameInstance(f))

                    val ref = f.functionReference!!
                    assertThat(ref, sameInstance(f))
                    assertThat(ref.positionalArity, `is`(5))
                    assertThat(ref.keywordArity, `is`(0))

                    val qname = ref.functionName!!
                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("format-date"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    assertThat(f.positionalArguments.size, `is`(4))
                    assertThat(f.keywordArguments.size, `is`(0))

                    assertThat(f.positionalArguments[0].text, `is`("1"))
                    assertThat(f.positionalArguments[1].text, `is`("2"))
                    assertThat(f.positionalArguments[2].text, `is`("3"))
                    assertThat(f.positionalArguments[3].text, `is`("4"))
                }

                @Test
                @DisplayName("positional and keyword arguments")
                fun positionalAndKeywordArguments() {
                    val f = parse<PluginArrowFunctionCall>(
                        "\$x => format-date(1, 2, calendar: 3,  place: 4)"
                    )[0] as XpmFunctionCall
                    assertThat(f.functionCallExpression, sameInstance(f))

                    val ref = f.functionReference!!
                    assertThat(ref, sameInstance(f))
                    assertThat(ref.positionalArity, `is`(3))
                    assertThat(ref.keywordArity, `is`(2))

                    val qname = ref.functionName!!
                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("format-date"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    assertThat(f.positionalArguments.size, `is`(2))
                    assertThat(f.keywordArguments.size, `is`(2))

                    assertThat(f.positionalArguments[0].text, `is`("1"))
                    assertThat(f.positionalArguments[1].text, `is`("2"))

                    assertThat(f.keywordArguments[0].keyName, `is`("calendar"))
                    assertThat(f.keywordArguments[1].keyName, `is`("place"))

                    assertThat(f.keywordArguments[0].valueExpression?.text, `is`("3"))
                    assertThat(f.keywordArguments[1].valueExpression?.text, `is`("4"))
                }

                @Test
                @DisplayName("keyword arguments")
                fun keywordArguments() {
                    val f = parse<PluginArrowFunctionCall>(
                        "\$x => format-date(picture: 1, language: 2, calendar: 3,  place: 4)"
                    )[0] as XpmFunctionCall
                    assertThat(f.functionCallExpression, sameInstance(f))

                    val ref = f.functionReference!!
                    assertThat(ref, sameInstance(f))
                    assertThat(ref.positionalArity, `is`(1))
                    assertThat(ref.keywordArity, `is`(4))

                    val qname = ref.functionName!!
                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("format-date"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    assertThat(f.positionalArguments.size, `is`(0))
                    assertThat(f.keywordArguments.size, `is`(4))

                    assertThat(f.keywordArguments[0].keyName, `is`("picture"))
                    assertThat(f.keywordArguments[1].keyName, `is`("language"))
                    assertThat(f.keywordArguments[2].keyName, `is`("calendar"))
                    assertThat(f.keywordArguments[3].keyName, `is`("place"))

                    assertThat(f.keywordArguments[0].valueExpression?.text, `is`("1"))
                    assertThat(f.keywordArguments[1].valueExpression?.text, `is`("2"))
                    assertThat(f.keywordArguments[2].valueExpression?.text, `is`("3"))
                    assertThat(f.keywordArguments[3].valueExpression?.text, `is`("4"))
                }

                @Test
                @DisplayName("empty arguments")
                fun emptyArguments() {
                    val f = parse<PluginArrowFunctionCall>("\$x => upper-case()")[0] as XpmFunctionCall
                    assertThat(f.functionCallExpression, sameInstance(f))

                    val ref = f.functionReference!!
                    assertThat(ref, sameInstance(f))
                    assertThat(ref.positionalArity, `is`(1))
                    assertThat(ref.keywordArity, `is`(0))

                    val qname = ref.functionName!!
                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("upper-case"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    assertThat(f.positionalArguments.size, `is`(0))
                    assertThat(f.keywordArguments.size, `is`(0))
                }

                @Test
                @DisplayName("invalid EQName")
                fun invalidEQName() {
                    val f = parse<PluginArrowFunctionCall>("\$x => :upper-case()")[0] as XpmFunctionCall
                    assertThat(f.functionCallExpression, sameInstance(f))

                    val ref = f.functionReference!!
                    assertThat(ref, sameInstance(f))
                    assertThat(ref.positionalArity, `is`(1))
                    assertThat(ref.keywordArity, `is`(0))
                    assertThat(ref.functionName, `is`(nullValue()))

                    assertThat(f.positionalArguments.size, `is`(0))
                    assertThat(f.keywordArguments.size, `is`(0))
                }

                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathEQName>("() => test()")[0] as XsQNameValue
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.FunctionRef))

                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("test"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))
                }

                @Test
                @DisplayName("reference rename")
                fun referenceRename() {
                    val expr = parse<PluginArrowFunctionCall>("1 => test()")[0] as XpmFunctionReference

                    val ref = (expr.functionName as PsiElement).reference!!
                    assertThat(ref, `is`(instanceOf(XPathFunctionNameReference::class.java)))

                    DebugUtil.performPsiModification<Throwable>("rename") {
                        val renamed = ref.handleElementRename("lorem-ipsum")
                        assertThat(renamed, `is`(instanceOf(XPathNCName::class.java)))
                        assertThat(renamed.text, `is`("lorem-ipsum"))
                        assertThat((renamed as PsiNameIdentifierOwner).name, `is`("lorem-ipsum"))
                    }

                    assertThat((expr.functionName as PsiElement).text, `is`("lorem-ipsum"))
                }
            }

            @Test
            @DisplayName("XPath 3.1 EBNF (55) ArrowFunctionSpecifier; XPath 3.1 EBNF (59) VarRef")
            fun arrowFunctionSpecifier_VarRef() {
                val f = parse<PluginArrowDynamicFunctionCall>(
                    "let \$f := format-date#5 return \$x => \$f(1, 2, 3,  4)"
                )[0] as XpmArrowFunctionCall

                assertThat(f.functionReference, `is`(nullValue())) // TODO: fn:format-date#5

                assertThat(f.positionalArguments.size, `is`(4))
                assertThat(f.positionalArguments[0].text, `is`("1"))
                assertThat(f.positionalArguments[1].text, `is`("2"))
                assertThat(f.positionalArguments[2].text, `is`("3"))

            }

            @Test
            @DisplayName("XPath 3.1 EBNF (55) ArrowFunctionSpecifier; XPath 3.1 EBNF (61) ParenthesizedExpr")
            fun arrowFunctionSpecifier_ParenthesizedExpr() {
                val f = parse<PluginArrowDynamicFunctionCall>(
                    "\$x => (format-date#5)(1, 2, 3,  4)"
                )[0] as XpmArrowFunctionCall

                val ref = f.functionReference!!
                assertThat(ref.positionalArity, `is`(5))
                assertThat(ref.keywordArity, `is`(0))

                val qname = ref.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("format-date"))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(f.positionalArguments.size, `is`(4))
                assertThat(f.positionalArguments[0].text, `is`("1"))
                assertThat(f.positionalArguments[1].text, `is`("2"))
                assertThat(f.positionalArguments[2].text, `is`("3"))
                assertThat(f.positionalArguments[3].text, `is`("4"))
            }

            @Nested
            @DisplayName("XPath 4.0 EBNF (38) FatArrowTarget ; XPath 4.0 EBNF (67) ArrowStaticFunction")
            internal inner class FatArrowTarget_ArrowStaticFunction {
                @Test
                @DisplayName("single function call")
                fun singleFunctionCall() {
                    val expr = parse<PluginArrowFunctionCall>("1 => fn:abs()")[0] as XpmExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARROW_FUNCTION_CALL))
                    assertThat(expr.expressionElement?.textOffset, `is`(5))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Chaining))
                    assertThat(f.sourceExpression.text, `is`("1"))
                }

                @Test
                @DisplayName("multiple function call; inner")
                fun multipleFunctionCallInner() {
                    val expr = parse<PluginArrowFunctionCall>("1 => fn:abs() => math:pow(2)")[0] as XpmExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARROW_FUNCTION_CALL))
                    assertThat(expr.expressionElement?.textOffset, `is`(5))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Chaining))
                    assertThat(f.sourceExpression.text, `is`("1"))
                }

                @Test
                @DisplayName("multiple function call; outer")
                fun multipleFunctionCallOuter() {
                    val expr = parse<PluginArrowFunctionCall>("1 => fn:abs() => math:pow(2)")[1] as XpmExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARROW_FUNCTION_CALL))
                    assertThat(expr.expressionElement?.textOffset, `is`(17))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Chaining))
                    assertThat(f.sourceExpression.text, `is`("fn:abs()"))
                }

                @Test
                @DisplayName("invalid EQName")
                fun invalidEQName() {
                    val expr = parse<PluginArrowFunctionCall>("1 => :abs()")[0] as XpmExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARROW_FUNCTION_CALL))
                    assertThat(expr.expressionElement?.textOffset, `is`(5))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Chaining))
                    assertThat(f.sourceExpression.text, `is`("1"))
                }
            }

            @Nested
            @DisplayName("XPath 4.0 EBNF (38) FatArrowTarget ; XPath 4.0 EBNF (67) ArrowDynamicFunction")
            internal inner class FatArrowTarget_ArrowDynamicFunction {
                @Test
                @DisplayName("single function call")
                fun singleFunctionCall() {
                    val expr = parse<PluginArrowDynamicFunctionCall>(
                        "let \$x := fn:abs#1 return 1 => \$x()"
                    )[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARGUMENT_LIST))
                    assertThat(expr.expressionElement?.textOffset, `is`(33))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Chaining))
                    assertThat(f.sourceExpression.text, `is`("1"))
                }

                @Test
                @DisplayName("multiple function call; inner")
                fun multipleFunctionCallInner() {
                    val expr = parse<PluginArrowDynamicFunctionCall>(
                        "let \$x := fn:abs#1 let \$y := math:pow#2 return 1 => \$x() => \$y(2)"
                    )[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARGUMENT_LIST))
                    assertThat(expr.expressionElement?.textOffset, `is`(54))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Chaining))
                    assertThat(f.sourceExpression.text, `is`("1"))
                }

                @Test
                @DisplayName("multiple function call; outer")
                fun multipleFunctionCallOuter() {
                    val expr = parse<PluginArrowDynamicFunctionCall>(
                        "let \$x := fn:abs#1 let \$y := math:pow#2 return 1 => \$x() => \$y(2)"
                    )[1] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARGUMENT_LIST))
                    assertThat(expr.expressionElement?.textOffset, `is`(62))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Chaining))
                    assertThat(f.sourceExpression.text, `is`("\$x()"))
                }
            }

            @Nested
            @DisplayName("XPath 4.0 EBNF (39) ThinArrowTarget ; XPath 4.0 EBNF (67) ArrowStaticFunction")
            internal inner class ThinArrowTarget_ArrowStaticFunction {
                @Test
                @DisplayName("single function call")
                fun singleFunctionCall() {
                    val expr = parse<PluginArrowFunctionCall>("1 -> fn:abs()")[0] as XpmExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARROW_FUNCTION_CALL))
                    assertThat(expr.expressionElement?.textOffset, `is`(5))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Mapping))
                    assertThat(f.sourceExpression.text, `is`("1"))
                }

                @Test
                @DisplayName("multiple function call; inner")
                fun multipleFunctionCallInner() {
                    val expr = parse<PluginArrowFunctionCall>("1 -> fn:abs() -> math:pow(2)")[0] as XpmExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARROW_FUNCTION_CALL))
                    assertThat(expr.expressionElement?.textOffset, `is`(5))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Mapping))
                    assertThat(f.sourceExpression.text, `is`("1"))
                }

                @Test
                @DisplayName("multiple function call; outer")
                fun multipleFunctionCallOuter() {
                    val expr = parse<PluginArrowFunctionCall>("1 -> fn:abs() -> math:pow(2)")[1] as XpmExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARROW_FUNCTION_CALL))
                    assertThat(expr.expressionElement?.textOffset, `is`(17))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Mapping))
                    assertThat(f.sourceExpression.text, `is`("fn:abs()"))
                }

                @Test
                @DisplayName("invalid EQName")
                fun invalidEQName() {
                    val expr = parse<PluginArrowFunctionCall>("1 -> :abs()")[0] as XpmExpression
                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARROW_FUNCTION_CALL))
                    assertThat(expr.expressionElement?.textOffset, `is`(5))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Mapping))
                    assertThat(f.sourceExpression.text, `is`("1"))
                }
            }

            @Nested
            @DisplayName("XPath 4.0 EBNF (39) ThinArrowTarget ; XPath 4.0 EBNF (67) ArrowDynamicFunction")
            internal inner class ThinArrowTarget_ArrowDynamicFunction {
                @Test
                @DisplayName("single function call")
                fun singleFunctionCall() {
                    val expr = parse<PluginArrowDynamicFunctionCall>(
                        "let \$x := fn:abs#1 return 1 -> \$x()"
                    )[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARGUMENT_LIST))
                    assertThat(expr.expressionElement?.textOffset, `is`(33))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Mapping))
                    assertThat(f.sourceExpression.text, `is`("1"))
                }

                @Test
                @DisplayName("multiple function call; inner")
                fun multipleFunctionCallInner() {
                    val expr = parse<PluginArrowDynamicFunctionCall>(
                        "let \$x := fn:abs#1 let \$y := math:pow#2 return 1 -> \$x() -> \$y(2)"
                    )[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARGUMENT_LIST))
                    assertThat(expr.expressionElement?.textOffset, `is`(54))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Mapping))
                    assertThat(f.sourceExpression.text, `is`("1"))
                }

                @Test
                @DisplayName("multiple function call; outer")
                fun multipleFunctionCallOuter() {
                    val expr = parse<PluginArrowDynamicFunctionCall>(
                        "let \$x := fn:abs#1 let \$y := math:pow#2 return 1 -> \$x() -> \$y(2)"
                    )[1] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathElementType.ARGUMENT_LIST))
                    assertThat(expr.expressionElement?.textOffset, `is`(62))

                    val f = expr as XpmArrowFunctionCall
                    assertThat(f.operation, `is`(XpmArrowOperation.Mapping))
                    assertThat(f.sourceExpression.text, `is`("\$x()"))
                }
            }
        }
    }
}
