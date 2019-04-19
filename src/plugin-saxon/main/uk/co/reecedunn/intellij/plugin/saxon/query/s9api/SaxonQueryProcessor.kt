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
import uk.co.reecedunn.intellij.plugin.processor.query.*
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQuery
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQueryProvider
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Processor
import javax.xml.transform.Source

internal class SaxonQueryProcessor(val classes: SaxonClasses, val source: Source?) :
    RunnableQueryProvider,
    ValidatableQueryProvider {

    private val processor by lazy {
        if (source == null)
            Processor(classes.loader, true)
        else
            Processor(classes.loader, source)
    }

    override val version: ExecutableOnPooledThread<String> = local_thread {
        processor.saxonEdition?.let { "$it ${processor.saxonProductVersion}" } ?: processor.saxonProductVersion
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
            XPath -> SaxonXPathRunner(processor, queryText, query.name)
            XQuery -> SaxonXQueryRunner(processor, queryText, query.name)
            XSLT -> SaxonXsltRunner(processor, queryText, query.name)
            else -> throw UnsupportedQueryType(language)
        }
    }

    override fun createValidatableQuery(query: VirtualFile, language: Language): ValidatableQuery {
        val queryText = query.decode()!!
        return when (language) {
            XPath -> SaxonXPathRunner(processor, queryText, query.name)
            XQuery -> SaxonXQueryRunner(processor, queryText, query.name)
            XSLT -> SaxonXsltRunner(processor, queryText, query.name)
            else -> throw UnsupportedQueryType(language)
        }
    }

    override fun close() {
    }
}
