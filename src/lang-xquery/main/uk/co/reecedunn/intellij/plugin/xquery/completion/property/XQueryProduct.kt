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
package uk.co.reecedunn.intellij.plugin.xquery.completion.property

import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProperty
import uk.co.reecedunn.intellij.plugin.intellij.lang.Product
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

object XQueryProduct : CompletionProperty {
    override fun computeProperty(element: PsiElement, context: ProcessingContext) {
        if (context[XPathCompletionProperty.XPATH_PRODUCT] == null) {
            context.put(XPathCompletionProperty.XPATH_PRODUCT, get(element))
        }
    }

    fun get(element: PsiElement): Product = XQueryProjectSettings.getInstance(element.project).product
}
