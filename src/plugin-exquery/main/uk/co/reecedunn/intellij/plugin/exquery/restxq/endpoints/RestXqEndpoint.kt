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
package uk.co.reecedunn.intellij.plugin.exquery.restxq.endpoints

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import uk.co.reecedunn.intellij.microservices.endpoints.Endpoint
import uk.co.reecedunn.intellij.plugin.intellij.resources.EXQueryIcons
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.model.expand
import javax.swing.Icon
import javax.swing.JComponent

class RestXqEndpoint(private val endpoint: XdmFunctionDeclaration) : Endpoint(), DataProvider {
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = EXQueryIcons.RESTXQ.Endpoint

    // endregion
    // region Endpoint

    override val details: JComponent? = null

    override val reference: PsiReference? = (endpoint as PsiElement).reference

    override val element: PsiElement = endpoint as PsiElement

    override val method: String? = rest?.methods?.joinToString(" ")

    override val path: String? = rest?.path

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? = when (dataId) {
        CommonDataKeys.PSI_ELEMENT.name -> endpoint as PsiElement
        else -> null
    }

    // endregion
    // region RestXqEndpoint

    val rest: RestXqAnnotations?
        get() {
            val annotations = endpoint.annotations
                .filter { annotation ->
                    annotation.name?.expand()?.find { it.namespace?.data == RESTXQ_NAMESPACE } != null
                }
            return annotations.takeIf { it.any() }?.let { RestXqAnnotations(it.toList()) }
        }

    companion object {
        const val RESTXQ_NAMESPACE = "http://exquery.org/ns/restxq"
    }

    // endregion
}
