// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xslt.tests.psi

import com.intellij.compat.lang.xml.registerBasicXmlElementFactory
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.impl.PsiCachedValuesFactory
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.xml.XmlFile
import com.intellij.util.CachedValuesManagerImpl
import com.intellij.xml.XmlExtension
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElementFactory
import uk.co.reecedunn.intellij.plugin.xslt.ast.exsl.EXslDocument
import uk.co.reecedunn.intellij.plugin.xslt.ast.marklogic.MarkLogicCatch
import uk.co.reecedunn.intellij.plugin.xslt.ast.marklogic.MarkLogicImportModule
import uk.co.reecedunn.intellij.plugin.xslt.ast.marklogic.MarkLogicTry
import uk.co.reecedunn.intellij.plugin.xslt.ast.saxon.*
import uk.co.reecedunn.intellij.plugin.xslt.ast.xslt.XsltResultDocument
import uk.co.reecedunn.intellij.plugin.xslt.ast.xslt.XsltStylesheet
import uk.co.reecedunn.intellij.plugin.xslt.ast.xslt.XsltTemplate
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.XsltShadowPsiElementFactory
import uk.co.reecedunn.intellij.plugin.xslt.tests.lang.XsltLanguageTestCase

@Suppress("RedundantVisibilityModifier")
@DisplayName("XQuery IntelliJ Plugin - IntelliJ Program Structure Interface (PSI) - XSLT")
class PluginPsiTest : IdeaPlatformTestCase(), LanguageParserTestCase<XmlFile>, XsltLanguageTestCase {
    companion object {
        private const val EXSL_COMMON_NAMESPACE = "http://exslt.org/common"
        private const val SAXON_NAMESPACE = "http://saxon.sf.net/"
        private const val SAXON6_NAMESPACE = "http://icl.com/saxon"
        private const val XDMP_NAMESPACE = "http://marklogic.com/xdmp"
    }

    override val pluginId: PluginId = PluginId.getId("PluginPsiTest")
    override val language: com.intellij.lang.Language = XMLLanguage.INSTANCE

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        requiresPsiFileGetChildren()

        XmlASTFactory().registerExtension(project, XMLLanguage.INSTANCE)
        XMLParserDefinition().registerExtension(project)
        XmlFileType.INSTANCE.registerFileType()

        XpmShadowPsiElementFactory.register(this, XsltShadowPsiElementFactory)

        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(XmlExtension.EP_NAME, XmlExtension::class.java, pluginDisposable)
        app.registerBasicXmlElementFactory()

