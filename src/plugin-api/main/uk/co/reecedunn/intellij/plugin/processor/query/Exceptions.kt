/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.query

import com.intellij.lang.Language
import org.apache.http.conn.HttpHostConnectException
import uk.co.reecedunn.intellij.plugin.core.http.HttpStatusException
import uk.co.reecedunn.intellij.plugin.processor.intellij.resources.PluginApiBundle
import java.lang.UnsupportedOperationException
import java.lang.reflect.InvocationTargetException
import java.net.UnknownHostException

class ProcessTerminatedException : RuntimeException()

class ConfigurationFileNotFoundException(cause: Throwable) : RuntimeException("Configuration file not found.", cause)

class HostConnectionException(val host: String, cause: Throwable) : RuntimeException("Connection error.", cause)

class MissingHostNameException : RuntimeException("Missing hostname.")

class MissingJarFileException(jarType: String) : RuntimeException("Missing JAR file for $jarType.")

class UnsupportedJarFileException(jarType: String) : RuntimeException("Unsupported JAR file for $jarType.")

class UnsupportedQueryType(val language: Language) : RuntimeException("Unsupported query type: ${language.displayName}")

class UnsupportedSaxonConfiguration(val edition: String) : RuntimeException("Unsupported Saxon edition: $edition")

fun Throwable.toQueryUserMessage(): String = when (this) {
    is ConfigurationFileNotFoundException ->
        PluginApiBundle.message("processor.exception.configuration-file-not-found")
    is HostConnectionException ->
        PluginApiBundle.message("processor.exception.host-connection-error", host)
    is HttpHostConnectException ->
        PluginApiBundle.message("processor.exception.host-connection-error", host?.toHostString() ?: "")
    is HttpStatusException, is IllegalArgumentException, is IllegalStateException ->
        message!!
    is InvocationTargetException ->
        targetException.toQueryUserMessage()
    is MissingHostNameException ->
        PluginApiBundle.message("processor.exception.missing-hostname")
    is MissingJarFileException ->
        PluginApiBundle.message("processor.exception.missing-jar")
    is UnknownHostException ->
        PluginApiBundle.message("processor.exception.host-connection-error", message ?: "")
    is UnsupportedSaxonConfiguration ->
        PluginApiBundle.message("processor.exception.saxon.unsupported-edition", this.edition)
    is QueryError ->
        description!!
    is UnsupportedJarFileException ->
        PluginApiBundle.message("processor.exception.unsupported-jar")
    is UnsupportedOperationException ->
        PluginApiBundle.message("processor.exception.unsupported-operation")
    else ->
        message ?: javaClass.name
}
