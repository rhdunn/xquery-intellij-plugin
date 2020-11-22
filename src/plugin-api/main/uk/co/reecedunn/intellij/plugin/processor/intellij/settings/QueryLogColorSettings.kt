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
package uk.co.reecedunn.intellij.plugin.processor.intellij.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import uk.co.reecedunn.intellij.plugin.processor.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.intellij.resources.PluginFiles
import uk.co.reecedunn.intellij.plugin.processor.log.LogLevel
import javax.swing.Icon

class QueryLogColorSettings : ColorSettingsPage {
    override fun getDisplayName(): String = PluginApiBundle.message("query.log.color.settings.name")

    override fun getIcon(): Icon? = PlainTextFileType.INSTANCE.icon

    override fun getHighlighter(): SyntaxHighlighter = PlainSyntaxHighlighter()

    override fun getDemoText(): String = PluginFiles.ColorSettingsDemo

    @Suppress("PrivatePropertyName")
    private val ATTRIBUTE_DESCRIPTORS = arrayOf(
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.finest.name"), LogLevel.FINEST_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.finer.name"), LogLevel.FINER_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.fine.name"), LogLevel.FINE_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.debug.name"), LogLevel.DEBUG_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.config.name"), LogLevel.CONFIG_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.info.name"), LogLevel.INFO_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.notice.name"), LogLevel.NOTICE_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.warning.name"), LogLevel.WARNING_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.error.name"), LogLevel.ERROR_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.critical.name"), LogLevel.CRITICAL_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.alert.name"), LogLevel.ALERT_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.emergency.name"), LogLevel.EMERGENCY_KEY)
    )

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTRIBUTE_DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = arrayOf()

    @Suppress("PrivatePropertyName")
    private val ADDITIONAL_HIGHLIGHTING_TAGS = mutableMapOf(
        "finest" to LogLevel.FINEST_KEY,
        "finer" to LogLevel.FINER_KEY,
        "fine" to LogLevel.FINE_KEY,
        "debug" to LogLevel.DEBUG_KEY,
        "config" to LogLevel.CONFIG_KEY,
        "info" to LogLevel.INFO_KEY,
        "notice" to LogLevel.NOTICE_KEY,
        "warning" to LogLevel.WARNING_KEY,
        "error" to LogLevel.ERROR_KEY,
        "critical" to LogLevel.CRITICAL_KEY,
        "alert" to LogLevel.ALERT_KEY,
        "emergency" to LogLevel.EMERGENCY_KEY
    )

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey> {
        return ADDITIONAL_HIGHLIGHTING_TAGS
    }
}
