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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.microservices.endpoints.Endpoint
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.reference.ModuleUriElementReference
import javax.swing.Icon

class RewriterEndpoint(private val endpoint: XmlTag) : Endpoint, ItemPresentation {
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = null

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String? = null

    // endregion
    // region Endpoint

    override val presentation: ItemPresentation get() = this

    override val reference: PsiReference?
        get() = when {
            endpoint.value.text.isBlank() -> null
            else -> ModuleUriElementReference(endpoint)
        }

    override val element: PsiElement = endpoint

    // endregion
}
