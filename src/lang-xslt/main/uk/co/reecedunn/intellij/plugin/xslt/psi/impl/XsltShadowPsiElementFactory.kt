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
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElement
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElementFactory
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.psi.impl.xslt.*
import javax.xml.namespace.QName

object XsltShadowPsiElementFactory : XpmShadowPsiElementFactory {
    override fun create(element: PsiElement, name: QName?): XpmShadowPsiElement? = when (name?.namespaceURI) {
        XSLT.NAMESPACE -> createXsltElement(element, name.localPart)
        else -> null
    }

    private fun createXsltElement(element: PsiElement, name: String): XpmShadowPsiElement? = when (name) {
        "apply-imports" -> XsltApplyImportsPsiImpl(element)
        "apply-templates" -> XsltApplyTemplatesPsiImpl(element)
        "attribute" -> XsltAttributePsiImpl(element)
        "attribute-set" -> XsltAttributeSetPsiImpl(element)
        "call-template" -> XsltCallTemplatePsiImpl(element)
        "choose" -> XsltChoosePsiImpl(element)
        "comment" -> XsltCommentPsiImpl(element)
        "copy" -> XsltCopyPsiImpl(element)
        "copy-of" -> XsltCopyOfPsiImpl(element)
        "decimal-format" -> XsltDecimalFormatPsiImpl(element)
        "element" -> XsltElementPsiImpl(element)
        "for-each" -> XsltForEachPsiImpl(element)
        "if" -> XsltIfPsiImpl(element)
        "import" -> XsltImportPsiImpl(element)
        "include" -> XsltIncludePsiImpl(element)
        "key" -> XsltKeyPsiImpl(element)
        "namespace-alias" -> XsltNamespaceAliasPsiImpl(element)
        "number" -> XsltNumberPsiImpl(element)
        "otherwise" -> XsltOtherwisePsiImpl(element)
        "param" -> XsltParamPsiImpl(element)
        "processing-instruction" -> XsltProcessingInstructionPsiImpl(element)
        "sort" -> XsltSortPsiImpl(element)
        "stylesheet" -> XsltStylesheetPsiImpl(element)
        "template" -> XsltTemplatePsiImpl(element)
        "text" -> XsltTextPsiImpl(element)
        "transform" -> XsltStylesheetPsiImpl(element)
        "value-of" -> XsltValueOfPsiImpl(element)
        "variable" -> XsltVariablePsiImpl(element)
        "when" -> XsltWhenPsiImpl(element)
        "with-param" -> XsltWithParamPsiImpl(element)
        else -> null
    }
}
