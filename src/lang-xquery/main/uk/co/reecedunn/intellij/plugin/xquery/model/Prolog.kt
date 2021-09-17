/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestorsAndSelf
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.staticallyKnownNamespaces
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryLibraryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryMainModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog

fun <Decl : Any> XQueryProlog.annotatedDeclarations(klass: Class<Decl>, reversed: Boolean = true): Sequence<Decl> {
    return when {
        reversed -> reverse(children()).filterIsInstance(klass)
        else -> children().filterIsInstance(klass)
    }
}

inline fun <reified Decl : Any> XQueryProlog.annotatedDeclarations(reversed: Boolean = true): Sequence<Decl> {
    return annotatedDeclarations(Decl::class.java, reversed)
}

fun PsiElement.fileProlog(): XQueryProlog? {
    val module = when (this) {
        is XQueryModule -> mainOrLibraryModule
        else -> ancestorsAndSelf().filter { it is XQueryMainModule || it is XQueryLibraryModule }.firstOrNull()
    }
    return (module as? XQueryPrologResolver)?.prolog?.firstOrNull()
}

private class PrologVisitor(
    private val prologs: Iterator<Pair<XsQNameValue?, XQueryProlog>>
) : Iterator<Pair<XsQNameValue?, XQueryProlog>> {
    var item: Pair<XsQNameValue?, XQueryProlog>? = null
    val visited: MutableSet<Pair<String, String>> = mutableSetOf()

    override fun hasNext(): Boolean {
        while (prologs.hasNext()) {
            item = prologs.next()

            val ns = item!!.first?.namespace?.data ?: ""
            val file = item!!.second.resourcePath()
            val visit = ns to file
            if (!visited.contains(visit)) {
                visited.add(visit)
                return true
            }
        }
        return false
    }

    override fun next(): Pair<XsQNameValue?, XQueryProlog> = item!!
}

fun XQueryProlog.importedPrologs(): Sequence<XQueryProlog> = sequenceOf(
    sequenceOf(this),
    staticallyKnownNamespaces().flatMap { ns ->
        (ns.namespaceUri?.element?.parent as? XQueryPrologResolver)?.prolog ?: emptySequence()
    }
).flatten()

fun XsQNameValue.importedPrologsForQName(): Sequence<Pair<XsQNameValue?, XQueryProlog>> {
    val thisProlog = element?.fileProlog()
    val prologs = this.expand().flatMap { name ->
        sequenceOf(
            // 1. Imported modules in the current module.
            thisProlog?.let {
                reverse(it.children()).flatMap { child ->
                    if (child is XpmNamespaceDeclaration && child.namespaceUri?.data == name.namespace?.data)
                        (child as? XQueryPrologResolver)?.prolog?.map { prolog -> name to prolog } ?: emptySequence()
                    else
                        emptySequence()
                }
            } ?: emptySequence(),

            // 2. The current module.
            thisProlog?.let { sequenceOf(name to it) } ?: emptySequence(),

            // 3. The built-in module.
            (name.namespace?.element?.parent as? XQueryPrologResolver)?.prolog?.map { prolog ->
                name to prolog
            } ?: emptySequence()
        ).flatten()
    }
    return PrologVisitor(prologs.iterator()).asSequence()
}
