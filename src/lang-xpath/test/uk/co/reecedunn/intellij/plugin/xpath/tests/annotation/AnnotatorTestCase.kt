/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.annotation

import com.intellij.lang.ASTNode
import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.tests.annotation.AnnotationCollector
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AnnotatorTestCase :
    ParsingTestCase<XPath>("xqy", XPathParserDefinition) {

    @BeforeAll
    override fun setUp() {
        super.setUp()
        addExplicitExtension(
            LanguageASTFactory.INSTANCE, uk.co.reecedunn.intellij.plugin.intellij.lang.XPath, XPathASTFactory()
        )
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    private fun annotateTree(node: ASTNode, annotationHolder: AnnotationHolder, annotator: Annotator) {
        if (node is CompositeElement) {
            annotator.annotate(node.psi, annotationHolder)
        } else if (node is LeafPsiElement) {
            annotator.annotate(node, annotationHolder)
        }

        for (child in node.getChildren(null)) {
            annotateTree(child, annotationHolder, annotator)
        }
    }

    internal fun annotateTree(file: XPath, annotator: Annotator): List<Annotation> {
        val annotationHolder = AnnotationCollector()
        annotateTree(file.node, annotationHolder, annotator)
        return annotationHolder.annotations
    }

    fun info(
        annotation: Annotation, start: Int, end: Int, enforced: TextAttributes?, attributes: TextAttributesKey
    ) {
        assertThat(annotation.severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotation.startOffset, `is`(start))
        assertThat(annotation.endOffset, `is`(end))
        assertThat(annotation.message, `is`(nullValue()))
        if (enforced != null)
            assertThat(annotation.enforcedTextAttributes, `is`(enforced))
        else
            assertThat(annotation.enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotation.textAttributes, `is`(attributes))
    }

    fun error(annotation: Annotation, start: Int, end: Int, message: String) {
        assertThat(annotation.severity, `is`(HighlightSeverity.ERROR))
        assertThat(annotation.startOffset, `is`(start))
        assertThat(annotation.endOffset, `is`(end))
        assertThat(annotation.message, `is`(message))
        assertThat(annotation.enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotation.textAttributes, `is`(CodeInsightColors.ERRORS_ATTRIBUTES))
    }
}
