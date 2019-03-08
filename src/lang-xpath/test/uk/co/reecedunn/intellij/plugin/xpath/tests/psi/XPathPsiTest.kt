/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathDecimalLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathIntegerLiteral
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDecimalValue
import uk.co.reecedunn.intellij.plugin.xpath.model.XsIntegerValue
import uk.co.reecedunn.intellij.plugin.xpath.model.toInt
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase
import java.math.BigDecimal
import java.math.BigInteger

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XPath 3.1 - IntelliJ Program Structure Interface (PSI)")
private class XPathPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XPath 3.1 (3.1.1) Literals")
    internal inner class Literals {
        @Test
        @DisplayName("XPath 3.1 EBNF (113) IntegerLiteral")
        fun integerLiteral() {
            val literal = parse<XPathIntegerLiteral>("123")[0] as XsIntegerValue
            assertThat(literal.data, `is`(BigInteger.valueOf(123)))
            assertThat(literal.toInt(), `is`(123))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (114) DecimalLiteral")
        fun decimalLiteral() {
            val literal = parse<XPathDecimalLiteral>("12.34")[0] as XsDecimalValue
            assertThat(literal.data, `is`(BigDecimal(BigInteger.valueOf(1234), 2)))
        }
    }
}
