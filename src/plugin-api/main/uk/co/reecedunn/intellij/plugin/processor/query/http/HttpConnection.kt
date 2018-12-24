/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.query.http

import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import uk.co.reecedunn.intellij.plugin.processor.query.ConnectionSettings
import uk.co.reecedunn.intellij.plugin.processor.query.MissingHostNameException
import java.io.Closeable

class HttpConnection(val settings: ConnectionSettings) : Closeable {
    init {
        if (settings.hostname.isEmpty())
            throw MissingHostNameException()
    }

    private var client: CloseableHttpClient? = null
    private val httpClient: CloseableHttpClient
        get() {
            if (client == null) {
                if (settings.username == null || settings.password == null) {
                    client = HttpClients.createDefault()
                } else {
                    val credentials = BasicCredentialsProvider()
                    credentials.setCredentials(
                        AuthScope(settings.hostname, settings.databasePort),
                        UsernamePasswordCredentials(settings.username, settings.password)
                    )
                    client = HttpClients.custom().setDefaultCredentialsProvider(credentials).build()
                }
            }
            return client!!
        }

    fun execute(request: HttpUriRequest): CloseableHttpResponse {
        return httpClient.execute(request)
    }

    override fun close() {
        if (client != null) {
            client!!.close()
            client = null
        }
    }
}
