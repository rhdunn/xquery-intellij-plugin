// Copyright (C) 2016-2018, 2021, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.ui.IconManager
import com.intellij.util.BitUtil
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAccessLevel
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl
import javax.swing.Icon

open class XQueryAnnotatedDeclPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryAnnotatedDecl {
    // region ElementBase

    override fun getElementIcon(flags: Int): Icon? = when (this) {
        is ItemPresentation -> getIcon(false)?.let { icon ->
            val isLocked = BitUtil.isSet(flags, ICON_FLAG_READ_STATUS) && !isWritable
            val baseIcon = IconManager.getInstance().createLayeredIcon(this, icon, if (isLocked) FLAGS_LOCKED else 0)
            if (BitUtil.isSet(flags, ICON_FLAG_VISIBILITY)) {
                accessLevel.icon?.let { baseIcon.setIcon(it, 1) }
            }
            baseIcon
        }
        else -> null
    }

    // endregion
    // region XdmAnnotatedDeclaration

    override val annotations: Sequence<XdmAnnotation>
        get() = children().filterIsInstance<XdmAnnotation>()

    override val accessLevel: XpmAccessLevel
        get() = XpmAccessLevel.get(this)

    // endregion
}
