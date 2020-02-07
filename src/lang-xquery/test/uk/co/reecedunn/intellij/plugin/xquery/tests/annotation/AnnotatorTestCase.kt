/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.annotation

import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl
import com.intellij.compat.lang.annotation.runAnnotatorWithContext
import com.intellij.lang.ASTNode
import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.annotation.*
import com.intellij.lang.annotation.Annotation
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AnnotatorTestCase :
    ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition, XPathParserDefinition) {

    @BeforeAll
    override fun setUp() {
        super.setUp()
        myProject.registerService(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XPath, XPathASTFactory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XQuery, XQueryASTFactory())
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    private fun annotateTree(node: ASTNode, annotationHolder: AnnotationHolder, annotator: Annotator) {
        if (node is CompositeElement) {
            annotationHolder.runAnnotatorWithContext(node.psi, annotator)
        } else if (node is LeafPsiElement) {
            annotationHolder.runAnnotatorWithContext(node, annotator)
        }

        for (child in node.getChildren(null)) {
            annotateTree(child, annotationHolder, annotator)
        }
    }

    internal fun annotateTree(file: XQueryModule, annotator: Annotator): List<Annotation> {
        @Suppress("UnstableApiUsage") val annotationHolder = AnnotationHolderImpl(AnnotationSession(file))
        annotateTree(file.node, annotationHolder, annotator)
        return annotationHolder
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
