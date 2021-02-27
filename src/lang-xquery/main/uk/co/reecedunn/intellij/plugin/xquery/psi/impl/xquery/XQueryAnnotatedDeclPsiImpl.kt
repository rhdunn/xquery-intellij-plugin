/*
 * Copyright (C) 2016-2018, 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.ui.IconManager
import com.intellij.util.BitUtil
import com.intellij.util.PlatformIcons
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmAnnotated
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl
import javax.swing.Icon

open class XQueryAnnotatedDeclPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryAnnotatedDecl {
    // region ElementBase

    override fun getElementIcon(flags: Int): Icon? = when (this) {
        is ItemPresentation -> {
            val icon = getIcon(false)
            val isLocked = BitUtil.isSet(flags, ICON_FLAG_READ_STATUS) && !isWritable
            val baseIcon = IconManager.getInstance().createLayeredIcon(this, icon, if (isLocked) FLAGS_LOCKED else 0)
            if (BitUtil.isSet(flags, ICON_FLAG_VISIBILITY)) when (isPublic) {
                true -> baseIcon.setIcon(PlatformIcons.PUBLIC_ICON, 1)
                false -> baseIcon.setIcon(PlatformIcons.PRIVATE_ICON, 1)
            }
            baseIcon
        }
        else -> null
    }

    // endregion
    // region XdmAnnotatedDeclaration

    override val annotations: Sequence<XdmAnnotation>
        get() = children().filterIsInstance<XdmAnnotation>()

    override val isPublic: Boolean
        get() = annotation(XpmAnnotated.PRIVATE) == null

    // endregion
}
