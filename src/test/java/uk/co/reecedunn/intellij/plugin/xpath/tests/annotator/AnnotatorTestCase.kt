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
package uk.co.reecedunn.intellij.plugin.xpath.tests.annotator

import com.intellij.lang.ASTNode
import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AnnotatorTestCase : ParsingTestCase<XQueryModule>("xqy", XQueryParserDefinition()) {
    @BeforeAll
    override fun setUp() {
        super.setUp()
        registerApplicationService(XQueryProjectSettings::class.java, XQueryProjectSettings())
        addExplicitExtension(LanguageASTFactory.INSTANCE, language!!, XQueryASTFactory())
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

    internal fun annotateTree(file: XQueryModule, annotator: Annotator): List<Annotation> {
        val annotationHolder = AnnotationCollector()
        annotateTree(file.node, annotationHolder, annotator)
        return annotationHolder.annotations
    }
}
