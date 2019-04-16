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
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQuery
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.Processor
import javax.xml.transform.ErrorListener

internal class SaxonXQueryRunner(
    val processor: Processor,
    val query: String,
    val queryPath: String,
    val classes: SaxonClasses
) : RunnableQuery, ValidatableQuery {
    private val errorListener: ErrorListener = SaxonErrorListener(queryPath, classes)

    private val compiler by lazy {
        val ret = processor.newXQueryCompiler()
        ret.setErrorListener(errorListener)
        ret
    }

    private val executable by lazy { compiler.compile(query) }

    private val evaluator by lazy { executable.load() }

    override var rdfOutputFormat: Language? = null

    override var updating: Boolean
        get() = compiler.updatingEnabled
        set(value) {
            compiler.updatingEnabled = value
        }

    override var xpathSubset: XPathSubset = XPathSubset.XPath

    override var server: String = ""

    override var database: String = ""

    override var modulePath: String = ""

    private var context: Any? = null

    override fun bindVariable(name: String, value: Any?, type: String?): Unit = classes.check(queryPath) {
        evaluator.setExternalVariable(classes.toQName(name), classes.toXdmValue(value, type))
    }

    override fun bindContextItem(value: Any?, type: String?): Unit = classes.check(queryPath) {
        context = when (value) {
            is DatabaseModule -> classes.toXdmValue(value.path, type)
            is VirtualFile -> classes.toXdmValue(value.decode()!!, type)
            else -> classes.toXdmValue(value, type)
        }
    }

    override fun run(): ExecutableOnPooledThread<Sequence<QueryResult>> = pooled_thread {
        classes.check(queryPath) {
            context?.let { evaluator.setContextItem(it) }
            SaxonQueryResultIterator(evaluator.iterator(), classes).asSequence()
        }
    }

    override fun validate(): ExecutableOnPooledThread<QueryError?> = pooled_thread {
        try {
            classes.check(queryPath) { executable } // Compile the query.
            null
        } catch (e: QueryError) {
            e
        }
    }

    override fun close() {
    }
}
