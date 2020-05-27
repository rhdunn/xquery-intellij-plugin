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
package uk.co.reecedunn.intellij.plugin.xpath.tests.intellij.lang

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lang.refactoring.XPathNamesValidator

@DisplayName("IntelliJ - Custom Language Support - Rename Refactoring - Names Validator")
class XPathNamesValidatorTest {
    @Test
    @DisplayName("empty")
    fun empty() {
        assertThat(XPathNamesValidator.isKeyword("", null), `is`(false))
        assertThat(XPathNamesValidator.isIdentifier("", null), `is`(false))
    }

    @Test
    @DisplayName("keyword")
    fun keyword() {
        assertThat(XPathNamesValidator.isKeyword("for", null), `is`(false)) // Is a valid identifier name.
        assertThat(XPathNamesValidator.isIdentifier("for", null), `is`(true))
    }

    @Test
    @DisplayName("NCName")
    fun ncname() {
        assertThat(XPathNamesValidator.isKeyword("abc2def.GHI-jkl\u0330mno", null), `is`(false))
        assertThat(XPathNamesValidator.isIdentifier("abc2def.GHI-jkl\u0330mno", null), `is`(true))
    }

    @Test
    @DisplayName("whitespace before")
    fun whitespaceBefore() {
        assertThat(XPathNamesValidator.isKeyword(" \t  test", null), `is`(false))
        assertThat(XPathNamesValidator.isIdentifier(" \t  test", null), `is`(true))
    }

    @Test
    @DisplayName("whitespace after")
    fun whitespaceAfter() {
        assertThat(XPathNamesValidator.isKeyword("test \t  ", null), `is`(false))
        assertThat(XPathNamesValidator.isIdentifier("test \t  ", null), `is`(true))
    }

    @Test
    @DisplayName("invalid NCName start character")
    fun invalidNCNameStartChar() {
        assertThat(XPathNamesValidator.isKeyword("123test", null), `is`(false))
        assertThat(XPathNamesValidator.isIdentifier("123test", null), `is`(false))
    }

    @Test
    @DisplayName("number (not an identifier)")
    fun number() {
        assertThat(XPathNamesValidator.isKeyword("123", null), `is`(false))
        assertThat(XPathNamesValidator.isIdentifier("123", null), `is`(false))
    }

    @Test
    @DisplayName("VarRef")
    fun varRef() {
        assertThat(XPathNamesValidator.isKeyword("\$test", null), `is`(false))
        assertThat(XPathNamesValidator.isIdentifier("\$test", null), `is`(false))
    }

    @Test
    @DisplayName("multiple tokens")
    fun multipleTokens() {
        assertThat(XPathNamesValidator.isKeyword("lorem ipsum", null), `is`(false))
        assertThat(XPathNamesValidator.isIdentifier("lorem ipsum", null), `is`(false))
    }
}
