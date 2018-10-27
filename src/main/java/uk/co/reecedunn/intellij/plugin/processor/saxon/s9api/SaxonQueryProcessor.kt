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
package uk.co.reecedunn.intellij.plugin.processor.saxon.s9api

import uk.co.reecedunn.intellij.plugin.core.reflection.getMethodOrNull
import uk.co.reecedunn.intellij.plugin.processor.query.MimeTypes
import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.query.UnsupportedQueryType

internal class SaxonQueryProcessor(val classes: SaxonClasses) :
    QueryProcessor {
    private val processor = classes.processorClass.getConstructor(Boolean::class.java).newInstance(true)

    override val version: String by lazy {
        val edition = classes.processorClass.getMethodOrNull("getSaxonEdition")?.invoke(processor) as? String
        val version = classes.processorClass.getMethod("getSaxonProductVersion").invoke(processor) as String
        edition?.let { "$it $version" } ?: version
    }

    override val supportedQueryTypes: Array<String> = arrayOf(MimeTypes.XQUERY)

    override fun eval(query: String, mimetype: String): Query = classes.check {
        when (mimetype) {
            MimeTypes.XQUERY -> {
                val compiler = classes.processorClass.getMethod("newXQueryCompiler").invoke(processor)
                val executable =
                    classes.xqueryCompilerClass.getMethod("compile", String::class.java).invoke(compiler, query)
                val evaluator = classes.xqueryExecutableClass.getMethod("load").invoke(executable)
                SaxonXQueryRunner(evaluator, classes)
            }
            else -> throw UnsupportedQueryType(mimetype)
        }
    }

    override fun invoke(path: String, mimetype: String): Query {
        throw UnsupportedOperationException()
    }

    override fun close() {
    }
}
