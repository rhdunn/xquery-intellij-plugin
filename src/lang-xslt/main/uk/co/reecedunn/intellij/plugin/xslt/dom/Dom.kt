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
package uk.co.reecedunn.intellij.plugin.xslt.dom

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xml.DomManager
import uk.co.reecedunn.intellij.plugin.xslt.ast.XslDomElement

private val INTELLIJ_XPATH_PLUGIN_ID = PluginId.getId("XPathView")

fun isIntellijXPathPluginEnabled(): Boolean {
    return PluginManager.getPlugin(INTELLIJ_XPATH_PLUGIN_ID)?.isEnabled == true
}

fun XmlTag.xslt(): XslDomElement? {
    return DomManager.getDomManager(project).getDomElement(this) as? XslDomElement
}

fun XmlTag.xsltFile(): XslDomElement? {
    return (containingFile as XmlFile).rootTag?.xslt()
}
