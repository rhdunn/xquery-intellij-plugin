/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lexer

import com.intellij.lexer.Lexer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_START_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification

@DisplayName("XQuery IntelliJ Plugin - Lexer")
class PluginLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(XQueryLexer(), 0x50000000, 0, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
        lexer.addState(XQueryLexer(), 0x60000000, 0, STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
        return lexer
    }

    // region MarkLogic 6.0 :: TransactionSeparator

    @Test
    fun testTransactionSeparator() {
        val lexer = createLexer()

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR)
    }

    // endregion
    // region MarkLogic 6.0 :: CompatibilityAnnotation

    @Test
    fun testCompatibilityAnnotation_MarkLogic() {
        val lexer = createLexer()

        matchSingleToken(lexer, "private", XQueryTokenType.K_PRIVATE)
    }

    // endregion
    // region MarkLogic 6.0 :: StylesheetImport

    @Test
    fun testStylesheetImport() {
        val lexer = createLexer()

        matchSingleToken(lexer, "import", XQueryTokenType.K_IMPORT)
        matchSingleToken(lexer, "stylesheet", XQueryTokenType.K_STYLESHEET)
        matchSingleToken(lexer, "at", XQueryTokenType.K_AT)
    }

    // endregion
    // region MarkLogic 6.0 :: ValidateExpr

    @Test
    fun testValidateExpr_ValidateAs() {
        val lexer = createLexer()

        matchSingleToken(lexer, "validate", XQueryTokenType.K_VALIDATE)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    // endregion
    // region MarkLogic 6.0 :: ForwardAxis

    @Test
    fun testForwardAxis_MarkLogic() {
        val lexer = createLexer()

        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE)
        matchSingleToken(lexer, "property", XQueryTokenType.K_PROPERTY)
        matchSingleToken(lexer, "::", XQueryTokenType.AXIS_SEPARATOR)
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryConstructor

    @Test
    fun testBinaryConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "binary", XQueryTokenType.K_BINARY)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    // endregion
    // region MarkLogic 7.0 :: AttributeDeclTest

    @Test
    fun testAttributeDeclTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "attribute-decl", XQueryTokenType.K_ATTRIBUTE_DECL)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 7.0 :: ComplexTypeTest

    @Test
    fun testComplexTypeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "complex-type", XQueryTokenType.K_COMPLEX_TYPE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 7.0 :: ElementDeclTest

    @Test
    fun testElementDeclTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "element-decl", XQueryTokenType.K_ELEMENT_DECL)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 7.0 :: SchemaComponentTest

    @Test
    fun testSchemaComponentTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-component", XQueryTokenType.K_SCHEMA_COMPONENT)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 7.0 :: SchemaParticleTest

    @Test
    fun testSchemaParticleTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-particle", XQueryTokenType.K_SCHEMA_PARTICLE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 7.0 :: SchemaRootTest

    @Test
    fun testSchemaRootTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-root", XQueryTokenType.K_SCHEMA_ROOT)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 7.0 :: SchemaTypeTest

    @Test
    fun testSchemaTypeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-type", XQueryTokenType.K_SCHEMA_TYPE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 7.0 :: SimpleTypeTest

    @Test
    fun testSimpleTypeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "simple-type", XQueryTokenType.K_SIMPLE_TYPE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: SchemaFacetTest

    @Test
    fun testSchemaFacetTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-facet", XQueryTokenType.K_SCHEMA_FACET)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: ArrayConstructor

    @Test
    fun testArrayConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "array-node", XQueryTokenType.K_ARRAY_NODE)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: BooleanConstructor

    @Test
    fun testBooleanConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "boolean-node", XQueryTokenType.K_BOOLEAN_NODE)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: NullConstructor

    @Test
    fun testNullConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "null-node", XQueryTokenType.K_NULL_NODE)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: NumberConstructor

    @Test
    fun testNumberConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "number-node", XQueryTokenType.K_NUMBER_NODE)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: MapConstructor

    @Test
    fun testMapConstructor_MarkLogic() {
        val lexer = createLexer()

        matchSingleToken(lexer, "object-node", XQueryTokenType.K_OBJECT_NODE)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, ":", XQueryTokenType.QNAME_SEPARATOR)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: AnyKindTest

    @Test
    fun testAnyKindTest_MarkLogic() {
        val lexer = createLexer()

        matchSingleToken(lexer, "node", XQueryTokenType.K_NODE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, "*", XQueryTokenType.STAR)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: ArrayTest

    @Test
    fun testArrayTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "array-node", XQueryTokenType.K_ARRAY_NODE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: BooleanTest

    @Test
    fun testBooleanTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "boolean-node", XQueryTokenType.K_BOOLEAN_NODE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: NullTest

    @Test
    fun testNullTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "null-node", XQueryTokenType.K_NULL_NODE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: NumberTest

    @Test
    fun testNumberTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "number-node", XQueryTokenType.K_NUMBER_NODE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region MarkLogic 8.0 :: MapTest

    @Test
    fun testMapTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "object-node", XQueryTokenType.K_OBJECT_NODE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region BaseX 6.1 :: FTFuzzyOption

    @Test
    @Specification(name = "BaseX Full-Text", reference = "http://docs.basex.org/wiki/Full-Text#Fuzzy_Querying")
    fun testFTFuzzyOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "fuzzy", XQueryTokenType.K_FUZZY)
    }

    // endregion
    // region BaseX 7.8 :: UpdateExpr

    @Test
    fun testUpdateExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "update", XQueryTokenType.K_UPDATE)
    }

    // endregion
    // region BaseX 8.4 :: NonDeterministicFunctionCall

    @Test
    fun testNonDeterministicFunctionCall() {
        val lexer = createLexer()

        matchSingleToken(lexer, "non-deterministic", XQueryTokenType.K_NON_DETERMINISTIC)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region BaseX 8.5 :: UpdateExpr

    @Test
    fun testUpdateExpr_BaseX85() {
        val lexer = createLexer()

        matchSingleToken(lexer, "update", XQueryTokenType.K_UPDATE)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    // endregion
    // region Saxon 9.8 :: UnionType

    @Test
    @Specification(name = "Saxon 9.8", reference = "http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/union-types")
    fun testUnionType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "union", XQueryTokenType.K_UNION)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region Saxon 9.8 :: TupleType

    @Test
    @Specification(name = "Saxon 9.8", reference = "http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/tuple-types")
    fun testTupleType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "tuple", XQueryTokenType.K_TUPLE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    // endregion
    // region Saxon 9.8 :: TupleTypeField

    @Test
    @Specification(name = "Saxon 9.8", reference = "http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/tuple-types")
    fun testTupleTypeField() {
        val lexer = createLexer()

        matchSingleToken(lexer, ":", XQueryTokenType.QNAME_SEPARATOR)
    }

    // endregion
    // region Saxon 9.8 :: TypeDecl

    @Test
    @Specification(name = "Saxon 9.8", reference = "http://www.saxonica.com/documentation/index.html#!extensions/syntax-extensions/tuple-types")
    fun testTypeDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "type", XQueryTokenType.K_TYPE)
        matchSingleToken(lexer, "=", XQueryTokenType.EQUAL)
    }

    // endregion
}
