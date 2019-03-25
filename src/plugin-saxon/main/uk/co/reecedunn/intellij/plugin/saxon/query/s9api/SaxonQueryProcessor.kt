/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.local_thread
import uk.co.reecedunn.intellij.plugin.core.reflection.getMethodOrNull
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.intellij.lang.XSLT
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.query.*
import javax.xml.transform.Source

internal class SaxonQueryProcessor(val classes: SaxonClasses, val source: Source?) : QueryProcessor {
    private val processor by lazy {
        if (source == null)
            classes.processorClass.getConstructor(Boolean::class.java).newInstance(true)
        else
            classes.processorClass.getConstructor(Source::class.java).newInstance(source)
    }

    override val version: ExecutableOnPooledThread<String> = local_thread {
        val edition = classes.processorClass.getMethodOrNull("getSaxonEdition")?.invoke(processor) as? String
        val version = classes.processorClass.getMethod("getSaxonProductVersion").invoke(processor) as String
        edition?.let { "$it $version" } ?: version
    }

    override val servers: ExecutableOnPooledThread<List<String>> = local_thread {
        listOf<String>()
    }

    override val databases: ExecutableOnPooledThread<List<String>> = local_thread {
        listOf<String>()
    }

    override fun createRunnableQuery(query: VirtualFile, language: Language): RunnableQuery {
        val queryText = query.decode()!!
        return when (language) {
            XPath -> SaxonXPathRunner(processor, queryText, query.name, classes)
            XQuery -> SaxonXQueryRunner(processor, queryText, query.name, classes)
            XSLT -> SaxonXsltRunner(processor, queryText, query.name, classes)
            else -> throw UnsupportedQueryType(language)
        }
    }

    override fun createProfileableQuery(query: VirtualFile, language: Language): ProfileableQuery {
        throw UnsupportedOperationException()
    }

    override fun close() {
    }
}
