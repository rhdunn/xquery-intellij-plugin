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
package uk.co.reecedunn.intellij.plugin.processor.impl.basex.session

import uk.co.reecedunn.intellij.plugin.core.io.decode
import uk.co.reecedunn.intellij.plugin.intellij.resources.Resources
import uk.co.reecedunn.intellij.plugin.processor.query.ConnectionSettings
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorInstanceManager
import java.io.File
import java.net.UnknownHostException

internal fun mapType(type: String?): String? {
    return if (type == "xs:dateTimeStamp") // BaseX does not support XML Schema 1.1 Part 2
        "xs:dateTime"
    else
        type
}

class BaseX(path: File) : QueryProcessorInstanceManager {
    private val classes = BaseXClasses(path)
    private val context =
        try {
            classes.contextClass.getConstructor(Boolean::class.java).newInstance(true)
        } catch (e: NoSuchMethodException) {
            classes.contextClass.getConstructor().newInstance()
        }

    override fun create(): QueryProcessor {
        return BaseXLocalQueryProcessor(context, classes)
    }

    override fun connect(settings: ConnectionSettings): QueryProcessor {
        if (settings.hostname.isEmpty())
            throw UnknownHostException("")

        val session = classes.clientSessionClass.getConstructor(
            String::class.java, Int::class.java, String::class.java, String::class.java
        ).newInstance(settings.hostname, settings.databasePort, settings.username, settings.password)
        return BaseXClientQueryProcessor(session, classes)
    }
}
