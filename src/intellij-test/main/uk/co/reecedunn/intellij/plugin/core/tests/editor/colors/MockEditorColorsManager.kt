/*
 * Copyright (C) 2021-2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.editor.colors

import com.intellij.compat.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme

class MockEditorColorsManager : EditorColorsManager() {
    override fun addColorsScheme(scheme: EditorColorsScheme): Unit = TODO()

    override fun getAllSchemes(): Array<EditorColorsScheme> = TODO()

    override fun setGlobalScheme(scheme: EditorColorsScheme?): Unit = TODO()

    override fun getGlobalScheme(): EditorColorsScheme = TODO()

    override fun getScheme(schemeName: String): EditorColorsScheme = TODO()

    override fun isDefaultScheme(scheme: EditorColorsScheme?): Boolean = TODO()

    override fun isUseOnlyMonospacedFonts(): Boolean = TODO()

    override fun setUseOnlyMonospacedFonts(b: Boolean): Unit = TODO()

    override fun isDarkEditor(): Boolean = false
}
