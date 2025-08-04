// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.mock.MockProjectEx
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import com.intellij.mock.MockResolveScopeManager
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.module.ModuleManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.impl.ResolveScopeManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.ProjectScopeBuilder
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockProjectScopeBuilder
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
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
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmUsageType
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAccessLevel
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAnnotated
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmVariadic
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.*
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmContextItem
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmEmptyExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.type.XpmSequenceTypeExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.type.XpmSequenceTypeOperation
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.*
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.impl.XpmMissingArgument
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.XpmArrayExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.XpmMapExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.keyName
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.path.XpmAxisType
import uk.co.reecedunn.intellij.plugin.xpm.optree.path.XpmPathStep
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDefaultCaseClause
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirNamespaceAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.model.getNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryFunctionProvider
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase
import java.math.BigDecimal
import java.math.BigInteger

@Suppress("Reformat", "ClassName", "RedundantVisibilityModifier")
@DisplayName("XQuery 3.1 - IntelliJ Program Structure Interface (PSI)")
class XQueryPsiTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryPsiTest")

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        registerPsiModification()

        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
        XpmFunctionProvider.register(this, XQueryFunctionProvider)

        project.registerService<ProjectScopeBuilder>(MockProjectScopeBuilder())
        project.registerService<ResolveScopeManager>(MockResolveScopeManager(project))

        val manager = MockModuleManager(project as MockProjectEx)
        manager.addModule(res.findFileByPath("tests/module-xquery")!!)
        project.registerService<ModuleManager>(manager)
    }

    @Nested
    @DisplayName("XQuery 4.0 ED (2) Basics ; XQuery 3.1 (2) Basics")
    internal inner class Basics {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (217) URILiteral")
        internal inner class URILiteral {
            @Test
            @DisplayName("uri literal content")
            fun uriLiteral() {
                val literal = parse<XPathUriLiteral>("module namespace test = \"Lorem ipsum.\uFFFF\"")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("unclosed uri literal content")
            fun unclosedUriLiteral() {
                val literal = parse<XPathUriLiteral>("module namespace test = \"Lorem ipsum.")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("Lorem ipsum."))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val literal = parse<XPathUriLiteral>("module namespace test = '''\"\"'")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("'\"\""))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val literal = parse<XPathUriLiteral>("module namespace test = \"''\"\"\"")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("''\""))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val literal = parse<XPathUriLiteral>("module namespace test = \"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val literal = parse<XPathUriLiteral>("module namespace test = \"&#xA0;&#160;&#x20;&#x1D520;\"")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020\uD835\uDD20"))
                assertThat(literal.element, sameInstance(literal as PsiElement))
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
                assertThat(expanded.size, `is`(1))

                // The URIQualifiedName itself, not bound to a namespace.
                assertThat(expanded[0].isLexicalQName, `is`(false))
                assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2001/XMLSchema"))
                assertThat(expanded[0].prefix, `is`(nullValue()))
                assertThat(expanded[0].localName!!.data, `is`("test"))
                assertThat(expanded[0].element, sameInstance(qname as PsiElement))
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

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val literal = parse<XPathBracedURILiteral>("Q{&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
                assertThat(literal.context, `is`(XdmUriContext.Namespace))
                assertThat(literal.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(literal.element, sameInstance(literal as PsiElement))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val literal = parse<XPathBracedURILiteral>("Q{&#xA0;&#160;&#x20;&#x1D520;}")[0] as XsAnyUriValue
                assertThat(literal.data, `is`("\u00A0\u00A0\u0020\uD835\uDD20"))
                assertThat(literal.context, `is`(XdmUriContext.Namespace))
                assertThat(literal.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
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
    @DisplayName("XQuery 4.0 ED (3.4) Sequence Types ; XQuery 3.1 (2.5.4) SequenceType Syntax")
    internal inner class SequenceTypes {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (184) SequenceType")
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
    @DisplayName("XQuery 4.0 ED (3.6) Item Types ; XQuery 3.1 (2.5.5) SequenceType Matching")
    internal inner class ItemTypes {
        @Nested
        @DisplayName("XQuery 4.0 ED (3.6.1) General Item Types")
        internal inner class GeneralItemTypes {
            @Test
            @DisplayName("XQuery 3.1 EBNF (186) ItemType ; XQuery 4.0 ED EBNF (203) AnyItemTest")
            fun itemType() {
                val type = parse<XPathAnyItemTest>("() instance of item ( (::) )")[0] as XdmItemType
                assertThat(type.typeName, `is`("item()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmItem::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (216) ParenthesizedItemType")
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
                    val type = parse<XPathParenthesizedItemType>("() instance of ( empty-sequence ( (::) ) )")[0] as XdmSequenceType
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
        @DisplayName("XQuery 4.0 ED (3.6.2.1) Local Union Types")
        internal inner class AtomicAndUnionTypes {
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
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultType))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

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
                @DisplayName("item type")
                fun itemType() {
                    val test = parse<XPathAtomicOrUnionType>("() instance of xs:string")[0]
                    assertThat(qname_presentation(test.type), `is`("xs:string"))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("xs:string"))
                    assertThat(type.typeClass, `is`(sameInstance(XsAnySimpleType::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
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
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultType))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

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
        @DisplayName("XQuery 4.0 ED (3.6.2.1) Local Union Types")
        internal inner class LocalUnionTypes {
            @Nested
            @DisplayName("XQuery 4.0 ED EBNF (233) LocalUnionType")
            internal inner class LocalUnionType {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathEQName>(
                        """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    declare type decl = union(test);
                    """
                    )[1] as XsQNameValue
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultType))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

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
        @DisplayName("XQuery 4.0 ED (3.6.2.2) Enumeration Types")
        internal inner class EnumerationTypes {
            @Nested
            @DisplayName("XQuery 4.0 ED EBNF (236) EnumerationType")
            internal inner class LocalUnionType {
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
        @DisplayName("XQuery 4.0 ED (3.6.3.1) Simple Node Tests")
        internal inner class SimpleNodeTests {
            @Test
            @DisplayName("XQuery 3.1 EBNF (189) AnyKindTest")
            fun anyKindTest() {
                val type = parse<XPathAnyKindTest>("() instance of node ( (::) )")[0] as XdmItemType
                assertThat(type.typeName, `is`("node()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (190) DocumentTest")
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
            @DisplayName("XQuery 3.1 EBNF (191) TextTest")
            fun textTest() {
                val type = parse<PluginAnyTextTest>("() instance of text ( (::) )")[0] as XdmItemType
                assertThat(type.typeName, `is`("text()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmTextNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (192) CommentTest")
            fun commentTest() {
                val type = parse<XPathCommentTest>("() instance of comment ( (::) )")[0] as XdmItemType
                assertThat(type.typeName, `is`("comment()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmCommentNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (193) NamespaceNodeTest")
            fun namespaceNodeTest() {
                val type = parse<XPathNamespaceNodeTest>("() instance of namespace-node ( (::) )")[0] as XdmItemType
                assertThat(type.typeName, `is`("namespace-node()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmNamespaceNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (194) PITest")
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

                    val nodeName = test.nodeName as XsNCNameValue
                    assertThat(nodeName.data, `is`("test"))
                    assertThat(nodeName.element, `is`(sameInstance(test.children().filterIsInstance<XPathStringLiteral>().firstOrNull())))

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
        @DisplayName("XQuery 4.0 ED (3.6.3.2) Element Test ; XQuery 3.1 (2.5.5.3) Element Test")
        internal inner class ElementTest {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (199) ElementTest")
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
                @DisplayName("nillable type only")
                fun nillableTypeOnly() {
                    val test = parse<XPathElementTest>("() instance of element ( * , elem-type ? )")[0]
                    assertThat(test.nodeName, `is`(nullValue()))

                    val nodeType = test.nodeType!!
                    assertThat(nodeType.typeName, `is`("elem-type?"))
                    assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                    assertThat(nodeType.lowerBound, `is`(0))
                    assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("element(*,elem-type?)"))
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
                @DisplayName("name and nillable type")
                fun nameAndNillableType() {
                    val test = parse<XPathElementTest>("() instance of element ( test , elem-type ? )")[0]
                    assertThat(test.nodeName?.localName!!.data, `is`("test"))

                    val nodeType = test.nodeType!!
                    assertThat(nodeType.typeName, `is`("elem-type?"))
                    assertThat(nodeType.itemType?.typeClass, `is`(sameInstance(XsAnyType::class.java)))
                    assertThat(nodeType.lowerBound, `is`(0))
                    assertThat(nodeType.upperBound, `is`(Int.MAX_VALUE))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("element(test,elem-type?)"))
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
            @DisplayName("XQuery 4.0 ED EBNF (215) ElementTest ; XQuery 4.0 ED EBNF (128) NameTest")
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
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultElement))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Element))

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
        @DisplayName("XQuery 4.0 ED (3.6.3.3) Schema Element Test ; XQuery 3.1 (2.5.5.4) Schema Element Test")
        internal inner class SchemaElementTest {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (201) SchemaElementTest")
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
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultElement))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Element))

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
        @DisplayName("XQuery 4.0 ED (3.6.3.4) Attribute Test ; XQuery 3.1 (2.5.5.5) Attribute Test")
        internal inner class AttributeTest {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (195) AttributeTest")
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
            @DisplayName("XQuery 4.0 ED EBNF (109) AttributeTest ; XQuery 4.0 ED EBNF (55) NameTest")
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
            @DisplayName("XQuery 3.1 EBNF (203) AttributeName")
            internal inner class AttributeName {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathNCName>("() instance of attribute(test)")[0] as XsQNameValue
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Attribute))

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
        @DisplayName("XQuery 4.0 ED (3.6.3.5) Schema Attribute Test ; XQuery 3.1 (2.5.5.6) Schema Attribute Test")
        internal inner class SchemaAttributeTest {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (197) SchemaAttributeTest")
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
            @DisplayName("XQuery 3.1 EBNF (198) AttributeDeclaration")
            internal inner class AttributeDeclaration {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathNCName>("() instance of schema-attribute(test)")[0] as XsQNameValue
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Attribute))

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
        @DisplayName("XQuery 4.0 ED (3.6.4.1) Element Test ; XQuery 3.1 (2.5.5.7) Function Test")
        internal inner class FunctionTest {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (207) FunctionTest")
            internal inner class FunctionTest {
                @Test
                @DisplayName("one annotation ; any function test")
                fun oneAnnotation() {
                    val test = parse<XPathAnyFunctionTest>("() instance of % test function ( * )")[0]

                    val annotations = test.annotations.toList()
                    assertThat(annotations.size, `is`(1))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("test"))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("%test function(*)"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("multiple annotations ; typed function test")
                fun multipleAnnotations() {
                    val test = parse<XPathTypedFunctionTest>(
                        "() instance of % one % two function ( xs:long ) as xs:long"
                    )[0]

                    val annotations = test.annotations.toList()
                    assertThat(annotations.size, `is`(2))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("one"))
                    assertThat(qname_presentation(annotations[1].name!!), `is`("two"))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("%one %two function(xs:long) as xs:long"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }

                @Test
                @DisplayName("annotation with missing name")
                fun annotationWithoutName() {
                    val test = parse<XPathTypedFunctionTest>("() instance of % % two function ( xs:long ) as xs:long")[0]

                    val annotations = test.annotations.toList()
                    assertThat(annotations.size, `is`(2))
                    assertThat(annotations[0].name, `is`(nullValue()))
                    assertThat(qname_presentation(annotations[1].name!!), `is`("two"))

                    val type = test as XdmItemType
                    assertThat(type.typeName, `is`("%two function(xs:long) as xs:long"))
                    assertThat(type.typeClass, `is`(sameInstance(XdmFunction::class.java)))

                    assertThat(type.itemType, `is`(sameInstance(type)))
                    assertThat(type.lowerBound, `is`(1))
                    assertThat(type.upperBound, `is`(1))
                }
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (208) AnyFunctionTest")
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
            @DisplayName("XQuery 3.1 EBNF (209) TypedFunctionTest")
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
        @DisplayName("XQuery 4.0 ED (3.6.4.2) Map Test ; XQuery 3.1 (2.5.5.8) Map Test")
        internal inner class MapTest {
            @Test
            @DisplayName("XQuery 3.1 EBNF (211) AnyMapTest")
            fun anyMapTest() {
                val type = parse<XPathAnyMapTest>("() instance of map ( * )")[0] as XdmItemType
                assertThat(type.typeName, `is`("map(*)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (212) TypedMapTest")
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
            @DisplayName("XQuery 4.0 ED EBNF (227) TypedMapTest ; XQuery 4.0 ED EBNF (233) LocalUnionType")
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
        @DisplayName("XQuery 4.0 ED (3.6.4.3) Record Test")
        internal inner class RecordTest {
            @Nested
            @DisplayName("XQuery 4.0 ED EBNF (230) RecordTest")
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
            @DisplayName("XQuery 4.0 ED EBNF (231) FieldDeclaration")
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
            @DisplayName("XQuery 4.0 ED EBNF (231) FieldDeclaration ; XQuery 4.0 ED EBNF (202) SequenceType")
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
            @DisplayName("XQuery 4.0 ED EBNF (231) FieldDeclaration ; XQuery 4.0 ED EBNF (233) SelfReference")
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
        @DisplayName("XQuery 4.0 ED (3.6.4.4) Element Test ; XQuery 3.1 (2.5.5.9) Array Test")
        internal inner class ArrayTest {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (214) AnyArrayTest")
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
            @DisplayName("XQuery 3.1 EBNF (215) TypedArrayTest")
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
    @DisplayName("XQuery 4.0 ED (4) Expressions ; XQuery 3.1 (3) Expressions")
    internal inner class Expressions {
        @Nested
        @DisplayName("XQuery 4.0 ED (4.1) Setting Namespace Context")
        internal inner class SettingNamespaceContext {
            @Nested
            @DisplayName("XQuery 4.0 ED EBNF (43) WithExpr")
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
            @DisplayName("XQuery 4.0 ED EBNF (44) NamespaceDeclaration")
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
        @DisplayName("XQuery 4.0 ED (4.3) Primary Expressions ; XQuery 3.1 (3.1) Primary Expressions")
        internal inner class PrimaryExpressions {
            @Nested
            @DisplayName("XQuery 3.1 (3.1.1) Literals")
            internal inner class Literals {
                @Test
                @DisplayName("XQuery 3.1 EBNF (219) IntegerLiteral")
                fun integerLiteral() {
                    val literal = parse<XPathIntegerLiteral>("123")[0] as XsIntegerValue
                    assertThat(literal.data, `is`(BigInteger.valueOf(123)))
                    assertThat(literal.toInt(), `is`(123))

                    val expr = literal as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (220) DecimalLiteral")
                fun decimalLiteral() {
                    val literal = parse<XPathDecimalLiteral>("12.34")[0] as XsDecimalValue
                    assertThat(literal.data, `is`(BigDecimal(BigInteger.valueOf(1234), 2)))

                    val expr = literal as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (221) DoubleLiteral")
                fun doubleLiteral() {
                    val literal = parse<XPathDoubleLiteral>("1e3")[0] as XsDoubleValue
                    assertThat(literal.data, `is`(1e3))

                    val expr = literal as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
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

                    @Test
                    @DisplayName("PredefinedEntityRef tokens")
                    fun predefinedEntityRef() {
                        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                        val literal = parse<XPathStringLiteral>(
                            "\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\""
                        )[0] as XsStringValue
                        assertThat(literal.data, `is`("<áā\uD835\uDD04≪\u0338"))
                        assertThat(literal.element, sameInstance(literal as PsiElement))

                        val expr = literal as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("CharRef tokens")
                    fun charRef() {
                        val literal = parse<XPathStringLiteral>("\"&#xA0;&#160;&#x20;&#x1D520;\"")[0] as XsStringValue
                        assertThat(literal.data, `is`("\u00A0\u00A0\u0020\uD835\uDD20"))
                        assertThat(literal.element, sameInstance(literal as PsiElement))

                        val expr = literal as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
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
            @DisplayName("XQuery 3.1 (3.1.3) Parenthesized Expressions")
            internal inner class ParenthesizedExpressions {
                @Test
                @DisplayName("XQuery 3.1 EBNF (133) ParenthesizedExpr ; XQuery IntelliJ Plugin XQuery EBNF (134) EmptyExpr")
                fun emptyExpr() {
                    val expr = parse<PluginEmptyExpr>("()")[0] as XpmConcatenatingExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                    assertThat(expr.expressions.count(), `is`(0))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 (3.1.4) Context Item Expression")
            internal inner class ContextItemExpression {
                @Test
                @DisplayName("XQuery 3.1 EBNF (134) ContextItemExpr")
                fun contextItemExpr() {
                    val expr = parse<XPathContextItemExpr>("() ! .")[0] as XpmContextItemExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.4) Functions ; XQuery 3.1 (3.1) Primary Expressions")
        internal inner class Functions {
            @Nested
            @DisplayName("XQuery 4.0 ED (4.4.1) Static Functions ; XQuery 3.1 (3.1.5) Static Function Calls")
            internal inner class StaticFunctions {
                @Nested
                @DisplayName("%variadic(\"no\"); empty parameters")
                internal inner class VariadicNo_EmptyParameters {
                    @Test
                    @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
                    fun functionDecl() {
                        val decl = parse<XpmFunctionDeclaration>("declare function fn:true() external;")[0]
                        assertThat(decl.variadicType, `is`(XpmVariadic.No))
                        assertThat(decl.declaredArity, `is`(0))
                        assertThat(decl.requiredArity, `is`(0))
                    }

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
                    @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
                    fun functionDecl() {
                        val decl = parse<XpmFunctionDeclaration>("declare function test(\$one, \$two) external;")[0]
                        assertThat(decl.variadicType, `is`(XpmVariadic.No))
                        assertThat(decl.declaredArity, `is`(2))
                        assertThat(decl.requiredArity, `is`(2))
                    }

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
                    @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
                    fun functionDecl() {
                        val decl = parse<XpmFunctionDeclaration>("declare function test(\$one, \$two ...) external;")[0]
                        assertThat(decl.variadicType, `is`(XpmVariadic.Ellipsis))
                        assertThat(decl.declaredArity, `is`(2))
                        assertThat(decl.requiredArity, `is`(1))
                    }

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
            @DisplayName("XQuery 4.0 ED (4.4.1.1) Static Function Call Syntax ; XQuery 3.1 (3.1.5) Static Function Calls")
            internal inner class StaticFunctionCallSyntax {
                @Nested
                @DisplayName("XQuery 4.0 ED EBNF (137) KeywordArgument")
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
                @DisplayName("XQuery 3.1 EBNF (137) FunctionCall")
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
                        assertThat(f.positionalArguments[0].text, `is`("1"))

                        assertThat(f.keywordArguments.size, `is`(0))

                        val expr = f as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XPathElementType.FUNCTION_CALL))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
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
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultFunctionRef))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.FunctionRef))

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
                @DisplayName("XQuery 3.1 EBNF (139) ArgumentPlaceholder")
                fun argumentPlaceholder() {
                    val arg = parse<XPathArgumentPlaceholder>("math:sin(?)")[0] as XpmExpression
                    assertThat(arg.expressionElement?.elementType, `is`(XPathTokenType.OPTIONAL))
                }
            }

            @Nested
            @DisplayName("XQuery 4.0 ED (4.4.1.2) Evaluating Static Function Calls ; XQuery 3.1 (3.1.5.1) Evaluating Static and Dynamic Function Calls")
            internal inner class EvaluatingStaticFunctionCalls {
                @Nested
                @DisplayName("For non-variadic functions")
                internal inner class ForNonVariadicFunctions {
                    @Test
                    @DisplayName("positional arguments")
                    fun positionalArguments() {
                        val f = parse<XPathFunctionCall>("math:pow(2, 8)")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("math:pow"))
                        assertThat(bindings.size, `is`(2))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("x"))
                        assertThat(arg.variableType?.typeName, `is`("xs:double?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[0]))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("y"))
                        assertThat(arg.variableType?.typeName, `is`("xs:numeric"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[1]))
                    }

                    @Test
                    @DisplayName("positional and keyword arguments")
                    fun positionalAndKeywordArguments() {
                        val f = parse<XPathFunctionCall>("math:pow(2, y: 8)")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("math:pow"))
                        assertThat(bindings.size, `is`(2))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("x"))
                        assertThat(arg.variableType?.typeName, `is`("xs:double?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[0]))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("y"))
                        assertThat(arg.variableType?.typeName, `is`("xs:numeric"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[0].valueExpression))
                    }

                    @Test
                    @DisplayName("keyword arguments")
                    fun keywordArguments() {
                        val f = parse<XPathFunctionCall>("math:pow(y: 2, x: 8)")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("math:pow"))
                        assertThat(bindings.size, `is`(2))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("x"))
                        assertThat(arg.variableType?.typeName, `is`("xs:double?"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[1].valueExpression))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("y"))
                        assertThat(arg.variableType?.typeName, `is`("xs:numeric"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[0].valueExpression))
                    }

                    @Test
                    @DisplayName("keyword argument not matching any parameter names")
                    fun keywordArgumentNotMatchingAnyParameterNames() {
                        val f = parse<XPathFunctionCall>("math:pow(y: 2, z: 8)")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("math:pow"))
                        assertThat(bindings.size, `is`(2))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("x"))
                        assertThat(arg.variableType?.typeName, `is`("xs:double?"))
                        assertThat(arg.variableExpression, sameInstance(XpmMissingArgument))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("y"))
                        assertThat(arg.variableType?.typeName, `is`("xs:numeric"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[0].valueExpression))
                    }

                    @Test
                    @DisplayName("empty arguments")
                    fun emptyArguments() {
                        val f = parse<XPathFunctionCall>("fn:true()")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:true"))
                        assertThat(bindings.size, `is`(0))
                    }
                }

                @Nested
                @DisplayName("For non-variadic arrow functions")
                internal inner class ForNonVariadicArrowFunctions {
                    @Test
                    @DisplayName("positional arguments")
                    fun positionalArguments() {
                        val f = parse<PluginArrowFunctionCall>("\$x => format-date(1, 2, 3,  4)")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:format-date"))
                        assertThat(bindings.size, `is`(5))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value"))
                        assertThat(arg.variableType?.typeName, `is`("xs:date?"))
                        assertThat(arg.variableExpression, sameInstance(f.sourceExpression))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("picture"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[0]))

                        arg = bindings[2]
                        assertThat(qname_presentation(arg.variableName!!), `is`("language"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[1]))

                        arg = bindings[3]
                        assertThat(qname_presentation(arg.variableName!!), `is`("calendar"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[2]))

                        arg = bindings[4]
                        assertThat(qname_presentation(arg.variableName!!), `is`("place"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[3]))
                    }

                    @Test
                    @DisplayName("positional and keyword arguments")
                    fun positionalAndKeywordArguments() {
                        val f = parse<PluginArrowFunctionCall>(
                            "\$x => format-date(1, 2, place: 3,  calendar: 4)"
                        )[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:format-date"))
                        assertThat(bindings.size, `is`(5))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value"))
                        assertThat(arg.variableType?.typeName, `is`("xs:date?"))
                        assertThat(arg.variableExpression, sameInstance(f.sourceExpression))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("picture"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[0]))

                        arg = bindings[2]
                        assertThat(qname_presentation(arg.variableName!!), `is`("language"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[1]))

                        arg = bindings[3]
                        assertThat(qname_presentation(arg.variableName!!), `is`("calendar"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[1].valueExpression))

                        arg = bindings[4]
                        assertThat(qname_presentation(arg.variableName!!), `is`("place"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[0].valueExpression))
                    }

                    @Test
                    @DisplayName("keyword arguments")
                    fun keywordArguments() {
                        val f = parse<PluginArrowFunctionCall>(
                            "\$x => format-date(calendar: 1, language: 2, picture: 3,  place: 4)"
                        )[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:format-date"))
                        assertThat(bindings.size, `is`(5))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value"))
                        assertThat(arg.variableType?.typeName, `is`("xs:date?"))
                        assertThat(arg.variableExpression, sameInstance(f.sourceExpression))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("picture"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[2].valueExpression))

                        arg = bindings[2]
                        assertThat(qname_presentation(arg.variableName!!), `is`("language"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[1].valueExpression))

                        arg = bindings[3]
                        assertThat(qname_presentation(arg.variableName!!), `is`("calendar"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[0].valueExpression))

                        arg = bindings[4]
                        assertThat(qname_presentation(arg.variableName!!), `is`("place"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[3].valueExpression))
                    }

                    @Test
                    @DisplayName("keyword argument not matching any parameter names")
                    fun keywordArgumentNotMatchingAnyParameterNames() {
                        val f = parse<PluginArrowFunctionCall>(
                            "\$x => format-date(calender: 1, language: 2, picture: 3,  place: 4)"
                        )[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:format-date"))
                        assertThat(bindings.size, `is`(5))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value"))
                        assertThat(arg.variableType?.typeName, `is`("xs:date?"))
                        assertThat(arg.variableExpression, sameInstance(f.sourceExpression))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("picture"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[2].valueExpression))

                        arg = bindings[2]
                        assertThat(qname_presentation(arg.variableName!!), `is`("language"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[1].valueExpression))

                        arg = bindings[3]
                        assertThat(qname_presentation(arg.variableName!!), `is`("calendar"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(XpmMissingArgument))

                        arg = bindings[4]
                        assertThat(qname_presentation(arg.variableName!!), `is`("place"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.keywordArguments[3].valueExpression))
                    }

                    @Test
                    @DisplayName("empty arguments")
                    fun emptyArguments() {
                        val f = parse<PluginArrowFunctionCall>("\$x => upper-case()")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:upper-case"))
                        assertThat(bindings.size, `is`(1))

                        val arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.sourceExpression))
                    }

                    @Test
                    @DisplayName("chained arrows")
                    fun chainedArrows() {
                        val f = parse<PluginArrowFunctionCall>("\$x => upper-case() => string-to-codepoints()")[1]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:string-to-codepoints"))
                        assertThat(bindings.size, `is`(1))

                        val arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value"))
                        assertThat(arg.variableType?.typeName, `is`("xs:string?"))
                        assertThat(arg.variableExpression, sameInstance(f.sourceExpression))
                    }
                }

                @Nested
                @DisplayName("For ellipsis-variadic functions")
                internal inner class ForEllipsisVariadicFunctions {
                    @Test
                    @DisplayName("no arguments specified for the variadic parameter")
                    fun empty() {
                        val f = parse<XPathFunctionCall>("fn:concat(2, 4)")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:concat"))
                        assertThat(bindings.size, `is`(3))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value1"))
                        assertThat(arg.variableType?.typeName, `is`("xs:anyAtomicType?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[0]))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value2"))
                        assertThat(arg.variableType?.typeName, `is`("xs:anyAtomicType?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[1]))

                        arg = bindings[2]
                        assertThat(qname_presentation(arg.variableName!!), `is`("values"))
                        assertThat(arg.variableType?.typeName, `is`("xs:anyAtomicType?"))
                        assertThat(arg.variableExpression, sameInstance(XpmEmptyExpression))
                    }

                    @Test
                    @DisplayName("single argument specified for the variadic parameter")
                    fun single() {
                        val f = parse<XPathFunctionCall>("fn:concat(2, 4, 6)")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:concat"))
                        assertThat(bindings.size, `is`(3))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value1"))
                        assertThat(arg.variableType?.typeName, `is`("xs:anyAtomicType?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[0]))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value2"))
                        assertThat(arg.variableType?.typeName, `is`("xs:anyAtomicType?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[1]))

                        arg = bindings[2]
                        assertThat(qname_presentation(arg.variableName!!), `is`("values"))
                        assertThat(arg.variableType?.typeName, `is`("xs:anyAtomicType?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[2]))
                    }

                    @Test
                    @DisplayName("multiple arguments specified for the variadic parameter")
                    fun multiple() {
                        val f = parse<XPathFunctionCall>("fn:concat(2, 4, 6, 8)")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:concat"))
                        assertThat(bindings.size, `is`(3))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value1"))
                        assertThat(arg.variableType?.typeName, `is`("xs:anyAtomicType?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[0]))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value2"))
                        assertThat(arg.variableType?.typeName, `is`("xs:anyAtomicType?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[1]))

                        arg = bindings[2]
                        assertThat(qname_presentation(arg.variableName!!), `is`("values"))
                        assertThat(arg.variableType?.typeName, `is`("xs:anyAtomicType?"))

                        val rest = (arg.variableExpression as XpmConcatenatingExpression).expressions.toList()
                        assertThat(rest.size, `is`(2))
                        assertThat(rest[0], sameInstance(f.positionalArguments[2]))
                        assertThat(rest[1], sameInstance(f.positionalArguments[3]))
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 4.0 ED (4.4.2.1) Evaluating Dynamic Function Calls ; XQuery 3.1 (3.1.5.1) Evaluating Static and Dynamic Function Calls")
            internal inner class EvaluatingDynamicFunctionCalls {
                @Nested
                @DisplayName("XQuery IntelliJ Plugin EBNF (129) DynamicFunctionCall")
                internal inner class DynamicFunctionCall {
                    @Test
                    @DisplayName("positional arguments")
                    fun positionalArguments() {
                        val f = parse<PluginDynamicFunctionCall>("math:pow#2(2, 8)")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("math:pow"))
                        assertThat(bindings.size, `is`(2))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("x"))
                        assertThat(arg.variableType?.typeName, `is`("xs:double?"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[0]))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("y"))
                        assertThat(arg.variableType?.typeName, `is`("xs:numeric"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[1]))
                    }

                    @Test
                    @DisplayName("empty arguments")
                    fun emptyArguments() {
                        val f = parse<PluginDynamicFunctionCall>("fn:true#0()")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:true"))
                        assertThat(bindings.size, `is`(0))
                    }
                }

                @Nested
                @DisplayName("XQuery IntelliJ Plugin EBNF (119) ArrowDynamicFunctionCall")
                internal inner class ArrowDynamicFunctionCall {
                    @Test
                    @DisplayName("positional arguments")
                    fun positionalArguments() {
                        val f = parse<PluginArrowDynamicFunctionCall>("2 => (math:pow#2)(8)")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("math:pow"))
                        assertThat(bindings.size, `is`(2))

                        var arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("x"))
                        assertThat(arg.variableType?.typeName, `is`("xs:double?"))
                        assertThat(arg.variableExpression, sameInstance(f.sourceExpression))

                        arg = bindings[1]
                        assertThat(qname_presentation(arg.variableName!!), `is`("y"))
                        assertThat(arg.variableType?.typeName, `is`("xs:numeric"))
                        assertThat(arg.variableExpression, sameInstance(f.positionalArguments[0]))
                    }

                    @Test
                    @DisplayName("empty arguments")
                    fun emptyArguments() {
                        val f = parse<PluginArrowDynamicFunctionCall>("2 => (fn:abs#1)()")[0]
                        val (decl, bindings) = f.resolve!!

                        assertThat(qname_presentation(decl.functionName!!), `is`("fn:abs"))
                        assertThat(bindings.size, `is`(1))

                        val arg = bindings[0]
                        assertThat(qname_presentation(arg.variableName!!), `is`("value"))
                        assertThat(arg.variableType?.typeName, `is`("xs:numeric?"))
                        assertThat(arg.variableExpression, sameInstance(f.sourceExpression))
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 4.0 ED (4.4.2.3) Named Function References ; XQuery 3.1 (3.1.6) Named Function References")
            internal inner class NamedFunctionReferences {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
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
                        val qname = parse<XPathEQName>(
                            """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    test#1
                    """
                        )[0] as XsQNameValue
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultFunctionRef))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.FunctionRef))

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
            @DisplayName("XQuery 4.0 ED (4.4.2.3) Inline Function Expressions ; XQuery 3.1 (3.1.7) Inline Function Expressions")
            internal inner class InlineFunctionExpressions {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr")
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
        @DisplayName("XQuery 4.0 ED (4.5) Postfix Expressions ; XQuery 3.1 (3.2) Postfix Expressions")
        internal inner class PostfixExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (121) PostfixExpr")
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
            @DisplayName("XQuery 3.1 (3.2.1) Filter Expressions")
            internal inner class FilterExpressions {
                @Nested
                @DisplayName("XQuery IntelliJ Plugin EBNF (128) FilterExpr")
                internal inner class FilterExpr {
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
                @DisplayName("XQuery 3.1 EBNF (168) NamedFunctionRef")
                fun namedFunctionRef() {
                    val f = parse<PluginDynamicFunctionCall>("fn:abs#1(1)")[0]
                    assertThat(f.positionalArguments.size, `is`(1))
                    assertThat(f.positionalArguments[0].text, `is`("1"))

                    val ref = f.functionReference
                    assertThat(qname_presentation(ref?.functionName!!), `is`("fn:abs"))
                    assertThat(ref.positionalArity, `is`(1))
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (133) ParenthesizedExpr ; XQuery 3.1 EBNF (168) NamedFunctionRef")
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
        @DisplayName("XQuery 4.0 ED (4.6) Path Expressions ; XQuery 3.1 (3.3) Path Expressions")
        internal inner class PathExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (108) PathExpr")
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
                @DisplayName("XQuery IntelliJ Plugin EBNF (126) AbbrevDescendantOrSelfStep")
                fun abbrevDescendantOrSelfStep() {
                    val step = parse<PluginAbbrevDescendantOrSelfStep>("//test")[0] as XpmPathStep
                    assertThat(step.axisType, `is`(XpmAxisType.DescendantOrSelf))
                    assertThat(step.nodeName, `is`(nullValue()))
                    assertThat(step.nodeType, sameInstance(XdmNodeItem))
                    assertThat(step.predicateExpression, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 (3.3.2) Steps")
            internal inner class Steps {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (39) AxisStep")
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
                    @DisplayName("XQuery 3.1 EBNF (117) AbbrevReverseStep")
                    fun abbrevReverseStep() {
                        val step = parse<XPathAxisStep>("..[1]")[0] as XpmPathStep
                        assertThat(step.axisType, `is`(XpmAxisType.Parent))
                        assertThat(step.nodeName, `is`(nullValue()))
                        assertThat(step.nodeType, sameInstance(XdmNodeItem))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.ONE))
                    }

                    @Test
                    @DisplayName("XQuery 3.1 EBNF (119) NameTest")
                    fun nameTest() {
                        val step = parse<XPathAxisStep>("child::test[1]")[0] as XpmPathStep
                        val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Child))
                        assertThat(step.nodeName, sameInstance(qname))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                        assertThat((step.predicateExpression as? XsIntegerValue)?.data, `is`(BigInteger.ONE))
                    }

                    @Test
                    @DisplayName("XQuery 3.1 EBNF (188) KindTest")
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
            @DisplayName("XQuery 3.1 (3.3.2.1) Axes")
            internal inner class Axes {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (113) ForwardAxis")
                internal inner class ForwardAxis {
                    @Test
                    @DisplayName("XQuery 3.1 EBNF (119) NameTest")
                    fun nameTest() {
                        val step = parse<XPathForwardStep>("child::test")[0] as XpmPathStep
                        val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Child))
                        assertThat(step.nodeName, sameInstance(qname))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                        assertThat(step.predicateExpression, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("XQuery 3.1 EBNF (188) KindTest")
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
                @DisplayName("XQuery 3.1 EBNF (116) ReverseAxis")
                internal inner class ReverseAxis {
                    @Test
                    @DisplayName("XQuery 3.1 EBNF (119) NameTest")
                    fun nameTest() {
                        val step = parse<XPathReverseStep>("parent::test")[0] as XpmPathStep
                        val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Parent))
                        assertThat(step.nodeName, sameInstance(qname))
                        assertThat(step.nodeType, sameInstance(XdmElementItem))
                        assertThat(step.predicateExpression, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("XQuery 3.1 EBNF (188) KindTest")
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
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultElement))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Element))

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
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Attribute))

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
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Namespace))

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
            @DisplayName("XQuery 3.1 (3.3.3) Predicates within Steps")
            internal inner class PredicatesWithinSteps {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (124) Predicate")
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
            @DisplayName("XQuery 3.1 (3.3.5) Abbreviated Syntax")
            internal inner class AbbreviatedSyntax {
                @Nested
                @DisplayName("1. The attribute axis attribute:: can be abbreviated by @.")
                internal inner class AbbreviatedAttributeAxis {
                    @Test
                    @DisplayName("XQuery 3.1 EBNF (119) NameTest")
                    fun nameTest() {
                        val step = parse<XPathAbbrevForwardStep>("@test")[0] as XpmPathStep
                        val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                        assertThat(step.axisType, `is`(XpmAxisType.Attribute))
                        assertThat(step.nodeName, sameInstance(qname))
                        assertThat(step.nodeType, sameInstance(XdmAttributeItem))
                        assertThat(step.predicateExpression, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("XQuery 3.1 EBNF (188) KindTest ; XQuery 3.1 EBNF (189) AnyKindTest")
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
                        @DisplayName("XQuery 3.1 EBNF (119) NameTest")
                        fun nameTest() {
                            val step = parse<XPathNameTest>("test")[0] as XpmPathStep
                            val qname = (step as PsiElement).walkTree().filterIsInstance<XsQNameValue>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, sameInstance(qname))
                            assertThat(step.nodeType, sameInstance(XdmElementItem))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XQuery 3.1 EBNF (188) KindTest ; XQuery 3.1 EBNF (189) AnyKindTest")
                        fun anyKindTest() {
                            val step = parse<XPathNodeTest>("node()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XQuery 3.1 EBNF (188) KindTest ; XQuery 3.1 EBNF (190) DocumentTest")
                        fun documentTest() {
                            val step = parse<XPathNodeTest>("document-node()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XQuery 3.1 EBNF (188) KindTest ; XQuery 3.1 EBNF (191) TextTest")
                        fun textTest() {
                            val step = parse<XPathNodeTest>("text()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XQuery 3.1 EBNF (188) KindTest ; XQuery 3.1 EBNF (192) CommentTest")
                        fun commentTest() {
                            val step = parse<XPathNodeTest>("comment()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XQuery 3.1 EBNF (188) KindTest ; XQuery 3.1 EBNF (194) PITest")
                        fun piTest() {
                            val step = parse<XPathNodeTest>("processing-instruction()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XQuery 3.1 EBNF (188) KindTest ; XQuery 3.1 EBNF (199) ElementTest")
                        fun elementTest() {
                            val step = parse<XPathNodeTest>("element()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Child))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XQuery 3.1 EBNF (188) KindTest ; XQuery 3.1 EBNF (201) SchemaElementTest")
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
                        @DisplayName("XQuery 3.1 EBNF (188) KindTest ; XQuery 3.1 EBNF (195) AttributeTest")
                        fun attributeTest() {
                            val step = parse<XPathNodeTest>("attribute()")[0] as XpmPathStep
                            val itemType = (step as PsiElement).walkTree().filterIsInstance<XdmItemType>().first()
                            assertThat(step.axisType, `is`(XpmAxisType.Attribute))
                            assertThat(step.nodeName, `is`(nullValue()))
                            assertThat(step.nodeType, sameInstance(itemType))
                            assertThat(step.predicateExpression, `is`(nullValue()))
                        }

                        @Test
                        @DisplayName("XQuery 3.1 EBNF (188) KindTest ; XQuery 3.1 EBNF (197) SchemaAttributeTest")
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
                @DisplayName("XQuery 3.1 EBNF (114) AbbrevForwardStep")
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
        @DisplayName("XQuery 4.0 ED (4.7.1) Sequence Concatenation ; XQuery 3.1 (3.4.1) Constructing Sequences")
        internal inner class SequenceConcatenation {
            @Test
            @DisplayName("XQuery 3.1 EBNF (39) Expr")
            fun expr() {
                val expr = parse<XPathExpr>("(1, 2 + 3, 4)")[0] as XpmConcatenatingExpression
                assertThat(expr.expressionElement, `is`(nullValue()))

                val exprs = expr.expressions.toList()
                assertThat(exprs.size, `is`(3))
                assertThat(exprs[0].text, `is`("1"))
                assertThat(exprs[1].text, `is`("2 + 3"))
                assertThat(exprs[2].text, `is`("4"))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.7.2) Range Expressions ; XQuery 3.1 (3.4.1) Constructing Sequences")
        internal inner class RangeExpressions {
            @Test
            @DisplayName("XQuery 3.1 EBNF (20) RangeExpr")
            fun rangeExpr() {
                val expr = parse<XPathRangeExpr>("1 to 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_TO))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.7.3) Combining Node Sequences ; XQuery 3.1 (3.4.2) Combining Node Sequences")
        internal inner class CombiningNodeSequences {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (90) UnionExpr")
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
            @DisplayName("XQuery 3.1 EBNF (91) IntersectExceptExpr")
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
        @DisplayName("XQuery 4.0 ED (4.8) Arithmetic Expressions ; XQuery 3.1 (3.5) Arithmetic Expressions")
        internal inner class ArithmeticExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (88) AdditiveExpr")
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
            @DisplayName("XQuery 3.1 EBNF (89) MultiplicativeExpr")
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
            @DisplayName("XQuery 3.1 EBNF (97) UnaryExpr")
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
        @DisplayName("XQuery 4.0 ED (4.9) String Concatenation Expressions ; XQuery 3.1 (3.6) String Concatenation Expressions")
        internal inner class StringConcatenationExpressions {
            @Test
            @DisplayName("XPath 3.1 EBNF (86) StringConcatExpr")
            fun stringConcatExpr() {
                val expr = parse<XPathStringConcatExpr>("1 || 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.CONCATENATION))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.10) Comparison Expressions ; XQuery 3.1 (3.7) Comparison Expressions")
        internal inner class ComparisonExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (85) ComparisonExpr ; XQuery 3.1 EBNF (99) GeneralComp")
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
            @DisplayName("XQuery 3.1 EBNF (85) ComparisonExpr ; XQuery 3.1 EBNF (100) ValueComp")
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
            @DisplayName("XQuery 3.1 EBNF (85) ComparisonExpr ; XQuery 3.1 EBNF (101) NodeComp")
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
        @DisplayName("XQuery 4.0 ED (4.11) Logical Expressions ; XQuery 3.1 (3.8) Logical Expressions")
        internal inner class LogicalExpressions {
            @Test
            @DisplayName("XQuery 1.0 EBNF (83) OrExpr")
            fun orExpr() {
                val expr = parse<XPathOrExpr>("1 or 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_OR))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (83) AndExpr")
            fun andExpr() {
                val expr = parse<XPathAndExpr>("1 and 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_AND))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.12) Node Constructors ; XQuery 3.1 (3.9) Node Constructors")
        internal inner class NodeConstructors {
            @Nested
            @DisplayName("XQuery 4.0 ED (4.12.1) Direct Element Constructors ; XQuery 3.1 (3.9.1) Direct Element Constructors")
            internal inner class DirectElementConstructors {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
                internal inner class DirElemConstructor {
                    @Test
                    @DisplayName("open and close tags")
                    fun openAndCloseTags() {
                        val element = parse<XQueryDirElemConstructor>("<a:b></a:b>")[0]

                        val open = element.nodeName!!
                        assertThat(open.prefix!!.data, `is`("a"))
                        assertThat(open.localName!!.data, `is`("b"))

                        val close = element.closingTag!!
                        assertThat(close, `is`(not(sameInstance(open))))
                        assertThat(close.prefix!!.data, `is`("a"))
                        assertThat(close.localName!!.data, `is`("b"))

                        val expr = element as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("self-closing")
                    fun selfClosing() {
                        val element = parse<XQueryDirElemConstructor>("<h:br/>")[0]

                        val open = element.nodeName!!
                        assertThat(open.prefix!!.data, `is`("h"))
                        assertThat(open.localName!!.data, `is`("br"))

                        val close = element.closingTag
                        assertThat(close, `is`(sameInstance(open)))

                        val expr = element as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("error recovery: missing close tag")
                    fun missingClosingTag() {
                        val element = parse<XQueryDirElemConstructor>("<a:b>")[0]

                        val open = element.nodeName!!
                        assertThat(open.prefix!!.data, `is`("a"))
                        assertThat(open.localName!!.data, `is`("b"))

                        val close = element.closingTag
                        assertThat(close, `is`(sameInstance(open)))

                        val expr = element as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("error recovery: incomplete open tag")
                    fun incompleteOpenTag() {
                        val element = parse<XQueryDirElemConstructor>("<a:></a:b>")[0]

                        val open = element.nodeName!!
                        assertThat(open.prefix!!.data, `is`("a"))
                        assertThat(open.localName, `is`(nullValue()))

                        val close = element.closingTag!!
                        assertThat(close, `is`(not(sameInstance(open))))
                        assertThat(close.prefix!!.data, `is`("a"))
                        assertThat(close.localName!!.data, `is`("b"))

                        val expr = element as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("error recovery: incomplete close tag")
                    fun incompleteCloseTag() {
                        val element = parse<XQueryDirElemConstructor>("<a:b></a:>")[0]

                        val open = element.nodeName!!
                        assertThat(open.prefix!!.data, `is`("a"))
                        assertThat(open.localName!!.data, `is`("b"))

                        val close = element.closingTag!!
                        assertThat(close, `is`(not(sameInstance(open))))
                        assertThat(close.prefix!!.data, `is`("a"))
                        assertThat(close.localName, `is`(nullValue()))

                        val expr = element as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("error recovery: partial close tag only")
                    fun partialCloseTagOnly() {
                        val element = parse<XQueryDirElemConstructor>("</<test>")[0]

                        val open = element.nodeName
                        assertThat(open, `is`(nullValue()))

                        val close = element.closingTag
                        assertThat(close, `is`(nullValue()))

                        val expr = element as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("error recovery: close tag only")
                    fun soloCloseTag() {
                        val element = parse<XQueryDirElemConstructor>("</a:b>")[0]

                        val open = element.nodeName!!
                        assertThat(open.prefix!!.data, `is`("a"))
                        assertThat(open.localName!!.data, `is`("b"))

                        val close = element.closingTag!!
                        assertThat(close, `is`(sameInstance(open)))

                        val expr = element as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.DIR_ELEM_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
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
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultElement))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Element))

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
                    @DisplayName("NCName namespace resolution with xmlns")
                    fun ncnameWithXmlns() {
                        val qname = parse<XPathEQName>(
                            """
                            declare default function namespace "http://www.example.co.uk/function";
                            declare default element namespace "http://www.example.co.uk/element/2";
                            <test xmlns="http://www.example.co.uk/element"/>
                            """
                        )[0] as XsQNameValue
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultElement))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Element))

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
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Attribute))

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
                    @DisplayName("QName")
                    fun qname() {
                        val qname = parse<XPathQName>("<elem fn:test=\"\"/>")[0] as XsQNameValue
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Attribute))

                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("fn"))
                        assertThat(qname.localName!!.data, `is`("test"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))

                        val expanded = qname.expand().toList()
                        assertThat(expanded.size, `is`(1))

                        assertThat(expanded[0].isLexicalQName, `is`(false))
                        assertThat(expanded[0].namespace!!.data, `is`("http://www.w3.org/2005/xpath-functions"))
                        assertThat(expanded[0].prefix!!.data, `is`("fn"))
                        assertThat(expanded[0].localName!!.data, `is`("test"))
                        assertThat(expanded[0].element, sameInstance(qname as PsiElement))
                    }

                    @Test
                    @DisplayName("namespace declaration attributes")
                    fun namespaceDeclarationAttributes() {
                        val qname = parse<XPathQName>("<elem xmlns:abc=\"http://www.example.com\"/>")[0] as XsQNameValue
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Namespace))

                        assertThat(qname.isLexicalQName, `is`(true))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("xmlns"))
                        assertThat(qname.localName!!.data, `is`("abc"))
                        assertThat(qname.element, sameInstance(qname as PsiElement))

                        val expanded = qname.expand().toList()
                        assertThat(expanded.size, `is`(0))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList ; XQuery IntelliJ Plugin EBNF (154) DirNamespaceAttribute")
                internal inner class DirNamespaceAttribute {
                    @Test
                    @DisplayName("namespace prefix")
                    fun namespacePrefix() {
                        val expr = parse<PluginDirNamespaceAttribute>(
                            "<a xmlns:b='http://www.example.com'/>"
                        )[0] as XpmNamespaceDeclaration

                        assertThat(expr.namespacePrefix!!.data, `is`("b"))
                        assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                        assertThat(expr.namespaceUri!!.context, `is`(XdmUriContext.NamespaceDeclaration))
                        assertThat(expr.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

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
                        val expr = parse<PluginDirNamespaceAttribute>(
                            "<a xmlns='http://www.example.com'/>"
                        )[0] as XpmNamespaceDeclaration

                        assertThat(expr.namespacePrefix?.data, `is`(""))
                        assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
                        assertThat(expr.namespaceUri!!.context, `is`(XdmUriContext.NamespaceDeclaration))
                        assertThat(expr.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                        assertThat(expr.accepts(XdmNamespaceType.DefaultElement), `is`(true))
                        assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                        assertThat(expr.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                        assertThat(expr.accepts(XdmNamespaceType.DefaultType), `is`(true))
                        assertThat(expr.accepts(XdmNamespaceType.None), `is`(false))
                        assertThat(expr.accepts(XdmNamespaceType.Prefixed), `is`(false))
                        assertThat(expr.accepts(XdmNamespaceType.Undefined), `is`(false))
                        assertThat(expr.accepts(XdmNamespaceType.XQuery), `is`(false))
                    }

                    @Nested
                    @DisplayName("resolve uri")
                    internal inner class ResolveUri {
                        @Test
                        @DisplayName("empty")
                        fun empty() {
                            val file = parseResource("tests/resolve-xquery/files/DirAttributeList_Empty.xq")
                            val psi = file.walkTree().filterIsInstance<PluginDirNamespaceAttribute>().toList()[0]

                            assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                        }

                        @Test
                        @DisplayName("same directory")
                        fun sameDirectory() {
                            val file = parseResource("tests/resolve-xquery/files/DirAttributeList_SameDirectory.xq")
                            val psi = file.walkTree().filterIsInstance<PluginDirNamespaceAttribute>().toList()[0]

                            assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                        }

                        @Test
                        @DisplayName("http:// file matching")
                        fun httpProtocol() {
                            val file = parseResource("tests/resolve-xquery/files/DirAttributeList_HttpProtocol.xq")
                            val psi = file.walkTree().filterIsInstance<PluginDirNamespaceAttribute>().toList()[0]

                            val prologs = (psi as XQueryPrologResolver).prolog.toList()
                            assertThat(prologs.size, `is`(1))

                            assertThat(
                                prologs[0].resourcePath(),
                                endsWith("/org/w3/www/2005/xpath-functions/array.xqy")
                            )
                        }

                        @Test
                        @DisplayName("http:// file missing")
                        fun httpProtocolMissing() {
                            val file = parseResource("tests/resolve-xquery/files/DirAttributeList_HttpProtocol_FileNotFound.xq")
                            val psi = file.walkTree().filterIsInstance<PluginDirNamespaceAttribute>().toList()[0]

                            assertThat((psi as XQueryPrologResolver).prolog.count(), `is`(0))
                        }
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 4.0 ED (4.12.2) Other Direct Constructors ; XQuery 3.1 (3.9.2) Other Direct Constructors")
            internal inner class OtherDirectConstructors {
                @Test
                @DisplayName("XQuery 3.1 EBNF (149) DirCommentConstructor")
                fun dirCommentConstructor() {
                    val expr = parse<XQueryDirCommentConstructor>("<!-- test -->")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.DIR_COMMENT_CONSTRUCTOR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (151) DirPIConstructor")
                fun dirPIConstructor() {
                    val expr = parse<XQueryDirPIConstructor>("<?test?>")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.DIR_PI_CONSTRUCTOR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }
            }

            @Nested
            @DisplayName("XQuery 4.0 ED (4.12.3) Computed Constructors ; XQuery 3.1 (3.9.3) Computed Constructors")
            internal inner class ComputedElementConstructors {
                @Test
                @DisplayName("XQuery 3.1 EBNF (156) CompDocConstructor")
                fun compDocConstructor() {
                    val expr = parse<XQueryCompDocConstructor>("document { <test/> }")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.COMP_DOC_CONSTRUCTOR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
                internal inner class CompElemConstructor {
                    @Test
                    @DisplayName("nodeName as an EQName")
                    fun nodeNameEQName() {
                        val element = parse<XQueryCompElemConstructor>("element a:b {}")[0]

                        val name = element.nodeName!!
                        assertThat(name.prefix!!.data, `is`("a"))
                        assertThat(name.localName!!.data, `is`("b"))

                        val expr = element as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("nodeName is an expression")
                    fun nodeNameExpr() {
                        val element = parse<XQueryCompElemConstructor>("element { \"a:\" || \"b\" } {}")[0]
                        assertThat(element.nodeName, `is`(nullValue()))

                        val expr = element as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.COMP_ELEM_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

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
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultElement))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Element))

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
                @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
                internal inner class CompAttrConstructor {
                    @Test
                    @DisplayName("expression")
                    fun expression() {
                        val expr = parse<XQueryCompAttrConstructor>("attribute a:b {}")[0] as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("NCName namespace resolution")
                    fun ncname() {
                        val qname = parse<XPathNCName>("attribute test {}")[0] as XsQNameValue
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Attribute))

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
                @DisplayName("XQuery 3.1 EBNF (160) CompNamespaceConstructor")
                internal inner class CompNamespaceConstructor {
                    @Test
                    @DisplayName("nodeName as an EQName")
                    fun nodeNameEQName() {
                        val ns = parse<XQueryCompNamespaceConstructor>(
                            "namespace test { \"http://www.example.co.uk\" }"
                        )[0]

                        val expr = ns as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("nodeName as an expression")
                    fun nodeNameExpr() {
                        val ns = parse<XQueryCompNamespaceConstructor>(
                            "namespace { \"test\" } { \"http://www.example.co.uk\" }"
                        )[0]

                        val expr = ns as XpmExpression
                        assertThat(expr.expressionElement, `is`(nullValue()))
                    }
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (164) CompTextConstructor")
                fun compTextConstructor() {
                    val expr = parse<XQueryCompTextConstructor>("text { 1 }")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.COMP_TEXT_CONSTRUCTOR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (165) CompCommentConstructor")
                fun compCommentConstructor() {
                    val expr = parse<XQueryCompCommentConstructor>("comment { 1 }")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.COMP_COMMENT_CONSTRUCTOR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (166) CompPIConstructor")
                internal inner class CompPIConstructor {
                    @Test
                    @DisplayName("nodeName as an EQName")
                    fun nodeNameEQName() {
                        val ns = parse<XQueryCompPIConstructor>(
                            "processing-instruction test {}"
                        )[0]

                        val expr = ns as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }

                    @Test
                    @DisplayName("nodeName as an expression")
                    fun nodeNameExpr() {
                        val ns = parse<XQueryCompPIConstructor>(
                            "processing-instruction { \"test\" } {}"
                        )[0]

                        val expr = ns as XpmExpression
                        assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.COMP_PI_CONSTRUCTOR))
                        assertThat(expr.expressionElement?.textOffset, `is`(0))
                    }
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.13) String Constructors ; XQuery 3.1 (3.10) String Constructors")
        internal inner class StringConstructors {
            @Test
            @DisplayName("XQuery 1.0 EBNF (177) StringConstructor")
            fun stringConstructor() {
                val expr = parse<XQueryStringConstructor>("``[`{1}` + `{2}`]``")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.STRING_CONSTRUCTOR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }
        }

        @Nested
        @DisplayName("XXQuery 4.0 ED (4.14) Maps and Arrays ; Query 3.1 (3.11) Maps and Arrays")
        internal inner class MapsAndArrays {
            @Nested
            @DisplayName("XQuery 3.1 (3.11.1) Maps")
            internal inner class Maps {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (170) MapConstructor")
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
                @DisplayName("XQuery 3.1 EBNF (171) MapConstructorEntry")
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
                    @DisplayName("variable key, no value")
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
            @DisplayName("XQuery 3.1 (3.11.2) Arrays")
            internal inner class Arrays {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (175) SquareArrayConstructor")
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
                @DisplayName("XQuery 3.1 EBNF (176) CurlyArrayConstructor")
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
            @DisplayName("XQuery 3.1 (3.11.3.1) Unary Lookup")
            internal inner class UnaryLookup {
                @Test
                @DisplayName("XQuery 3.1 EBNF (181) UnaryLookup")
                fun unaryLookup() {
                    val expr = parse<XPathUnaryLookup>("map{} ! ?name")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.OPTIONAL))
                    assertThat(expr.expressionElement?.textOffset, `is`(8))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier ; XQuery 3.1 EBNF (219) IntegerLiteral")
                fun keySpecifier_expression() {
                    val expr = parse<XPathUnaryLookup>("map{} ! ?2")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression, sameInstance(XpmContextItem))

                    val key = expr.keyExpression as XsIntegerValue
                    assertThat(key.data, `is`(BigInteger.valueOf(2)))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier ; XQuery 3.1 EBNF (235) NCName")
                fun keySpecifier_ncname() {
                    val expr = parse<XPathUnaryLookup>("map{} ! ?name")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression, sameInstance(XpmContextItem))

                    val key = expr.keyExpression as XsNCNameValue
                    assertThat(key.data, `is`("name"))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier ; wildcard")
                fun keySpecifier_wildcard() {
                    val expr = parse<XPathUnaryLookup>("map{} ! ?*")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression, sameInstance(XpmContextItem))

                    val key = expr.keyExpression as XdmWildcardValue
                    assertThat(key.data, `is`("*"))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 (3.11.3.2) Postfix Lookup")
            internal inner class PostfixLookup {
                @Test
                @DisplayName("XQuery IntelliJ Plugin EBNF (130) PostfixLookup")
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
                @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier ; XQuery 3.1 EBNF (219) IntegerLiteral")
                fun keySpecifier_expression() {
                    val expr = parse<XPathPostfixExpr>("\$x?2")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression.text, `is`("\$x"))

                    val key = expr.keyExpression as XsIntegerValue
                    assertThat(key.data, `is`(BigInteger.valueOf(2)))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier ; XQuery 3.1 EBNF (235) NCName")
                fun keySpecifier_ncname() {
                    val expr = parse<XPathPostfixExpr>("\$x?name")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression.text, `is`("\$x"))

                    val key = expr.keyExpression as XsNCNameValue
                    assertThat(key.data, `is`("name"))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier ; wildcard")
                fun keySpecifier_wildcard() {
                    val expr = parse<XPathPostfixExpr>("\$x?*")[0] as XpmLookupExpression
                    assertThat(expr.contextExpression.text, `is`("\$x"))

                    val key = expr.keyExpression as XdmWildcardValue
                    assertThat(key.data, `is`("*"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.15) FLWOR Expressions ; XQuery 3.1 (3.12) FLWOR Expressions")
        internal inner class FLWORExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (41) FLWORExpr")
            internal inner class FLWORExpr {
                @Test
                @DisplayName("for")
                fun `for`() {
                    val expr = parse<XQueryFLWORExpr>("for \$x in (1, 2, 3) return \$x = 1")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.FLWOR_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }

                @Test
                @DisplayName("let")
                fun let() {
                    val expr = parse<XQueryFLWORExpr>("let \$x := 1 return \$x = 1")[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.FLWOR_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (47) PositionalVar")
            internal inner class PositionalVar {
                @Test
                @DisplayName("NCName")
                fun ncname() {
                    val expr = parse<XQueryPositionalVar>("for \$x at \$y in \$z return \$w")[0] as XpmVariableBinding

                    val qname = expr.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("y"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                }

                @Test
                @DisplayName("QName")
                fun qname() {
                    val expr = parse<XQueryPositionalVar>(
                        "for \$a:x at \$a:y in \$a:z return \$a:w"
                    )[0] as XpmVariableBinding

                    val qname = expr.variableName!!
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix!!.data, `is`("a"))
                    assertThat(qname.localName!!.data, `is`("y"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                }

                @Test
                @DisplayName("URIQualifiedName")
                fun uriQualifiedName() {
                    val expr = parse<XQueryPositionalVar>(
                        "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
                        "return \$Q{http://www.example.com}w"
                    )[0] as XpmVariableBinding

                    val qname = expr.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                    assertThat(qname.localName!!.data, `is`("y"))

                    val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                    assertThat(localScope.scope.size, `is`(1))
                    assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                }

                @Test
                @DisplayName("missing VarName")
                fun missingVarName() {
                    val expr = parse<XQueryPositionalVar>("for \$x at \$ \$z return \$w")[0] as XpmVariableBinding
                    assertThat(expr.variableName, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 (3.12.2) For Clause ; XQuery 4.0 ED (4.15.2) For Clause")
            internal inner class ForClause {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (45) ForBinding")
                internal inner class ForBinding {
                    @Test
                    @DisplayName("NCName")
                    fun ncname() {
                        val expr = parse<XQueryForBinding>("for \$x at \$y in \$z return \$w")[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$z"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQueryForBinding>(
                            "for \$a:x at \$a:y in \$a:z return \$a:w"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$a:z"))

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQueryForBinding>(
                            "for \$Q{http://www.example.com}x at \$Q{http://www.example.com}y in \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$Q{http://www.example.com}z"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("missing VarName")
                    fun missingVarName() {
                        val expr = parse<XQueryForBinding>("for \$ \$y return \$w")[0] as XpmCollectionBinding
                        assertThat(expr.variableName, `is`(nullValue()))
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$y"))
                    }

                    @Test
                    @DisplayName("with type")
                    fun withType() {
                        val expr = parse<XQueryForBinding>(
                            "for \$x as  node ( (: :) ) ? at \$y in \$z return \$w"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`("node()?"))
                        assertThat(expr.bindingExpression?.text, `is`("\$z"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))
                    }
                }

                @Nested
                @DisplayName("XQuery 4.0 ED EBNF (53) ForMemberBinding")
                internal inner class ForMemberBinding {
                    @Test
                    @DisplayName("NCName")
                    fun ncname() {
                        val expr = parse<XQueryForMemberBinding>(
                            "for member \$x at \$y in \$z return \$w"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$z"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQueryForMemberBinding>(
                            "for member \$a:x at \$a:y in \$a:z return \$a:w"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$a:z"))

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQueryForMemberBinding>(
                            "for member \$Q{http://www.example.com}x at \$Q{http://www.example.com}y " +
                            "in \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$Q{http://www.example.com}z"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("missing VarName")
                    fun missingVarName() {
                        val expr = parse<XQueryForMemberBinding>(
                            "for member \$ \$y return \$w"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableName, `is`(nullValue()))
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$y"))
                    }

                    @Test
                    @DisplayName("with type")
                    fun withType() {
                        val expr = parse<XQueryForMemberBinding>(
                            "for member \$x as  node ( (: :) ) ? at \$y in \$z return \$w"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`("node()?"))
                        assertThat(expr.bindingExpression?.text, `is`("\$z"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))
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
                    fun ncname() {
                        val expr = parse<XQueryLetBinding>("let \$x := 2 return \$w")[0]
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression?.text, `is`("2"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQueryLetBinding>("let \$a:x := 2 return \$a:w")[0]
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression?.text, `is`("2"))

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQueryLetBinding>(
                            "let \$Q{http://www.example.com}x := 2 return \$Q{http://www.example.com}w"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression?.text, `is`("2"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("missing VarName")
                    fun missingVarName() {
                        val expr = parse<XQueryLetBinding>("let \$ := 2 return \$w")[0] as XpmAssignableVariable
                        assertThat(expr.variableName, `is`(nullValue()))
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression?.text, `is`("2"))
                    }

                    @Test
                    @DisplayName("with type")
                    fun withType() {
                        val expr = parse<XQueryLetBinding>(
                            "let \$x  as  node ( (::) )? := 2 return \$w"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`("node()?"))
                        assertThat(expr.variableExpression?.text, `is`("2"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))
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
                    fun ncname() {
                        val expr = parse<XQueryTumblingWindowClause>(
                            "for tumbling window \$x in \$y return \$z"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$y"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQueryTumblingWindowClause>(
                            "for tumbling window \$a:x in \$a:y return \$a:z"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$a:y"))

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQueryTumblingWindowClause>(
                            "for tumbling window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "return \$Q{http://www.example.com}z"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$Q{http://www.example.com}y"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("missing VarName")
                    fun missingVarName() {
                        val expr = parse<XQueryTumblingWindowClause>(
                            "for tumbling window \$ \$y return \$w"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableName, `is`(nullValue()))
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$y"))
                    }

                    @Test
                    @DisplayName("with type")
                    fun withType() {
                        val expr = parse<XQueryTumblingWindowClause>(
                            "for tumbling window \$x  as  node ( (: :) ? in \$y return \$z"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`("node()?"))
                        assertThat(expr.bindingExpression?.text, `is`("\$y"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (52) SlidingWindowClause")
                internal inner class SlidingWindowClause {
                    @Test
                    @DisplayName("NCName")
                    fun ncname() {
                        val expr = parse<XQuerySlidingWindowClause>(
                            "for sliding window \$x in \$y return \$z"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$y"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQuerySlidingWindowClause>(
                            "for sliding window \$a:x in \$a:y return \$a:z"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$a:y"))

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQuerySlidingWindowClause>(
                            "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "return \$Q{http://www.example.com}z"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$Q{http://www.example.com}y"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("x"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("missing VarName")
                    fun missingVarName() {
                        val expr = parse<XQuerySlidingWindowClause>(
                            "for sliding window \$ \$y return \$w"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableName, `is`(nullValue()))
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.bindingExpression?.text, `is`("\$y"))
                    }

                    @Test
                    @DisplayName("with type")
                    fun withType() {
                        val expr = parse<XQuerySlidingWindowClause>(
                            "for sliding window \$x  as  node ( (: :) ) ? in \$y return \$z"
                        )[0] as XpmCollectionBinding
                        assertThat(expr.variableType?.typeName, `is`("node()?"))
                        assertThat(expr.bindingExpression?.text, `is`("\$y"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("x"))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (56) CurrentItem")
                internal inner class CurrentItem {
                    @Test
                    @DisplayName("NCName namespace resolution")
                    fun ncnameResolution() {
                        val qname = parse<XPathNCName>(
                            "for sliding window \$x in () start \$test when () end when () return ()"
                        )[1] as XsQNameValue
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Variable))

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
                    fun ncname() {
                        val expr = parse<XQueryCurrentItem>(
                            "for sliding window \$x in \$y start \$w when true() return \$z"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("w"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQueryCurrentItem>(
                            "for sliding window \$a:x in \$a:y start \$a:w when true() return \$a:z"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("w"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQueryCurrentItem>(
                            "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("w"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (57) PreviousItem")
                internal inner class PreviousItem {
                    @Test
                    @DisplayName("NCName namespace resolution")
                    fun ncnameResolution() {
                        val qname = parse<XPathNCName>(
                            "for sliding window \$x in () start previous \$test when () end when () return ()"
                        )[1] as XsQNameValue
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Variable))

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
                    fun ncname() {
                        val expr = parse<XQueryPreviousItem>(
                            "for sliding window \$x in \$y start \$v previous \$w when true() return \$z"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("w"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQueryPreviousItem>(
                            "for sliding window \$a:x in \$a:y start \$a:v previous \$a:w when true() return \$a:z"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("w"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQueryPreviousItem>(
                            "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}v previous \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("w"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (58) NextItem")
                internal inner class NextItem {
                    @Test
                    @DisplayName("NCName namespace resolution")
                    fun ncnameResolution() {
                        val qname = parse<XPathNCName>(
                            "for sliding window \$x in () start next \$test when () end when () return ()"
                        )[1] as XsQNameValue
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Variable))

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
                    fun ncname() {
                        val expr = parse<XQueryNextItem>(
                            "for sliding window \$x in \$y start \$v next \$w when true() return \$z"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("w"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQueryNextItem>(
                            "for sliding window \$a:x in \$a:y start \$a:v next \$a:w when true() return \$a:z"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("w"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQueryNextItem>(
                            "for sliding window \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "start \$Q{http://www.example.com}v next \$Q{http://www.example.com}w when true() " +
                            "return \$Q{http://www.example.com}z"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("w"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
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
                    fun ncname() {
                        val expr = parse<XQueryCountClause>("for \$x in \$y count \$z return \$w")[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("z"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQueryCountClause>(
                            "for \$a:x in \$a:y count \$a:z return \$a:w"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("z"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQueryCountClause>(
                            "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y count \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                        )[0] as XpmVariableBinding

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("z"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
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
                    fun ncname() {
                        val expr = parse<XQueryGroupingSpec>(
                            "for \$x in \$y group by \$z return \$w"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression, `is`(nullValue()))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("z"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQueryGroupingSpec>(
                            "for \$a:x in \$a:y group by \$a:z return \$a:w"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression, `is`(nullValue()))

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("z"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQueryGroupingSpec>(
                            "for \$Q{http://www.example.com}x in \$Q{http://www.example.com}y " +
                            "group by \$Q{http://www.example.com}z " +
                            "return \$Q{http://www.example.com}w"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression, `is`(nullValue()))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("z"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryFLWORExpr::class.java)))
                    }

                    @Test
                    @DisplayName("missing VarName")
                    fun missingVarName() {
                        val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$")[0] as XpmAssignableVariable
                        assertThat(expr.variableName, `is`(nullValue()))
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression, `is`(nullValue()))
                    }

                    @Test
                    @DisplayName("with expression")
                    fun withExpression() {
                        val expr = parse<XQueryGroupingSpec>(
                            "for \$x in \$y group by \$z := 2 return \$w"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression?.text, `is`("2"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("z"))
                    }

                    @Test
                    @DisplayName("with type and expression")
                    fun withTypeAndExpression() {
                        val expr = parse<XQueryGroupingSpec>(
                            "for \$x in \$y group by \$z  as  node ( (: :) ) ? := 2 return \$w"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`("node()?"))
                        assertThat(expr.variableExpression?.text, `is`("2"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("z"))
                    }

                    @Test
                    @DisplayName("non-empty collation uri")
                    fun nonEmptyUri() {
                        val expr = parse<XQueryGroupingSpec>(
                            "for \$x in \$y group by \$x collation 'http://www.example.com'"
                        )[0]
                        assertThat(expr.collation!!.data, `is`("http://www.example.com"))
                        assertThat(expr.collation!!.context, `is`(XdmUriContext.Collation))
                        assertThat(expr.collation!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
                    }

                    @Test
                    @DisplayName("missing collation uri")
                    fun noNamespaceUri() {
                        val expr = parse<XQueryGroupingSpec>("for \$x in \$y group by \$x")[0]
                        assertThat(expr.collation, `is`(nullValue()))
                    }
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 (3.12.8) Order By Clause")
            internal inner class OrderByClause {
                @Nested
                @DisplayName("XQuery 3.1 EBNF (68) OrderModifier")
                internal inner class OrderModifier {
                    @Test
                    @DisplayName("non-empty collation uri")
                    fun nonEmptyUri() {
                        val expr = parse<XQueryOrderModifier>(
                            "for \$x in \$y order by \$x collation 'http://www.example.com'"
                        )[0]
                        assertThat(expr.collation!!.data, `is`("http://www.example.com"))
                        assertThat(expr.collation!!.context, `is`(XdmUriContext.Collation))
                        assertThat(expr.collation!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
                    }

                    @Test
                    @DisplayName("missing collation uri")
                    fun noNamespaceUri() {
                        val expr = parse<XQueryOrderModifier>("for \$x in \$y order by \$x ascending")[0]
                        assertThat(expr.collation, `is`(nullValue()))
                    }
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.16) Ordered and Unordered Expressions ; XQuery 3.1 (3.13) Ordered and Unordered Expressions")
        internal inner class OrderedAndUnorderedExpressions {
            @Test
            @DisplayName("XQuery 3.1 EBNF (135) OrderedExpr")
            fun orderedExpr() {
                val expr = parse<XQueryOrderedExpr>("ordered { 1, 2, 3 }")[0] as XpmExpression
                assertThat(expr.expressionElement, `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (136) UnorderedExpr")
            fun unorderedExpr() {
                val expr = parse<XQueryUnorderedExpr>("unordered { 1, 2, 3 }")[0] as XpmExpression
                assertThat(expr.expressionElement, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.17) Conditional Expressions ; XQuery 3.1 (3.14) Conditional Expressions")
        internal inner class ConditionalExpressions {
            @Test
            @DisplayName("XQuery 4.0 ED EBNF (45) TernaryConditionalExpr")
            fun ternaryConditionalExpr() {
                val expr = parse<XPathTernaryConditionalExpr>("true() ?? 1 !! 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.TERNARY_IF))
                assertThat(expr.expressionElement?.textOffset, `is`(7))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (77) IfExpr")
            fun ifExpr() {
                val expr = parse<XPathIfExpr>("if (true()) then 1 else 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.IF_EXPR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.18) Otherwise Expressions")
        internal inner class OtherwiseExpressions {
            @Test
            @DisplayName("XQuery 4.0 ED EBNF (96) OtherwiseExpr")
            fun otherwiseExpr() {
                val expr = parse<XPathOtherwiseExpr>("1 otherwise 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_OTHERWISE))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.19) Switch Expressions ; XQuery 3.1 (3.15) Switch Expression")
        internal inner class SwitchExpression {
            @Test
            @DisplayName("XQuery 3.1 EBNF (71) SwitchExpr")
            fun switchExpr() {
                val expr = parse<XQuerySwitchExpr>("switch (1) case 1 return 1 default return 2")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.SWITCH_EXPR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.20) Quantified Expressions ; XQuery 3.1 (3.16) Quantified Expressions")
        internal inner class QuantifiedExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (70) QuantifiedExpr")
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
            @DisplayName("XQuery 3.1 EBNF (70) QuantifiedExpr ; XQuery 4.0 ED EBNF (78) QuantifierBinding")
            internal inner class QuantifierBinding {
                @Test
                @DisplayName("NCName")
                fun ncname() {
                    val expr = parse<XPathQuantifierBinding>("some \$x in \$y satisfies \$z")[0] as XpmCollectionBinding
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
                    val expr = parse<XPathQuantifierBinding>("some \$a:x in \$a:y satisfies \$a:z")[0] as XpmCollectionBinding
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

                @Test
                @DisplayName("with type")
                fun withType() {
                    val expr = parse<XPathQuantifierBinding>("some \$x  as  node ( (: :) ) ? in \$y satisfies \$z")[0] as XpmCollectionBinding
                    assertThat(expr.variableType?.typeName, `is`("node()?"))
                    assertThat(expr.bindingExpression?.text, `is`("\$y"))

                    val qname = expr.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("x"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.21) Try/Catch Expressions ; XQuery 3.1 (3.17) Try/Catch Expressions")
        internal inner class TryCatchExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (78) TryCatchExpr")
            internal inner class TryCatchExpr {
                @Test
                @DisplayName("with expression ; single catch clause")
                fun expression() {
                    val expr = parse<XQueryTryCatchExpr>("try { 1 } catch * { 2 }")[0] as XpmTryCatchExpression
                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.TRY_CATCH_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                    assertThat(expr.tryExpression.text, `is`("1"))

                    val catchClauses = expr.catchClauses.toList()
                    assertThat(catchClauses.size, `is`(1))
                    assertThat((catchClauses[0] as PsiElement).text, `is`("catch * { 2 }"))
                }

                @Test
                @DisplayName("empty expression ; single catch clause")
                fun emptyExpression() {
                    val expr = parse<XQueryTryCatchExpr>("try { } catch * { 2 }")[0] as XpmTryCatchExpression
                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.TRY_CATCH_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                    assertThat(expr.tryExpression, sameInstance(XpmEmptyExpression))

                    val catchClauses = expr.catchClauses.toList()
                    assertThat(catchClauses.size, `is`(1))
                    assertThat((catchClauses[0] as PsiElement).text, `is`("catch * { 2 }"))
                }

                @Test
                @DisplayName("with expression ; multiple catch clauses")
                fun multipleCatchClauses() {
                    val expr = parse<XQueryTryCatchExpr>(
                        "try { 1 } catch err:XPTY0004 { 2 } catch err:XPTY0005 { 3 }"
                    )[0] as XpmTryCatchExpression
                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.TRY_CATCH_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                    assertThat(expr.tryExpression.text, `is`("1"))

                    val catchClauses = expr.catchClauses.toList()
                    assertThat(catchClauses.size, `is`(2))
                    assertThat((catchClauses[0] as PsiElement).text, `is`("catch err:XPTY0004 { 2 }"))
                    assertThat((catchClauses[1] as PsiElement).text, `is`("catch err:XPTY0005 { 3 }"))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (81) CatchClause")
            internal inner class CatchClause {
                @Test
                @DisplayName("with expression ; wildcard error name")
                fun wildcard() {
                    val expr = parse<XQueryCatchClause>("try { 1 } catch * { 2 }")[0] as XpmCatchClause
                    assertThat(expr.variableName, `is`(nullValue()))
                    assertThat(expr.catchExpression.text, `is`("2"))

                    val errorList = expr.errorList.toList()
                    assertThat(errorList.size, `is`(1))
                    assertThat(qname_presentation(errorList[0]), `is`("*:*"))
                }

                @Test
                @DisplayName("with expression ; single error name")
                fun expression() {
                    val expr = parse<XQueryCatchClause>("try { 1 } catch err:XPTY0004 { 2 }")[0] as XpmCatchClause
                    assertThat(expr.variableName, `is`(nullValue()))
                    assertThat(expr.catchExpression.text, `is`("2"))

                    val errorList = expr.errorList.toList()
                    assertThat(errorList.size, `is`(1))
                    assertThat(qname_presentation(errorList[0]), `is`("err:XPTY0004"))
                }

                @Test
                @DisplayName("empty expression ; single error name")
                fun emptyExpression() {
                    val expr = parse<XQueryCatchClause>("try { 1 } catch err:XPTY0004 { }")[0] as XpmCatchClause
                    assertThat(expr.variableName, `is`(nullValue()))
                    assertThat(expr.catchExpression, sameInstance(XpmEmptyExpression))

                    val errorList = expr.errorList.toList()
                    assertThat(errorList.size, `is`(1))
                    assertThat(qname_presentation(errorList[0]), `is`("err:XPTY0004"))
                }

                @Test
                @DisplayName("with expression ; multiple error names")
                fun multipleErrorNames() {
                    val expr = parse<XQueryCatchClause>(
                        "try { 1 } catch err:XPTY0004 | err:XPTY0005 { 2 }"
                    )[0] as XpmCatchClause
                    assertThat(expr.variableName, `is`(nullValue()))
                    assertThat(expr.catchExpression.text, `is`("2"))

                    val errorList = expr.errorList.toList()
                    assertThat(errorList.size, `is`(2))
                    assertThat(qname_presentation(errorList[0]), `is`("err:XPTY0004"))
                    assertThat(qname_presentation(errorList[1]), `is`("err:XPTY0005"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.22) Expressions on Sequence Types ; XQuery 3.1 (3.18) Expressions on SequenceTypes")
        internal inner class ExpressionsOnSequenceTypes {
            @Test
            @DisplayName("XQuery 3.1 EBNF (92) InstanceofExpr")
            fun instanceOfExpr() {
                val expr = parse<XPathInstanceofExpr>("1 instance of xs:string")[0] as XpmSequenceTypeExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_INSTANCE))
                assertThat(expr.expressionElement?.textOffset, `is`(2))

                assertThat(expr.operation, `is`(XpmSequenceTypeOperation.InstanceOf))
                assertThat((expr.expression as XsIntegerValue).data, `is`(BigInteger.ONE))
                assertThat(expr.type.typeName, `is`("xs:string"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (93) TreatExpr")
            fun treatExpr() {
                val expr = parse<XPathTreatExpr>("1 treat as xs:string")[0] as XpmSequenceTypeExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_TREAT))
                assertThat(expr.expressionElement?.textOffset, `is`(2))

                assertThat(expr.operation, `is`(XpmSequenceTypeOperation.TreatAs))
                assertThat((expr.expression as XsIntegerValue).data, `is`(BigInteger.ONE))
                assertThat(expr.type.typeName, `is`("xs:string"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (94) CastableExpr")
            fun castableExpr() {
                val expr = parse<XPathCastableExpr>("1 castable as xs:string")[0] as XpmSequenceTypeExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_CASTABLE))
                assertThat(expr.expressionElement?.textOffset, `is`(2))

                assertThat(expr.operation, `is`(XpmSequenceTypeOperation.CastableAs))
                assertThat((expr.expression as XsIntegerValue).data, `is`(BigInteger.ONE))
                assertThat(expr.type.typeName, `is`("xs:string"))
            }

            @Nested
            @DisplayName("XQuery 3.1 (3.18.2) Typeswitch")
            internal inner class Typeswitch {
                @Test
                @DisplayName("XQuery 3.1 EBNF (74) TypeswitchExpr")
                fun typeswitchExpr() {
                    val expr = parse<XQueryTypeswitchExpr>(
                        "typeswitch (1) case xs:string return 2 default return 3"
                    )[0] as XpmExpression

                    assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.TYPESWITCH_EXPR))
                    assertThat(expr.expressionElement?.textOffset, `is`(0))
                }

                @Nested
                @DisplayName("XQuery 3.1 EBNF (75) CaseClause")
                internal inner class CaseClause {
                    @Test
                    @DisplayName("NCName")
                    fun ncname() {
                        val expr = parse<XQueryCaseClause>(
                            "typeswitch (\$x) case \$y as xs:string return \$z"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`("xs:string"))
                        assertThat(expr.variableExpression?.text, `is`("\$x"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("y"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryCaseClause::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<XQueryCaseClause>(
                            "typeswitch (\$a:x) case \$a:y as xs:string return \$a:z"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`("xs:string"))
                        assertThat(expr.variableExpression?.text, `is`("\$a:x"))

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("y"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryCaseClause::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<XQueryCaseClause>(
                            "typeswitch (\$Q{http://www.example.com}x) " +
                            "case \$Q{http://www.example.com}y as xs:string " +
                            "return \$Q{http://www.example.com}z"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`("xs:string"))
                        assertThat(expr.variableExpression?.text, `is`("\$Q{http://www.example.com}x"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("y"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(XQueryCaseClause::class.java)))
                    }

                    @Test
                    @DisplayName("without VarName")
                    fun withoutVarName() {
                        val expr = parse<XQueryCaseClause>(
                            "typeswitch (2 + 3) case xs:string return \$z"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableName, `is`(nullValue()))
                        assertThat(expr.variableType?.typeName, `is`("xs:string"))
                        assertThat(expr.variableExpression?.text, `is`("2 + 3"))
                    }
                }

                @Nested
                @DisplayName("XQuery IntelliJ Plugin EBNF (6) DefaultCaseClause")
                internal inner class DefaultCaseClause {
                    @Test
                    @DisplayName("NCName")
                    fun ncname() {
                        val expr = parse<PluginDefaultCaseClause>(
                            "typeswitch (\$x) default \$y return \$z"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression?.text, `is`("\$x"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.localName!!.data, `is`("y"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(PluginDefaultCaseClause::class.java)))
                    }

                    @Test
                    @DisplayName("QName")
                    fun qname() {
                        val expr = parse<PluginDefaultCaseClause>(
                            "typeswitch (\$a:x) default \$a:y return \$a:z"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression?.text, `is`("\$a:x"))

                        val qname = expr.variableName!!
                        assertThat(qname.namespace, `is`(nullValue()))
                        assertThat(qname.prefix!!.data, `is`("a"))
                        assertThat(qname.localName!!.data, `is`("y"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(PluginDefaultCaseClause::class.java)))
                    }

                    @Test
                    @DisplayName("URIQualifiedName")
                    fun uriQualifiedName() {
                        val expr = parse<PluginDefaultCaseClause>(
                            """
                            typeswitch (${'$'}Q{http://www.example.com}x)
                            default ${'$'}Q{http://www.example.com}y
                            return ${'$'}Q{http://www.example.com}z
                            """
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression?.text, `is`("\$Q{http://www.example.com}x"))

                        val qname = expr.variableName!!
                        assertThat(qname.prefix, `is`(nullValue()))
                        assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                        assertThat(qname.localName!!.data, `is`("y"))

                        val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                        assertThat(localScope.scope.size, `is`(1))
                        assertThat(localScope.scope[0], `is`(instanceOf(PluginDefaultCaseClause::class.java)))
                    }

                    @Test
                    @DisplayName("without VarName")
                    fun withoutVarName() {
                        val expr = parse<PluginDefaultCaseClause>(
                            "typeswitch (\$x) default return \$z"
                        )[0] as XpmAssignableVariable
                        assertThat(expr.variableName, `is`(nullValue()))
                        assertThat(expr.variableType?.typeName, `is`(nullValue()))
                        assertThat(expr.variableExpression?.text, `is`("\$x"))
                    }
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (76) SequenceTypeUnion")
                fun sequenceTypeUnion() {
                    val test = parse<XQuerySequenceTypeUnion>(
                        "typeswitch (\$x) case \$y as node ( (::) ) | xs:string | array ( * ) return \$y"
                    )[0]
                    assertThat(test.isParenthesized, `is`(false))

                    val type = test as XdmSequenceTypeUnion
                    assertThat(type.typeName, `is`("node() | xs:string | array(*)"))

                    val types = type.types.toList()
                    assertThat(types.size, `is`(3))
                    assertThat(types[0].typeName, `is`("node()"))
                    assertThat(types[1].typeName, `is`("xs:string"))
                    assertThat(types[2].typeName, `is`("array(*)"))

                    assertThat(type.itemType?.typeName, `is`("item()"))
                    assertThat(type.lowerBound, `is`(0))
                    assertThat(type.upperBound, `is`(Int.MAX_VALUE))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 (3.18.3) Cast")
            internal inner class Cast {
                @Test
                @DisplayName("XQuery 3.1 EBNF (95) CastExpr")
                fun castExpr() {
                    val expr = parse<XPathCastExpr>("1 cast as xs:string")[0] as XpmSequenceTypeExpression

                    assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_CAST))
                    assertThat(expr.expressionElement?.textOffset, `is`(2))

                    assertThat(expr.operation, `is`(XpmSequenceTypeOperation.CastAs))
                    assertThat((expr.expression as XsIntegerValue).data, `is`(BigInteger.ONE))
                    assertThat(expr.type.typeName, `is`("xs:string"))
                }

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
                        assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultType))
                        assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Type))

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
                @DisplayName("XQuery 3.1 EBNF (182) SingleType")
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
        @DisplayName("XQuery 4.0 ED (4.23) Simple Map Operator ; XQuery 3.1 (3.19) Simple Map Operator")
        internal inner class SimpleMapOperator {
            @Test
            @DisplayName("XQuery 3.1 EBNF (107) SimpleMapExpr")
            fun simpleMapExpr() {
                val expr = parse<XPathSimpleMapExpr>("/lorem ! fn:abs(.)")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.MAP_OPERATOR))
                assertThat(expr.expressionElement?.textOffset, `is`(7))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.24) Arrow Expressions ; XQuery 3.1 (3.20) Arrow Operator")
        internal inner class ArrowExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr")
            internal inner class ArrowExpr {
                @Test
                @DisplayName("XQuery 3.1 EBNF (218) EQName")
                fun eqname() {
                    val expr = parse<XPathArrowExpr>("1 => fn:abs()")[0] as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (131) VarRef")
                fun varRef() {
                    val expr = parse<XPathArrowExpr>("let \$x := fn:abs#1 return 1 => \$x()")[0] as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }

                @Test
                @DisplayName("XQuery 3.1 EBNF (133) ParenthesizedExpr")
                fun parenthesizedExpr() {
                    val expr = parse<XPathArrowExpr>("1 => (fn:abs#1)()")[0] as XpmExpression
                    assertThat(expr.expressionElement, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (127) ArrowFunctionSpecifier; XQuery 3.1 EBNF (218) EQName")
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
                    val qname = parse<XPathEQName>(
                        """
                    declare default function namespace "http://www.example.co.uk/function";
                    declare default element namespace "http://www.example.co.uk/element";
                    () => test()
                    """
                    )[0] as XsQNameValue
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultFunctionRef))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.FunctionRef))

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
            @DisplayName("XQuery 3.1 EBNF (127) ArrowFunctionSpecifier; XQuery 3.1 EBNF (131) VarRef")
            fun arrowFunctionSpecifier_VarRef() {
                val f = parse<PluginArrowDynamicFunctionCall>(
                    "let \$f := format-date#5 return \$x => \$f(1, 2, 3,  4)"
                )[0] as XpmArrowFunctionCall

                assertThat(f.functionReference, `is`(nullValue())) // TODO: fn:format-date#5

                assertThat(f.positionalArguments.size, `is`(4))
                assertThat(f.positionalArguments[0].text, `is`("1"))
                assertThat(f.positionalArguments[1].text, `is`("2"))
                assertThat(f.positionalArguments[2].text, `is`("3"))
                assertThat(f.positionalArguments[3].text, `is`("4"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (127) ArrowFunctionSpecifier; XQuery 3.1 EBNF (133) ParenthesizedExpr")
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
            @DisplayName("XQuery 4.0 ED EBNF (108) FatArrowTarget ; XQuery 4.0 ED EBNF (142) ArrowStaticFunction")
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
            @DisplayName("XQuery 4.0 ED EBNF (108) FatArrowTarget ; XQuery 4.0 ED EBNF (143) ArrowDynamicFunction")
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
            @DisplayName("XQuery 4.0 ED EBNF (108) ThinArrowTarget ; XQuery 4.0 ED EBNF (142) ArrowStaticFunction")
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
            @DisplayName("XQuery 4.0 ED EBNF (108) ThinArrowTarget ; XQuery 4.0 ED EBNF (143) ArrowDynamicFunction")
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

        @Nested
        @DisplayName("XQuery 4.0 ED (4.25) Validate Expressions ; XQuery 3.1 (3.21) Validate Expressions")
        internal inner class ValidateExpressions {
            @Test
            @DisplayName("XQuery 3.1 EBNF (102) ValidateExpr")
            fun validateExpr() {
                val expr = parse<XQueryValidateExpr>("validate lax { () }")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.VALIDATE_EXPR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (4.26) Extension Expressions ; XQuery 3.1 (3.22) Extension Expressions")
        internal inner class ExtensionExpressions {
            @Test
            @DisplayName("XQuery 3.1 EBNF (104) ExtensionExpr")
            fun extensionExpr() {
                val expr = parse<XQueryExtensionExpr>("(# test #) { () }")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.EXTENSION_EXPR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (105) Pragma")
            internal inner class Pragma {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathNCName>("(# test #) {}")[0] as XsQNameValue
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Pragma))

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
    }

    @Nested
    @DisplayName("XQuery 4.0 ED (5) Modules and Prologs ; XQuery 3.1 (4) Modules and Prologs")
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

        @Nested
        @DisplayName("XQuery 3.1 EBNF (38) QueryBody")
        internal inner class QueryBody {
            @Test
            @DisplayName("item presentation")
            fun itemPresentation() {
                val decl = parse<XQueryQueryBody>("1 div 2")[0]

                val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
                assertThat(presentation.getIcon(false), `is`(sameInstance(XQueryIcons.Nodes.QueryBody)))
                assertThat(presentation.getIcon(true), `is`(sameInstance(XQueryIcons.Nodes.QueryBody)))
                assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), startsWith("query body ["))
                assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`("query body"))
                assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`("query body"))
                assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`("query body"))
                assertThat(presentation.presentableText, startsWith("query body ["))
                assertThat(presentation.locationString, `is`(nullValue()))
            }

            @Test
            @DisplayName("single")
            fun single() {
                val expr = parse<XQueryQueryBody>("1")[0] as XpmConcatenatingExpression
                assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.QUERY_BODY))
                assertThat(expr.expressionElement?.textOffset, `is`(0))

                val exprs = expr.expressions.toList()
                assertThat(exprs.size, `is`(1))
                assertThat(exprs[0].text, `is`("1"))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expr = parse<XQueryQueryBody>("1, 2 + 3, 4")[0] as XpmConcatenatingExpression
                assertThat(expr.expressionElement, `is`(nullValue()))

                val exprs = expr.expressions.toList()
                assertThat(exprs.size, `is`(3))
                assertThat(exprs[0].text, `is`("1"))
                assertThat(exprs[1].text, `is`("2 + 3"))
                assertThat(exprs[2].text, `is`("4"))
            }

            @Test
            @DisplayName("literal-only expression")
            fun literalOnly() {
                val expr = parse<XQueryQueryBody>("  1, 2, 3.5, 4e2, \"test\"")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.QUERY_BODY))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }

            @Test
            @DisplayName("literal-only parenthesized expression")
            fun literalOnlyParenthesized() {
                val expr = parse<XQueryQueryBody>("  (1, 2, 3.5, 4e2, \"test\")")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.QUERY_BODY))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }

            @Test
            @DisplayName("literal-only nested expression")
            fun literalOnlyNested() {
                val expr = parse<XQueryQueryBody>("  (1, 2, 3.5, (4e2, \"test\"))")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.QUERY_BODY))
                assertThat(expr.expressionElement?.textOffset, `is`(2))
            }

            @Test
            @DisplayName("with non-literal sub-expression")
            fun nonLiteralSubExpression() {
                val expr = parse<XQueryQueryBody>("  (1, 2, (3, 4 + 5))")[0] as XpmExpression
                assertThat(expr.expressionElement, `is`(nullValue()))
            }

            @Test
            @DisplayName("with non-literal expression")
            fun nonLiteralExpression() {
                val expr = parse<XQueryQueryBody>("  1, 2, 3 + 4")[0] as XpmExpression
                assertThat(expr.expressionElement, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (5.1) Version Declaration ; XQuery 3.1 (4.1) Version Declaration")
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
                    assertThat(decl.version!!.data, `is`("1.0"))
                    assertThat(decl.encoding, `is`(nullValue()))
                }

                @Test
                @DisplayName("no version, encoding")
                fun encodingOnly() {
                    val decl = parse<XQueryVersionDecl>("xquery encoding \"latin1\";")[0]
                    assertThat(decl.version, `is`(nullValue()))
                    assertThat(decl.encoding!!.data, `is`("latin1"))
                }

                @Test
                @DisplayName("empty version, no encoding")
                fun emptyVersion() {
                    val decl = parse<XQueryVersionDecl>("xquery version \"\";")[0]
                    assertThat(decl.version!!.data, `is`(""))
                    assertThat(decl.encoding, `is`(nullValue()))
                }

                @Test
                @DisplayName("no version, empty encoding")
                fun emptyEncoding() {
                    val decl = parse<XQueryVersionDecl>("xquery encoding \"\";")[0]
                    assertThat(decl.version, `is`(nullValue()))
                    assertThat(decl.encoding!!.data, `is`(""))
                }

                @Test
                @DisplayName("version, encoding")
                fun versionAndEncoding() {
                    val decl = parse<XQueryVersionDecl>("xquery version \"1.0\" encoding \"latin1\";")[0]
                    assertThat(decl.version!!.data, `is`("1.0"))
                    assertThat(decl.encoding!!.data, `is`("latin1"))
                }

                @Test
                @DisplayName("version, empty encoding")
                fun emptyEncodingWithVersion() {
                    val decl = parse<XQueryVersionDecl>("xquery version \"1.0\" encoding \"\";")[0]
                    assertThat(decl.version!!.data, `is`("1.0"))
                    assertThat(decl.encoding!!.data, `is`(""))
                }

                @Test
                @DisplayName("comment before declaration")
                fun commentBefore() {
                    val decl = parse<XQueryVersionDecl>("(: test :)\nxquery version \"1.0\";")[0]
                    assertThat(decl.version!!.data, `is`("1.0"))
                    assertThat(decl.encoding, `is`(nullValue()))
                }

                @Test
                @DisplayName("comment as whitespace")
                fun commentAsWhitespace() {
                    val decl = parse<XQueryVersionDecl>("xquery(: A :)version(: B :)\"1.0\"(: C :)encoding(: D :)\"latin1\";")[0]
                    assertThat(decl.version!!.data, `is`("1.0"))
                    assertThat(decl.encoding!!.data, `is`("latin1"))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (5.2) Module Declaration ; XQuery 3.1 (4.2) Module Declaration")
        internal inner class ModuleDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (5) ModuleDecl")
            internal inner class ModuleDecl {
                @Test
                @DisplayName("without prolog")
                fun withoutProlog() {
                    val decl = parse<XQueryModuleDecl>("module namespace test = \"http://www.example.com\";")[0] as XpmNamespaceDeclaration
                    assertThat(decl.namespacePrefix!!.data, `is`("test"))
                    assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.Namespace))
                    assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                    assertThat((decl as XQueryPrologResolver).prolog.count(), `is`(0))

                    val prefix = decl.namespacePrefix?.element?.parent!!
                    assertThat(prefix.getUsageType(), `is`(XpmUsageType.Namespace))
                }

                @Test
                @DisplayName("with prolog")
                fun withProlog() {
                    val decl = parse<XQueryModuleDecl>("""
                    module namespace test = "http://www.example.com";
                    declare function test:func() {};
                """)[0] as XpmNamespaceDeclaration
                    assertThat(decl.namespacePrefix!!.data, `is`("test"))
                    assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.Namespace))
                    assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                    val prologs = (decl as XQueryPrologResolver).prolog.toList()
                    assertThat(prologs.size, `is`(1))

                    val name = prologs[0].walkTree().filterIsInstance<XPathEQName>().first()
                    assertThat(name.text, `is`("test:func"))
                }

                @Test
                @DisplayName("missing namespace prefix")
                fun noNamespacePrefix() {
                    val decl = parse<XQueryModuleDecl>("module namespace = \"http://www.example.com\";")[0] as XpmNamespaceDeclaration
                    assertThat(decl.namespacePrefix, `is`(nullValue()))
                    assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.Namespace))
                    assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))
                }

                @Test
                @DisplayName("missing namespace uri")
                fun noNamespaceUri() {
                    val decl = parse<XQueryModuleDecl>("module namespace test = ;")[0] as XpmNamespaceDeclaration
                    assertThat(decl.namespacePrefix!!.data, `is`("test"))
                    assertThat(decl.namespaceUri, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (5.4) Default Collation Declaration ; XQuery 3.1 (4.4) Default Collation Declaration")
        internal inner class DefaultCollationDecl {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (10) DefaultCollationDecl")
            internal inner class DefaultCollationDecl {
                @Test
                @DisplayName("non-empty collation uri")
                fun nonEmptyUri() {
                    val expr = parse<XQueryDefaultCollationDecl>("declare default collation 'http://www.example.com';")[0]
                    assertThat(expr.collation!!.data, `is`("http://www.example.com"))
                    assertThat(expr.collation!!.context, `is`(XdmUriContext.Collation))
                    assertThat(expr.collation!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
                }

                @Test
                @DisplayName("missing collation uri")
                fun noNamespaceUri() {
                    val expr = parse<XQueryDefaultCollationDecl>("declare default collation;")[0]
                    assertThat(expr.collation, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (5.5) Base URI Declaration ; XQuery 3.1 (4.5) Base URI Declaration")
        internal inner class BaseURIDecl {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (11) BaseURIDecl")
            internal inner class BaseURIDecl {
                @Test
                @DisplayName("non-empty uri")
                fun nonEmptyUri() {
                    val expr = parse<XQueryBaseURIDecl>("declare base-uri 'http://www.example.com';")[0]
                    assertThat(expr.baseUri!!.data, `is`("http://www.example.com"))
                    assertThat(expr.baseUri!!.context, `is`(XdmUriContext.BaseUri))
                    assertThat(expr.baseUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.NONE)))
                }

                @Test
                @DisplayName("missing namespace uri")
                fun noNamespaceUri() {
                    val expr = parse<XQueryBaseURIDecl>("declare base-uri;")[0]
                    assertThat(expr.baseUri, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (5.10) Decimal Format Declaration ; XQuery 3.1 (4.10) Decimal Format Declaration")
        internal inner class DecimalFormatDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (18) DecimalFormatDecl")
            internal inner class DecimalFormatDecl {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathNCName>("declare decimal-format test;")[0] as XsQNameValue
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.DecimalFormat))

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
        @DisplayName("XQuery 4.0 ED (5.11) Schema Import ; XQuery 3.1 (4.11) Schema Import")
        internal inner class SchemaImport {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (21) SchemaImport")
            internal inner class SchemaImport {
                @Test
                @DisplayName("specified namespace prefix and uri")
                fun namespacePrefixAndUri() {
                    val import = parse<XQuerySchemaImport>("import schema namespace test = 'http://www.example.com';")[0] as XpmNamespaceDeclaration
                    assertThat(import.namespacePrefix!!.data, `is`("test"))
                    assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                    assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))

                    val uris = (import as XQueryImport).locationUris.toList()
                    assertThat(uris.size, `is`(0))

                    assertThat(import.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(import.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.XQuery), `is`(false))

                    val prefix = import.namespacePrefix?.element?.parent!!
                    assertThat(prefix.getUsageType(), `is`(XpmUsageType.Namespace))
                }

                @Test
                @DisplayName("missing namespace prefix")
                fun noNamespacePrefix() {
                    val import = parse<XQuerySchemaImport>("import schema namespace = 'http://www.example.com';")[0] as XpmNamespaceDeclaration
                    assertThat(import.namespacePrefix?.data, `is`(""))
                    assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                    assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))

                    val uris = (import as XQueryImport).locationUris.toList()
                    assertThat(uris.size, `is`(0))

                    assertThat(import.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(import.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("missing namespace uri")
                fun noNamespaceUri() {
                    val import = parse<XQuerySchemaImport>("import schema namespace test = ;")[0] as XpmNamespaceDeclaration
                    assertThat(import.namespacePrefix!!.data, `is`("test"))
                    assertThat(import.namespaceUri, `is`(nullValue()))

                    val uris = (import as XQueryImport).locationUris.toList()
                    assertThat(uris.size, `is`(0))

                    assertThat(import.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(import.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("default element namespace")
                fun defaultElementNamespace() {
                    val import = parse<XQuerySchemaImport>("import schema default element namespace 'http://www.example.com';")[0] as XpmNamespaceDeclaration
                    assertThat(import.namespacePrefix?.data, `is`(""))
                    assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                    assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))

                    val uris = (import as XQueryImport).locationUris.toList()
                    assertThat(uris.size, `is`(0))

                    assertThat(import.accepts(XdmNamespaceType.DefaultElement), `is`(true))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultType), `is`(true))
                    assertThat(import.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.Prefixed), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("location uris; single uri")
                fun singleLocationUri() {
                    val import = parse<XQuerySchemaImport>(
                        "import schema namespace test = 'http://www.example.com' at 'test1.xsd';"
                    )[0]

                    val uris = import.locationUris.toList()
                    assertThat(uris.size, `is`(1))

                    assertThat(uris[0].data, `is`("test1.xsd"))
                    assertThat(uris[0].context, `is`(XdmUriContext.Location))
                    assertThat(uris[0].moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))
                }

                @Test
                @DisplayName("location uris; multiple uris")
                fun multipleLocationUris() {
                    val import = parse<XQuerySchemaImport>(
                        "import schema namespace test = 'http://www.example.com' at 'test1.xsd' , 'test2.xsd';"
                    )[0]

                    val uris = import.locationUris.toList()
                    assertThat(uris.size, `is`(2))

                    assertThat(uris[0].data, `is`("test1.xsd"))
                    assertThat(uris[0].context, `is`(XdmUriContext.Location))
                    assertThat(uris[0].moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))

                    assertThat(uris[1].data, `is`("test2.xsd"))
                    assertThat(uris[1].context, `is`(XdmUriContext.Location))
                    assertThat(uris[1].moduleTypes, `is`(sameInstance(XdmModuleType.SCHEMA)))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (5.12) Module Import ; XQuery 3.1 (4.12) Module Import")
        internal inner class ModuleImport {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (23) ModuleImport")
            internal inner class ModuleImport {
                @Test
                @DisplayName("specified namespace prefix and uri")
                fun namespacePrefixAndUri() {
                    val import = parse<XQueryModuleImport>(
                        "import module namespace test = 'http://www.example.com';"
                    )[0] as XpmNamespaceDeclaration
                    assertThat(import.namespacePrefix!!.data, `is`("test"))
                    assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                    assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                    val uris = (import as XQueryImport).locationUris.toList()
                    assertThat(uris.size, `is`(0))

                    val prefix = import.namespacePrefix?.element?.parent!!
                    assertThat(prefix.getUsageType(), `is`(XpmUsageType.Namespace))

                    assertThat(import.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(import.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("missing namespace prefix NCName")
                fun noNamespacePrefixNCName() {
                    val import = parse<XQueryModuleImport>(
                        "import module namespace = 'http://www.example.com';"
                    )[0] as XpmNamespaceDeclaration
                    assertThat(import.namespacePrefix, `is`(nullValue()))
                    assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                    assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                    val uris = (import as XQueryImport).locationUris.toList()
                    assertThat(uris.size, `is`(0))

                    assertThat(import.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(import.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("missing namespace prefix")
                fun noNamespacePrefix() {
                    val import = parse<XQueryModuleImport>(
                        "import module 'http://www.example.com';"
                    )[0] as XpmNamespaceDeclaration
                    assertThat(import.namespacePrefix, `is`(nullValue()))
                    assertThat(import.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(import.namespaceUri!!.context, `is`(XdmUriContext.TargetNamespace))
                    assertThat(import.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                    val uris = (import as XQueryImport).locationUris.toList()
                    assertThat(uris.size, `is`(0))

                    assertThat(import.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(import.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("missing namespace uri")
                fun noNamespaceUri() {
                    val import = parse<XQueryModuleImport>("import module namespace test = ;")[0] as XpmNamespaceDeclaration
                    assertThat(import.namespacePrefix!!.data, `is`("test"))
                    assertThat(import.namespaceUri, `is`(nullValue()))

                    val uris = (import as XQueryImport).locationUris.toList()
                    assertThat(uris.size, `is`(0))

                    assertThat(import.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(import.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(import.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("location uris; single uri")
                fun singleLocationUri() {
                    val import = parse<XQueryModuleImport>(
                        "import module namespace test = 'http://www.example.com' at 'test1.xqy';"
                    )[0]

                    val uris = import.locationUris.toList()
                    assertThat(uris.size, `is`(1))

                    assertThat(uris[0].data, `is`("test1.xqy"))
                    assertThat(uris[0].context, `is`(XdmUriContext.Location))
                    assertThat(uris[0].moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))
                }

                @Test
                @DisplayName("location uris; multiple uris")
                fun multipleLocationUris() {
                    val import = parse<XQueryModuleImport>(
                        "import module namespace test = 'http://www.example.com' at 'test1.xqy' , 'test2.xqy';"
                    )[0]

                    val uris = import.locationUris.toList()
                    assertThat(uris.size, `is`(2))

                    assertThat(uris[0].data, `is`("test1.xqy"))
                    assertThat(uris[0].context, `is`(XdmUriContext.Location))
                    assertThat(uris[0].moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))

                    assertThat(uris[1].data, `is`("test2.xqy"))
                    assertThat(uris[1].context, `is`(XdmUriContext.Location))
                    assertThat(uris[1].moduleTypes, `is`(sameInstance(XdmModuleType.MODULE)))
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
                        assertThat(prologs.size, `is`(1))

                        assertThat(prologs[0].resourcePath(), endsWith("/tests/module-xquery/namespaces/ModuleDecl.xq"))
                    }

                    @Test
                    @DisplayName("module in relative directory")
                    fun moduleInRelativeDirectory() {
                        val file = parseResource("tests/resolve-xquery/ModuleImport_URILiteral_InDirectory.xq")
                        val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                        val prologs = (psi as XQueryPrologResolver).prolog.toList()
                        assertThat(prologs.size, `is`(1))

                        assertThat(prologs[0].resourcePath(), endsWith("/tests/module-xquery/namespaces/ModuleDecl.xq"))
                    }

                    @Test
                    @DisplayName("http:// file matching")
                    fun httpProtocol() {
                        val file = parseResource("tests/resolve-xquery/files/ModuleImport_URILiteral_HttpProtocol.xq")
                        val psi = file.walkTree().filterIsInstance<XQueryModuleImport>().toList()[0]

                        val prologs = (psi as XQueryPrologResolver).prolog.toList()
                        assertThat(prologs.size, `is`(1))

                        assertThat(prologs[0].resourcePath(), endsWith("/org/w3/www/2005/xpath-functions/array.xqy"))
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

                        assertThat(prologs[0].resourcePath(), endsWith("/org/w3/www/2005/xpath-functions/array.xqy"))
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
        @DisplayName("XQuery 4.0 ED (5.13) Namespace Declaration ; XQuery 3.1 (4.13) Namespace Declaration")
        internal inner class NamespaceDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (24) NamespaceDecl")
            internal inner class NamespaceDecl {
                @Test
                @DisplayName("specified namespace prefix and uri")
                fun namespacePrefixAndUri() {
                    val decl = parse<XQueryNamespaceDecl>("declare namespace test = 'http://www.example.com';")[0] as XpmNamespaceDeclaration
                    assertThat(decl.namespacePrefix!!.data, `is`("test"))
                    assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.NamespaceDeclaration))
                    assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                    val prefix = decl.namespacePrefix?.element?.parent!!
                    assertThat(prefix.getUsageType(), `is`(XpmUsageType.Namespace))

                    assertThat(decl.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("missing namespace prefix")
                fun noNamespacePrefix() {
                    val decl = parse<XQueryNamespaceDecl>("declare namespace = 'http://www.example.com';")[0] as XpmNamespaceDeclaration
                    assertThat(decl.namespacePrefix, `is`(nullValue()))
                    assertThat(decl.namespaceUri!!.data, `is`("http://www.example.com"))
                    assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.NamespaceDeclaration))
                    assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                    assertThat(decl.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("missing namespace uri")
                fun noNamespaceUri() {
                    val decl = parse<XQueryNamespaceDecl>("declare namespace test = ;")[0] as XpmNamespaceDeclaration
                    assertThat(decl.namespacePrefix!!.data, `is`("test"))
                    assertThat(decl.namespaceUri, `is`(nullValue()))

                    assertThat(decl.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Prefixed), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.XQuery), `is`(false))
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

                        assertThat(prologs[0].resourcePath(), endsWith("/org/w3/www/2005/xpath-functions/array.xqy"))
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
        @DisplayName("XQuery 4.0 ED (5.14) Default Namespace Declaration ; XQuery 3.1 (4.14) Default Namespace Declaration")
        internal inner class DefaultNamespaceDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (25) DefaultNamespaceDecl")
            internal inner class DefaultNamespaceDecl {
                @Test
                @DisplayName("default element/type namespace declaration")
                fun element() {
                    val decl = parse<XpmNamespaceDeclaration>(
                        "declare default element namespace 'http://www.w3.org/1999/xhtml';"
                    )[0]

                    assertThat(decl.namespacePrefix?.data, `is`(""))
                    assertThat(decl.namespaceUri?.data, `is`("http://www.w3.org/1999/xhtml"))
                    assertThat(decl.namespaceUri?.context, `is`(XdmUriContext.Namespace))
                    assertThat(decl.namespaceUri?.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                    assertThat(decl.accepts(XdmNamespaceType.DefaultElement), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultType), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Prefixed), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("default function namespace declaration")
                fun function() {
                    val decl = parse<XpmNamespaceDeclaration>(
                        "declare default function namespace 'http://www.w3.org/2005/xpath-functions/math';"
                    )[0]

                    assertThat(decl.namespacePrefix?.data, `is`(""))
                    assertThat(decl.namespaceUri?.data, `is`("http://www.w3.org/2005/xpath-functions/math"))
                    assertThat(decl.namespaceUri?.context, `is`(XdmUriContext.Namespace))
                    assertThat(decl.namespaceUri?.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                    assertThat(decl.accepts(XdmNamespaceType.DefaultElement), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultType), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Prefixed), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("empty namespace")
                fun emptyNamespace() {
                    val decl = parse<XpmNamespaceDeclaration>("declare default element namespace '';")[0]

                    assertThat(decl.namespacePrefix?.data, `is`(""))
                    assertThat(decl.namespaceUri!!.data, `is`(""))
                    assertThat(decl.namespaceUri!!.context, `is`(XdmUriContext.Namespace))
                    assertThat(decl.namespaceUri!!.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))

                    assertThat(decl.accepts(XdmNamespaceType.DefaultElement), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultType), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Prefixed), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.XQuery), `is`(false))
                }

                @Test
                @DisplayName("missing namespace")
                fun missingNamespace() {
                    val decl = parse<XpmNamespaceDeclaration>("declare default element namespace;")[0]

                    assertThat(decl.namespacePrefix?.data, `is`(""))
                    assertThat(decl.namespaceUri, `is`(nullValue()))

                    assertThat(decl.accepts(XdmNamespaceType.DefaultElement), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionDecl), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultFunctionRef), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.DefaultType), `is`(true))
                    assertThat(decl.accepts(XdmNamespaceType.None), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Prefixed), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.Undefined), `is`(false))
                    assertThat(decl.accepts(XdmNamespaceType.XQuery), `is`(false))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (5.15) Annotations ; XQuery 3.1 (4.15) Annotations")
        internal inner class Annotations {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (27) Annotation")
            internal inner class Annotation {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathNCName>("declare function %test f() {};")[0] as XsQNameValue
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.XQuery))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Annotation))

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

                @Test
                @DisplayName("name only")
                fun nameOnly() {
                    val annotation = parse<XQueryAnnotation>("declare % private function f() {};")[0] as XdmAnnotation
                    assertThat(qname_presentation(annotation.name!!), `is`("private"))

                    val values = annotation.values.toList()
                    assertThat(values.size, `is`(0))

                    val presentation = annotation as ItemPresentation
                    assertThat(presentation.getIcon(false), `is`(XQueryIcons.Nodes.Annotation))
                    assertThat(presentation.locationString, `is`(nullValue()))
                    assertThat(presentation.presentableText, `is`("%private"))
                }

                @Test
                @DisplayName("missing name")
                fun missingName() {
                    val annotation = parse<XQueryAnnotation>("declare % () {};")[0] as XdmAnnotation
                    assertThat(annotation.name, `is`(nullValue()))

                    val values = annotation.values.toList()
                    assertThat(values.size, `is`(0))

                    val presentation = annotation as ItemPresentation
                    assertThat(presentation.getIcon(false), `is`(XQueryIcons.Nodes.Annotation))
                    assertThat(presentation.locationString, `is`(nullValue()))
                    assertThat(presentation.presentableText, `is`(nullValue()))
                }

                @Test
                @DisplayName("values")
                fun values() {
                    val annotation = parse<XQueryAnnotation>("declare % test ( 1 , 2.3 , 4e3 , 'lorem ipsum' ) function f() {};")[0] as XdmAnnotation
                    assertThat(qname_presentation(annotation.name!!), `is`("test"))

                    val values = annotation.values.toList()
                    assertThat(values.size, `is`(4))
                    assertThat((values[0] as XsIntegerValue).data, `is`(BigInteger.ONE))
                    assertThat((values[1] as XsDecimalValue).data, `is`(BigDecimal.valueOf(2.3)))
                    assertThat((values[2] as XsDoubleValue).data, `is`(4e3))
                    assertThat((values[3] as XsStringValue).data, `is`("lorem ipsum"))

                    val presentation = annotation as ItemPresentation
                    assertThat(presentation.getIcon(false), `is`(XQueryIcons.Nodes.Annotation))
                    assertThat(presentation.locationString, `is`(nullValue()))
                    assertThat(presentation.presentableText, `is`("%test(1, 2.3, 4e3, 'lorem ipsum')"))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (28) VarDecl")
            internal inner class VarDecl {
                fun useScope(element: XpmAnnotated): String = when (val scope = (element as PsiElement).useScope) {
                    is LocalSearchScope -> "LocalSearchScope(${scope.scope.joinToString { it.javaClass.simpleName }})"
                    is GlobalSearchScope -> "GlobalSearchScope"
                    else -> scope.javaClass.simpleName
                }

                @Test
                @DisplayName("no annotations")
                fun none() {
                    val decl = parse<XpmAnnotated>("declare variable \$v := 2;")[0]

                    val annotations = decl.annotations.toList()
                    assertThat(annotations.size, `is`(0))
                }

                @Test
                @DisplayName("single annotation")
                fun single() {
                    val decl = parse<XpmAnnotated>("declare %private variable \$v := 2;")[0]

                    val annotations = decl.annotations.toList()
                    assertThat(annotations.size, `is`(1))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("private"))
                }

                @Test
                @DisplayName("multiple annotations")
                fun multiple() {
                    val decl = parse<XpmAnnotated>("declare %public %private variable \$v := 2;")[0]

                    val annotations = decl.annotations.toList()
                    assertThat(annotations.size, `is`(2))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("public"))
                    assertThat(qname_presentation(annotations[1].name!!), `is`("private"))
                }

                @Test
                @DisplayName("visibility (%public/%private)")
                fun visibility() {
                    val decls = parse<XpmAnnotated>(
                        """
                        declare variable ${'$'}a := 1;
                        declare %public variable ${'$'}b := 2;
                        declare %private variable ${'$'}c := 3;
                        """.trimIndent()
                    )

                    assertThat(decls[0].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(decls[1].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(decls[2].accessLevel, `is`(XpmAccessLevel.Private))

                    assertThat(useScope(decls[0]), `is`("GlobalSearchScope"))
                    assertThat(useScope(decls[1]), `is`("GlobalSearchScope"))
                    assertThat(useScope(decls[2]), `is`("LocalSearchScope(XQueryModuleImpl)"))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
            internal inner class FunctionDecl {
                @Test
                @DisplayName("no annotation")
                fun none() {
                    val decl = parse<XpmAnnotated>("declare function f() {};")[0]
                    assertThat(decl.accessLevel, `is`(XpmAccessLevel.Public))

                    val annotations = decl.annotations.toList()
                    assertThat(annotations.size, `is`(0))
                }

                @Test
                @DisplayName("single annotation")
                fun single() {
                    val decl = parse<XpmAnnotated>("declare %private function f() {};")[0]

                    val annotations = decl.annotations.toList()
                    assertThat(annotations.size, `is`(1))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("private"))
                }

                @Test
                @DisplayName("multiple annotations")
                fun multiple() {
                    val decl = parse<XpmAnnotated>("declare %public %private function f() {};")[0]

                    val annotations = decl.annotations.toList()
                    assertThat(annotations.size, `is`(2))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("public"))
                    assertThat(qname_presentation(annotations[1].name!!), `is`("private"))
                }

                @Test
                @DisplayName("visibility (%public/%private)")
                fun visibility() {
                    val decls = parse<XpmAnnotated>(
                        """
                        declare function a() {};
                        declare %public function b() {};
                        declare %private function c() {};
                        """.trimIndent()
                    )
                    assertThat(decls[0].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(decls[1].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(decls[2].accessLevel, `is`(XpmAccessLevel.Private))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr")
            internal inner class InlineFunctionExpr {
                @Test
                @DisplayName("no annotation")
                fun none() {
                    val expr = parse<XpmAnnotated>("function() {};")[0]
                    assertThat(expr.accessLevel, `is`(XpmAccessLevel.Public))

                    val annotations = expr.annotations.toList()
                    assertThat(annotations.size, `is`(0))
                }

                @Test
                @DisplayName("single annotation")
                fun single() {
                    val expr = parse<XpmAnnotated>("%private function() {};")[0]

                    val annotations = expr.annotations.toList()
                    assertThat(annotations.size, `is`(1))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("private"))
                }

                @Test
                @DisplayName("multiple annotations")
                fun multiple() {
                    val expr = parse<XpmAnnotated>("%public %private function() {};")[0]

                    val annotations = expr.annotations.toList()
                    assertThat(annotations.size, `is`(2))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("public"))
                    assertThat(qname_presentation(annotations[1].name!!), `is`("private"))
                }

                @Test
                @DisplayName("visibility (%public/%private)")
                fun visibility() {
                    val exprs = parse<XpmAnnotated>(
                        """
                        function() {},
                        %public function() {},
                        %private function() {}
                        """.trimIndent()
                    )
                    assertThat(exprs[0].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(exprs[1].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(exprs[2].accessLevel, `is`(XpmAccessLevel.Private))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (207) FunctionTest ; XQuery 3.1 EBNF (208) AnyFunctionTest")
            internal inner class AnyFunctionTest {
                @Test
                @DisplayName("no annotations")
                fun none() {
                    val test = parse<XpmAnnotated>("1 instance of function(*)")[0]

                    val annotations = test.annotations.toList()
                    assertThat(annotations.size, `is`(0))
                }

                @Test
                @DisplayName("single annotation")
                fun single() {
                    val test = parse<XpmAnnotated>("1 instance of %private function(*)")[0]

                    val annotations = test.annotations.toList()
                    assertThat(annotations.size, `is`(1))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("private"))
                }

                @Test
                @DisplayName("multiple annotations")
                fun multiple() {
                    val test = parse<XpmAnnotated>("1 instance of %public %private function(*)")[0]

                    val annotations = test.annotations.toList()
                    assertThat(annotations.size, `is`(2))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("public"))
                    assertThat(qname_presentation(annotations[1].name!!), `is`("private"))
                }

                @Test
                @DisplayName("visibility (%public/%private)")
                fun visibility() {
                    val tests = parse<XpmAnnotated>(
                        """
                        1 instance of function(*),
                        1 instance of %public function(*),
                        1 instance of %private function(*)
                        """.trimIndent()
                    )
                    assertThat(tests[0].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(tests[1].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(tests[2].accessLevel, `is`(XpmAccessLevel.Private))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (207) FunctionTest ; XQuery 3.1 EBNF (209) TypedFunctionTest")
            internal inner class TypedFunctionTest {
                @Test
                @DisplayName("no annotations")
                fun none() {
                    val test = parse<XpmAnnotated>("1 instance of function(item()) as item()")[0]

                    val annotations = test.annotations.toList()
                    assertThat(annotations.size, `is`(0))
                }

                @Test
                @DisplayName("single annotation")
                fun single() {
                    val test = parse<XpmAnnotated>("1 instance of %private function(item()) as item()")[0]

                    val annotations = test.annotations.toList()
                    assertThat(annotations.size, `is`(1))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("private"))
                }

                @Test
                @DisplayName("multiple annotations")
                fun multiple() {
                    val test = parse<XpmAnnotated>("1 instance of %public %private function(item()) as item()")[0]

                    val annotations = test.annotations.toList()
                    assertThat(annotations.size, `is`(2))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("public"))
                    assertThat(qname_presentation(annotations[1].name!!), `is`("private"))
                }

                @Test
                @DisplayName("visibility (%public/%private)")
                fun visibility() {
                    val tests = parse<XpmAnnotated>(
                        """
                        1 instance of function(item()) as item(),
                        1 instance of %public function(item()) as item(),
                        1 instance of %private function(item()) as item()
                        """.trimIndent()
                    )
                    assertThat(tests[0].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(tests[1].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(tests[2].accessLevel, `is`(XpmAccessLevel.Private))
                }
            }

            @Nested
            @DisplayName("XQuery 4.0 ED EBNF (38) ItemTypeDecl")
            internal inner class ItemTypeDecl {
                @Test
                @DisplayName("no annotation")
                fun none() {
                    val decl = parse<XpmAnnotated>("declare item-type test as item();")[0]
                    assertThat(decl.accessLevel, `is`(XpmAccessLevel.Public))

                    val annotations = decl.annotations.toList()
                    assertThat(annotations.size, `is`(0))
                }

                @Test
                @DisplayName("single annotation")
                fun single() {
                    val decl = parse<XpmAnnotated>("declare %private item-type test as item();")[0]

                    val annotations = decl.annotations.toList()
                    assertThat(annotations.size, `is`(1))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("private"))
                }

                @Test
                @DisplayName("multiple annotations")
                fun multiple() {
                    val decl = parse<XpmAnnotated>("declare %public %private item-type test as item();")[0]

                    val annotations = decl.annotations.toList()
                    assertThat(annotations.size, `is`(2))
                    assertThat(qname_presentation(annotations[0].name!!), `is`("public"))
                    assertThat(qname_presentation(annotations[1].name!!), `is`("private"))
                }

                @Test
                @DisplayName("visibility (%public/%private)")
                fun visibility() {
                    val decls = parse<XpmAnnotated>(
                        """
                        declare item-type a as item();
                        declare %public item-type b as item();
                        declare %private item-type c as item();
                        """.trimIndent()
                    )
                    assertThat(decls[0].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(decls[1].accessLevel, `is`(XpmAccessLevel.Public))
                    assertThat(decls[2].accessLevel, `is`(XpmAccessLevel.Private))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (5.16) Variable Declaration ; XQuery 3.1 (4.16) Variable Declaration")
        internal inner class VariableDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (28) VarDecl")
            internal inner class VarDecl {
                @Test
                @DisplayName("NCName")
                fun ncname() {
                    val decl = parse<XpmVariableDeclaration>("declare variable \$x := \$y;")[0]
                    assertThat(decl.isExternal, `is`(false))
                    assertThat(decl.variableType?.typeName, `is`(nullValue()))
                    assertThat(decl.variableExpression?.text, `is`("\$y"))

                    val qname = decl.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val presentation = (decl as NavigationItem).presentation!!
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.presentableText, `is`("x"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("QName")
                fun qname() {
                    val decl = parse<XpmVariableDeclaration>("declare variable \$a:x := \$a:y;")[0]
                    assertThat(decl.isExternal, `is`(false))
                    assertThat(decl.variableType?.typeName, `is`(nullValue()))
                    assertThat(decl.variableExpression?.text, `is`("\$a:y"))

                    val qname = decl.variableName!!
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix!!.data, `is`("a"))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val presentation = (decl as NavigationItem).presentation!!
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.presentableText, `is`("a:x"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("URIQualifiedName")
                fun uriQualifiedName() {
                    val decl = parse<XpmVariableDeclaration>(
                        "declare variable \$Q{http://www.example.com}x := \$Q{http://www.example.com}y;"
                    )[0]
                    assertThat(decl.isExternal, `is`(false))
                    assertThat(decl.variableType?.typeName, `is`(nullValue()))
                    assertThat(decl.variableExpression?.text, `is`("\$Q{http://www.example.com}y"))

                    val qname = decl.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val presentation = (decl as NavigationItem).presentation!!
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.presentableText, `is`("Q{http://www.example.com}x"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("with CompatibilityAnnotation")
                fun withCompatibilityAnnotation() {
                    val decl = parse<XpmVariableDeclaration>("declare private variable \$a:x := \$a:y;")[0]
                    assertThat(decl.isExternal, `is`(false))
                    assertThat(decl.variableType?.typeName, `is`(nullValue()))
                    assertThat(decl.variableExpression?.text, `is`("\$a:y"))

                    val qname = decl.variableName!!
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix!!.data, `is`("a"))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val presentation = (decl as NavigationItem).presentation!!
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.presentableText, `is`("a:x"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("missing VarName")
                fun missingVarName() {
                    val decl = parse<XpmVariableDeclaration>("declare variable \$ := \$y;")[0]
                    assertThat(decl.isExternal, `is`(false))
                    assertThat(decl.variableType?.typeName, `is`(nullValue()))
                    assertThat(decl.variableExpression?.text, `is`("\$y"))

                    val presentation = (decl as NavigationItem).presentation!!
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.presentableText, `is`(nullValue()))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("invalid VarName")
                fun invalidVarName() {
                    val decl = parse<XpmVariableDeclaration>("declare variable \$: := \$y;")[0]
                    assertThat(decl.isExternal, `is`(false))
                    assertThat(decl.variableType?.typeName, `is`(nullValue()))
                    assertThat(decl.variableExpression?.text, `is`("\$y"))
                    assertThat(decl.variableType?.typeName, `is`(nullValue()))

                    val presentation = (decl as NavigationItem).presentation!!
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.presentableText, `is`(nullValue()))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("with type")
                fun withType() {
                    val decl = parse<XpmVariableDeclaration>("declare variable \$a:x  as  node ( (::) )? := \$a:y;")[0]
                    assertThat(decl.isExternal, `is`(false))
                    assertThat(decl.variableType?.typeName, `is`("node()?"))
                    assertThat(decl.variableExpression?.text, `is`("\$a:y"))

                    val qname = decl.variableName!!
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix!!.data, `is`("a"))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val presentation = (decl as NavigationItem).presentation!!
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.presentableText, `is`("a:x as node()?"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("external without default")
                fun externalWithoutDefault() {
                    val decl = parse<XpmVariableDeclaration>("declare variable \$x external;")[0]
                    assertThat(decl.isExternal, `is`(true))
                    assertThat(decl.variableType?.typeName, `is`(nullValue()))
                    assertThat(decl.variableExpression, `is`(nullValue()))

                    val qname = decl.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val presentation = (decl as NavigationItem).presentation!!
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.presentableText, `is`("x"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("external with default")
                fun externalWithDefault() {
                    val decl = parse<XpmVariableDeclaration>("declare variable \$x external := \$y;")[0]
                    assertThat(decl.isExternal, `is`(true))
                    assertThat(decl.variableType?.typeName, `is`(nullValue()))
                    assertThat(decl.variableExpression?.text, `is`("\$y"))

                    val qname = decl.variableName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("x"))

                    val presentation = (decl as NavigationItem).presentation!!
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.VarDecl)))
                    assertThat(presentation.presentableText, `is`("x"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED (5.18) Function Declaration ; XQuery 3.1 (4.18) Function Declaration")
        internal inner class FunctionDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (32) FunctionDecl")
            internal inner class FunctionDecl {
                @Test
                @DisplayName("empty ParamList")
                fun emptyParamList() {
                    val decl = parse<XpmFunctionDeclaration>("declare function fn:true() external;")[0]
                    assertThat(decl.returnType, `is`(nullValue()))
                    assertThat(decl.parameters.size, `is`(0))
                    assertThat(decl.functionBody, `is`(nullValue()))

                    val qname = decl.functionName!!
                    assertThat(qname.prefix!!.data, `is`("fn"))
                    assertThat(qname.localName!!.data, `is`("true"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), `is`("fn:true"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`("fn:true()"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`("fn:true"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`("fn:true()"))
                    assertThat(presentation.presentableText, `is`("fn:true"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("non-empty ParamList")
                fun nonEmptyParamList() {
                    val decl = parse<XpmFunctionDeclaration>("declare function test(\$one, \$two) external;")[0]
                    assertThat(decl.returnType, `is`(nullValue()))
                    assertThat(decl.functionBody, `is`(nullValue()))

                    assertThat(decl.parameters.size, `is`(2))
                    assertThat(qname_presentation(decl.parameters[0].variableName!!), `is`("one"))
                    assertThat(qname_presentation(decl.parameters[1].variableName!!), `is`("two"))

                    val qname = decl.functionName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("test"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), `is`("test"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`("test(\$one, \$two)"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`("test"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`("test(\$one, \$two)"))
                    assertThat(presentation.presentableText, `is`("test"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("non-empty ParamList with types")
                fun nonEmptyParamListWithTypes() {
                    val decl = parse<XpmFunctionDeclaration>("declare function test(\$one  as  array ( * ), \$two  as  node((::))) external;")[0]
                    assertThat(decl.returnType, `is`(nullValue()))
                    assertThat(decl.functionBody, `is`(nullValue()))

                    assertThat(decl.parameters.size, `is`(2))
                    assertThat(qname_presentation(decl.parameters[0].variableName!!), `is`("one"))
                    assertThat(qname_presentation(decl.parameters[1].variableName!!), `is`("two"))

                    val qname = decl.functionName!!
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("test"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), `is`("test"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`("test(\$one as array(*), \$two as node())"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`("test"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`("test(\$one as array(*), \$two as node())"))
                    assertThat(presentation.presentableText, `is`("test"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("return type")
                fun returnType() {
                    val decl = parse<XpmFunctionDeclaration>("declare function fn:true()  as  xs:boolean  external;")[0]
                    assertThat(decl.returnType?.typeName, `is`("xs:boolean"))
                    assertThat(decl.parameters.size, `is`(0))
                    assertThat(decl.functionBody, `is`(nullValue()))

                    val qname = decl.functionName!!
                    assertThat(qname.prefix!!.data, `is`("fn"))
                    assertThat(qname.localName!!.data, `is`("true"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), `is`("fn:true"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`("fn:true() as xs:boolean"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`("fn:true"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`("fn:true() as xs:boolean"))
                    assertThat(presentation.presentableText, `is`("fn:true"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("invalid EQName")
                fun invalidEQName() {
                    val decl = parse<XpmFunctionDeclaration>("declare function :true() external;")[0]
                    assertThat(decl.returnType, `is`(nullValue()))
                    assertThat(decl.functionName, `is`(nullValue()))
                    assertThat(decl.functionBody, `is`(nullValue()))

                    assertThat(decl.parameters.size, `is`(0))

                    val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), `is`(nullValue()))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`(nullValue()))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`(nullValue()))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`(nullValue()))
                    assertThat(presentation.presentableText, `is`(nullValue()))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("invalid local name")
                fun invalidLocalName() {
                    // e.g. MarkLogic's roxy framework has template files declaring 'c:#function-name'.
                    val decl = parse<XpmFunctionDeclaration>("declare function fn:() external;")[0]
                    assertThat(decl.returnType, `is`(nullValue()))
                    assertThat(decl.parameters.size, `is`(0))
                    assertThat(decl.functionBody, `is`(nullValue()))

                    val qname = decl.functionName!!
                    assertThat(qname.prefix!!.data, `is`("fn"))
                    assertThat(qname.localName, `is`(nullValue()))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), `is`(nullValue()))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`(nullValue()))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`(nullValue()))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`(nullValue()))
                    assertThat(presentation.presentableText, `is`(nullValue()))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("with CompatibilityAnnotation")
                fun withCompatibilityAnnotation() {
                    val decl = parse<XpmFunctionDeclaration>("declare private function fn:true() external;")[0]
                    assertThat(decl.returnType, `is`(nullValue()))
                    assertThat(decl.parameters.size, `is`(0))
                    assertThat(decl.functionBody, `is`(nullValue()))

                    val qname = decl.functionName!!
                    assertThat(qname.prefix!!.data, `is`("fn"))
                    assertThat(qname.localName!!.data, `is`("true"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), `is`("fn:true"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`("fn:true()"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`("fn:true"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`("fn:true()"))
                    assertThat(presentation.presentableText, `is`("fn:true"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("empty function body")
                fun emptyFunctionBody() {
                    val decl = parse<XpmFunctionDeclaration>("declare function fn:true() {};")[0]
                    assertThat(decl.returnType, `is`(nullValue()))
                    assertThat(decl.parameters.size, `is`(0))
                    assertThat(decl.functionBody, sameInstance(XpmEmptyExpression))

                    val qname = decl.functionName!!
                    assertThat(qname.prefix!!.data, `is`("fn"))
                    assertThat(qname.localName!!.data, `is`("true"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), `is`("fn:true"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`("fn:true()"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`("fn:true"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`("fn:true()"))
                    assertThat(presentation.presentableText, `is`("fn:true"))
                    assertThat(presentation.locationString, `is`(nullValue()))
                }

                @Test
                @DisplayName("function body expression")
                fun functionBodyExpression() {
                    val decl = parse<XpmFunctionDeclaration>("declare function fn:true() { \"1\" cast as xs:boolean };")[0]
                    assertThat(decl.returnType, `is`(nullValue()))
                    assertThat(decl.parameters.size, `is`(0))
                    assertThat(decl.functionBody?.text, `is`("\"1\" cast as xs:boolean "))

                    val qname = decl.functionName!!
                    assertThat(qname.prefix!!.data, `is`("fn"))
                    assertThat(qname.localName!!.data, `is`("true"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val presentation = (decl as NavigationItem).presentation!! as ItemPresentationEx
                    assertThat(presentation.getIcon(false), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getIcon(true), `is`(sameInstance(XPathIcons.Nodes.FunctionDecl)))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.Default), `is`("fn:true"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.StructureView), `is`("fn:true()"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBar), `is`("fn:true"))
                    assertThat(presentation.getPresentableText(ItemPresentationEx.Type.NavBarPopup), `is`("fn:true()"))
                    assertThat(presentation.presentableText, `is`("fn:true"))
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
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.DefaultFunctionDecl))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.FunctionDecl))

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
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.None))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Parameter))

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
        @DisplayName("XQuery 4.0 ED (5.20) Option Declaration ; XQuery 3.1 (4.19) Option Declaration")
        internal inner class OptionDeclaration {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (37) OptionDecl")
            internal inner class OptionDecl {
                @Test
                @DisplayName("NCName namespace resolution")
                fun ncname() {
                    val qname = parse<XPathNCName>("declare option test \"lorem ipsum\";")[0] as XsQNameValue
                    assertThat(qname.getNamespaceType(), `is`(XdmNamespaceType.XQuery))
                    assertThat(qname.element!!.getUsageType(), `is`(XpmUsageType.Option))

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
}
