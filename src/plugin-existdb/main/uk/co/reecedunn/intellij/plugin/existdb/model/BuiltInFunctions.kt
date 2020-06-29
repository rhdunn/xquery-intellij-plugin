/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.existdb.model

import uk.co.reecedunn.intellij.plugin.xpm.module.JarModuleResolver

object BuiltInFunctions : JarModuleResolver() {
    override val classLoader: ClassLoader = this::class.java.classLoader

    override val modules: Map<String, String> = mapOf(
        "http://exist-db.org/xquery/compression" to "org/exist-db/xquery/compression.xqy",
        "http://exist-db.org/xquery/console" to "org/exist-db/xquery/console.xqy",
        "http://exist-db.org/xquery/contextextraction" to "org/exist-db/xquery/contextextraction.xqy",
        "http://exist-db.org/xquery/counter" to "org/exist-db/xquery/counter.xqy",
        "http://exist-db.org/xquery/datetime" to "org/exist-db/xquery/datetime.xqy",
        "http://exist-db.org/xquery/example" to "org/exist-db/xquery/example.xqy",
        "http://exist-db.org/xquery/file" to "org/exist-db/xquery/file.xqy",
        "http://exist-db.org/xquery/httpclient" to "org/exist-db/xquery/httpclient.xqy",
        "http://exist-db.org/xquery/image" to "org/exist-db/xquery/image.xqy",
        "http://exist-db.org/xquery/inspect" to "org/exist-db/xquery/inspect.xqy",
        "http://exist-db.org/xquery/lucene" to "org/exist-db/xquery/lucene.xqy",
        "http://exist-db.org/xquery/mail" to "org/exist-db/xquery/mail.xqy",
        "http://exist-db.org/xquery/math" to "org/exist-db/xquery/math.xqy",
        "http://exist-db.org/xquery/ngram" to "org/exist-db/xquery/ngram.xqy",
        "http://exist-db.org/xquery/process" to "org/exist-db/xquery/process.xqy",
        "http://exist-db.org/xquery/range" to "org/exist-db/xquery/range.xqy",
        "http://exist-db.org/xquery/repo" to "org/exist-db/xquery/repo.xqy",
        "http://exist-db.org/xquery/request" to "org/exist-db/xquery/request.xqy",
        "http://exist-db.org/xquery/response" to "org/exist-db/xquery/response.xqy",
        "http://exist-db.org/xquery/scheduler" to "org/exist-db/xquery/scheduler.xqy",
        "http://exist-db.org/xquery/securitymanager" to "org/exist-db/xquery/securitymanager.xqy",
        "http://exist-db.org/xquery/session" to "org/exist-db/xquery/session.xqy",
        "http://exist-db.org/xquery/sort" to "org/exist-db/xquery/sort.xqy",
        "http://exist-db.org/xquery/sql" to "org/exist-db/xquery/sql.xqy",
        "http://exist-db.org/xquery/system" to "org/exist-db/xquery/system.xqy",
        "http://exist-db.org/xquery/transform" to "org/exist-db/xquery/transform.xqy",
        "http://exist-db.org/xquery/util" to "org/exist-db/xquery/util.xqy",
        "http://exist-db.org/xquery/validation" to "org/exist-db/xquery/validation.xqy",
        "http://exist-db.org/xquery/xmldb" to "org/exist-db/xquery/xmldb.xqy",
        "http://exist-db.org/xquery/xmldiff" to "org/exist-db/xquery/xmldiff.xqy",
        "http://exist-db.org/xquery/xqdoc" to "org/exist-db/xquery/xqdoc.xqy",
        "http://exist-db.org/xquery/xslfo" to "org/exist-db/xquery/xslfo.xqy"
    )
}
