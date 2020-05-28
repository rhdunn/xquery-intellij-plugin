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

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.microservices.endpoints.Endpoint
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.core.xml.ancestors
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.resources.MarkLogicBundle
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.lang.Rewriter
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.reference.ModuleUriElementReference
import java.awt.Color
import javax.swing.Icon
import javax.swing.JPanel

class RewriterEndpoint(private val endpoint: XmlTag) : Endpoint(), DataProvider {
    // region ItemPresentation

    override fun getLocationString(): String? = dispatch.split("/").lastOrNull()

    override fun getIcon(unused: Boolean): Icon? = MarkLogicIcons.Rewriter.Endpoint

    // endregion
    // region Endpoint

    override val details: JPanel
        get() = detailsPanel {
            row /* dispatch / set-error-handler / set-path */ {
                label(column.vgap()) {
                    text = when (endpoint.localName) {
                        "dispatch" -> MarkLogicBundle.message("endpoints.rewriter.dispatch.label")
                        "set-error-handler" -> MarkLogicBundle.message("endpoints.rewriter.set-error-handler.label")
                        "set-path" -> MarkLogicBundle.message("endpoints.rewriter.set-path.label")
                        else -> null
                    }
                    foreground = Color.GRAY
                }
                label(dispatch, column.hgap().vgap())
            }
            row /* match-path */ {
                label(MarkLogicBundle.message("endpoints.rewriter.path.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(paths, column.hgap().vgap())
            }
            row /* match-method */ {
                label(MarkLogicBundle.message("endpoints.rewriter.method.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(method, column.hgap().vgap())
            }
            row /* match-accept */ {
                label(MarkLogicBundle.message("endpoints.rewriter.accept.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(accept, column.hgap().vgap())
            }
            row /* match-content-type */ {
                label(MarkLogicBundle.message("endpoints.rewriter.content-type.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(contentType, column.hgap().vgap())
            }
            row /* match-cookie */ {
                label(MarkLogicBundle.message("endpoints.rewriter.cookie.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(cookie, column.hgap().vgap())
            }
            row /* match-execute-privilege */ {
                label(MarkLogicBundle.message("endpoints.rewriter.execute-privilege.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(executePrivilege, column.hgap().vgap())
            }
            row /* match-header */ {
                label(MarkLogicBundle.message("endpoints.rewriter.header.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(header, column.hgap().vgap())
            }
            row /* match-query-param */ {
                label(MarkLogicBundle.message("endpoints.rewriter.query-param.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(queryParam, column.hgap().vgap())
            }
            row /* match-role */ {
                label(MarkLogicBundle.message("endpoints.rewriter.role.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(role, column.hgap().vgap())
            }
            row /* match-user */ {
                label(MarkLogicBundle.message("endpoints.rewriter.user.label"), column.vgap()) {
                    foreground = Color.GRAY
                }
                label(user, column.hgap().vgap())
            }
        }

    override val reference: PsiReference?
        get() = when {
            endpoint.value.text.isBlank() -> null
            else -> ModuleUriElementReference(endpoint)
        }

    override val element: PsiElement = endpoint

    override val method: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-method").firstOrNull()?.getAttributeValue("any-of")

    override val path: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-path").firstOrNull()?.let { matchPath ->
            val matches = matchPath.getAttributeValue("matches")
            val anyOf = matchPath.getAttributeValue("any-of")?.split("\\s+".toRegex())?.getOrNull(0)
            val prefix = matchPath.getAttributeValue("prefix")
            matches ?: anyOf ?: prefix
        }

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? = when (dataId) {
        CommonDataKeys.PSI_ELEMENT.name -> endpoint
        else -> null
    }

    // endregion
    // region RewriterEndpoint

    private val dispatch: String get() = endpoint.value.text

    private val accept: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-accept").firstOrNull()?.getAttributeValue("any-of")

    private val contentType: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-content-type").firstOrNull()?.getAttributeValue("any-of")

    private val cookie: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-cookie").firstOrNull()?.getAttributeValue("name")

    private val executePrivilege: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-execute-privilege").firstOrNull()?.let { executePrivilege ->
            val anyOf = executePrivilege.getAttributeValue("any-of")
            val allOf = executePrivilege.getAttributeValue("all-of")
            anyOf ?: allOf
        }

    private val header: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-header").firstOrNull()?.let { matchPath ->
            val name = matchPath.getAttributeValue("name")
            val matches = matchPath.getAttributeValue("matches")
            name ?: matches
        }

    private val paths: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-path").firstOrNull()?.let { matchPath ->
            val matches = matchPath.getAttributeValue("matches")
            val anyOf = matchPath.getAttributeValue("any-of")
            val prefix = matchPath.getAttributeValue("prefix")
            matches ?: anyOf ?: prefix
        }

    private val queryParam: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-query-param").firstOrNull()?.getAttributeValue("name")

    private val role: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-role").firstOrNull()?.let { matchPath ->
            val anyOf = matchPath.getAttributeValue("any-of")
            val allOf = matchPath.getAttributeValue("all-of")
            anyOf ?: allOf
        }

    private val user: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-user").firstOrNull()?.let { matchPath ->
            val name = matchPath.getAttributeValue("name")
            val default = matchPath.getAttributeValue("default-user") == "true"
            if (default)
                MarkLogicBundle.message("endpoints.rewriter.default-user.label")
            else
                name
        }

    // endregion
}
