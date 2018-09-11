/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDefaultNamespaceDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog

class XQueryPrologPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XQueryProlog,
        XPathStaticContext {

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedDefaultElementOrTypeNamespace.invalidate()
        cachedDefaultFunctionNamespace.invalidate()
        cachedVariables.invalidate()
    }

    override val defaultElementOrTypeNamespace get(): Sequence<XsAnyUriValue> =
        cachedDefaultElementOrTypeNamespace.get() ?: emptySequence()

    private val cachedDefaultElementOrTypeNamespace = CacheableProperty {
        children().reversed()
            .filterIsInstance<XPathDefaultNamespaceDeclaration>()
            .map { decl -> if (decl.namespaceType == XPathNamespaceType.DefaultElementOrType) decl.namespaceUri else null }
            .filterNotNull() `is` Cacheable
    }

    override val defaultFunctionNamespace get(): Sequence<XsAnyUriValue> =
        cachedDefaultFunctionNamespace.get() ?: emptySequence()

    private val cachedDefaultFunctionNamespace = CacheableProperty {
        children().reversed()
            .filterIsInstance<XPathDefaultNamespaceDeclaration>()
            .map { decl -> if (decl.namespaceType == XPathNamespaceType.DefaultFunction) decl.namespaceUri else null }
            .filterNotNull() `is` Cacheable
    }

    override val variables get(): Sequence<XPathVariableDeclaration> =
        cachedVariables.get() ?: emptySequence()

    private val cachedVariables = CacheableProperty {
        children().reversed().filterIsInstance<XQueryAnnotatedDecl>().map { decl ->
            decl.children().filterIsInstance<XPathVariableDeclaration>().firstOrNull()
        }.filterNotNull().filter { variable -> variable.variableName != null } `is` Cacheable
    }
}
