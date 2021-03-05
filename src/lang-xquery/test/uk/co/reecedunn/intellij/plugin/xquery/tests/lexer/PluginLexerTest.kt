/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@DisplayName("XQuery IntelliJ Plugin - Lexer")
class PluginLexerTest : LexerTestCase() {
    override val lexer: Lexer = run {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(
            XQueryLexer(), 0x50000000, 0,
            XQueryLexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
        )
        lexer.addState(
            XQueryLexer(), 0x60000000, 0,
            XQueryLexer.STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG
        )
        lexer
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (2) DirAttribute")
    fun dirAttribute() {
        token("=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (3) QuantifiedExpr")
    fun quantifiedExpr() {
        token("some", XPathTokenType.K_SOME)
        token("every", XPathTokenType.K_EVERY)
        token(",", XPathTokenType.COMMA)
        token("satisfies", XPathTokenType.K_SATISFIES)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (5) TypeswitchExpr")
    fun typeswitchExpr() {
        token("typeswitch", XQueryTokenType.K_TYPESWITCH)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (6) DefaultCaseClause")
    fun defaultCaseClause() {
        token("default", XPathTokenType.K_DEFAULT)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (7) CastExpr")
    fun castExpr() {
        token("default", XPathTokenType.K_DEFAULT)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (8) TransformWithExpr")
    fun transformWithExpr() {
        token("transform", XQueryTokenType.K_TRANSFORM)
        token("with", XPathTokenType.K_WITH)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (9) BlockVarDecl")
    fun blockVarDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (10) BlockVarDeclEntry")
    fun blockVarDeclEntry() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token(":=", XPathTokenType.ASSIGN_EQUAL)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (11) AndExpr")
    fun andExpr() {
        token("and", XPathTokenType.K_AND)
        token("andAlso", XPathTokenType.K_ANDALSO)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (12) UpdateExpr")
    fun updateExpr() {
        token("update", XQueryTokenType.K_UPDATE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (14) FTFuzzyOption")
    fun ftFuzzyOption() {
        token("fuzzy", XQueryTokenType.K_FUZZY)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (16) NonDeterministicFunctionCall")
    fun nonDeterministicFunctionCall() {
        token("non-deterministic", XQueryTokenType.K_NON_DETERMINISTIC)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (17) MapConstructorEntry")
    fun mapConstructorEntry() {
        token(":", XPathTokenType.QNAME_SEPARATOR)
        token(":=", XPathTokenType.ASSIGN_EQUAL)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (19) ItemTypeDecl")
    fun typeDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("type", XPathTokenType.K_TYPE)
        token("=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (21) TypedMapTest")
    fun typedMapTest() {
        token("map", XPathTokenType.K_MAP)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (23) RecordTest")
    fun recordTest() {
        token("tuple", XPathTokenType.K_TUPLE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (24) FieldDeclaration")
    fun fieldDeclaration() {
        token("?", XPathTokenType.OPTIONAL)
        token("as", XPathTokenType.K_AS)

        token("?:", XPathTokenType.ELVIS) // compact whitespace
        token(":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (25) ForwardAxis")
    fun forwardAxis() {
        token("attribute", XPathTokenType.K_ATTRIBUTE)
        token("child", XPathTokenType.K_CHILD)
        token("descendant", XPathTokenType.K_DESCENDANT)
        token("descendant-or-self", XPathTokenType.K_DESCENDANT_OR_SELF)
        token("following", XPathTokenType.K_FOLLOWING)
        token("following-sibling", XPathTokenType.K_FOLLOWING_SIBLING)
        token("namespace", XPathTokenType.K_NAMESPACE) // XPath and MarkLogic
        token("property", XPathTokenType.K_PROPERTY) // MarkLogic
        token("self", XPathTokenType.K_SELF)
        token("::", XPathTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (26) CompatibilityAnnotation")
    fun compatibilityAnnotation() {
        token("assignable", XQueryTokenType.K_ASSIGNABLE)
        token("private", XQueryTokenType.K_PRIVATE)
        token("sequential", XQueryTokenType.K_SEQUENTIAL)
        token("simple", XQueryTokenType.K_SIMPLE)
        token("unassignable", XQueryTokenType.K_UNASSIGNABLE)
        token("updating", XQueryTokenType.K_UPDATING)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (27) ValidateExpr")
    fun validateExpr() {
        token("validate", XQueryTokenType.K_VALIDATE)
        token("type", XPathTokenType.K_TYPE)
        token("as", XPathTokenType.K_AS)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (29) BinaryTest")
    fun binaryTest() {
        token("binary", XQueryTokenType.K_BINARY)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (30) BinaryConstructor")
    fun binaryConstructor() {
        token("binary", XQueryTokenType.K_BINARY)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (31) CatchClause")
    fun catchClause() {
        token("catch", XQueryTokenType.K_CATCH)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (33) StylesheetImport")
    fun stylesheetImport() {
        token("import", XQueryTokenType.K_IMPORT)
        token("stylesheet", XQueryTokenType.K_STYLESHEET)
        token("at", XPathTokenType.K_AT)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (35) TransactionSeparator")
    fun transactionSeparator() {
        token(";", XQueryTokenType.SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (37) AttributeDeclTest")
    fun attributeDeclTest() {
        token("attribute-decl", XQueryTokenType.K_ATTRIBUTE_DECL)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (38) ComplexTypeTest")
    fun complexTypeTest() {
        token("complex-type", XQueryTokenType.K_COMPLEX_TYPE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (39) ElementDeclTest")
    fun elementDeclTest() {
        token("element-decl", XQueryTokenType.K_ELEMENT_DECL)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (40) SchemaComponentTest")
    fun schemaComponentTest() {
        token("schema-component", XQueryTokenType.K_SCHEMA_COMPONENT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (41) SchemaParticleTest")
    fun schemaParticleTest() {
        token("schema-particle", XQueryTokenType.K_SCHEMA_PARTICLE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (42) SchemaRootTest")
    fun schemaRootTest() {
        token("schema-root", XQueryTokenType.K_SCHEMA_ROOT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (43) SchemaTypeTest")
    fun schemaTypeTest() {
        token("schema-type", XQueryTokenType.K_SCHEMA_TYPE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (44) SimpleTypeTest")
    fun simpleTypeTest() {
        token("simple-type", XQueryTokenType.K_SIMPLE_TYPE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (45) SchemaFacetTest")
    fun schemaFacetTest() {
        token("schema-facet", XQueryTokenType.K_SCHEMA_FACET)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (48) AnyBooleanNodeTest")
    fun anyBooleanNodeTest() {
        token("boolean-node", XPathTokenType.K_BOOLEAN_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (49) NamedBooleanNodeTest")
    fun namedBooleanNodeTest() {
        token("boolean-node", XPathTokenType.K_BOOLEAN_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (50) BooleanConstructor")
    fun booleanConstructor() {
        token("boolean-node", XPathTokenType.K_BOOLEAN_NODE)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (52) AnyNumberNodeTest")
    fun anyNumberNodeTest() {
        token("number-node", XPathTokenType.K_NUMBER_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (53) NamedNumberNodeTest")
    fun namedNumberNodeTest() {
        token("number-node", XPathTokenType.K_NUMBER_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (54) NumberConstructor")
    fun numberConstructor() {
        token("number-node", XPathTokenType.K_NUMBER_NODE)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (56) AnyNullNodeTest")
    fun anyNullNodeTest() {
        token("null-node", XPathTokenType.K_NULL_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (57) NamedNullNodeTest")
    fun namedNullNodeTest() {
        token("null-node", XPathTokenType.K_NULL_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (58) NullConstructor")
    fun nullConstructor() {
        token("null-node", XPathTokenType.K_NULL_NODE)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (60) AnyArrayNodeTest")
    fun anyArrayNodeTest() {
        token("array-node", XPathTokenType.K_ARRAY_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (61) NamedArrayNodeTest")
    fun namedArrayNodeTest() {
        token("array-node", XPathTokenType.K_ARRAY_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (62) CurlyArrayConstructor")
    fun curlyArrayConstructor() {
        token("array", XPathTokenType.K_ARRAY)
        token("array-node", XPathTokenType.K_ARRAY_NODE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (64) AnyMapNodeTest")
    fun anyMapNodeTest() {
        token("object-node", XPathTokenType.K_OBJECT_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (65) NamedMapNodeTest")
    fun namedMapNodeTest() {
        token("object-node", XPathTokenType.K_OBJECT_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (66) MapConstructor")
    fun mapConstructor() {
        token("map", XPathTokenType.K_MAP)
        token("object-node", XPathTokenType.K_OBJECT_NODE)
        token("{", XPathTokenType.BLOCK_OPEN)
        token(",", XPathTokenType.COMMA)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (67) AnyKindTest")
    fun anyKindTest() {
        token("node", XPathTokenType.K_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token("*", XPathTokenType.STAR)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (68) NamedKindTest")
    fun namedKindTest() {
        token("node", XPathTokenType.K_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (70) AnyTextTest")
    fun anyTextTest() {
        token("text", XPathTokenType.K_TEXT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (71) NamedTextTest")
    fun namedTextTest() {
        token("text", XPathTokenType.K_TEXT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (72) DocumentTest")
    fun documentTest() {
        token("document-node", XPathTokenType.K_DOCUMENT_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (76) Wildcard")
    fun wildcard() {
        token(":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (77) WildcardIndicator")
    fun wildcardIndicator() {
        token("*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (78) SequenceType")
    fun sequenceType() {
        token("empty-sequence", XPathTokenType.K_EMPTY_SEQUENCE)
        token("empty", XPathTokenType.K_EMPTY)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (79) OrExpr")
    fun orExpr() {
        token("or", XPathTokenType.K_OR)
        token("orElse", XPathTokenType.K_ORELSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (81) ContextItemFunctionExpr")
    fun contextItemFunctionExpr() {
        token("fn", XPathTokenType.K_FN)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)

        token(".{", XPathTokenType.CONTEXT_FUNCTION)
        token("}", XPathTokenType.BLOCK_CLOSE)

        token(".", XPathTokenType.DOT)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (85) ParenthesizedSequenceType")
    fun parenthesizedSequenceType() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (86) SequenceTypeUnion")
    fun sequenceTypeUnion() {
        token("|", XPathTokenType.UNION)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (87) SequenceTypeList")
    fun sequenceTypeList() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (88) AnyItemTest")
    fun anyItemTest() {
        token("item", XPathTokenType.K_ITEM)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (93) ElvisExpr")
    fun elvis() {
        token("?:", XPathTokenType.ELVIS)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (95) ParamList")
    fun paramList() {
        token(",", XPathTokenType.COMMA)
        token("...", XPathTokenType.ELLIPSIS)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (100) ValidateMode")
    fun validateMode() {
        token("lax", XQueryTokenType.K_LAX)
        token("strict", XQueryTokenType.K_STRICT)
        token("full", XQueryTokenType.K_FULL)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (103) SchemaWildcardTest")
    fun schemaWildcardTest() {
        token("schema-wildcard", XQueryTokenType.K_SCHEMA_WILDCARD)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (104) ModelGroupTest")
    fun modelGroupTest() {
        token("model-group", XQueryTokenType.K_MODEL_GROUP)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (105) UsingDecl")
    fun usingDecl() {
        token("using", XPathTokenType.K_USING)
        token("namespace", XPathTokenType.K_NAMESPACE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (116) TypeAlias")
    fun typeAlias() {
        token("~", XPathTokenType.TYPE_ALIAS)

        token("type", XPathTokenType.K_TYPE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (117) LambdaFunctionExpr")
    fun lambdaFunctionExpr() {
        token("_{", XPathTokenType.LAMBDA_FUNCTION)
        token("}", XPathTokenType.BLOCK_CLOSE)

        token("_", XPathTokenType.K__)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (119) ParamRef")
    fun paramRef() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (121) ForMemberClause")
    fun forMemberClause() {
        token("for", XPathTokenType.K_FOR)
        token("member", XPathTokenType.K_MEMBER)
        token(",", XPathTokenType.COMMA)
    }
}
