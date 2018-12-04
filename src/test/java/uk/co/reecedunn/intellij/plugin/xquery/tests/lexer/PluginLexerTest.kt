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
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_START_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@DisplayName("XQuery IntelliJ Plugin - Lexer")
class PluginLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(XQueryLexer(), 0x50000000, 0, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
        lexer.addState(XQueryLexer(), 0x60000000, 0, STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
        return lexer
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (2) DirAttribute")
    fun dirAttribute() {
        val lexer = createLexer()

        matchSingleToken(lexer, "=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (3) QuantifiedExpr")
    fun quantifiedExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "some", XPathTokenType.K_SOME)
        matchSingleToken(lexer, "every", XPathTokenType.K_EVERY)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, "satisfies", XPathTokenType.K_SATISFIES)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (4) QuantifiedExprBinding")
    fun quantifiedExprBinding() {
        val lexer = createLexer()

        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (5) TypeswitchExpr")
    fun typeswitchExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "typeswitch", XQueryTokenType.K_TYPESWITCH)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (6) DefaultCaseClause")
    fun defaultCaseClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (7) CastExpr")
    fun castExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (8) TransformWithExpr")
    fun transformWithExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "transform", XQueryTokenType.K_TRANSFORM)
        matchSingleToken(lexer, "with", XQueryTokenType.K_WITH)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (9) BlockVarDecl")
    fun blockVarDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (10) BlockVarDeclEntry")
    fun blockVarDeclEntry() {
        val lexer = createLexer()

        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, ":=", XPathTokenType.ASSIGN_EQUAL)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (11) AndExpr")
    fun andExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "and", XPathTokenType.K_AND)
        matchSingleToken(lexer, "andAlso", XPathTokenType.K_ANDALSO)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (12) UpdateExpr")
    fun updateExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "update", XQueryTokenType.K_UPDATE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (14) FTFuzzyOption")
    fun ftFuzzyOption() {
        val lexer = createLexer()

        matchSingleToken(lexer, "fuzzy", XQueryTokenType.K_FUZZY)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (16) NonDeterministicFunctionCall")
    fun nonDeterministicFunctionCall() {
        val lexer = createLexer()

        matchSingleToken(lexer, "non-deterministic", XQueryTokenType.K_NON_DETERMINISTIC)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (17) MapConstructorEntry")
    fun mapConstructorEntry() {
        val lexer = createLexer()

        matchSingleToken(lexer, ":", XPathTokenType.QNAME_SEPARATOR)
        matchSingleToken(lexer, ":=", XPathTokenType.ASSIGN_EQUAL)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (19) TypeDecl")
    fun typeDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "type", XQueryTokenType.K_TYPE)
        matchSingleToken(lexer, "=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (21) TypedMapTest")
    fun typedMapTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "map", XPathTokenType.K_MAP)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (22) UnionType")
    fun unionType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "union", XPathTokenType.K_UNION)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (23) TupleType")
    fun tupleType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "tuple", XQueryTokenType.K_TUPLE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (24) TupleField")
    fun tupleField() {
        val lexer = createLexer()

        matchSingleToken(lexer, ":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (25) ForwardAxis")
    fun forwardAxis() {
        val lexer = createLexer()

        matchSingleToken(lexer, "attribute", XPathTokenType.K_ATTRIBUTE)
        matchSingleToken(lexer, "child", XPathTokenType.K_CHILD)
        matchSingleToken(lexer, "descendant", XPathTokenType.K_DESCENDANT)
        matchSingleToken(lexer, "descendant-or-self", XPathTokenType.K_DESCENDANT_OR_SELF)
        matchSingleToken(lexer, "following", XPathTokenType.K_FOLLOWING)
        matchSingleToken(lexer, "following-sibling", XPathTokenType.K_FOLLOWING_SIBLING)
        matchSingleToken(lexer, "namespace", XPathTokenType.K_NAMESPACE) // XPath and MarkLogic
        matchSingleToken(lexer, "property", XQueryTokenType.K_PROPERTY) // MarkLogic
        matchSingleToken(lexer, "self", XPathTokenType.K_SELF)
        matchSingleToken(lexer, "::", XPathTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (26) CompatibilityAnnotation")
    fun compatibilityAnnotation() {
        val lexer = createLexer()

        matchSingleToken(lexer, "assignable", XQueryTokenType.K_ASSIGNABLE)
        matchSingleToken(lexer, "private", XQueryTokenType.K_PRIVATE)
        matchSingleToken(lexer, "sequential", XQueryTokenType.K_SEQUENTIAL)
        matchSingleToken(lexer, "simple", XQueryTokenType.K_SIMPLE)
        matchSingleToken(lexer, "unassignable", XQueryTokenType.K_UNASSIGNABLE)
        matchSingleToken(lexer, "updating", XQueryTokenType.K_UPDATING)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (27) ValidateExpr")
    fun validateExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "validate", XQueryTokenType.K_VALIDATE)
        matchSingleToken(lexer, "type", XQueryTokenType.K_TYPE)
        matchSingleToken(lexer, "as", XPathTokenType.K_AS)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (29) BinaryTest")
    fun binaryTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "binary", XQueryTokenType.K_BINARY)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (30) BinaryConstructor")
    fun binaryConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "binary", XQueryTokenType.K_BINARY)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (31) CatchClause")
    fun catchClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "catch", XQueryTokenType.K_CATCH)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (33) StylesheetImport")
    fun stylesheetImport() {
        val lexer = createLexer()

        matchSingleToken(lexer, "import", XQueryTokenType.K_IMPORT)
        matchSingleToken(lexer, "stylesheet", XQueryTokenType.K_STYLESHEET)
        matchSingleToken(lexer, "at", XQueryTokenType.K_AT)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (35) TransactionSeparator")
    fun transactionSeparator() {
        val lexer = createLexer()

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (37) AttributeDeclTest")
    fun attributeDeclTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "attribute-decl", XQueryTokenType.K_ATTRIBUTE_DECL)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (38) ComplexTypeTest")
    fun complexTypeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "complex-type", XQueryTokenType.K_COMPLEX_TYPE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (39) ElementDeclTest")
    fun elementDeclTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "element-decl", XQueryTokenType.K_ELEMENT_DECL)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (40) SchemaComponentTest")
    fun schemaComponentTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-component", XQueryTokenType.K_SCHEMA_COMPONENT)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (41) SchemaParticleTest")
    fun schemaParticleTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-particle", XQueryTokenType.K_SCHEMA_PARTICLE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (42) SchemaRootTest")
    fun schemaRootTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-root", XQueryTokenType.K_SCHEMA_ROOT)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (43) SchemaTypeTest")
    fun schemaTypeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-type", XQueryTokenType.K_SCHEMA_TYPE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (44) SimpleTypeTest")
    fun simpleTypeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "simple-type", XQueryTokenType.K_SIMPLE_TYPE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (45) SchemaFacetTest")
    fun schemaFacetTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-facet", XQueryTokenType.K_SCHEMA_FACET)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (48) AnyBooleanNodeTest")
    fun anyBooleanNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "boolean-node", XPathTokenType.K_BOOLEAN_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (49) NamedBooleanNodeTest")
    fun namedBooleanNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "boolean-node", XPathTokenType.K_BOOLEAN_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (50) BooleanConstructor")
    fun booleanConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "boolean-node", XPathTokenType.K_BOOLEAN_NODE)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (52) AnyNumberNodeTest")
    fun anyNumberNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "number-node", XQueryTokenType.K_NUMBER_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (53) NamedNumberNodeTest")
    fun namedNumberNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "number-node", XQueryTokenType.K_NUMBER_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (54) NumberConstructor")
    fun numberConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "number-node", XQueryTokenType.K_NUMBER_NODE)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (56) AnyNullNodeTest")
    fun anyNullNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "null-node", XQueryTokenType.K_NULL_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (57) NamedNullNodeTest")
    fun namedNullNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "null-node", XQueryTokenType.K_NULL_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (58) NullConstructor")
    fun nullConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "null-node", XQueryTokenType.K_NULL_NODE)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (60) AnyArrayNodeTest")
    fun anyArrayNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "array-node", XPathTokenType.K_ARRAY_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (61) NamedArrayNodeTest")
    fun namedArrayNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "array-node", XPathTokenType.K_ARRAY_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (62) CurlyArrayConstructor")
    fun curlyArrayConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "array", XPathTokenType.K_ARRAY)
        matchSingleToken(lexer, "array-node", XPathTokenType.K_ARRAY_NODE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (64) AnyMapNodeTest")
    fun anyMapNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "object-node", XQueryTokenType.K_OBJECT_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (65) NamedMapNodeTest")
    fun namedMapNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "object-node", XQueryTokenType.K_OBJECT_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (66) MapConstructor")
    fun mapConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "map", XPathTokenType.K_MAP)
        matchSingleToken(lexer, "object-node", XQueryTokenType.K_OBJECT_NODE)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (67) AnyKindTest")
    fun anyKindTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "node", XPathTokenType.K_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, "*", XPathTokenType.STAR)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (68) NamedKindTest")
    fun namedKindTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "node", XPathTokenType.K_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (70) AnyTextTest")
    fun anyTextTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "text", XPathTokenType.K_TEXT)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (71) NamedTextTest")
    fun namedTextTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "text", XPathTokenType.K_TEXT)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (72) DocumentTest")
    fun documentTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "document-node", XPathTokenType.K_DOCUMENT_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (74) ApplyExpr")
    fun applyExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (75) ConcatExpr")
    fun concatExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (76) Wildcard")
    fun wildcard() {
        val lexer = createLexer()

        matchSingleToken(lexer, ":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (77) WildcardIndicator")
    fun wildcardIndicator() {
        val lexer = createLexer()

        matchSingleToken(lexer, "*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (78) SequenceType")
    fun sequenceType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "empty-sequence", XPathTokenType.K_EMPTY_SEQUENCE)
        matchSingleToken(lexer, "empty", XQueryTokenType.K_EMPTY)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (79) OrExpr")
    fun orExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "or", XPathTokenType.K_OR)
        matchSingleToken(lexer, "orElse", XPathTokenType.K_ORELSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (81) SimpleInlineFunctionExpr")
    fun simpleInlineFunctionExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "fn", XQueryTokenType.K_FN)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (85) ParenthesizedSequenceType")
    fun parenthesizedSequenceType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (86) SequenceTypeUnion")
    fun sequenceTypeUnion() {
        val lexer = createLexer()

        matchSingleToken(lexer, "|", XPathTokenType.UNION)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (87) SequenceTypeList")
    fun sequenceTypeList() {
        val lexer = createLexer()

        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (88) AnyItemType")
    fun anyItemType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "item", XPathTokenType.K_ITEM)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (90) AnnotatedSequenceType")
    fun annotatedSequenceType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "for", XPathTokenType.K_FOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (92) TernaryIfExpr")
    fun ternaryIfExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "??", XQueryTokenType.TERNARY_IF)
        matchSingleToken(lexer, "!!", XQueryTokenType.TERNARY_ELSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (93) ElvisExpr")
    fun elvis() {
        val lexer = createLexer()

        matchSingleToken(lexer, "?:", XQueryTokenType.ELVIS)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (95) ParamList")
    fun paramList() {
        val lexer = createLexer()

        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, "...", XQueryTokenType.ELLIPSIS)
    }
}
