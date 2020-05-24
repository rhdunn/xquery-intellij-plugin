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

import java.awt.Color
import javax.swing.border.EmptyBorder
import javax.swing.border.MatteBorder

object Borders {
    val TableHeaderBottom = MatteBorder(0, 0, 1, 0, Color(192, 192, 192))

    val ConsoleToolbarTop = MatteBorder(1, 0, 0, 0, Color(202, 202, 202))
    val ConsoleToolbarRight = MatteBorder(0, 0, 0, 1, Color(202, 202, 202))

    val TabPanel = EmptyBorder(4, 5, 4, 5)

    val EndpointDetails = EmptyBorder(6, 6, 6, 6)
}
