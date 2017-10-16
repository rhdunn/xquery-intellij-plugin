/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.ast.xquery

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xquery.lang.Specification
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

data class XQueryVersionRef(val declaration: XQueryStringLiteral?, val version: Specification?) {
    fun getVersionOrDefault(project: Project): Specification {
        if (version == null) {
            val settings: XQueryProjectSettings = XQueryProjectSettings.getInstance(project)
            val product = settings.product
            val productVersion = settings.productVersion
            val xquery = settings.XQueryVersion
            if (product == null || productVersion == null || xquery == null)
                return XQuery.REC_1_0_20070123
            return XQuery.versionForXQuery(product, productVersion, xquery) ?: XQuery.REC_1_0_20070123
        }
        return version
    }
}

/**
 * An XQuery 1.0 `Module` node in the XQuery AST.
 *
 * The EBNF grammar used in this implementation of the AST differs slightly
 * from the XQuery AST. The EBNF grammar used is:
 *
 * <pre>
 *    Module        ::= LibraryModule | MainModule
 *    LibraryModule ::= VersionDecl? ModuleDecl Prolog
 *    MainModule    ::= VersionDecl? Prolog QueryBody
 * </pre>
 *
 * This simplifies the AST tree and makes it easier to reason what the
 * module type is from the [XQueryFile] node.
 *
 * Because the child nodes of a `Module` are only referenced from
 * the `Module` node in the grammar, the `Module` nodes
 * are stored as instances of the child nodes instead of as distinct nodes
 * themselves.
 *
 * In the case of an invalid XQuery file that only contains a
 * `VersionDecl` node, its parent is a `Module` node.
 * This is because there is not enough information to know what module type
 * the XQuery file is implementing.
 */
interface XQueryModule : PsiElement {
    val XQueryVersion: XQueryVersionRef
}
