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
package uk.co.reecedunn.intellij.plugin.xslt.parser

import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestorsAndSelf
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XSLT

// region Standard Attribute :: xsl:expand-text

val XmlTag.expandText: Boolean
    get() = ancestorsAndSelf().filterIsInstance<XmlTag>().mapNotNull {
        when (it.namespace) {
            XSLT.NAMESPACE -> it.getAttribute("expand-text")?.yesOrNo
            else -> null
        }
    }.firstOrNull() ?: false

// endregion
// region Schema Type :: xsl:yes-or-no

val XmlAttribute.yesOrNo: Boolean?
    get() = when (value) {
        "no", "false", "0" -> false
        "yes", "true", "1" -> true
        else -> null
    }

// endregion
