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
import uk.co.reecedunn.intellij.plugin.processor.log.LogFileContentType
import javax.swing.Icon

class QueryLogColorSettings : ColorSettingsPage {
    override fun getDisplayName(): String = PluginApiBundle.message("query.log.color.settings.name")

    override fun getIcon(): Icon? = PlainTextFileType.INSTANCE.icon

    override fun getHighlighter(): SyntaxHighlighter = PlainSyntaxHighlighter()

    override fun getDemoText(): String = PluginFiles.ColorSettingsDemo

    @Suppress("PrivatePropertyName")
    private val ATTRIBUTE_DESCRIPTORS = arrayOf(
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.datetime.name"), LogFileContentType.DATE_TIME_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.verbose.name"), LogFileContentType.VERBOSE_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.finest.name"), LogFileContentType.FINEST_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.finer.name"), LogFileContentType.FINER_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.fine.name"), LogFileContentType.FINE_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.debug.name"), LogFileContentType.DEBUG_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.info.name"), LogFileContentType.INFO_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.config.name"), LogFileContentType.CONFIG_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.ok.name"), LogFileContentType.OK_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.request.name"), LogFileContentType.REQUEST_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.warning.name"), LogFileContentType.WARNING_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.notice.name"), LogFileContentType.NOTICE_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.error.name"), LogFileContentType.ERROR_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.critical.name"), LogFileContentType.CRITICAL_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.alert.name"), LogFileContentType.ALERT_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.fatal.name"), LogFileContentType.FATAL_KEY),
        AttributesDescriptor(PluginApiBundle.message("query.log.color.settings.emergency.name"), LogFileContentType.EMERGENCY_KEY)
    )

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTRIBUTE_DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = arrayOf()

    @Suppress("PrivatePropertyName")
    private val ADDITIONAL_HIGHLIGHTING_TAGS = mutableMapOf(
        "datetime" to LogFileContentType.DATE_TIME_KEY,
        "finest" to LogFileContentType.FINEST_KEY,
        "finer" to LogFileContentType.FINER_KEY,
        "fine" to LogFileContentType.FINE_KEY,
        "debug" to LogFileContentType.DEBUG_KEY,
        "config" to LogFileContentType.CONFIG_KEY,
        "info" to LogFileContentType.INFO_KEY,
        "notice" to LogFileContentType.NOTICE_KEY,
        "warning" to LogFileContentType.WARNING_KEY,
        "error" to LogFileContentType.ERROR_KEY,
        "critical" to LogFileContentType.CRITICAL_KEY,
        "alert" to LogFileContentType.ALERT_KEY,
        "emergency" to LogFileContentType.EMERGENCY_KEY,
        "fatal" to LogFileContentType.FATAL_KEY,
        "ok" to LogFileContentType.OK_KEY,
        "request" to LogFileContentType.REQUEST_KEY
    )

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey> {
        return ADDITIONAL_HIGHLIGHTING_TAGS
    }
}
