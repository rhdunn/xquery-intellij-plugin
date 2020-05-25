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
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.intellij.resources.EXQueryBundle
import uk.co.reecedunn.intellij.plugin.intellij.resources.EXQueryIcons
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.model.expand
import java.awt.Color
import javax.swing.Icon
import javax.swing.JComponent

class RestXqEndpoint(private val endpoint: XdmFunctionDeclaration) : Endpoint(), DataProvider {
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = EXQueryIcons.RESTXQ.Endpoint

    // endregion
    // region Endpoint

    override val details: JComponent?
        get() = panel {
            border = Borders.EndpointDetails
            row /* RESTXQ 3.2.1 Path Annotation */ {
                label(EXQueryBundle.message("endpoints.restxq.path.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(rest?.path, column.hgap().vgap())
            }
            row /* RESTXQ 3.2.2 Method Annotation */ {
                label(EXQueryBundle.message("endpoints.restxq.method.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(rest?.methods?.joinToString(" "), column.hgap().vgap())
            }
            row /* RESTXQ 3.2.3 Consumes Annotation */ {
                label(EXQueryBundle.message("endpoints.restxq.consumes.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(rest?.consumes?.joinToString(" "), column.hgap().vgap())
            }
            row /* RESTXQ 3.2.4 Produces Annotation */ {
                label(EXQueryBundle.message("endpoints.restxq.produces.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(rest?.produces?.joinToString(" "), column.hgap().vgap())
            }
            row /* RESTXQ 3.3.1 Query Parameters */ {
                label(EXQueryBundle.message("endpoints.restxq.query-params.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(rest?.queryParams?.joinToString(" "), column.hgap().vgap())
            }
            row /* RESTXQ 3.3.2 Form Parameters */ {
                label(EXQueryBundle.message("endpoints.restxq.form-params.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(rest?.formParams?.joinToString(" "), column.hgap().vgap())
            }
            row /* RESTXQ 3.3.3 HTTP Header Parameters */ {
                label(EXQueryBundle.message("endpoints.restxq.header-params.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(rest?.headerParams?.joinToString(" "), column.hgap().vgap())
            }
            row /* RESTXQ 3.3.4 Cookie Parameters */ {
                label(EXQueryBundle.message("endpoints.restxq.cookie-params.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(rest?.cookieParams?.joinToString(" "), column.hgap().vgap())
            }
            row {
                spacer(column.vertical())
                spacer(column.horizontal())
            }
        }

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

    val rest: RestXqAnnotations? get() = RestXqAnnotations.create(endpoint)

    // endregion
}
