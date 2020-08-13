/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.tests.parser

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xdm.psi.tree.ISchemaType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltSchemaType
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XsltSchemaTypes
import uk.co.reecedunn.intellij.plugin.xslt.parser.XsltSchemaTypesParserDefinition

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XSLT 3.0 - Schema Types")
private class XsltSchemaTypesTest : ParserTestCase(XsltSchemaTypesParserDefinition(), XPathParserDefinition()) {
    fun parseResource(resource: String, schemaType: ISchemaType): XsltSchemaType {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
        file.putUserData(ISchemaType.XDM_SCHEMA_TYPE, schemaType)
        return file.toPsiFile(myProject) as XsltSchemaType
    }

    fun loadResource(resource: String): String? {
        return ResourceVirtualFile.create(this::class.java.classLoader, resource).decode()
    }

    @Nested
    @DisplayName("xsl:EQName")
    inner class EQName {
        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val expected = loadResource("tests/parser/schema-type/qname/QName.txt")
            val actual = parseResource("tests/parser/schema-type/qname/QName.input", XsltSchemaTypes.EQName)
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val expected = loadResource("tests/parser/schema-type/qname/NCName.txt")
            val actual = parseResource("tests/parser/schema-type/qname/NCName.input", XsltSchemaTypes.EQName)
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (117) URIQualifiedName")
        fun eqname() {
            val expected = loadResource("tests/parser/schema-type/eqname/URIQualifiedName.txt")
            val actual = parseResource("tests/parser/schema-type/eqname/URIQualifiedName.input", XsltSchemaTypes.EQName)
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Test
    @DisplayName("xsl:expression")
    fun expression() {
        val expected = loadResource("tests/parser/schema-type/expression/XPath.txt")
        val actual = parseResource("tests/parser/schema-type/expression/XPath.input", XsltSchemaTypes.Expression)
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @DisplayName("xsl:item-type")
    fun itemType() {
        val expected = loadResource("tests/parser/schema-type/item-type/ItemType.txt")
        val actual = parseResource("tests/parser/schema-type/item-type/ItemType.input", XsltSchemaTypes.ItemType)
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @DisplayName("xsl:pattern")
    fun pattern() {
        val expected = loadResource("tests/parser/schema-type/pattern/XPath.txt")
        val actual = parseResource("tests/parser/schema-type/pattern/XPath.input", XsltSchemaTypes.Pattern)
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Nested
    @DisplayName("xsl:QName")
    inner class QName {
        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val expected = loadResource("tests/parser/schema-type/qname/QName.txt")
            val actual = parseResource("tests/parser/schema-type/qname/QName.input", XsltSchemaTypes.QName)
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val expected = loadResource("tests/parser/schema-type/qname/NCName.txt")
            val actual = parseResource("tests/parser/schema-type/qname/NCName.input", XsltSchemaTypes.QName)
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }
}
