/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.xml

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue

fun PsiElement.toXmlAttributeValue(): XmlAttributeValue? {
    // Case #1: The file is an XML file.
    if (this is XmlAttributeValue) return this
    // Case #2: The file is an injected language file.
    return PsiTreeUtil.getContextOfType(this, XmlAttributeValue::class.java)
}

val XmlAttributeValue.attribute: XmlAttribute? get() = parent as? XmlAttribute

fun XmlAttribute.eqname(namespaces: Map<String, String>): String {
    val prefix = namespaces.entries.find { (_, value) -> value == namespace } ?: return "Q{$namespace}$localName"
    return "${prefix.key}:$localName"
}
