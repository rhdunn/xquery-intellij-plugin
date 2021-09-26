/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAttributeNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.resolve
import uk.co.reecedunn.intellij.plugin.xpm.module.resolveUri
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryMainModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryQueryBody

class ModuleUriReference(element: PsiElement, uriValue: String) : PsiReferenceBase<PsiElement>(element) {
    private val uri: XsAnyUriValue = XsAnyUri(uriValue, XdmUriContext.Location, XdmModuleType.XQUERY)

    private val module: PsiElement? by lazy {
        uri.resolve(element) ?: uri.resolveUri(element.project)
    }

    override fun resolve(): PsiElement? {
        val mainModule = module?.children()?.filterIsInstance<XQueryMainModule>()?.firstOrNull()
        val queryBody = mainModule?.children()?.filterIsInstance<XQueryQueryBody>()?.firstOrNull()
        return queryBody ?: module
    }

    override fun getVariants(): Array<Any> = arrayOf()

    companion object : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            val uriValue = getUriValue(element)
            return when {
                uriValue.isNullOrBlank() -> arrayOf()
                else -> arrayOf(ModuleUriReference(element, uriValue))
            }
        }

        private fun getUriValue(element: PsiElement): String? = when (element) {
            is XmlTag -> element.value.text
            is XdmAttributeNode -> element.stringValue
            is XQueryDirAttributeValue -> getUriValue(element.parent)
            else -> null
        }
    }
}
