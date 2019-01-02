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
package uk.co.reecedunn.intellij.plugin.existdb.resources

import uk.co.reecedunn.intellij.plugin.core.io.decode
import uk.co.reecedunn.intellij.plugin.processor.query.TextSource

object EXistDBQueries {
    private fun loadText(path: String): String {
        val loader = EXistDBQueries::class.java.classLoader
        return loader.getResourceAsStream(path)!!.decode()
    }

    val PostQueryTemplate = loadText("queries/existdb/post-query.xml")

    val Version = TextSource(loadText("queries/existdb/version.xq"))
}
