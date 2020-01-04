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
package uk.co.reecedunn.intellij.plugin.marklogic.documentation

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCName
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

data class ApiDocsFunction(private val xml: XmlElement, override val namespace: XsAnyUriValue?) : XsQNameValue {
    // region apidoc:function

    val isBuiltin: Boolean by lazy { xml.attribute("type") == "builtin" }

    // endregion
    // region XsQNameValue

    override val prefix: XsNCName by lazy { XsNCName(xml.attribute("lib")!!, null as PsiElement?) }

    override val localName: XsNCName by lazy { XsNCName(xml.attribute("name")!!, null as PsiElement?) }

    override val isLexicalQName: Boolean = true

    override val element: PsiElement? = null

    // endregion
}
