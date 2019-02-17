/*
 * Copyright (C) 2016-2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.tests.fileTypes

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.fileTypes.FileTypeToArrayConsumer
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.FileTypeFactory
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.ServerSideJavaScriptFileType
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.XQueryFileType

@DisplayName("IntelliJ - Custom Language Support - Registering a File Type")
class FileTypeFactoryTest {
    @Test
    @DisplayName("FileTypeFactory")
    fun testFactory() {
        val factory = FileTypeFactory()
        val consumer = FileTypeToArrayConsumer()
        factory.createFileTypes(consumer)

        assertThat(consumer.fileTypes.size, `is`(2))
        assertThat(consumer.fileMatchers.size, `is`(0))

        assertThat(consumer.fileTypes[0].first.javaClass.name, `is`(XQueryFileType::class.java.name))
        assertThat(consumer.fileTypes[0].second, `is`("xq;xqy;xquery;xqu;xql;xqm;xqws"))

        assertThat(consumer.fileTypes[1].first.javaClass.name, `is`(ServerSideJavaScriptFileType::class.java.name))
        assertThat(consumer.fileTypes[1].second, `is`("sjs"))
    }
}
