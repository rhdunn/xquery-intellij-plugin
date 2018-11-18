/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory

@DisplayName("IntelliJ - Custom Language Support - Implementing a Parser and PSI - XPath ASTFactory")
class XPathASTFactoryTest {
    @Test
    @DisplayName("createComposite")
    fun createComposite() {
        val factory = XPathASTFactory()
        assertThat(factory.createComposite(XPathTokenType.INTEGER_LITERAL)!!.javaClass.name, `is`(CompositeElement::class.java.name))
    }

    @Test
    @DisplayName("createLeaf")
    fun createLeaf() {
        val factory = XPathASTFactory()
        assertThat(factory.createLeaf(XPathTokenType.COMMA, ",")!!.javaClass.name, `is`(LeafPsiElement::class.java.name))
    }
}
