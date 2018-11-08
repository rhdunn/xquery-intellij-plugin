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
package uk.co.reecedunn.intellij.plugin.processor.query

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe

data class ConnectionSettings(
    var hostname: String,
    var databasePort: Int,
    var adminPort: Int,
    var username: String?
) {
    constructor(): this("", 0, 0, null)

    private val serviceName: String get() = "uk.co.reecedunn.intellij.plugin.processor: $hostname:$databasePort"

    val password: String?
        get() {
            val credentialAttributes = CredentialAttributes(serviceName, username)
            return PasswordSafe.instance.get(credentialAttributes)?.getPasswordAsString()
        }

    fun setPassword(password: CharArray?) {
        val credentialAttributes = CredentialAttributes(serviceName, username)
        PasswordSafe.instance.set(credentialAttributes, password?.let { Credentials(username, it) })
    }
}
