/*
 * Copyright (C) 2017-2020 Reece H. Dunn
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

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("Reformat")
@DisplayName("XQuery IntelliJ Plugin - Implementation Conformance Checks")
private class PluginConformanceTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
        return file.toPsiFile(myProject) as XQueryModule
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (24) TupleField")
    internal inner class TupleField {
        @Test
        @DisplayName("tuple field")
        fun tupleField() {
            val file = parseResource("tests/parser/saxon-9.8/TupleField.xq")
            val conformance = file.walkTree().filterIsInstance<PluginTupleField>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement.elementType, `is`(XQueryElementType.NCNAME))
        }

        @Test
        @DisplayName("as SequenceType")
        fun asType() {
            val file = parseResource("tests/parser/saxon-10.0/TupleField.xq")
            val conformance = file.walkTree().filterIsInstance<PluginTupleField>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_10_0))

            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_AS))
        }

        @Test
        @DisplayName("optional tuple field")
        fun optional() {
            val file = parseResource("tests/parser/saxon-9.9/TupleField_OptionalFieldName.xq")
            val conformance = file.walkTree().filterIsInstance<PluginTupleField>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.OPTIONAL))
        }

        @Test
        @DisplayName("optional tuple field; compact whitespace")
        fun optional_CompactWhitespace() {
            val file = parseResource("tests/parser/saxon-9.9/TupleField_OptionalFieldName_CompactWhitespace.xq")
            val conformance = file.walkTree().filterIsInstance<PluginTupleField>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            // "?:" with compact whitespace
            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.ELVIS))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (19) TypeDecl")
    fun testTypeDecl() {
        val file = parseResource("tests/parser/saxon-9.8/TypeDecl.xq")

        val typeDeclPsi = file.descendants().filterIsInstance<PluginTypeDecl>().first()
        val conformance = typeDeclPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.K_TYPE))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (78) SequenceType")
    internal inner class SequenceType {
        @Test
        @DisplayName("empty sequence; working draft syntax")
        fun emptySequence() {
            val file = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")
            val versioned = file.walkTree().filterIsInstance<XPathSequenceType>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(3))
            assertThat(versioned.requiresConformance[0], `is`(XQuerySpec.WD_1_0_20030502))
            assertThat(versioned.requiresConformance[1], `is`(XQuerySpec.MARKLOGIC_0_9))
            assertThat(versioned.requiresConformance[2], `is`(until(EXistDB.VERSION_4_0)))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_EMPTY))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (79) OrExpr")
    internal inner class OrExpr {
        @Test
        @DisplayName("or only")
        fun or() {
            val file = parseResource("tests/parser/xquery-1.0/OrExpr.xq")
            val versioned = file.walkTree().filterIsInstance<XPathOrExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(0))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathElementType.NODE_TEST))
        }

        @Test
        @DisplayName("orElse only")
        fun orElse() {
            val file = parseResource("tests/parser/saxon-9.9/OrExpr_SingleOrElse.xq")
            val versioned = file.walkTree().filterIsInstance<XPathOrExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ORELSE))
        }

        @Test
        @DisplayName("orElse first")
        fun orElseFirst() {
            val file = parseResource("tests/parser/saxon-9.9/OrExpr_Mixed_OrElseFirst.xq")
            val versioned = file.walkTree().filterIsInstance<XPathOrExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ORELSE))
        }

        @Test
        @DisplayName("orElse last")
        fun orElseLast() {
            val file = parseResource("tests/parser/saxon-9.9/OrExpr_Mixed_OrElseLast.xq")
            val versioned = file.walkTree().filterIsInstance<XPathOrExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ORELSE))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (11) AndExpr")
    internal inner class AndExpr {
        @Test
        @DisplayName("and only")
        fun and() {
            val file = parseResource("tests/parser/xquery-1.0/AndExpr.xq")
            val versioned = file.walkTree().filterIsInstance<XPathAndExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(0))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathElementType.NODE_TEST))
        }

        @Test
        @DisplayName("andAlso only")
        fun andAlso() {
            val file = parseResource("tests/parser/saxon-9.9/AndExpr_SingleAndAlso.xq")
            val versioned = file.walkTree().filterIsInstance<XPathAndExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ANDALSO))
        }

        @Test
        @DisplayName("andAlso first")
        fun andAlsoFirst() {
            val file = parseResource("tests/parser/saxon-9.9/AndExpr_Mixed_AndAlsoFirst.xq")
            val versioned = file.walkTree().filterIsInstance<XPathAndExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ANDALSO))
        }

        @Test
        @DisplayName("andAlso last")
        fun andAlsoLast() {
            val file = parseResource("tests/parser/saxon-9.9/AndExpr_Mixed_AndAlsoLast.xq")
            val versioned = file.walkTree().filterIsInstance<XPathAndExpr>().first() as VersionConformance

            assertThat(versioned.requiresConformance.size, `is`(1))
            assertThat(versioned.requiresConformance[0], `is`(Saxon.VERSION_9_9))

            assertThat(versioned.conformanceElement.elementType, `is`(XPathTokenType.K_ANDALSO))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (86) SequenceTypeUnion")
    fun testSequenceTypeUnion() {
        val file = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion.xq")
        val conformance = file.walkTree().filterIsInstance<XQuerySequenceTypeUnion>().first() as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(XQueryIntelliJPlugin.VERSION_1_3))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.UNION))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (87) SequenceTypeList")
    inner class SequenceTypeList {
        @Test
        @DisplayName("sequence type list")
        fun sequenceTypeList() {
            val file = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList.xq")
            val conformance = file.walkTree().filterIsInstance<PluginSequenceTypeList>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(XQueryIntelliJPlugin.VERSION_1_3))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.COMMA))
        }

        @Test
        @DisplayName("in typed function test")
        fun inTypedFunctionTest() {
            val file = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq")
            val conformance = file.walkTree().filterIsInstance<PluginSequenceTypeList>().first() as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XPathElementType.ANY_ITEM_TYPE))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (95) ParamList")
    fun paramList() {
        val file = parseResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Untyped.xq")
        val conformance = file.walkTree().filterIsInstance<XPathParamList>().first() as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(XQueryIntelliJPlugin.VERSION_1_4))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.ELLIPSIS))
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (199) ElementTest ; XQuery IntelliJ Plugin EBNF (111) ElementNameOrWildcard")
    fun elementTest() {
        val conformance = parse<XPathElementTest>("() instance of element(*:test)")[0] as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_10_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathElementType.WILDCARD))
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (195) AttributeTest ; XQuery IntelliJ Plugin EBNF (112) AttribNameOrWildcard")
    fun attributeTest() {
        val conformance = parse<XPathAttributeTest>("() instance of attribute(*:test)")[0] as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_10_0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathElementType.WILDCARD))
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (116) TypeAlias")
    fun typeAlias() {
        val conformance = parse<PluginTypeAlias>("a instance of ~b")[0] as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Saxon.VERSION_9_8))

        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.TYPE_ALIAS))
    }
}
