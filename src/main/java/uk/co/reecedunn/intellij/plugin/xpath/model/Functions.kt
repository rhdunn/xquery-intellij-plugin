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

import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.functions.op.op_qname_equal
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver

private fun XQueryProlog.staticallyKnownFunctions(name: XsQNameValue): Sequence<XQueryFunctionDecl?> {
    return children().filterIsInstance<XQueryAnnotatedDecl>().map { annotation ->
        val function = annotation.children().filterIsInstance<XQueryFunctionDecl>().firstOrNull()
        val functionName = function?.children()?.filterIsInstance<XsQNameValue>()?.firstOrNull()
        if (functionName?.expand()?.firstOrNull()?.let { op_qname_equal(it, name) } == true) {
            function
        } else {
            null
        }
    }
}

fun XPathEQName.staticallyKnownFunctions(): Sequence<XQueryFunctionDecl> {
    var thisProlog: XQueryProlog? =
        (ancestors().filter { element ->
            element is XQueryMainModule || element is XQueryLibraryModule
        }.first() as XQueryPrologResolver).prolog.firstOrNull()
    val functions = (this as XsQNameValue).expand().flatMap { name ->
        val prologs = (name.namespace?.element?.parent as? XQueryPrologResolver)?.prolog ?: emptySequence()
        prologs.flatMap { prolog ->
            if (prolog == thisProlog)
                thisProlog = null
            prolog.staticallyKnownFunctions(name)
        }
    }.toList()
    return if (thisProlog != null)
        sequenceOf(
            functions.asSequence(),
            (this as XsQNameValue).expand().flatMap { name -> thisProlog!!.staticallyKnownFunctions(name) }
        ).flatten().filterNotNull()
    else
        functions.asSequence().filterNotNull()
}
