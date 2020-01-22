/*
 * Copyright (C) 2019 Reece H. Dunn
 * Copyright 2000-2019 JetBrains s.r.o.
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
package uk.co.reecedunn.compat.testFramework

import com.intellij.openapi.extensions.*

abstract class PlatformLiteFixture : com.intellij.compat.testFramework.PlatformTestCase() {
    protected fun registerFileBasedIndex() {
        // Not needed for using the XML DOM on IntelliJ <= 2019.2
    }

    @Suppress("UnstableApiUsage")
    protected fun registerCodeStyleSettingsModifier() {
        try {
            val epClass = Class.forName("com.intellij.application.options.CodeStyleCachingUtil")
            val epname = epClass.getDeclaredField("CODE_STYLE_SETTINGS_MODIFIER_EP_NAME")
            epname.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            registerExtensionPoint(epname.get(null) as ExtensionPointName<Any>, epClass as Class<Any>)
        } catch (e: Exception) {
            // Don't register the extension point, as the associated class is not found.
        }
    }
}
