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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import com.intellij.lang.Language
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.core.xml.toStreamSource
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery
import javax.xml.transform.Source

internal class SaxonXsltRunner(val processor: Any, val query: String, val classes: SaxonClasses) : RunnableQuery {
    private var hasContextItem = false

    private val compiler by lazy {
        classes.processorClass.getMethod("newXsltCompiler").invoke(processor)
    }

    private val executable by lazy {
        val source = query.toStreamSource()
        classes.xsltCompilerClass.getMethod("compile", Source::class.java).invoke(compiler, source)
    }

    private val transformer by lazy {
        classes.xsltExecutableClass.getMethod("load").invoke(executable)
    }

    override fun setRdfOutputFormat(language: Language?) {
    }

    override fun bindVariable(name: String, value: Any?, type: String?): Unit = classes.check {
        throw UnsupportedOperationException()
    }

    override fun bindContextItem(value: Any?, type: String?): Unit = classes.check {
        val source = value?.toString()?.toStreamSource()
        hasContextItem = source != null
        classes.xsltExecutableClass.getMethod("setSource", Source::class.java).invoke(transformer, source)
    }

    override fun run(): ExecutableOnPooledThread<Sequence<QueryResult>> = pooled_thread {
        classes.check {
            if (!hasContextItem) {
                // The Saxon processor throws a NPE if source is null.
                val message = PluginApiBundle.message("error.missing-xslt-source")
                return@check sequenceOf(QueryResult.fromItemType(0, message, "fn:error"))
            }

            val destination = classes.rawDestinationClass.getConstructor().newInstance()
            classes.xsltTransformerClass
                .getMethod("setDestination", classes.destinationClass)
                .invoke(transformer, destination)

            classes.xsltTransformerClass.getMethod("transform").invoke(transformer)
            val result = classes.rawDestinationClass.getMethod("getXdmValue").invoke(destination)

            val iterator = classes.xdmValueClass.getMethod("iterator").invoke(result)
            SaxonQueryResultIterator(iterator, classes).asSequence()
        }
    }

    override fun close() {
    }
}
