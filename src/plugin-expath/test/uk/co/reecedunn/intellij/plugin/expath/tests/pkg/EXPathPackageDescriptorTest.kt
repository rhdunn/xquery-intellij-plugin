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
package uk.co.reecedunn.intellij.plugin.expath.tests.pkg

import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.psi.xml.XmlFile
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.expath.pkg.EXPathPackageDescriptor

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("EXPath Packaging System 9 May 2012")
private class EXPathPackageDescriptorTest : ParsingTestCase<XmlFile>(null, XMLParserDefinition()) {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    private fun pkg(xml: String): EXPathPackageDescriptor {
        return EXPathPackageDescriptor(createVirtualFile("package.xml", xml))
    }

    @Test
    @DisplayName("missing fields")
    fun missingFields() {
        @Language("XML")
        val pkg = pkg("<package xmlns=\"http://expath.org/ns/pkg\"/>")

        assertThat(pkg.name, `is`(nullValue()))
        assertThat(pkg.abbrev, `is`(nullValue()))
        assertThat(pkg.version, `is`(nullValue()))
        assertThat(pkg.spec, `is`(nullValue()))
        assertThat(pkg.title, `is`(nullValue()))
    }

    @Test
    @DisplayName("properties")
    fun properties() {
        @Language("XML")
        val pkg = pkg(
            """
            <package xmlns="http://expath.org/ns/pkg"
                     name="http://www.example.com"
                     abbrev="test"
                     version="2.4"
                     spec="1.0">
                <title>Test Package</title>
            </package>
            """.trimIndent()
        )

        assertThat(pkg.name?.data, `is`("http://www.example.com"))
        assertThat(pkg.abbrev, `is`("test"))
        assertThat(pkg.version, `is`("2.4"))
        assertThat(pkg.spec, `is`("1.0"))
        assertThat(pkg.title, `is`("Test Package"))
    }
}
