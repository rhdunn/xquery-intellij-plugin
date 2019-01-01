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
package uk.co.reecedunn.intellij.plugin.core.ui

import java.awt.Color
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.border.MatteBorder
import javax.swing.border.TitledBorder

class LabelledDivider(title: String) : JPanel() {
    init {
        border = TitledBorder(VERTICAL_DIVIDER, title)
        updateHeight()
    }

    override fun updateUI() {
        super.updateUI()
        updateHeight()
    }

    private fun updateHeight() {
        // NOTE: updateHeight may be called via updateUI before border is set in the constructor.
        val border = border as? TitledBorder ?: return
        val height = (border.titleFont.size * 1.5).toInt()
        minimumSize = Dimension(0, height)
        maximumSize = Dimension(0, height)
        preferredSize = Dimension(0, height)
    }

    companion object {
        private val VERTICAL_DIVIDER = MatteBorder(1, 0, 0, 0, Color(205, 205, 205))
    }
}
