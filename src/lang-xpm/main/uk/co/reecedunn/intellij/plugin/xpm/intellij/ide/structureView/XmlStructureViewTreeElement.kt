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
package uk.co.reecedunn.intellij.plugin.xpm.intellij.ide.structureView

import com.intellij.ide.structureView.impl.xml.XmlTagTreeElement
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.xpm.psi.shadow.XpmShadowPsiElementFactory
import javax.swing.Icon

class XmlStructureViewTreeElement(tag: XmlTag) : XmlTagTreeElement(tag) {
    override fun getPresentableText(): String? {
        val element = element ?: return super.getPresentableText()
        val id = toCanonicalForm(element.getAttributeValue("id") ?: element.getAttributeValue("name"))
        return id?.let { "$id [${element.name}]" } ?: element.name // Fix IDEA-247202
    }

    override fun getIcon(open: Boolean): Icon? {
        return element?.let { XpmShadowPsiElementFactory.create(it) ?: it }?.let {
            val file = element as? PsiFile
            val flags = when {
                file == null || !file.isWritable -> Iconable.ICON_FLAG_READ_STATUS or Iconable.ICON_FLAG_VISIBILITY
                else -> Iconable.ICON_FLAG_READ_STATUS
            }
            return it.getIcon(flags)
        }
    }
}
