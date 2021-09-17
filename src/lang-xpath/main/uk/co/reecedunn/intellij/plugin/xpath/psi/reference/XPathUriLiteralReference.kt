/*
 * Copyright (C) 2016-2017, 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.resolve
import uk.co.reecedunn.intellij.plugin.xpm.module.resolveUri

class XPathUriLiteralReference(element: XPathUriLiteral, range: TextRange) :
    PsiReferenceBase<XPathUriLiteral>(element, range) {

    override fun resolve(): PsiElement? {
        val uri = element as XsAnyUriValue
        return uri.resolve() ?: uri.resolveUri()
    }

    override fun getVariants(): Array<Any> = arrayOf()
}
