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
package uk.co.reecedunn.intellij.plugin.processor.query.connection

import com.google.compat.gson.JsonParser
import com.google.gson.JsonArray
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.util.SystemProperties
import uk.co.reecedunn.intellij.plugin.core.io.decode

data class AWSConnectionSettings(
    var application: String,
    var profile: String,
    var region: String,
    var instanceName: String,
    override var databasePort: Int,
    override var username: String?
) : InstanceDetails {
    constructor() : this("", "", "", "", 0, null)
    // region hostname

    private val awsCommandLine: GeneralCommandLine
        get() {
            val commandLine = GeneralCommandLine()
            commandLine.setWorkDirectory(SystemProperties.getUserHome())
            commandLine.exePath = application
            commandLine.addParameter("ec2")
            commandLine.addParameter("--profile")
            commandLine.addParameter(profile)
            commandLine.addParameter("--region")
            commandLine.addParameter(region)
            return commandLine
        }

    override val hostname: String by lazy {
        val aws = awsCommandLine
        aws.addParameter("describe-instances")
        aws.addParameter("--filters")
        aws.addParameter("Name=tag:Name,Values=$instanceName")
        aws.addParameter("--query")
        aws.addParameter("Reservations[*].Instances[*].PrivateIpAddress")

        val process = aws.createProcess()
        val out = process.inputStream.decode().trim()
        val err = process.errorStream.decode().trim()
        if (err.isNotEmpty()) {
            throw AWSClientError(err)
        }

        val results = JsonParser.parseString(out).asJsonArray
        if (results.size() == 0) {
            throw AWSClientError("No instances found")
        }
        results.get(0).asJsonArray.get(0).asString
    }

    // endregion
    // region password

    private val serviceName: String
        get() = "uk.co.reecedunn.intellij.plugin.processor: aws:$region:$instanceName:$databasePort"

    override val password: String?
        get() {
            val credentialAttributes = CredentialAttributes(serviceName, username)
            return PasswordSafe.instance.get(credentialAttributes)?.getPasswordAsString()
        }

    override fun setPassword(password: CharArray?) {
        val credentialAttributes = CredentialAttributes(serviceName, username)
        PasswordSafe.instance.set(credentialAttributes, password?.let { Credentials(username, it) })
    }

    // endregion
}
