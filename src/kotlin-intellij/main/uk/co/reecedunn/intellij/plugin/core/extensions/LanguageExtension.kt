// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.extensions

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer

fun <T> Project.registerExplicitExtension(
    instance: LanguageExtension<T>,
    language: Language,
    `object`: T
) {
    instance.addExplicitExtension(language, `object`!!)
    Disposer.register(this, {
        instance.removeExplicitExtension(language, `object`)
    })
}
