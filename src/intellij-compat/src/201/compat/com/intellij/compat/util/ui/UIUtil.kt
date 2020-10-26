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
package com.intellij.compat.util.ui

import java.awt.Color

object UIUtil {
    fun makeTransparent(color: Color, backgroundColor: Color, transparency: Double): Color? {
        val r: Int = makeTransparent(transparency, color.red, backgroundColor.red)
        val g: Int = makeTransparent(transparency, color.green, backgroundColor.green)
        val b: Int = makeTransparent(transparency, color.blue, backgroundColor.blue)
        return Color(r, g, b)
    }

    private fun makeTransparent(transparency: Double, channel: Int, backgroundChannel: Int): Int {
        val result = (backgroundChannel * (1 - transparency) + channel * transparency).toInt()
        return if (result < 0) 0 else java.lang.Math.min(result, 255)
    }
}