        project.registerService<CachedValuesManager>(CachedValuesManagerImpl(project, PsiCachedValuesFactory(project)))
    }

    @Nested
    @DisplayName("EXSL Common")
    internal inner class EXSLCommon {
        @Nested
        @DisplayName("exsl:document")
        internal inner class Document {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:exsl="http://exslt.org/common" version="2.0" extension-element-prefixes="exsl">
                        <xsl:template match="lorem">
                            <exsl:document href="ipsum.xml"/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<EXslDocument>(xml, EXSL_COMMON_NAMESPACE, "document")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }
    }

    @Nested
    @DisplayName("MarkLogic")
    internal inner class MarkLogic {
        @Nested
        @DisplayName("xdmp:catch")
        internal inner class Catch {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:xdmp="http://marklogic.com/xdmp" version="2.0" extension-element-prefixes="xdmp">
                        <xsl:template match="lorem">
                            <xdmp:try>
                                <xdmp:catch name="e"/>
                            </xdmp:try>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<MarkLogicCatch>(xml, XDMP_NAMESPACE, "catch")[0]

                assertThat(psi.parent, `is`(instanceOf(MarkLogicTry::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("xdmp:import-module")
        internal inner class ImportModule {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:xdmp="http://marklogic.com/xdmp" version="2.0" extension-element-prefixes="xdmp">
                        <xdmp:import-module namespace="urn:lorem:ipsum" href="lorem-ipsum.xqy"/>
                    </xsl:stylesheet>
                """
                val psi = parse<MarkLogicImportModule>(xml, XDMP_NAMESPACE, "import-module")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("xdmp:try")
        internal inner class Try {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:xdmp="http://marklogic.com/xdmp" version="2.0" extension-element-prefixes="xdmp">
                        <xsl:template match="lorem">
                            <xdmp:try/>
                        </xsl:template>
                    </xsl:stylesheet>
                """
                val psi = parse<MarkLogicTry>(xml, XDMP_NAMESPACE, "try")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltTemplate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }
    }

    @Nested
    @DisplayName("Saxon")
    internal inner class Saxon {
        @Nested
        @DisplayName("saxon:array")
        internal inner class Array {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:array/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonArray>(xml, SAXON_NAMESPACE, "array")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:array-member")
        internal inner class ArrayMember {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:array-member select="(1, 2, 3)"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonArrayMember>(xml, SAXON_NAMESPACE, "array-member")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:assign")
        internal inner class Assign {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:assign name="lorem-ipsum">Ipsum</saxon:assign>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonAssign>(xml, SAXON_NAMESPACE, "assign")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:change")
        internal inner class Change {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem">
                            <saxon:change select="ipsum" to="dolor"/>
                        </saxon:update>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonChange>(xml, SAXON_NAMESPACE, "change")[0]

                assertThat(psi.parent, `is`(instanceOf(SaxonUpdate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:deep-update")
        internal inner class DeepUpdate {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:deep-update root="${'$'}m" select="?*[?lorem = 'ipsum']" action="()"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonDeepUpdate>(xml, SAXON_NAMESPACE, "deep-update")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:delete")
        internal inner class Delete {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem">
                            <saxon:delete select="ipsum"/>
                        </saxon:update>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonDelete>(xml, SAXON_NAMESPACE, "delete")[0]

                assertThat(psi.parent, `is`(instanceOf(SaxonUpdate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:do")
        internal inner class Do {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:do action="${'$'}file?close()"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonDo>(xml, SAXON_NAMESPACE, "do")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:doctype")
        internal inner class Doctype {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:doctype/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonDoctype>(xml, SAXON_NAMESPACE, "doctype")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:entity-ref")
        internal inner class EntityRef {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:entity-ref name="nbsp"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonEntityRef>(xml, SAXON_NAMESPACE, "entity-ref")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:for-each-member")
        internal inner class ForEachMember {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:for-each-member select="array { 1, 2, 3 }" bind-to="x"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonForEachMember>(xml, SAXON_NAMESPACE, "for-each-member")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:import-query")
        internal inner class ImportQuery {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:import-query namespace="urn:lorem:ipsum"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonImportQuery>(xml, SAXON_NAMESPACE, "import-query")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:insert")
        internal inner class Insert {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem">
                            <saxon:insert select="ipsum" position="after"/>
                        </saxon:update>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonInsert>(xml, SAXON_NAMESPACE, "insert")[0]

                assertThat(psi.parent, `is`(instanceOf(SaxonUpdate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:item-type")
        internal inner class ItemType {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:item-type name="lorem-ipsum" as="xs:string"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonItemType>(xml, SAXON_NAMESPACE, "item-type")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon6:output")
        internal inner class Output {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://icl.com/saxon" version="3.0" extension-element-prefixes="saxon">
                        <saxon:output/>
                    </xsl:stylesheet>
                """
                val psi = parse<XsltResultDocument>(xml, SAXON6_NAMESPACE, "output")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:rename")
        internal inner class Rename {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem">
                            <saxon:rename select="ipsum" to="'dolor'"/>
                        </saxon:update>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonRename>(xml, SAXON_NAMESPACE, "rename")[0]

                assertThat(psi.parent, `is`(instanceOf(SaxonUpdate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:replace")
        internal inner class Replace {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem">
                            <saxon:replace select="ipsum"/>
                        </saxon:update>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonReplace>(xml, SAXON_NAMESPACE, "replace")[0]

                assertThat(psi.parent, `is`(instanceOf(SaxonUpdate::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:tabulate-maps")
        internal inner class TabulateMaps {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:tabulate-maps root="${'$'}lorem" select="?ipsum"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonTabulateMaps>(xml, SAXON_NAMESPACE, "tabulate-maps")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:type-alias")
        internal inner class TypeAlias {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:type-alias name="lorem-ipsum" as="xs:string"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonItemType>(xml, SAXON_NAMESPACE, "type-alias")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:update")
        internal inner class Update {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:update select="//lorem"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonUpdate>(xml, SAXON_NAMESPACE, "update")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }

        @Nested
        @DisplayName("saxon:while")
        internal inner class While {
            @Test
            @DisplayName("hierarchy")
            fun hierarchy() {
                @Language("XML") val xml = """
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                    xmlns:saxon="http://saxon.sf.net/" version="3.0" extension-element-prefixes="saxon">
                        <saxon:while test="${'$'}x &lt; 10"/>
                    </xsl:stylesheet>
                """
                val psi = parse<SaxonWhile>(xml, SAXON_NAMESPACE, "while")[0]

                assertThat(psi.parent, `is`(instanceOf(XsltStylesheet::class.java)))
                assertThat(psi.children.size, `is`(0))
                assertThat(psi.prevSibling, `is`(nullValue()))
                assertThat(psi.nextSibling, `is`(nullValue()))

                val parent = psi.parent!!
                assertThat(parent.children.size, `is`(1))
                assertThat(parent.children[0], `is`(sameInstance(psi)))
            }
        }
    }
}
