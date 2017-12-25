/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl

import com.intellij.psi.impl.source.tree.LeafPsiElement
import uk.co.reecedunn.intellij.plugin.xdm.XsNCName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmAtomicValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XmlNCNameImpl(text: CharSequence):
        LeafPsiElement(XQueryTokenType.NCNAME, text),
        XdmAtomicValue {

    override val staticType: XdmSequenceType = XsNCName

    override val lexicalRepresentation get(): String = text

    override fun toString(): String = "XmlNCNameImpl"
}
