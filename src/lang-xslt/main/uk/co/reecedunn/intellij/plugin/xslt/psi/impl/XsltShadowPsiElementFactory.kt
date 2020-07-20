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
package uk.co.reecedunn.intellij.plugin.xslt.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElement
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElementFactory
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.marklogic.MarkLogicCatchPsiImpl
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.marklogic.MarkLogicImportModulePsiImpl
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.marklogic.MarkLogicTryPsiImpl
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.saxon.*
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.xml.XsltDirElemConstructorPsiImpl
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.xslt.*
import javax.xml.namespace.QName

object XsltShadowPsiElementFactory : XpmShadowPsiElementFactory {
    // region XpmShadowPsiElementFactory

    override fun create(element: PsiElement, name: QName?): XpmShadowPsiElement? = when (name?.namespaceURI) {
        XSLT.NAMESPACE -> createXsltElement(element, name.localPart)
        XDMP_NAMESPACE -> createMarkLogicElement(element, name.localPart)
        SAXON_NAMESPACE -> createSaxonElement(element, name.localPart)
        SAXON6_NAMESPACE -> createSaxon6Element(element, name.localPart)
        else -> null
    }

    override fun createDefault(element: PsiElement): XpmShadowPsiElement? = when (element) {
        is XmlTag -> element.ancestors().filterIsInstance<XmlTag>().find { it.namespace == XSLT.NAMESPACE }?.let {
            XsltDirElemConstructorPsiImpl(element)
        }
        else -> null
    }

    // endregion
    // region XSLT Elements

    private fun createXsltElement(element: PsiElement, name: String): XpmShadowPsiElement? = when (name) {
        "accept" -> XsltAcceptPsiImpl(element)
        "accumulator" -> XsltAccumulatorPsiImpl(element)
        "accumulator-rule" -> XsltAccumulatorRulePsiImpl(element)
        "analyze-string" -> XsltAnalyzeStringPsiImpl(element)
        "apply-imports" -> XsltApplyImportsPsiImpl(element)
        "apply-templates" -> XsltApplyTemplatesPsiImpl(element)
        "assert" -> XsltAssertPsiImpl(element)
        "attribute" -> XsltAttributePsiImpl(element)
        "attribute-set" -> XsltAttributeSetPsiImpl(element)
        "break" -> XsltBreakPsiImpl(element)
        "call-template" -> XsltCallTemplatePsiImpl(element)
        "catch" -> XsltCatchPsiImpl(element)
        "character-map" -> XsltCharacterMapPsiImpl(element)
        "choose" -> XsltChoosePsiImpl(element)
        "comment" -> XsltCommentPsiImpl(element)
        "context-item" -> XsltContextItemPsiImpl(element)
        "copy" -> XsltCopyPsiImpl(element)
        "copy-of" -> XsltCopyOfPsiImpl(element)
        "decimal-format" -> XsltDecimalFormatPsiImpl(element)
        "document" -> XsltDocumentPsiImpl(element)
        "element" -> XsltElementPsiImpl(element)
        "evaluate" -> XsltEvaluatePsiImpl(element)
        "expose" -> XsltExposePsiImpl(element)
        "fallback" -> XsltFallbackPsiImpl(element)
        "for-each" -> XsltForEachPsiImpl(element)
        "for-each-group" -> XsltForEachGroupPsiImpl(element)
        "fork" -> XsltForkPsiImpl(element)
        "function" -> XsltFunctionPsiImpl(element)
        "global-context-item" -> XsltGlobalContextItemPsiImpl(element)
        "if" -> XsltIfPsiImpl(element)
        "import" -> XsltImportPsiImpl(element)
        "import-schema" -> XsltImportSchemaPsiImpl(element)
        "include" -> XsltIncludePsiImpl(element)
        "iterate" -> XsltIteratePsiImpl(element)
        "key" -> XsltKeyPsiImpl(element)
        "map" -> XsltMapPsiImpl(element)
        "map-entry" -> XsltMapEntryPsiImpl(element)
        "matching-substring" -> XsltMatchingSubstringPsiImpl(element)
        "merge" -> XsltMergePsiImpl(element)
        "merge-action" -> XsltMergeActionPsiImpl(element)
        "merge-key" -> XsltMergeKeyPsiImpl(element)
        "merge-source" -> XsltMergeSourcePsiImpl(element)
        "message" -> XsltMessagePsiImpl(element)
        "mode" -> XsltModePsiImpl(element)
        "namespace" -> XsltNamespacePsiImpl(element)
        "namespace-alias" -> XsltNamespaceAliasPsiImpl(element)
        "next-iteration" -> XsltNextIterationPsiImpl(element)
        "next-match" -> XsltNextMatchPsiImpl(element)
        "non-matching-substring" -> XsltNonMatchingSubstringPsiImpl(element)
        "number" -> XsltNumberPsiImpl(element)
        "on-completion" -> XsltOnCompletionPsiImpl(element)
        "on-empty" -> XsltOnEmptyPsiImpl(element)
        "on-non-empty" -> XsltOnNonEmptyPsiImpl(element)
        "otherwise" -> XsltOtherwisePsiImpl(element)
        "output" -> XsltOutputPsiImpl(element)
        "output-character" -> XsltOutputCharacterPsiImpl(element)
        "override" -> XsltOverridePsiImpl(element)
        "package" -> XsltPackagePsiImpl(element)
        "param" -> XsltParamPsiImpl(element)
        "perform-sort" -> XsltPerformSortPsiImpl(element)
        "preserve-space" -> XsltPreserveSpacePsiImpl(element)
        "processing-instruction" -> XsltProcessingInstructionPsiImpl(element)
        "result-document" -> XsltResultDocumentPsiImpl(element)
        "sequence" -> XsltSequencePsiImpl(element)
        "sort" -> XsltSortPsiImpl(element)
        "source-document" -> XsltSourceDocumentPsiImpl(element)
        "strip-space" -> XsltStripSpacePsiImpl(element)
        "stylesheet" -> XsltStylesheetPsiImpl(element)
        "template" -> XsltTemplatePsiImpl(element)
        "text" -> XsltTextPsiImpl(element)
        "transform" -> XsltStylesheetPsiImpl(element)
        "try" -> XsltTryPsiImpl(element)
        "use-package" -> XsltUsePackagePsiImpl(element)
        "value-of" -> XsltValueOfPsiImpl(element)
        "variable" -> XsltVariablePsiImpl(element)
        "when" -> XsltWhenPsiImpl(element)
        "where-populated" -> XsltWherePopulatedPsiImpl(element)
        "with-param" -> XsltWithParamPsiImpl(element)
        else -> null
    }

