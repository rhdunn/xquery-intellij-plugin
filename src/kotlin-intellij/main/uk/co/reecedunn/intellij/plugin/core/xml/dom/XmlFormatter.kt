/*
 * Copyright (C) 2017-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.xml.dom

import javax.xml.transform.OutputKeys
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory

object XmlFormatter {
    private val formatter by lazy {
        val factory = TransformerFactory.newInstance()
        factory.newTransformer()
    }

    @Synchronized
    fun format(source: Source, output: Result, omitXmlDeclaration: Boolean = false) {
        formatter.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, if (omitXmlDeclaration) "yes" else "no")
        formatter.transform(source, output)
    }
}
