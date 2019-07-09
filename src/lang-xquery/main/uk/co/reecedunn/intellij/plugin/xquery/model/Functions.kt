/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.model

import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.functions.op_qname_equal
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*

private fun XQueryProlog.staticallyKnownFunctions(name: XsQNameValue): Sequence<XPathFunctionDeclaration?> {
    return annotatedDeclarations<XPathFunctionDeclaration>().filter { function ->
        val functionName = function?.functionName
        // NOTE: Opening the context menu on a call to MarkLogic's `xdmp:version()`
        // is slow (~10 seconds) when just checking the expanded QName, so check
        // local-name first ...
        if (functionName?.let { it.localName?.data == name.localName?.data } == true) {
            // ... then check the expanded QName namespace.
            functionName.expand().find { op_qname_equal(it, name) } != null
        } else {
            false
        }
    }
}

fun XPathEQName.staticallyKnownFunctions(): Sequence<XPathFunctionDeclaration> {
    return importedPrologsForQName().flatMap { (name, prolog) ->
        prolog.staticallyKnownFunctions(name!!)
    }.filterNotNull()
}

fun XQueryProlog.staticallyKnownFunctions(): Sequence<XPathFunctionDeclaration?> {
    return importedPrologs().flatMap { prolog ->
        prolog.annotatedDeclarations<XPathFunctionDeclaration>()
    }.filter { decl -> decl?.functionName != null }
}