    // endregion
    // region MarkLogic XSLT Extension Elements

    private const val XDMP_NAMESPACE = "http://marklogic.com/xdmp"

    private fun createMarkLogicElement(element: PsiElement, name: String): XpmShadowPsiElement? = when (name) {
        "catch" -> MarkLogicCatchPsiImpl(element)
        "import-module" -> MarkLogicImportModulePsiImpl(element)
        "try" -> MarkLogicTryPsiImpl(element)
        else -> null
    }

    // endregion
    // region Saxon XSLT Extension Elements

    private const val SAXON_NAMESPACE = "http://saxon.sf.net/"
    private const val SAXON6_NAMESPACE = "http://icl.com/saxon"

    private fun createSaxonElement(element: PsiElement, name: String): XpmShadowPsiElement? = when (name) {
        "array" -> SaxonArrayPsiImpl(element)
        "array-member" -> SaxonArrayMemberPsiImpl(element)
        "assign" -> SaxonAssignPsiImpl(element)
        "change" -> SaxonChangePsiImpl(element)
        "deep-update" -> SaxonDeepUpdatePsiImpl(element)
        "delete" -> SaxonDeletePsiImpl(element)
        "do" -> SaxonDoPsiImpl(element)
        "doctype" -> SaxonDoctypePsiImpl(element)
        "entity-ref" -> SaxonEntityRefPsiImpl(element)
        "for-each-member" -> SaxonForEachMemberPsiImpl(element)
        "import-query" -> SaxonImportQueryPsiImpl(element)
        "insert" -> SaxonInsertPsiImpl(element)
        "item-type" -> SaxonItemTypePsiImpl(element)
        "rename" -> SaxonRenamePsiImpl(element)
        "replace" -> SaxonReplacePsiImpl(element)
        "tabulate-maps" -> SaxonTabulateMapsPsiImpl(element)
        "type-alias" -> SaxonItemTypePsiImpl(element)
        "update" -> SaxonUpdatePsiImpl(element)
        "while" -> SaxonWhilePsiImpl(element)
        else -> null
    }

    private fun createSaxon6Element(element: PsiElement, name: String): XpmShadowPsiElement? = when (name) {
        "output" -> XsltResultDocumentPsiImpl(element)
        else -> null
    }

    // endregion
}
