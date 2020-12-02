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
package uk.co.reecedunn.intellij.plugin.core.ui

import com.intellij.ui.JBColor
import java.awt.Color
import javax.swing.border.Border
import javax.swing.border.EmptyBorder
import javax.swing.border.MatteBorder

object Borders {
    val TableHeaderBottom: Border = MatteBorder(0, 0, 1, 0, JBColor(Color(192, 192, 192), Color(63, 63, 63)))

    val ConsoleToolbarTop: Border = MatteBorder(1, 0, 0, 0, JBColor(Color(202, 202, 202), Color(53, 53, 53)))
    val ConsoleToolbarRight: Border = MatteBorder(0, 0, 0, 1, JBColor(Color(202, 202, 202), Color(53, 53, 53)))

    val TabPanel: Border = EmptyBorder(4, 5, 4, 5)

    val Details: Border = EmptyBorder(6, 6, 6, 6)
}
