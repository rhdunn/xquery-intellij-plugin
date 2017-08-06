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
package uk.co.reecedunn.intellij.plugin.xquery.resolve.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import uk.co.reecedunn.intellij.plugin.core.extensions.walkTree
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryEQName
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver

class XQueryVariableNameReference(element: XQueryEQName, range: TextRange) : PsiReferenceBase<XQueryEQName>(element, range) {
    override fun resolve(): PsiElement? {
        val name = element
        return name.walkTree().filterIsInstance<XQueryVariableResolver>().map { e ->
            val resolved = e.resolveVariable(name)
            if (resolved.isDefined) {
                resolved.get().variable
            } else {
                null
            }
        }.filterNotNull().firstOrNull()
    }

    override fun getVariants(): Array<Any> {
        return arrayOf()
    }
}
