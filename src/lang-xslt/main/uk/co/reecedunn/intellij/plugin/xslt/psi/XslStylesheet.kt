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

private const val XSLT_NAMESPACE = "http://www.w3.org/1999/XSL/Transform"

fun PsiElement.isXslStylesheet(): Boolean {
    val file = containingFile as? XmlFile ?: return false
    if (file.rootTag?.namespace != XSLT_NAMESPACE) return false
    return file.rootTag?.localName == "stylesheet" || file.rootTag?.localName == "transform"
}
