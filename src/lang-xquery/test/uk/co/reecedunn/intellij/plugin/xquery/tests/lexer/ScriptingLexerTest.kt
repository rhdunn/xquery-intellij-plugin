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

@DisplayName("XQuery Scripting Extensions 1.0 - Lexer")
class ScriptingLexerTest : LexerTestCase() {
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
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (24) VarDecl")
    fun varDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("unassignable", XQueryTokenType.K_UNASSIGNABLE)
        token("assignable", XQueryTokenType.K_ASSIGNABLE)
        token("variable", XQueryTokenType.K_VARIABLE)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token(":=", XPathTokenType.ASSIGN_EQUAL)
        token("external", XQueryTokenType.K_EXTERNAL)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (26) FunctionDecl")
    fun functionDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("function", XPathTokenType.K_FUNCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("as", XPathTokenType.K_AS)
        token("external", XQueryTokenType.K_EXTERNAL)

        token("sequential", XQueryTokenType.K_SEQUENTIAL)
        token("simple", XQueryTokenType.K_SIMPLE)
        token("updating", XQueryTokenType.K_UPDATING)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (32) ApplyExpr")
    fun applyExpr() {
        token(";", XQueryTokenType.SEPARATOR)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (33) ConcatExpr")
    fun concatExpr() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (153) BlockExpr")
    fun blockExpr() {
        token("block", XQueryTokenType.K_BLOCK)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (154) Block")
    fun block() {
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (155) BlockDecls")
    fun blockDecls() {
        token(";", XQueryTokenType.SEPARATOR)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (156) BlockVarDecl")
    fun blockVarDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token(":=", XPathTokenType.ASSIGN_EQUAL)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (158) AssignmentExpr")
    fun assignmentExpr() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token(":=", XPathTokenType.ASSIGN_EQUAL)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (159) ExitExpr")
    fun exitExpr() {
        token("exit", XQueryTokenType.K_EXIT)
        token("returning", XQueryTokenType.K_RETURNING)
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (160) WhileExpr")
    fun whileExpr() {
        token("while", XQueryTokenType.K_WHILE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }
}
