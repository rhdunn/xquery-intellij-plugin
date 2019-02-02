/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import uk.co.reecedunn.intellij.plugin.core.xml.qname
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.XsltSpec
import javax.xml.namespace.QName

private const val XSL_NAMESPACE = "http://www.w3.org/1999/XSL/Transform"

private val XSL_ROOT_ELEMENTS = listOf(
    QName(XSL_NAMESPACE, "stylesheet"),
    QName(XSL_NAMESPACE, "transform")
)

fun PsiElement.isXslStylesheet(): Boolean {
    val file = containingFile as? XmlFile ?: return false
    return file.rootTag?.let { XSL_ROOT_ELEMENTS.contains(it.qname()) } ?: false
}

fun PsiElement.getXslVersion(): Version? {
    val file = containingFile as? XmlFile ?: return null
    val version = file.rootTag?.getAttribute("version", "")?.value
    return when (version) {
        "1.0" -> XsltSpec.REC_1_0_19991116
        "2.0" -> XsltSpec.REC_2_0_20070123
        "3.0" -> XsltSpec.REC_3_0_20170608
        else -> null
    }
}
