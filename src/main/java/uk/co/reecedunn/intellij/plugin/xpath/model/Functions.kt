/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver

interface XPathFunctionReference {
    val functionName: XsQNameValue?

    val arity: Int
}

fun XPathEQName.staticallyKnownFunctions(): Sequence<XQueryFunctionDecl> {
    val prologs = resolvePrefixNamespace().map { ns ->
        (ns as? XQueryPrologResolver)?.prolog
    }.filterNotNull()

    return prologs.flatMap { prolog ->
        prolog.children().filterIsInstance<XQueryAnnotatedDecl>().map { annotation ->
            val function = annotation.children().filterIsInstance<XQueryFunctionDecl>().firstOrNull()
            val functionName = function?.children()?.filterIsInstance<XPathEQName>()?.firstOrNull()
            if (functionName?.equals(this) == true) {
                function
            } else {
                null
            }
        }
    }.filterNotNull()
}
