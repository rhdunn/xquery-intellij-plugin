/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lang

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Parameter Info - XPath ParameterInfoHandler")
private class XPathParameterInfoHandlerTest : ParserTestCase() {
    @Test
    @DisplayName("find element for parameter info")
    fun findElementForParameterInfo() {
        val context = createParameterInfoContext("abs(2)", 4)
        val item = XPathParameterInfoHandler.findElementForParameterInfo(context)
        assertThat(item, `is`(sameInstance(context.file.walkTree().filterIsInstance<XPathFunctionCall>().first())))

        assertThat(context.highlightedElement, `is`(nullValue()))
        assertThat(context.itemsToShow, `is`(nullValue()))
        assertThat(context.parameterListStart, `is`(0))
    }
}
