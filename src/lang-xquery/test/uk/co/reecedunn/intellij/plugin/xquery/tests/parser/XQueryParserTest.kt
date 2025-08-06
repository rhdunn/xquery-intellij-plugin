// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.psi.toPsiTreeString
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parseText
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.requiresVirtualFileGetCharset
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("ClassName", "Reformat", "RedundantVisibilityModifier")
@DisplayName("XQuery 3.1 - Parser")
class XQueryParserTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("XQueryParserTest")
    override val language: Language = XQuery

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        requiresIFileElementTypeParseContents()
        requiresVirtualFileGetCharset()
        requiresPsiFileGetChildren()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(XQueryProjectSettings())
    }

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    fun loadResource(resource: String): String? = res.findFileByPath(resource)!!.decode()

    @Nested
    @DisplayName("Parser")
    internal inner class Parser {
        @Test
        @DisplayName("empty buffer")
        fun emptyBuffer() {
            val expected = "XQueryModuleImpl[FILE(0:0)]\n"

            assertThat(parseText<XQueryModule>("").toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("bad characters")
        fun badCharacters() {
            val expected =
                    "XQueryModuleImpl[FILE(0:3)]\n" +
                    "   PsiErrorElementImpl[ERROR_ELEMENT(0:0)]('XPST0003: Missing library 'module' declaration or main module query body.')\n" +
                    "   PsiErrorElementImpl[ERROR_ELEMENT(0:1)]('XPST0003: Unexpected token.')\n" +
                    "      LeafPsiElement[BAD_CHARACTER(0:1)]('^')\n" +
                    "   LeafPsiElement[BAD_CHARACTER(1:2)]('\uFFFE')\n" +
                    "   LeafPsiElement[BAD_CHARACTER(2:3)]('\uFFFF')\n"

            assertThat(parseText<XQueryModule>("^\uFFFE\uFFFF").toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("invalid token")
        fun testInvalidToken() {
            val expected =
                    "XQueryModuleImpl[FILE(0:2)]\n" +
                    "   PsiErrorElementImpl[ERROR_ELEMENT(0:0)]('XPST0003: Missing library 'module' declaration or main module query body.')\n" +
                    "   PsiErrorElementImpl[ERROR_ELEMENT(0:2)]('XPST0003: Unexpected token.')\n" +
                    "      LeafPsiElement[XQUERY_INVALID_TOKEN(0:2)]('<!')\n"

            assertThat(parseText<XQueryModule>("<!").toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (2) VersionDecl")
    internal inner class VersionDecl_XQuery10 {
        @Test
        @DisplayName("version only")
        fun versionOnly() {
            val expected = loadResource("tests/parser/xquery-1.0/VersionDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VersionDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("version only; compact whitespace")
        fun versionOnly_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("version and encoding")
        fun versionAndEncoding() {
            val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("version and encoding; compact whitespace")
        fun versionAndEncoding_compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_WithEncoding_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'version' keyword")
        fun missingVersionKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing version string")
        fun missingVersionString() {
            val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionString.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingVersionString.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing encoding string")
        fun missingEncodingString() {
            val expected = loadResource("tests/parser/xquery-1.0/VersionDecl_MissingEncodingString.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VersionDecl_MissingEncodingString.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (3) MainModule")
    internal inner class MainModule {
        @Test
        @DisplayName("main module")
        fun mainModule() {
            val expected = loadResource("tests/parser/xquery-1.0/MainModule.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MainModule.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("with VersionDecl")
        fun withVersionDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/MainModule_WithVersionDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MainModule_WithVersionDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: tokens after QueryBody")
        fun tokensAfterQueryBody() {
            val expected = loadResource("tests/parser/xquery-1.0/MainModule_TokensAfterExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MainModule_TokensAfterExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (4) LibraryModule")
    internal inner class LibraryModule {
        @Test
        @DisplayName("library module")
        fun libraryModule() {
            val expected = loadResource("tests/parser/xquery-1.0/LibraryModule.txt")
            val actual = parseResource("tests/parser/xquery-1.0/LibraryModule.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("with VersionDecl")
        fun withVersionDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/LibraryModule_WithVersionDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/LibraryModule_WithVersionDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: invalid declaration")
        fun invalidDeclaration() {
            val expected = loadResource("tests/parser/xquery-1.0/LibraryModule_WithInvalidConstructRecovery.txt")
            val actual = parseResource("tests/parser/xquery-1.0/LibraryModule_WithInvalidConstructRecovery.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (5) ModuleDecl")
    internal inner class ModuleDecl {
        @Test
        @DisplayName("module declaration")
        fun moduleDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("module declaration; compact whitespace")
        fun moduleDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'namespace' keyword")
        fun missingNamespaceKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing namespace name")
        fun missingNamespaceName() {
            val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing '=' after name")
        fun missingEqualsAfterName() {
            val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingEqualsAfterName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingEqualsAfterName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing namespace URI")
        fun missingNamespaceUri() {
            val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceUri.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingNamespaceUri.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/ModuleDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ModuleDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (6) Prolog")
    internal inner class Prolog {
        @Test
        @DisplayName("multiple imports")
        fun multipleImports() {
            val expected = loadResource("tests/parser/xquery-1.0/Prolog_MultipleImports.txt")
            val actual = parseResource("tests/parser/xquery-1.0/Prolog_MultipleImports.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (8) Import ; XQuery 1.0 EBNF (21) SchemaImport ; XQuery 1.0 EBNF (23) ModuleImport")
    internal inner class Import {
        @Test
        @DisplayName("error recovery: missing 'schema' or 'module' keyword")
        fun missingSchemaOrModuleKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/Import_MissingSchemaOrModuleKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/Import_MissingSchemaOrModuleKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (21) SchemaImport")
        internal inner class SchemaImport {
            @Test
            @DisplayName("schema import")
            fun schemaImport() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("schema import; compact whitespace")
            fun schemaImport_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing schema URI")
            fun missingSchemaUri() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_MissingSchemaUri.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_MissingSchemaUri.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing semicolon")
            fun missingSemicolon() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_MissingSemicolon.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_MissingSemicolon.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with 'at' sequence")
            fun withAtSequence() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with 'at' sequence; compact whitespace")
            fun withAtSequence_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: with 'at' sequence; missing URI")
            fun withAtSequence_MissingUri() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_MissingUri.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_MissingUri.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with 'at' sequence; multiple")
            fun withAtSequence_Multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with 'at' sequence; multiple; compact whitespace")
            fun withAtSequence_Multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: with 'at' sequence; multiple; missing NCName after comma")
            fun withAtSequence_Multiple_MissingNCNameAfterComma() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_WithAtSequence_Multiple_MissingNCNameAfterComma.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: prolog body statements after declaration")
            fun prologBodyStatementsAfter() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaImport_PrologBodyStatementsAfter.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaImport_PrologBodyStatementsAfter.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (22) SchemaPrefix")
        internal inner class SchemaPrefix {
            @Test
            @DisplayName("schema prefix")
            fun schemaPrefix() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("schema prefix; compact whitespace")
            fun schemaPrefix_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing NCName")
            fun missingNCName() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_MissingNCName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_MissingNCName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing '='")
            fun missingEquals() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_MissingEquals.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_MissingEquals.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("default")
            fun default() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("default; compact whitespace")
            fun default_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: default; missing 'namespace' keyword")
            fun default_MissingNamespaceKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingNamespaceKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingNamespaceKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: default; missing 'element' keyword")
            fun default_MissingElementKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingElementKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SchemaPrefix_Default_MissingElementKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (23) ModuleImport")
        internal inner class ModuleImport {
            @Test
            @DisplayName("module import")
            fun moduleImport() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("module import; compact whitespace")
            fun moduleImport_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missingmodule URI")
            fun missingModuleUri() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_MissingModuleUri.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_MissingModuleUri.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing semicolon")
            fun missingSemicolon() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_MissingSemicolon.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_MissingSemicolon.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with namespace")
            fun withNamespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with namespace; compact whitespace")
            fun withNamespace_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: with namespace; missing NCName")
            fun withNamespace_MissingNCName() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingNCName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingNCName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing '='")
            fun missingEquals() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingEquals.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithNamespace_MissingEquals.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with 'at' sequence")
            fun withAtSequence() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with 'at' sequence; compact whitespace")
            fun withAtSequence_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: with 'at' sequence; missing URI")
            fun withAtSequence_MissingUri() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_MissingUri.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_MissingUri.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with 'at' sequence; multiple")
            fun withAtSequence_Multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with 'at' sequence; multiple; compact whitespace")
            fun withAtSequence_Multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: with 'at' sequence; multiple; missing NCName after comma")
            fun withAtSequence_Multiple_MissingNCNameAfterComma() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_WithAtSequence_Multiple_MissingNCNameAfterComma.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: prolog body statements after declaration")
            fun prologBodyStatementsAfter() {
                val expected = loadResource("tests/parser/xquery-1.0/ModuleImport_PrologBodyStatementsAfter.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ModuleImport_PrologBodyStatementsAfter.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (10) NamespaceDecl")
    internal inner class NamespaceDecl {
        @Test
        @DisplayName("namespace declaration")
        fun namespaceDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("namespace declaration; compact whitespace")
        fun namespaceDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing URI")
        fun missingUri() {
            val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingUri.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingUri.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing '='")
        fun missingEquals() {
            val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingEquals.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingEquals.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing NCName")
        fun missingNCName() {
            val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNCName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'namespace' keyword")
        fun missingNamespaceKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNamespaceKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingNamespaceKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog body statements after declaration")
        fun prologBodyStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/NamespaceDecl_PrologBodyStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NamespaceDecl_PrologBodyStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (11) BoundarySpaceDecl")
    internal inner class BoundarySpaceDecl {
        @Test
        @DisplayName("boundary declaration")
        fun boundarySpaceDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("boundary space declaration; compact whitespace")
        fun boundarySpaceDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'preserve' or 'strip' keyword")
        fun missingPreserveOrStripKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingPreserveOrStripKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingPreserveOrStripKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'boundary-space' keyword")
        fun missingBoundarySpaceKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog body statements after declaration")
        fun prologBodyStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_PrologBodyStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_PrologBodyStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (12) DefaultNamespaceDecl")
    internal inner class DefaultNamespaceDecl {
        @Test
        @DisplayName("element")
        fun element() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("function")
        fun function() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'element' or 'function' keyword")
        fun missingElementOrFunctionKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_MissingElementOrFunctionKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_MissingElementOrFunctionKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("element; compact whitespace")
        fun element_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("function; compact whitespace")
        fun function_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: element; missing URI")
        fun element_MissingUri() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingUri.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingUri.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: function; missing URI")
        fun function_MissingUri() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingUri.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingUri.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: element; missing 'namespace' keyword")
        fun element_MissingNamespaceKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingNamespaceKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingNamespaceKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: function; missing 'namespace' keyword")
        fun function_MissingNamespaceKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingNamespaceKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingNamespaceKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: element; missing 'default' keyword")
        fun element_MissingDefaultKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingDefaultKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingDefaultKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: function; missing 'default' keyword")
        fun function_MissingDefaultKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingDefaultKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingDefaultKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: element; missing semicolon")
        fun element_MissingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Element_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: function; missing semicolon")
        fun function_MissingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_Function_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog body statements after declaration")
        fun prologBodyStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_PrologBodyStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultNamespaceDecl_PrologBodyStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (13) OptionDecl")
    internal inner class OptionDecl {
        @Test
        @DisplayName("option declaration")
        fun optionDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/OptionDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OptionDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("option declaration; compact whitespace")
        fun optionDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing option value")
        fun missingOptionValue() {
            val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionValue.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionValue.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing option name")
        fun missingOptionName() {
            val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'option' keyword")
        fun missingOptionKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingOptionKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog header statements after declaration")
        fun prologHeaderStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/OptionDecl_PrologHeaderStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OptionDecl_PrologHeaderStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (14) OrderingModeDecl")
    internal inner class OrderingModeDecl {
        @Test
        @DisplayName("ordering mode declaration")
        fun orderingModeDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("ordering mode declaration; compact whitespace")
        fun orderingModeDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'ordered' or 'unordered' keyword")
        fun missingOrderedOrUnorderedKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderedOrUnorderedKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderedOrUnorderedKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'ordering' keyword")
        fun missingOrderingKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderingKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingOrderingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog body statements after declaration")
        fun prologBodyStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/OrderingModeDecl_PrologBodyStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrderingModeDecl_PrologBodyStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (15) EmptyOrderDecl")
    internal inner class EmptyOrderDecl {
        @Test
        @DisplayName("empty order declaration")
        fun emptyOrderDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty order declaration; compact whitespace")
        fun emptyOrderDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'greatest' or 'least' keyword")
        fun missingGreatestOrLeastKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingGreatestOrLeastKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingGreatestOrLeastKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'empty' keyword")
        fun missingEmptyKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingEmptyKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingEmptyKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'order' keyword")
        fun missingOrderKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingOrderKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingOrderKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'default' keyword")
        fun missingDefaultKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingDefaultKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingDefaultKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog body statements after declaration")
        fun prologBodyStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/EmptyOrderDecl_PrologBodyStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/EmptyOrderDecl_PrologBodyStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (16) CopyNamespacesDecl ; XQuery 1.0 EBNF (17) PreserveMode ; XQuery 1.0 EBNF (18) InheritMode")
    internal inner class CopyNamespacesDecl {
        @Test
        @DisplayName("copy namespace declaration")
        fun copyNamespacesDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("copy namespace declaration; compact whitespace")
        fun copyNamespacesDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing InheritMode")
        fun missingInheritMode() {
            val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingInheritMode.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingInheritMode.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing comma")
        fun missingComma() {
            val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingComma.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingComma.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing PreserveMode")
        fun missingPreserveMode() {
            val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingPreserveMode.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingPreserveMode.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'copy-namespaces' keyword")
        fun missingCopyNamespacesKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingCopyNamespacesKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingCopyNamespacesKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog body statements after declaration")
        fun prologBodyStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/CopyNamespacesDecl_PrologBodyStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CopyNamespacesDecl_PrologBodyStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (19) DefaultCollationDecl")
    internal inner class DefaultCollationDecl {
        @Test
        @DisplayName("default collation declaration")
        fun defaultCollationDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("default collation declaration; compact whitespace")
        fun defaultCollationDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing URI")
        fun missingUri() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingUri.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingUri.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'collation' keyword")
        fun missingCollationKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingCollationKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingCollationKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'default' keyword")
        fun missingDefaultKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingDefaultKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_MissingDefaultKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog body statements after declaration")
        fun prologBodyStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/DefaultCollationDecl_PrologBodyStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DefaultCollationDecl_PrologBodyStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (20) BaseURIDecl")
    internal inner class BaseURIDecl {
        @Test
        @DisplayName("base URI declaration")
        fun baseURIDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("base URI declaration; compact whitespace")
        fun baseURIDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing URI")
        fun missingUri() {
            val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingUri.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingUri.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'base-uri' keyword")
        fun missingBaseUriKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_MissingBaseUriKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_MissingBaseUriKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog body statements after declaration")
        fun prologBodyStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/BaseURIDecl_PrologBodyStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BaseURIDecl_PrologBodyStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (24) VarDecl")
    internal inner class VarDecl {
        @Test
        @DisplayName("variable declaration")
        fun varDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("variable declaration; compact whitespace")
        fun varDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: '=' instead of ':='")
        fun equal() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_Equal.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_Equal.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ExprSingle")
        fun missingExprSingle() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingExprSingle.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingExprSingle.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ':='")
        fun missingAssignment() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingAssignment.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingAssignment.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing variable name")
        fun missingVariableName() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing '$'")
        fun missingVariableMarker() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableMarker.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableMarker.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'variable' keyword")
        fun missingVariableKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingVariableKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingVariableKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("external")
        fun external() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_External.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_External.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("external; compact whitespace")
        fun external_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_External_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_External_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: external; missing variable name")
        fun external_MissingVariableName() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_External_MissingVariableName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_External_MissingVariableName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog header statements after declaration")
        fun prologHeaderStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/VarDecl_PrologHeaderStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarDecl_PrologHeaderStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery: 'external' as attribute/element test TypeName")
        internal inner class AttributeOrElementDecl {
            @Test
            @DisplayName("XQuery 1.0 EBNF (129) AttributeTest")
            fun attributeTest_MissingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/VarDecl_AttributeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/VarDecl_AttributeTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (133) ElementTest")
            fun elementTest_MissingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/VarDecl_ElementTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/VarDecl_ElementTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (20) ConstructionDecl")
    internal inner class ConstructionDecl {
        @Test
        @DisplayName("construction declaration")
        fun constructionDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("construction declaration; compact whitespace")
        fun constructionDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'preserve' or 'strip' keyword")
        fun missingPreserveOrStripKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_MissingPreserveOrStripKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_MissingPreserveOrStripKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'construction' keyword")
        fun missingConstructionKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/BoundarySpaceDecl_MissingBoundarySpaceKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: prolog body statements after declaration")
        fun prologBodyStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-1.0/ConstructionDecl_PrologBodyStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ConstructionDecl_PrologBodyStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (26) FunctionDecl ; XQuery 1.0 EBNF (27) ParamList ; XQuery 1.0 EBNF (29) EnclosedExpr")
    internal inner class FunctionDecl {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (26) FunctionDecl")
        internal inner class FunctionDecl {
            @Test
            @DisplayName("function declaration")
            fun functionDecl() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("function declaration; compact whitespace")
            fun functionDecl_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'function' keyword")
            fun missingFunctionKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing function name")
            fun missingFunctionName() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing opening parenthesis")
            fun missingOpeningParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingOpeningParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingOpeningParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("function body")
            fun enclosedExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: function body; missing opening brace")
            fun enclosedExpr_MissingOpeningBrace() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_EnclosedExpr_MissingOpeningBrace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_EnclosedExpr_MissingOpeningBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing function body")
            fun missingFunctionBody() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionBody.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingFunctionBody.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("return type")
            fun returnType() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: return type; missing SequenceType")
            fun returnType_MissingSequenceType() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType_MissingSequenceType.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_ReturnType_MissingSequenceType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing semicolon")
            fun testFunctionDecl_MissingSemicolon() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_MissingSemicolon.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_MissingSemicolon.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: prolog body statements after declaration")
            fun prologHeaderStatementsAfter() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl_PrologHeaderStatementsAfter.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl_PrologHeaderStatementsAfter.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (27) ParamList")
        internal inner class ParamList {
            @Test
            @DisplayName("parameter list")
            fun paramList() {
                val expected = loadResource("tests/parser/xquery-1.0/ParamList.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParamList.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("parameter list; compact whitespace")
            fun paramList_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ParamList_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParamList_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing comma")
            fun missingComma() {
                val expected = loadResource("tests/parser/xquery-1.0/ParamList_MissingComma.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParamList_MissingComma.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (28) Param")
        internal inner class Param {
            @Test
            @DisplayName("parameter")
            fun param() {
                val expected = loadResource("tests/parser/xquery-1.0/Param.txt")
                val actual = parseResource("tests/parser/xquery-1.0/Param.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("parameter list; compact whitespace")
            fun param_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/Param_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/Param_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing parameter name")
            fun missingParameterName() {
                val expected = loadResource("tests/parser/xquery-1.0/Param_MissingParameterName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/Param_MissingParameterName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing '$'")
            fun missingVariableMarker() {
                val expected = loadResource("tests/parser/xquery-1.0/Param_MissingVariableMarker.txt")
                val actual = parseResource("tests/parser/xquery-1.0/Param_MissingVariableMarker.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("type declaration")
            fun typeDeclaration() {
                val expected = loadResource("tests/parser/xquery-1.0/Param_TypeDeclaration.txt")
                val actual = parseResource("tests/parser/xquery-1.0/Param_TypeDeclaration.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("type declaration; compact whitespace")
            fun typeDeclaration_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/Param_TypeDeclaration_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/Param_TypeDeclaration_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (29) EnclosedExpr")
        internal inner class EnclosedExpr {
            @Test
            @DisplayName("enclosed expression")
            fun enclosedExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("enclosed expression; compact whitespace")
            fun enclosedExpr_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr_MissingClosingBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (30) QueryBody ; XQuery 1.0 EBNF (31) Expr")
    internal inner class QueryBody {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/QueryBody_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QueryBody_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/QueryBody_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QueryBody_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: multiple; missing Expr")
        fun multiple_MissingExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/QueryBody_Multiple_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QueryBody_Multiple_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; space before next comma")
        fun multiple_SpaceBeforeNextComma() {
            val expected = loadResource("tests/parser/xquery-1.0/QueryBody_Multiple_SpaceBeforeNextComma.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QueryBody_Multiple_SpaceBeforeNextComma.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (33) FLWORExpr")
    internal inner class FLWORExpr {
        @Test
        @DisplayName("error recovery: ReturnClause only")
        fun returnClauseOnly() {
            val expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_ReturnOnly.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_ReturnOnly.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("clause ordering")
        fun clauseOrdering() {
            val expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_ClauseOrdering.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("nested")
        fun nested() {
            val expected = loadResource("tests/parser/xquery-1.0/FLWORExpr_Nested.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FLWORExpr_Nested.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple ForClause")
        fun forClause_Multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/ForClause_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ForClause_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple LetClause")
        fun letClause_multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/LetClause_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/LetClause_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (33) FLWORExpr ; XQuery 1.0 EBNF (34) ForClause")
    internal inner class ForClause {
        @Nested
        @DisplayName("single ForBinding")
        internal inner class SingleForBinding {
            @Test
            @DisplayName("single binding")
            fun single() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single binding; compact whitespace")
            fun single_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing VarName")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword")
            fun missingInKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' ExprSingle")
            fun missingInExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' keyword")
            fun missingReturnKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingReturnKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingReturnKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' ExprSingle")
            fun missingReturnExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingReturnExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingReturnExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("multiple ForBindings")
        internal inner class MultipleForBindings {
            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable indicator")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MultipleVarName_MissingVarIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (35) PositionalVar")
        internal inner class PositionalVar {
            @Test
            @DisplayName("positional variable")
            fun positionalVar() {
                val expected = loadResource("tests/parser/xquery-1.0/PositionalVar.txt")
                val actual = parseResource("tests/parser/xquery-1.0/PositionalVar.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("positional variable; compact whitespace")
            fun positionalVar_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/PositionalVar_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/PositionalVar_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable indicator")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-1.0/PositionalVar_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-1.0/PositionalVar_MissingVarIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing VarName")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-1.0/PositionalVar_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/PositionalVar_MissingVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword")
            fun missingInKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword_PositionalVar.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_MissingInKeyword_PositionalVar.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (118) TypeDeclaration")
        internal inner class TypeDeclaration {
            @Test
            @DisplayName("type declaration")
            fun typeDeclaration() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword")
            fun missingInKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword after PositionalVar")
            fun missingInKeyword_PositionalVar() {
                val expected = loadResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword_PositionalVar.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForClause_TypeDeclaration_MissingInKeyword_PositionalVar.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (33) FLWORExpr ; XQuery 1.0 EBNF (36) LetClause")
    internal inner class LetClause {
        @Nested
        @DisplayName("single LetBinding")
        internal inner class SingleLetBinding {
            @Test
            @DisplayName("single binding")
            fun single() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single binding; compact whitespace")
            fun single_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing VarName")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: '=' instead of ':='")
            fun equal() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_Equal.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_Equal.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable assignment operator")
            fun missingVarAssignOperator() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignOperator.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignOperator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable assignment ExprSingle")
            fun missingVarAssignExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingVarAssignExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' keyword")
            fun missingReturnKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingReturnKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingReturnKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' ExprSingle")
            fun missingReturnExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MissingReturnExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MissingReturnExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("multiple LetBindings")
        internal inner class MultipleLetBindings {
            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable indicator")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_MultipleVarName_MissingVarIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (118) TypeDeclaration")
        internal inner class TypeDeclaration {
            @Test
            @DisplayName("type declaration")
            fun typeDeclaration() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable indicator")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-1.0/LetClause_TypeDeclaration_MissingVarIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (33) FLWORExpr ; XQuery 1.0 EBNF (37) WhereClause")
    internal inner class WhereClause {
        @Test
        @DisplayName("for clause")
        fun forClause() {
            val expected = loadResource("tests/parser/xquery-1.0/WhereClause_ForClause.txt")
            val actual = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("for clause; compact whitespace")
        fun forClause_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/WhereClause_ForClause_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing where expression")
        fun missingWhereExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/WhereClause_ForClause_MissingWhereExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/WhereClause_ForClause_MissingWhereExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("let clause")
        fun letClause() {
            val expected = loadResource("tests/parser/xquery-1.0/WhereClause_LetClause.txt")
            val actual = parseResource("tests/parser/xquery-1.0/WhereClause_LetClause.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (33) FLWORExpr ; XQuery 1.0 EBNF (38) OrderByClause")
    internal inner class OrderByClause {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (38) OrderByClause")
        internal inner class OrderByClause {
            @Test
            @DisplayName("for clause")
            fun forClause() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_ForClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_ForClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("let clause")
            fun letClause() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_LetClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_LetClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'by' keyword")
            fun missingByKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_MissingByKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_MissingByKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing OrderSpecList")
            fun missingOrderSpecList() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_MissingOrderSpecList.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_MissingOrderSpecList.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("for clause; stable")
            fun stable_ForClause() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_ForClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_ForClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("let clause; stable")
            fun stable_LetClause() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_LetClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_LetClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: stable; missing 'order' keyword")
            fun stable_MissingOrderKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: stable; missing 'by' keyword")
            fun stable_MissingByKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingByKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingByKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: stable; missing OrderSpecList")
            fun stable_MissingOrderSpecList() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderSpecList.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_Stable_MissingOrderSpecList.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (39) OrderSpecList ; XQuery 1.0 EBNF (40) OrderSpec")
        internal inner class OrderSpecList {
            @Test
            @DisplayName("order spec list")
            fun orderSpecList() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderByClause_ForClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderByClause_ForClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("order spec list; multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderSpecList_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderSpecList_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("order spec list; multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing OrderSpec")
            fun missingOrderSpec() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_MissingOrderSpec.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderSpecList_Multiple_MissingOrderSpec.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (41) OrderModifier")
        internal inner class OrderModifier {
            @Test
            @DisplayName("direction only")
            fun directionOnly() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderModifier_DirectionOnly.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderModifier_DirectionOnly.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("empty only")
            fun emptyOnly() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: empty only; missing specifier")
            fun emptyOnly_MissingSpecifier() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly_MissingSpecifier.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderModifier_EmptyOnly_MissingSpecifier.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("collation only")
            fun collationOnly() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: collation only; missing URI string")
            fun collationOnly_MissingUriString() {
                val expected = loadResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly_MissingUriString.txt")
                val actual = parseResource("tests/parser/xquery-1.0/OrderModifier_CollationOnly_MissingUriString.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (43) TypeswitchExpr ; XQuery 1.0 EBNF (44) CaseClause")
    internal inner class TypeswitchExpr {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (43) TypeswitchExpr")
        internal inner class TypeswitchExpr {
            @Test
            @DisplayName("typeswitch expression")
            fun typeswitchExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("typeswitch expression; compact whitespace")
            fun typeswitchExpr_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing type expression")
            fun missingTypeExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingTypeExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingTypeExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing CaseClause")
            fun missingCaseClause() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingCaseClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingCaseClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'default' keyword")
            fun testTypeswitchExpr_MissingDefaultKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingDefaultKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingDefaultKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' keyword")
            fun testTypeswitchExpr_MissingReturnKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing return expression")
            fun testTypeswitchExpr_MissingReturnExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MissingReturnExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple CaseClause")
            fun multipleCaseClause() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_MultipleCaseClause.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_MultipleCaseClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("variable")
            fun variable() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("variable; compact whitespace")
            fun variable_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: variable; missing VarName")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_Variable_MissingVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (44) CaseClause")
        internal inner class CaseClause {
            @Test
            @DisplayName("case clause")
            fun caseClause() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("case clause; compact whitespace")
            fun caseClause_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/TypeswitchExpr_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing SequenceType")
            fun missingSequenceType() {
                val expected = loadResource("tests/parser/xquery-1.0/CaseClause_MissingSequenceType.txt")
                val actual = parseResource("tests/parser/xquery-1.0/CaseClause_MissingSequenceType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' keyword")
            fun missingReturnKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/CaseClause_MissingReturnKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/CaseClause_MissingReturnKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing expression")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/CaseClause_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/CaseClause_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("variable")
            fun variable() {
                val expected = loadResource("tests/parser/xquery-1.0/CaseClause_Variable.txt")
                val actual = parseResource("tests/parser/xquery-1.0/CaseClause_Variable.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: variable; missing 'as' keyword")
            fun mariable_MissingAsKeyword() {
                val expected = loadResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingAsKeyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingAsKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable name")
            fun variable_MissingVarName() {
                val expected = loadResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/CaseClause_Variable_MissingVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (46) QuantifiedExpr")
    internal inner class QuantifiedExpr {
        @Test
        @DisplayName("some, every")
        fun quantifiedExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("some, every; compact whitespace")
        fun quantifiedExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing VarName")
        fun missingVarName() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' keyword")
        fun missingInKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' ExprSingle")
        fun testQuantifiedExpr_MissingInExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingInExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'satisfies' keyword")
        fun missingSatisfiesKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'satisfies' ExprSingle")
        fun missingSatisfiesExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MissingSatisfiesExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("type declaration")
        fun typeDeclaration() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_TypeDeclaration.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_TypeDeclaration.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName")
        fun multipleVarName() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName; compact whitespace")
        fun multipleVarName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName; missing variable indicator")
        fun multipleVarName_MissingVarIndicator() {
            val expected = loadResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_MissingVarIndicator.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QuantifiedExpr_MultipleVarName_MissingVarIndicator.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (45) IfExpr")
    internal inner class IfExpr {
        @Test
        @DisplayName("if expression")
        fun ifExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("if expression; compact whitespace")
        fun ifExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing condition Expr")
        fun missingCondExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingCondExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingCondExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'then' keyword")
        fun missingThenKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingThenKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingThenKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'then' ExprSingle")
        fun missingThenExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingThenExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingThenExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'else' keyword")
        fun missingElseKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingElseKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingElseKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'else' ExprSingle")
        fun missingElseExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IfExpr_MissingElseExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IfExpr_MissingElseExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (46) OrExpr")
    internal inner class OrExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-1.0/OrExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing AndExpr")
        fun missingAndExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/OrExpr_MissingAndExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrExpr_MissingAndExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/OrExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrExpr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (47) AndExpr")
    internal inner class AndExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-1.0/AndExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AndExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ComparisonExpr")
        fun missingComparisonExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/AndExpr_MissingComparisonExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AndExpr_MissingComparisonExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/AndExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AndExpr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (48) ComparisonExpr ; XQuery 1.0 EBNF (60) GeneralComp")
    internal inner class GeneralComp {
        @Test
        @DisplayName("general comparison")
        fun generalComp() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("general comparison; compact whitespace")
        fun generalComp_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing rhs RangeExpr")
        fun missingRhsRangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingRhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingRhsRangeExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing lhs RangeExpr")
        fun missingLhsRangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingLhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_GeneralComp_MissingLhsRangeExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (48) ComparisonExpr ; XQuery 1.0 EBNF (61) ValueComp")
    internal inner class ValueComp {
        @Test
        @DisplayName("value comparison")
        fun valueComp() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing rhs RangeExpr")
        fun missingRhsRangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp_MissingRhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_ValueComp_MissingRhsRangeExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (48) ComparisonExpr ; XQuery 1.0 EBNF (62) NodeComp")
    internal inner class NodeComp {
        @Test
        @DisplayName("node comparison")
        fun nodeComp() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("node comparison; compact whitespace")
        fun nodeComp_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing rhs RangeExpr")
        fun missingRhsRangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingRhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingRhsRangeExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing lhs RangeExpr")
        fun missingLhsRangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingLhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ComparisonExpr_NodeComp_MissingLhsRangeExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (49) RangeExpr")
    internal inner class RangeExpr {
        @Test
        @DisplayName("range expression")
        fun rangeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/RangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RangeExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing AdditiveExpr")
        fun missingAdditiveExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/RangeExpr_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RangeExpr_MissingAdditiveExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (50) AdditiveExpr")
    internal inner class AdditiveExpr {
        @Test
        @DisplayName("additive expression")
        fun additiveExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("additive expression; compact whitespace")
        fun additiveExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing MultiplicativeExpr")
        fun missingMultiplicativeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_MissingMultiplicativeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_MissingMultiplicativeExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/AdditiveExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AdditiveExpr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (51) MultiplicativeExpr")
    internal inner class MultiplicativeExpr {
        @Test
        @DisplayName("multiplicative expression")
        fun multiplicativeExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiplicative expression; compact whitespace")
        fun multiplicativeExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing UnionExpr")
        fun missingUnionExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_MissingUnionExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_MissingUnionExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/MultiplicativeExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/MultiplicativeExpr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (52) UnionExpr")
    internal inner class UnionExpr {
        @Test
        @DisplayName("union expression")
        fun unionExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/UnionExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnionExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("union expression; compact whitespace")
        fun unionExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnionExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnionExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing IntersectExceptExpr")
        fun missingIntersectExceptExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/UnionExpr_MissingIntersectExceptExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnionExpr_MissingIntersectExceptExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/UnionExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnionExpr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (53) IntersectExceptExpr")
    internal inner class IntersectExceptExpr {
        @Test
        @DisplayName("intersect")
        fun intersect() {
            val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: intersect; missing InstanceofExpr")
        fun intersect_MissingInstanceofExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect_MissingInstanceofExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Intersect_MissingInstanceofExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("except")
        fun except() {
            val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: except; missing InstanceofExpr")
        fun except_MissingInstanceofExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except_MissingInstanceofExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Except_MissingInstanceofExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/IntersectExceptExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntersectExceptExpr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (54) InstanceofExpr")
    internal inner class InstanceofExpr {
        @Test
        @DisplayName("instance of expression")
        fun instanceofExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'instance' keyword")
        fun missingInstanceKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingInstanceKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingInstanceKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'of' keyword")
        fun missingOfKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingOfKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingOfKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SequenceType")
        fun missingSequenceType() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr_MissingSequenceType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (55) TreatExpr")
    internal inner class TreatExpr {
        @Test
        @DisplayName("treat expression")
        fun treatExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/TreatExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/TreatExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'treat' keyword")
        fun missingTreatKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'as' keyword")
        fun missingAsKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/TreatExpr_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/TreatExpr_MissingAsKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SequenceType")
        fun missingSequenceType() {
            val expected = loadResource("tests/parser/xquery-1.0/TreatExpr_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/TreatExpr_MissingSequenceType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (56) CastableExpr ; XQuery 1.0 EBNF (117) SingleType")
    internal inner class CastableExpr {
        @Test
        @DisplayName("castable expression")
        fun castableExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CastableExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastableExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'castable' keyword")
        fun missingCastableKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'as' keyword")
        fun missingAsKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CastableExpr_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastableExpr_MissingAsKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SingleType")
        fun missingSingleType() {
            val expected = loadResource("tests/parser/xquery-1.0/CastableExpr_MissingSingleType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastableExpr_MissingSingleType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType")
        fun optionalAtomicType() {
            val expected = loadResource("tests/parser/xquery-1.0/CastableExpr_OptionalAtomicType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastableExpr_OptionalAtomicType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType; compact whitespace")
        fun optionalAtomicType_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CastableExpr_OptionalAtomicType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastableExpr_OptionalAtomicType_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (57) CastExpr ; XQuery 1.0 EBNF (117) SingleType")
    internal inner class CastExpr {
        @Test
        @DisplayName("cast expression")
        fun castExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'cast' keyword")
        fun missingCastKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingCastKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'as' keyword")
        fun missingAsKeyword() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingAsKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SingleType")
        fun missingSingleType() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_MissingSingleType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_MissingSingleType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType")
        fun optionalAtomicType() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_OptionalAtomicType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_OptionalAtomicType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType; compact whitespace")
        fun optionalAtomicType_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CastExpr_OptionalAtomicType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CastExpr_OptionalAtomicType_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (58) UnaryExpr")
    internal inner class UnaryExpr {
        @Test
        @DisplayName("plus; single")
        fun plusSingle() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Plus.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Plus.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("plus; single; compact whitespace")
        fun plusSingle_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Plus_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Plus_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("plus; multiple")
        fun plusMultiple() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Plus_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Plus_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("plus; multiple; compact whitespace")
        fun plusMultiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Plus_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Plus_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: plus; missing ValueExpr")
        fun plus_missingValueExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Plus_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Plus_MissingValueExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("minus; single")
        fun minusSingle() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Minus.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Minus.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("minus; single; compact whitespace")
        fun minusSingle_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Minus_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Minus_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("minus; multiple")
        fun minusMultiple() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Minus_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Minus_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("minus; multiple; compact whitespace")
        fun minusMultiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Minus_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Minus_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: minus; missing ValueExpr")
        fun minus_missingValueExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Minus_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Minus_MissingValueExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("mixed")
        fun mixed() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Mixed.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Mixed.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("mixed; compact whitespace")
        fun mixed_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnaryExpr_Mixed_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnaryExpr_Mixed_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (63) ValidateExpr ; XQuery 1.0 EBNF (64) ValidationMode")
    internal inner class ValidateExpr {
        @Test
        @DisplayName("validate expression")
        fun validateExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("validate expression; compact whitespace")
        fun validateExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("validation mode")
        fun validationMode() {
            val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("validation mode; compact whitespace")
        fun validationMode_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: validation mode; missing opening brace")
        fun validationMode_MissingOpeningBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_MissingOpeningBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_ValidationMode_MissingOpeningBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/ValidateExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ValidateExpr_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (65) ExtensionExpr ; XQuery 1.0 EBNF (66) Pragma")
    internal inner class ExtensionExpr {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (65) ExtensionExpr")
        internal inner class ExtensionExpr {
            @Test
            @DisplayName("extension expression")
            fun extensionExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("extension expression; compact whitespace")
            fun extensionExpr_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing opening brace")
            fun missingOpeningBrace() {
                val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MissingOpeningBrace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MissingOpeningBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("empty expression")
            fun emptyExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_EmptyExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_EmptyExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MissingClosingBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple pragmas")
            fun multiplePragmas() {
                val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple pragmas; compact whitespace")
            fun multiplePragmas_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr_MultiplePragmas_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (66) Pragma")
        internal inner class Pragma {
            @Test
            @DisplayName("pragma")
            fun pragma() {
                val expected = loadResource("tests/parser/xquery-1.0/ExtensionExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ExtensionExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing pragma name")
            fun missingPragmaName() {
                val expected = loadResource("tests/parser/xquery-1.0/Pragma_MissingPragmaName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/Pragma_MissingPragmaName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing pragma contents")
            fun missingPragmaContents() {
                // This is invalid according to the XQuery grammar, but is supported by
                // XQuery implementations.
                val expected = loadResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents.txt")
                val actual = parseResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing pragma contents; compact whitespace")
            fun missingPragmaContents_CompactWhitespace() {
                // This is valid according to the XQuery grammar.
                val expected = loadResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/Pragma_MissingPragmaContents_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: unclosed pragma")
            fun testPragma_UnclosedPragma() {
                val expected = loadResource("tests/parser/xquery-1.0/Pragma_UnclosedPragma.txt")
                val actual = parseResource("tests/parser/xquery-1.0/Pragma_UnclosedPragma.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (68) PathExpr")
    internal inner class PathExpr {
        @Test
        @DisplayName("leading forward slash")
        fun leadingForwardSlash() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("leading forward slash; compact whitespace")
        fun leadingForwardSlash_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingForwardSlash_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("leading double forward slash")
        fun leadingDoubleForwardSlash() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("leading double forward slash; compact whitespace")
        fun leadingDoubleForwardSlash_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LeadingDoubleForwardSlash_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("lone forward slash")
        fun loneForwardSlash() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LoneForwardSlash.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LoneForwardSlash.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("lone double forward slash")
        fun loneDoubleForwardSlash() {
            val expected = loadResource("tests/parser/xquery-1.0/PathExpr_LoneDoubleForwardSlash.txt")
            val actual = parseResource("tests/parser/xquery-1.0/PathExpr_LoneDoubleForwardSlash.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (69) RelativePathExpr")
    internal inner class RelativePathExpr {
        @Test
        @DisplayName("direct descendants")
        fun directDescendants() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("direct descendants; compact whitespace")
        fun directDescendants_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("all descendants")
        fun allDescendants() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("all descendants; compact whitespace")
        fun allDescendants_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_AllDescendants_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing StepExpr")
        fun missingStepExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_MissingStepExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_MissingStepExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/RelativePathExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/RelativePathExpr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (71) AxisStep")
    internal inner class AxisStep {
        @Test
        @DisplayName("error recovery: invalid axis")
        fun invalidAxis() {
            val expected = loadResource("tests/parser/xquery-1.0/AxisStep_InvalidAxis.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AxisStep_InvalidAxis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: invalid axis and missing NodeTest")
        fun invalidAxis_missingNodeTest() {
            val expected = loadResource("tests/parser/xquery-1.0/AxisStep_InvalidAxis_MissingNodeTest.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AxisStep_InvalidAxis_MissingNodeTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: invalid axis with PredicateList")
        fun invalidAxis_predicateList() {
            val expected = loadResource("tests/parser/xquery-1.0/AxisStep_InvalidAxis_PredicateList.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AxisStep_InvalidAxis_PredicateList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (72) ForwardStep ; XQuery 1.0 EBNF (73) ForwardAxis")
    internal inner class ForwardAxis {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (79) NameTest")
        internal inner class NameTest {
            @Test
            @DisplayName("child")
            fun child() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Child.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Child.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("child; compact whitespace")
            fun child_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Child_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Child_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("descendant")
            fun descendant() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Descendant.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Descendant.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("descendant; compact whitespace")
            fun descendant_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Descendant_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Descendant_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("attribute")
            fun attribute() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Attribute.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Attribute.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("attribute; compact whitespace")
            fun attribute_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Attribute_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Attribute_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("self")
            fun self() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Self.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Self.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("self; compact whitespace")
            fun self_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Self_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Self_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("descendant-or-self")
            fun descendantOrSelf() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_DescendantOrSelf.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_DescendantOrSelf.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("descendant-or-self; compact whitespace")
            fun descendantOrSelf_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_DescendantOrSelf_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_DescendantOrSelf_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("following-sibling")
            fun followingSibling() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_FollowingSibling.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_FollowingSibling.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("following-sibling; compact whitespace")
            fun followingSibling_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_FollowingSibling_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_FollowingSibling_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("following")
            fun following() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Following.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Following.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("following; compact whitespace")
            fun following_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Following_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_NameTest_Following_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (123) KindTest")
        internal inner class KindTest {
            @Test
            @DisplayName("self")
            fun self() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_KindTest_Self.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_KindTest_Self.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("self; compact whitespace")
            fun self_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_KindTest_Self_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_KindTest_Self_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Test
        @DisplayName("error recovery: missing NodeTest")
        fun missingNodeTest() {
            val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_MissingNodeTest.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_MissingNodeTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (82) PredicateList ; XQuery 1.0 EBNF (83) Predicate")
        internal inner class PredicateList {
            @Test
            @DisplayName("predicate list")
            fun predicateList() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("predicate list; compact whitespace")
            fun predicateList_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_MissingClosingBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ForwardAxis_PredicateList_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (72) ForwardStep ; XQuery 1.0 EBNF (74) AbbrevForwardStep")
    internal inner class AbbrevForwardStep {
        @Test
        @DisplayName("node test only")
        fun nodeTest() {
            val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("abbreviated attribute")
        fun attribute() {
            val expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("abbreviated attribute; compact whitespace")
        fun attribute_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: abbreviated attribute; missing NodeTest")
        fun attribute_MissingNodeTest() {
            val expected = loadResource("tests/parser/xquery-1.0/AbbrevForwardStep_MissingNodeTest.txt")
            val actual = parseResource("tests/parser/xquery-1.0/AbbrevForwardStep_MissingNodeTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (75) ReverseStep ; XQuery 1.0 EBNF (76) ReverseAxis")
    internal inner class ReverseAxis {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (79) NameTest")
        internal inner class NameTest {
            @Test
            @DisplayName("parent")
            fun parent() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Parent.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Parent.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("parent; compact whitespace")
            fun parent_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Parent_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Parent_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("ancestor")
            fun ancestor() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Ancestor.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Ancestor.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("ancestor; compact whitespace")
            fun ancestor_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Ancestor_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Ancestor_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("preceding-sibling")
            fun precedingSibling() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_PrecedingSibling.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_PrecedingSibling.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("preceding-sibling; compact whitespace")
            fun precedingSibling_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_PrecedingSibling_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_PrecedingSibling_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("preceding")
            fun preceding() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Preceding.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Preceding.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("preceding; compact whitespace")
            fun preceding_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Preceding_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_Preceding_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("ancestor-or-self")
            fun ancestorOrSelf() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_AncestorOrSelf.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_AncestorOrSelf.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("ancestor-or-self; compact whitespace")
            fun ancestorOrSelf_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_AncestorOrSelf_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_NameTest_AncestorOrSelf_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (123) KindTest")
        internal inner class KindTest {
            @Test
            @DisplayName("parent")
            fun parent() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_KindTest_Parent.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_KindTest_Parent.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("parent; compact whitespace")
            fun parent_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_KindTest_Parent_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_KindTest_Parent_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Test
        @DisplayName("error recovery: missing NodeTest")
        fun missingNodeTest() {
            val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_MissingNodeTest.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_MissingNodeTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (82) PredicateList ; XQuery 1.0 EBNF (83) Predicate")
        internal inner class PredicateList {
            @Test
            @DisplayName("predicate list")
            fun predicateList() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("predicate list; compact whitespace")
            fun predicateList_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_MissingClosingBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ReverseAxis_PredicateList_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (75) ReverseStep ; XQuery 1.0 EBNF (77) AbbrevReverseStep")
    fun abbrevReverseStep() {
        val expected = loadResource("tests/parser/xquery-1.0/AbbrevReverseStep.txt")
        val actual = parseResource("tests/parser/xquery-1.0/AbbrevReverseStep.xq")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (123) KindTest")
    internal inner class KindTest {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (124) AnyKindTest")
        internal inner class AnyKindTest {
            @Test
            @DisplayName("any kind test")
            fun anyKindTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("any kind test; compact whitespace")
            fun anyKindTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AnyKindTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (125) DocumentTest")
        internal inner class DocumentTest {
            @Test
            @DisplayName("document test")
            fun documentTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("document test; compact whitespace")
            fun documentTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("document test + element test")
            fun elementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_ElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_ElementTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("document test + element test; compact whitespace")
            fun elementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_ElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_ElementTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("document test + schema element test")
            fun schemaElementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_SchemaElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_SchemaElementTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("document test + schema element test; compact whitespace")
            fun schemaElementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_SchemaElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_DocumentTest_SchemaElementTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (126) TextTest")
        internal inner class TestTest {
            @Test
            @DisplayName("text test")
            fun textTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_TextTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_TextTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("text test; compact whitespace")
            fun textTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_TextTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_TextTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_TextTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_TextTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (127) CommentTest")
        internal inner class CommentTest {
            @Test
            @DisplayName("comment test")
            fun commentTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_CommentTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_CommentTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("comment test; compact whitespace")
            fun commentTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_CommentTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_CommentTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_CommentTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_CommentTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (128) PITest")
        internal inner class PITest {
            @Test
            @DisplayName("processing instruction test")
            fun piTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("processing instruction test; compact whitespace")
            fun piTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("NCName")
                fun ncname() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_NCName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_NCName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("NCName; compact whitespace")
                fun ncname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_NCName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_NCName_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }

            @Nested
            @DisplayName("StringLiteral")
            internal inner class StringLiteral {
                @Test
                @DisplayName("string literal")
                fun stringLiteral() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_StringLiteral.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_StringLiteral.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("string literal; compact whitespace")
                fun stringLiteral_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_PITest_StringLiteral_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_PITest_StringLiteral_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (129) AttributeTest")
        internal inner class AttributeTest {
            @Test
            @DisplayName("attribute test")
            fun attributeTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("attribute test; compact whitespace")
            fun attributeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Nested
            @DisplayName("XQuery 1.0 EBNF (129) AttribNameOrWildcard")
            internal inner class AttribNameOrWildcard {
                @Test
                @DisplayName("ncname")
                fun ncname() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_NCName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_NCName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("ncname: compact whitespace")
                fun ncname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_NCName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_NCName_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("qname")
                fun qname() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_QName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_QName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("qname: compact whitespace")
                fun qname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_QName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_QName_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("wildcard")
                fun wildcard() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_Wildcard.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_Wildcard.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("wildcard: compact whitespace")
                fun wildcard_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_Wildcard_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_Wildcard_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }

            @Nested
            @DisplayName("XQuery 1.0 EBNF (139) TypeName")
            internal inner class TypeName {
                @Test
                @DisplayName("type name")
                fun typeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("type name: compact whitespace")
                fun typeName_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing type name")
                fun typeName_MissingTypeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_MissingTypeName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_MissingTypeName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing comma")
                fun typeName_MissingComma() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_MissingComma.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_AttributeTest_TypeName_MissingComma.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (131) SchemaAttributeTest")
        internal inner class SchemaAttributeTest {
            @Test
            @DisplayName("schema attribute test")
            fun schemaAttributeTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("schema attribute test; compact whitespace")
            fun schemaAttributeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing attribute declaration")
            fun missingAttributeDeclaration() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_MissingAttributeDeclaration.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaAttributeTest_MissingAttributeDeclaration.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (133) ElementTest")
        internal inner class ElementTest {
            @Test
            @DisplayName("element test")
            fun elementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("element test; compact whitespace")
            fun elementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Nested
            @DisplayName("XQuery 1.0 EBNF (134) ElementNameOrWildcard")
            internal inner class ElementNameOrWildcard {
                @Test
                @DisplayName("ncname")
                fun ncname() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_NCName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_NCName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("ncname: compact whitespace")
                fun ncname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_NCName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_NCName_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("qname")
                fun qname() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_QName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_QName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("qname: compact whitespace")
                fun qname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_QName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_QName_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("wildcard")
                fun wildcard() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_Wildcard.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_Wildcard.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("wildcard: compact whitespace")
                fun wildcard_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_Wildcard_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_Wildcard_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }

            @Nested
            @DisplayName("XQuery 1.0 EBNF (139) TypeName")
            internal inner class TypeName {
                @Test
                @DisplayName("type name")
                fun typeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("type name: compact whitespace")
                fun typeName_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing type name")
                fun typeName_MissingTypeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_MissingTypeName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_MissingTypeName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing comma")
                fun typeName_MissingComma() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_MissingComma.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_MissingComma.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("optional type name")
                fun optionalTypeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("optional type name: compact whitespace")
                fun optionalTypeName_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional_CompactWhitespace.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing type name on optional type name")
                fun optionalTypeName_MissingTypeName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional_MissingTypeName.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NodeTest_ElementTest_TypeName_Optional_MissingTypeName.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (135) SchemaElementTest")
        internal inner class SchemaElementTest {
            @Test
            @DisplayName("schema element test")
            fun schemaElementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("schema element test; compact whitespace")
            fun schemaElementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing element declaration")
            fun missingAttributeDeclaration() {
                val expected = loadResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_MissingElementDeclaration.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NodeTest_SchemaElementTest_MissingElementDeclaration.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (78) NodeTest ; XQuery 1.0 EBNF (79) NameTest")
    internal inner class NameTest {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (154) QName")
        internal inner class QName {
            @Test
            @DisplayName("qname")
            fun qname() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("keyword prefix part")
            fun keywordPrefixPart() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_KeywordPrefixPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_KeywordPrefixPart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("keyword local part")
            fun keywordLocalPart() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_KeywordLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_KeywordLocalPart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Nested
            @DisplayName("error recovery: spaces between colon")
            internal inner class SpacesBetweenColon {
                @Test
                @DisplayName("space before colon")
                fun spaceBeforeColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_SpaceBeforeColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_SpaceBeforeColon.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("space after colon")
                fun spaceAfterColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_SpaceAfterColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_SpaceAfterColon.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("space before and after colon")
                fun spaceBeforeAndAfterColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_SpaceBeforeAndAfterColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_SpaceBeforeAndAfterColon.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }

            @Test
            @DisplayName("error recovery: integer literal local name")
            fun integerLiteralLocalPart() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_IntegerLiteralLocalName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_IntegerLiteralLocalName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Nested
            @DisplayName("error recovery: missing part")
            internal inner class MissingPart {
                @Test
                @DisplayName("missing local name")
                fun missingLocalName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_MissingLocalPart.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_MissingLocalPart.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("missing prefix")
                fun missingPrefix() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_MissingPrefixPart.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_MissingPrefixPart.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("missing prefix and local name")
                fun missingPrefixAndLocalName() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_QName_MissingPrefixAndLocalPart.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_QName_MissingPrefixAndLocalPart.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (155) NCName")
        internal inner class NCName {
            @Test
            @DisplayName("identifier")
            fun identifier() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_NCName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_NCName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("keyword")
            fun keyword() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_NCName_Keyword.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_NCName_Keyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("invalid NCName start character")
            fun invalidNCNameStartChar() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_InvalidNCNameStartChar.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_InvalidNCNameStartChar.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (80) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("ncname")
            fun ncname() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("qname: ncname prefix")
            fun ncnamePrefix() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCNamePrefixPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCNamePrefixPart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("qname: keyword prefix")
            fun keywordPrefix() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_KeywordPrefixPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_KeywordPrefixPart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("qname: ncname local name")
            fun ncnameLocalName() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCNameLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_NCNameLocalPart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("qname: keyword local name")
            fun keywordLocalName() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_KeywordLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_KeywordLocalPart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: prefix and local name wildcard")
            fun prefixAndLocalNameWildcard() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_PrefixAndLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_PrefixAndLocalPart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing prefix")
            fun missingPrefix() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_MissingPrefix.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_MissingPrefix.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing local name")
            fun missingLocalName() {
                val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_MissingLocalName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_MissingLocalName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Nested
            @DisplayName("error recovery: spaces between colon")
            internal inner class SpacesBetweenColon {
                @Test
                @DisplayName("space before colon")
                fun spaceBeforeColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceBeforeColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceBeforeColon.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("space after colon")
                fun spaceAfterColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceAfterColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceAfterColon.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }

                @Test
                @DisplayName("space before and after colon")
                fun spaceBeforeAndAfterColon() {
                    val expected = loadResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceBeforeAndAfterColon.txt")
                    val actual = parseResource("tests/parser/xquery-1.0/NameTest_Wildcard_SpaceBeforeAndAfterColon.xq")
                    assertThat(actual.toPsiTreeString(), `is`(expected))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (81) FilterExpr ; XQuery 1.0 EBNF (82) PredicateList ; XQuery 1.0 EBNF (83) Predicate")
    internal inner class FilterExpr {
        @Test
        @DisplayName("predicate list")
        fun predicateList() {
            val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("predicate list; compact whitespace")
        fun predicateList_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr")
        fun missingExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FilterExpr_PredicateList_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (87) VarRef ; XQuery 1.0 EBNF (88) VarName")
    internal inner class VarRef {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val expected = loadResource("tests/parser/xquery-1.0/VarRef_NCName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarRef_NCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("NCName; compat whitespace")
        fun ncname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/VarRef_NCName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarRef_NCName_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val expected = loadResource("tests/parser/xquery-1.0/VarRef.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarRef.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("QName; compat whitespace")
        fun qname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/VarRef_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarRef_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing VarName")
        fun missingVarName() {
            val expected = loadResource("tests/parser/xquery-1.0/VarRef_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/VarRef_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (89) ParenthesizedExpr ; XQuery 1.0 EBNF (31) Expr")
    internal inner class ParenthesizedExpr {
        @Test
        @DisplayName("empty expression")
        fun emptyExpression() {
            val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty expression; compact whitespace")
        fun emptyExpression_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun emptyExpression_MissingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_EmptyExpression_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (89) Expr")
        internal inner class Expr {
            @Test
            @DisplayName("single")
            fun single() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Single.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Single.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single; compact whitespace")
            fun single_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Single_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Single_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: multiple; missing Expr")
            fun multiple_MissingExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple; space before next comma")
            fun multiple_SpaceBeforeNextComma() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_SpaceBeforeNextComma.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_SpaceBeforeNextComma.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr_Expr_Multiple_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (90) ContextItemExpr")
    fun contextItemExpr() {
        val expected = loadResource("tests/parser/xquery-1.0/ContextItemExpr.txt")
        val actual = parseResource("tests/parser/xquery-1.0/ContextItemExpr.xq")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (91) OrderedExpr")
    internal inner class OrderedExpr {
        @Test
        @DisplayName("ordered expression")
        fun orderedExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/OrderedExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrderedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("ordered expression; compact whitespace")
        fun orderedExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/OrderedExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrderedExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/OrderedExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/OrderedExpr_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (92) UnorderedExpr")
    internal inner class UnorderedExpr {
        @Test
        @DisplayName("unordered expression")
        fun unorderedExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("unordered expression; compact whitepace")
        fun unorderedExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/UnorderedExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/UnorderedExpr_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (93) FunctionCall ; XQuery 3.0 EBNF (121) ArgumentList")
    internal inner class FunctionCall {
        @Test
        @DisplayName("keyword NCName")
        fun keywordNCName() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_NCName_Keyword.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_NCName_Keyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty argument list")
        fun argumentList_Empty() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty argument list; compact whitespace")
        fun argumentList_Empty_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing opening parenthesis")
        fun missingOpeningParenthesis() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_MissingOpeningParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_MissingOpeningParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_ArgumentList_Empty_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (134) Argument")
        internal inner class Argument {
            @Test
            @DisplayName("single argument")
            fun singleParam() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_SingleParam.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_SingleParam.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single argument; compact whitespace")
            fun singleParam_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_SingleParam_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_SingleParam_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple argument")
            fun multipleParam() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple argument; compact whitespace")
            fun multipleParam_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: multiple argument; missing ExprSingle")
            fun multipleParam_MissingExpr() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple argument; space before next comma")
            fun multipleParam_SpaceBeforeNextComma() {
                val expected = loadResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.txt")
                val actual = parseResource("tests/parser/xquery-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (96) DirElemConstructor")
    internal inner class DirElemConstructor {
        @Test
        @DisplayName("direct element constructor")
        fun dirElemConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("direct element constructor; compact whitespace")
        fun dirElemConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("self-closing")
        fun selfClosing() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("self-closing; compact whitespace")
        fun selfClosing_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: space before NCName")
        fun spaceBeforeNCName() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_SpaceBeforeNCName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SpaceBeforeNCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: incomplete open tag")
        fun incompleteOpenTag() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTag.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTag.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: incomplete closing tag")
        fun incompleteClosingTag() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTag.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTag.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing tag")
        fun missingClosingTag() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: closing tag without open tag")
        fun closingTagWithoutOpenTag() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_ClosingTagWithoutOpenTag.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_ClosingTagWithoutOpenTag.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: incomplete open tag QName")
        fun incompleteOpenTagQName() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTagQName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTagQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: incomplete closing tag QName")
        fun incompleteCloseTagQName() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTagQName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTagQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: empty closing tag QName")
        fun emptyCloseTagQName() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemConstructor_EmptyCloseTag.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemConstructor_EmptyCloseTag.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (97) DirAttributeList ; XQuery 1.0 EBNF (98) DirAttributeValue")
    internal inner class DirAttributeList {
        @Nested
        @DisplayName("XQuery 1.0 EBNF (97) DirAttributeList ; XQuery 1.0 EBNF (98) DirAttributeValue")
        internal inner class DirAttributeList {
            @Test
            @DisplayName("single; QName")
            fun qname() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single; QName; compact whitespace")
            fun qname_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single; NCName")
            fun ncname() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_NCName.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_NCName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single; NCName")
            fun ncname_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_NCName_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_NCName_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_Multiple_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing attribute value")
            fun missingAttributeValue() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_MissingAttributeValue.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_MissingAttributeValue.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing '='")
            fun missingEquals() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_MissingEquals.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_MissingEquals.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("namespace with prefix")
            fun namespaceWithPrefix() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_NamespaceWithPrefix.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_NamespaceWithPrefix.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("namespace without prefix")
            fun namespaceWithoutPrefix() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeList_NamespaceWithoutPrefix.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeList_NamespaceWithoutPrefix.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (99) QuotAttrValueContent ; XQuery 1.0 EBNF (100) AposAttrValueContent")
        fun attrValueContent() {
            val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_AttrContentChar.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_AttrContentChar.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent")
        fun commonContent() {
            val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EscapeCharacters.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EscapeCharacters.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent ; XQuery 1.0 EBNF (29) EnclosedExpr")
        fun enclosedExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EnclosedExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_EnclosedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent ; XQuery 1.0 EBNF (145) PredefinedEntityRef")
        internal inner class PredefinedEntityRef {
            @Test
            @DisplayName("predefined entity reference")
            fun predefinedEntityRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: empty reference")
            fun emptyRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_EmptyRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_EmptyRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: incomplete reference")
            fun incompleteRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_IncompleteRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_PredefinedEntityRef_IncompleteRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent; XQuery 1.0 EBNF (146) EscapeQuot")
        fun escapeQuot() {
            val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeQuot.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeQuot.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent ; XQuery 1.0 EBNF (147) EscapeApos")
        fun escapeApos() {
            val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeApos.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_EscapeApos.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent ; XQuery 1.0 EBNF (153) CharRef")
        internal inner class CharRef {
            @Test
            @DisplayName("character reference")
            fun charRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: empty hexadecimal reference")
            fun emptyHexadecimalRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyHexadecimalRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyHexadecimalRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: empty numeric reference")
            fun emptyNumericRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyNumericRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_EmptyNumericRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: incomplete reference")
            fun incompleteRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_IncompleteRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirAttributeValue_CommonContent_CharRef_IncompleteRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (101) DirElemContent")
    internal inner class DirElemContent {
        @Test
        @DisplayName("XQuery 1.0 EBNF (95) DirectConstructor ; XQuery 1.0 EBNF (96) DirElemConstructor")
        fun dirElemConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_DirElemConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_DirElemConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (95) DirectConstructor ; XQuery 1.0 EBNF (103) DirCommentConstructor")
        fun dirCommentConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_DirCommentConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_DirCommentConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (95) DirectConstructor ; XQuery 1.0 EBNF (105) DirPIConstructor")
        fun dirPIConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_DirPIConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_DirPIConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (101) ElementContentChar")
        fun elementContentChar() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_ElementContentChar.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_ElementContentChar.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent")
        fun commonContent() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EscapeCharacters.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EscapeCharacters.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent ; XQuery 1.0 EBNF (29) EnclosedExpr")
        fun enclosedExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EnclosedExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_EnclosedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent ; XQuery 1.0 EBNF (145) PredefinedEntityRef")
        internal inner class PredefinedEntityRef {
            @Test
            @DisplayName("predefined entity reference")
            fun predefinedEntityRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: empty reference")
            fun emptyRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_EmptyRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_EmptyRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: incomplete reference")
            fun incompleteRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_IncompleteRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_PredefinedEntityRef_IncompleteRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent ; XQuery 1.0 EBNF (153) CharRef")
        internal inner class CharRef {
            @Test
            @DisplayName("character reference")
            fun charRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: empty hexadecimal reference")
            fun emptyHexadecimalRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyHexadecimalRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyHexadecimalRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: empty numeric reference")
            fun emptyNumericRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyNumericRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_EmptyNumericRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: incomplete reference")
            fun incompleteRef() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_IncompleteRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CommonContent_CharRef_IncompleteRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (107) CDataSection ; XQuery 1.0 EBNF (108) CDataSectionContents")
        internal inner class CDataSection {
            @Test
            @DisplayName("cdata section")
            fun cdataSection() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CDataSection.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CDataSection.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("with surrounding text")
            fun withSurroundingText() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_WithSurroundingText.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_WithSurroundingText.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: unclosed")
            fun unclosed() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_Unclosed.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_Unclosed.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: unexpected end tag")
            fun unexpectedEndTag() {
                val expected = loadResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_UnexpectedEndTag.txt")
                val actual = parseResource("tests/parser/xquery-1.0/DirElemContent_CDataSection_UnexpectedEndTag.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (103) DirCommentContent")
    internal inner class DirCommentContent {
        @Test
        @DisplayName("direct comment constructor")
        fun dirCommentConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: unclosed comment")
        fun unclosedComment() {
            val expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor_UnclosedComment.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor_UnclosedComment.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: unexpected end tag")
        fun unexpectedCommentEndTag() {
            val expected = loadResource("tests/parser/xquery-1.0/DirCommentConstructor_UnexpectedCommentEndTag.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirCommentConstructor_UnexpectedCommentEndTag.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (105) DirPIConstructor ; XQuery 1.0 EBNF (106) DirPIContents")
    internal inner class DirPIConstructor {
        @Test
        @DisplayName("direct processing instruction constructor")
        fun dirPIConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: unexpected whitespace")
        fun unexpectedWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_UnexpectedWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_UnexpectedWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing NCName")
        fun missingNCName() {
            val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_MissingNCName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_MissingNCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing contents")
        fun missingContents() {
            val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_MissingContents.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_MissingContents.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing end tag")
        fun missingEndTag() {
            val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_MissingEndTag.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_MissingEndTag.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("QName PITarget")
        fun qnamePITarget() {
            val expected = loadResource("tests/parser/xquery-1.0/DirPIConstructor_QNamePITarget.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DirPIConstructor_QNamePITarget.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (107) CDataSection ; XQuery 1.0 EBNF (108) CDataSectionContents")
    internal inner class CDataSection {
        @Test
        @DisplayName("cdata section")
        fun cdataSection() {
            val expected = loadResource("tests/parser/xquery-1.0/CDataSection.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CDataSection.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: unclosed")
        fun unclosed() {
            val expected = loadResource("tests/parser/xquery-1.0/CDataSection_Unclosed.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CDataSection_Unclosed.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: unexpected end tag")
        fun unexpectedEndTag() {
            val expected = loadResource("tests/parser/xquery-1.0/CDataSection_UnexpectedEndTag.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CDataSection_UnexpectedEndTag.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (110) CompDocConstructor")
    internal inner class CompDocConstructor {
        @Test
        @DisplayName("computed document constructor")
        fun compDocConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/CompDocConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompDocConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("computed document constructor; compact whitespace")
        fun compDocConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompDocConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompDocConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompDocConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompDocConstructor_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (111) CompElemConstructor")
    internal inner class CompElemConstructor {
        @Test
        @DisplayName("tag name: QName")
        fun testCompElemConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: QName; compact whitespace")
        fun testCompElemConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("content expression; empty expression")
        fun testCompElemConstructor_NoExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_NoExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing closing brace")
        fun testCompElemConstructor_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression")
        fun testCompElemConstructor_ExprTagName() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; compact whitespace")
        fun testCompElemConstructor_ExprTagName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing expression")
        fun testCompElemConstructor_ExprTagName_MissingTagNameExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingTagNameExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingTagNameExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing closing brace")
        fun testCompElemConstructor_ExprTagName_MissingClosingTagNameBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingClosingTagNameBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_ExprTagName_MissingClosingTagNameBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: content expression; missing expression")
        fun testCompElemConstructor_MissingValueExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_MissingValueExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: string literal (error recovery)")
        fun testCompElemConstructor_StringLiteral() {
            val expected = loadResource("tests/parser/xquery-1.0/CompElemConstructor_StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompElemConstructor_StringLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("CompElemConstructor like NameTest followed by a keyword in an expression")
        fun nameTest_CompElemConstructorLike() {
            val expected = loadResource("tests/parser/xquery-1.0/NameTest_CompElemConstructorLike.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NameTest_CompElemConstructorLike.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (113) CompAttrConstructor")
    internal inner class CompAttrConstructor {
        @Test
        @DisplayName("tag name: QName")
        fun testCompAttrConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: QName; compact whitespace")
        fun testCompAttrConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("content expression; empty expression")
        fun testCompAttrConstructor_NoExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_NoExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing closing brace")
        fun testCompAttrConstructor_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression")
        fun testCompAttrConstructor_ExprTagName() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; compact whitespace")
        fun testCompAttrConstructor_ExprTagName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing expression")
        fun testCompAttrConstructor_ExprTagName_MissingTagNameExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingTagNameExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingTagNameExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing closing brace")
        fun testCompAttrConstructor_ExprTagName_MissingClosingTagNameBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingClosingTagNameBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_ExprTagName_MissingClosingTagNameBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: content expression; missing expression")
        fun testCompAttrConstructor_MissingValueExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_MissingValueExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: string literal (error recovery)")
        fun testCompAttrConstructor_StringLiteral() {
            val expected = loadResource("tests/parser/xquery-1.0/CompAttrConstructor_StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompAttrConstructor_StringLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("CompAttrConstructor like NameTest followed by a keyword in an expression")
        fun nameTest_CompAttrConstructorLike() {
            val expected = loadResource("tests/parser/xquery-1.0/NameTest_CompAttrConstructorLike.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NameTest_CompAttrConstructorLike.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (114) CompTextConstructor")
    internal inner class CompTextConstructor {
        @Test
        @DisplayName("computed text constructor")
        fun compTextConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/CompTextConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompTextConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompTextConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompTextConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompTextConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompTextConstructor_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (115) CompCommentConstructor")
    internal inner class CompCommentConstructor {
        @Test
        @DisplayName("computed comment constructor")
        fun compCommentConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/CompCommentConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompCommentConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompCommentConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompCommentConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompCommentConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompCommentConstructor_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (116) CompPIConstructor")
    internal inner class CompPIConstructor {
        @Test
        @DisplayName("tag name: NCName")
        fun testCompPIConstructor() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: NCName; compact whitespace")
        fun testCompPIConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("content expression; empty expression")
        fun testCompPIConstructor_NoExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_NoExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing closing brace")
        fun testCompPIConstructor_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression")
        fun testCompPIConstructor_ExprTagName() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; compact whitespace")
        fun testCompPIConstructor_ExprTagName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing expression")
        fun testCompPIConstructor_ExprTagName_MissingTagNameExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingTagNameExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingTagNameExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing closing brace")
        fun testCompPIConstructor_ExprTagName_MissingClosingTagNameBrace() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingClosingTagNameBrace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_ExprTagName_MissingClosingTagNameBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: content expression; missing expression")
        fun testCompPIConstructor_MissingValueExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_MissingValueExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: QName (error recovery)")
        fun testCompPIConstructor_QNameTagName() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_QNameTagName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_QNameTagName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: string literal (error recovery)")
        fun testCompPIConstructor_StringLiteral() {
            val expected = loadResource("tests/parser/xquery-1.0/CompPIConstructor_StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/CompPIConstructor_StringLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("CompPIConstructor like NameTest followed by a keyword in an expression")
        fun nameTest_CompPIConstructorLike() {
            val expected = loadResource("tests/parser/xquery-1.0/NameTest_CompPIConstructorLike.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NameTest_CompPIConstructorLike.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (118) TypeDeclaration ; XQuery 1.0 EBNF (122) AtomicType")
    internal inner class TypeDeclaration {
        @Test
        @DisplayName("type declaration")
        fun typeDeclaration() {
            val expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration.txt")
            val actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("type declaration; compact whitespace")
        fun typeDeclaration_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SequenceType")
        fun missingSequenceType() {
            val expected = loadResource("tests/parser/xquery-1.0/TypeDeclaration_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-1.0/TypeDeclaration_MissingSequenceType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (119) SequenceType")
    internal inner class SequenceType {
        @Nested
        @DisplayName("empty-sequence()")
        internal inner class SequenceType {
            @Test
            @DisplayName("empty sequence")
            fun empty() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("empty sequence; compact whitespace")
            fun empty_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun empty_MissingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_Empty_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (121) ItemType")
        fun itemType() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (121) ItemType ; XQuery 1.0 EBNF (120) OccurrenceIndicator")
        internal inner class OccurrenceIndicator {
            @Test
            @DisplayName("zero or one")
            fun zeroOrOne() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_ZeroOrOne.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_ZeroOrOne.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("zero or one; compact whitespace")
            fun zeroOrOne_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_ZeroOrOne_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_ZeroOrOne_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("zero or more")
            fun zeroOrMore() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_ZeroOrMore.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_ZeroOrMore.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("zero or more; compact whitespace")
            fun zeroOrMore_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_ZeroOrMore_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_ZeroOrMore_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("one or more")
            fun oneOrMore() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_OneOrMore.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_OneOrMore.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("one or more; compact whitespace")
            fun oneOrMore_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/SequenceType_OneOrMore_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/SequenceType_OneOrMore_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (121) ItemType")
    internal inner class ItemType {
        @Nested
        @DisplayName("item()")
        internal inner class Item {
            @Test
            @DisplayName("item")
            fun itemType() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("item; compact whitespace")
            fun itemType_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (123) AtomicType")
        fun atomicType() {
            val expected = loadResource("tests/parser/xquery-1.0/InstanceofExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/InstanceofExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (123) KindTest")
        internal inner class KindTest {
            @Test
            @DisplayName("XQuery 1.0 EBNF (124) AnyKindTest")
            fun anyKindTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_AnyKindTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_AnyKindTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (125) DocumentTest")
            fun documentTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_DocumentTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_DocumentTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (126) TextTest")
            fun textTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_TextTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_TextTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (127) CommentTest")
            fun commentTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_CommentTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_CommentTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (128) PITest")
            fun piTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_PITest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_PITest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (129) AttributeTest")
            fun attributeTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_AttributeTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_AttributeTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (131) SchemaAttributeTest")
            fun schemaAttributeTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_SchemaAttributeTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_SchemaAttributeTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (133) ElementTest")
            fun elementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_ElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_ElementTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 1.0 EBNF (135) SchemaElementTest")
            fun schemaElementTest() {
                val expected = loadResource("tests/parser/xquery-1.0/ItemType_SchemaElementTest.txt")
                val actual = parseResource("tests/parser/xquery-1.0/ItemType_SchemaElementTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (141) IntegerLiteral")
    fun integerLiteral() {
        val expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (142) DecimalLiteral")
    fun decimalLiteral() {
        val expected = loadResource("tests/parser/xquery-1.0/DecimalLiteral.txt")
        val actual = parseResource("tests/parser/xquery-1.0/DecimalLiteral.xq")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (143) DoubleLiteral")
    internal inner class DoubleLiteral {
        @Test
        @DisplayName("double literal")
        fun doubleLiteral() {
            val expected = loadResource("tests/parser/xquery-1.0/DoubleLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DoubleLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("double literal with incomplete exponent")
        fun incompleteExponent() {
            val expected = loadResource("tests/parser/xquery-1.0/DoubleLiteral_IncompleteExponent.txt")
            val actual = parseResource("tests/parser/xquery-1.0/DoubleLiteral_IncompleteExponent.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (144) StringLiteral")
    internal inner class StringLiteral {
        @Test
        @DisplayName("string literal")
        fun stringLiteral() {
            val expected = loadResource("tests/parser/xquery-1.0/StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/StringLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("unclosed string literal")
        fun unclosedString() {
            val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_UnclosedString.txt")
            val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_UnclosedString.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (145) PredefinedEntityRef")
        internal inner class PredefinedEntityRef {
            @Test
            @DisplayName("predefined entity reference")
            fun predefinedEntityRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("incomplete entity reference")
            fun incompleteRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_IncompleteRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_IncompleteRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("empty entity reference")
            fun emptyRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_EmptyRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_PredefinedEntityRef_EmptyRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("entity reference outside a string literal")
            fun misplacedEntityRef() {
                val expected = loadResource("tests/parser/xquery-1.0/PredefinedEntityRef_MisplacedEntityRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/PredefinedEntityRef_MisplacedEntityRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (153) CharRef")
        internal inner class CharRef {
            @Test
            @DisplayName("character reference")
            fun charRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("incomplete character reference")
            fun incompleteRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_IncompleteRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_IncompleteRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("empty numeric reference")
            fun emptyNumericRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyNumericRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyNumericRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("empty hexadecimal reference")
            fun emptyHexadecimalRef() {
                val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexadecimalRef.txt")
                val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_CharRef_EmptyHexadecimalRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (146) EscapeQuot")
        fun escapeQuot() {
            val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.txt")
            val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeQuot.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (147) EscapeApos")
        fun escapeApos() {
            val expected = loadResource("tests/parser/xquery-1.0/StringLiteral_EscapeApos.txt")
            val actual = parseResource("tests/parser/xquery-1.0/StringLiteral_EscapeApos.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (151) Comment ; XQuery 1.0 EBNF (159) CommentContents")
    internal inner class Comment {
        @Test
        @DisplayName("comment")
        fun comment() {
            val expected = loadResource("tests/parser/xquery-1.0/Comment.txt")
            val actual = parseResource("tests/parser/xquery-1.0/Comment.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("unclosed comment")
        fun unclosedComment() {
            val expected = loadResource("tests/parser/xquery-1.0/Comment_UnclosedComment.txt")
            val actual = parseResource("tests/parser/xquery-1.0/Comment_UnclosedComment.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("comment end tag without comment start tag")
        fun unexpectedCommentEndTag() {
            val expected = loadResource("tests/parser/xquery-1.0/Comment_UnexpectedCommentEndTag.txt")
            val actual = parseResource("tests/parser/xquery-1.0/Comment_UnexpectedCommentEndTag.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (154) QName")
    internal inner class QName {
        @Test
        @DisplayName("error recovery: wildcard prefix part")
        fun wildcardPrefixPart() {
            val expected = loadResource("tests/parser/xquery-1.0/QName_WildcardPrefixPart.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QName_WildcardPrefixPart.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: wildcard local part")
        fun wildcardLocalPart() {
            val expected = loadResource("tests/parser/xquery-1.0/QName_WildcardLocalPart.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QName_WildcardLocalPart.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: wildcard prefix part; missing prefix part")
        fun missingPrefixPart() {
            val expected = loadResource("tests/parser/xquery-1.0/QName_WildcardLocalPart_MissingPrefixPart.txt")
            val actual = parseResource("tests/parser/xquery-1.0/QName_WildcardLocalPart_MissingPrefixPart.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (155) NCName")
    internal inner class NCName {
        @Test
        @DisplayName("error recovery: unexpected QName")
        fun unexpectedQName() {
            val expected = loadResource("tests/parser/xquery-1.0/NCName_UnexpectedQName.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NCName_UnexpectedQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: unexpected QName; missing prefix part")
        fun unexpectedQName_MissingPrefixPart() {
            val expected = loadResource("tests/parser/xquery-1.0/NCName_UnexpectedQName_MissingPrefixPart.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NCName_UnexpectedQName_MissingPrefixPart.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: wildcard")
        fun wildcard() {
            val expected = loadResource("tests/parser/xquery-1.0/NCName_Wildcard.txt")
            val actual = parseResource("tests/parser/xquery-1.0/NCName_Wildcard.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Test
    @DisplayName("XML 1.0 EBNF (3) S")
    fun s() {
        val expected = loadResource("tests/parser/xquery-1.0/S.txt")
        val actual = parseResource("tests/parser/xquery-1.0/S.xq")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (2) VersionDecl")
    internal inner class VersionDecl_XQuery30 {
        @Test
        @DisplayName("encoding only")
        fun encoding() {
            val expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.txt")
            val actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("encoding only; compact whitespace")
        fun encoding_compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("encoding only; missing encoding string")
        fun missingEncodingString() {
            val expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingEncodingString.txt")
            val actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingEncodingString.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("encoding only; missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (18) DecimalFormatDecl")
    internal inner class DecimalFormatDecl {
        @Test
        @DisplayName("decimal format declaration")
        fun decimalFormatDecl() {
            val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl.txt")
            val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing EQName")
        fun missingEQName() {
            val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingEQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingEQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("default")
        fun default() {
            val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Default.txt")
            val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Default.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("all properties")
        fun allProperties() {
            val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_AllProperties.txt")
            val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_AllProperties.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing '='")
        fun missingEquals() {
            val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingEquals.txt")
            val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingEquals.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing string literal")
        fun missingStringLiteral() {
            val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingStringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_Property_MissingStringLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'decimal-format' keyword")
        fun missingDecimalFormatKeyword() {
            val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingDecimalFormatKeyword.txt")
            val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_MissingDecimalFormatKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (31) ContextItemDecl")
    internal inner class ContextItemDecl {
        @Test
        @DisplayName("context item declaration")
        fun contextItemDecl() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("context item declaration; compact whitespace")
        fun contextItemDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: using '=' instead of ':='")
        fun equal() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_Equal.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_Equal.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing variable value")
        fun missingVarValue() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingVarValue.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingVarValue.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ':='")
        fun missingAssignment() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingAssignment.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingAssignment.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'item' keyword")
        fun missingItemKeyword() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingItemKeyword.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingItemKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("as ItemType")
        fun asItemType() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: as ItemType; missing ItemType")
        fun missingItemType() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType_MissingItemType.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_AsItemType_MissingItemType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("external")
        fun external() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("external; default value")
        fun external_DefaultValue() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: external; default value; using '=' instead of ':='")
        fun external_DefaultValue_Equal() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_Equal.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_Equal.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: external; default value; missing value")
        fun external_DefaultValue_MissingValue() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_MissingValue.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_External_DefaultValue_MissingValue.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'context' keyword")
        fun missingContextKeyword() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingContextKeyword.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingContextKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing semicolon")
        fun missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-3.0/ContextItemDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ContextItemDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (26) AnnotatedDecl ; XQuery 3.0 EBNF (27) Annotation")
    internal inner class AnnotatedDecl {
        @Test
        @DisplayName("annotation")
        fun annotation() {
            val expected = loadResource("tests/parser/xquery-3.0/Annotation.txt")
            val actual = parseResource("tests/parser/xquery-3.0/Annotation.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("annotation; compact whitespace")
        fun annotation_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/Annotation_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/Annotation_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing EQName")
        fun missingEQName() {
            val expected = loadResource("tests/parser/xquery-3.0/Annotation_MissingQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/Annotation_MissingQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parameter list")
        fun parameterList() {
            val expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList.txt")
            val actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parameter list; compact whitespace")
        fun parameterList_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: parameter list; missing literal")
        fun parameterList_MissingLiteral() {
            val expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList_MissingLiteral.txt")
            val actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList_MissingLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parameter list; multiple")
        fun parameterList_Multiple() {
            val expected = loadResource("tests/parser/xquery-3.0/Annotation_ParameterList_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-3.0/Annotation_ParameterList_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (28) VarDecl")
    internal inner class VarDecl_XQuery30 {
        @Test
        @DisplayName("external; default value")
        fun external_defaultValue() {
            val expected = loadResource("tests/parser/xquery-3.0/VarDecl_External_DefaultValue.txt")
            val actual = parseResource("tests/parser/xquery-3.0/VarDecl_External_DefaultValue.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (41) FLWORExpr")
    internal inner class FLWORExpr_XQuery30 {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (41) FLWORExpr")
        internal inner class FLWORExpr {
            @Test
            @DisplayName("relaxed ordering")
            fun relaxedOrdering() {
                val expected = loadResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FLWORExpr_RelaxedOrdering.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("nested FLWOR expressions without return clauses")
            fun nestedWithoutReturnClause() {
                val expected = loadResource("tests/parser/xquery-3.0/FLWORExpr_NestedWithoutReturnClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FLWORExpr_NestedWithoutReturnClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (43) IntermediateClause")
        internal inner class IntermediateClause {
            @Test
            @DisplayName("where + for")
            fun whereFor() {
                val expected = loadResource("tests/parser/xquery-3.0/IntermediateClause_WhereFor.txt")
                val actual = parseResource("tests/parser/xquery-3.0/IntermediateClause_WhereFor.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("for + where + let")
            fun forWhereLet() {
                val expected = loadResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.txt")
                val actual = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForWhereLet.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("for + order by + let")
            fun forOrderByLet() {
                val expected = loadResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.txt")
                val actual = parseResource("tests/parser/xquery-3.0/IntermediateClause_ForOrderByLet.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (60) WhereClause")
        internal inner class WhereClause {
            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-3.0/WhereClause_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WhereClause_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (65) OrderByClause")
        internal inner class OrderByClause {
            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-3.0/OrderByClause_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-3.0/OrderByClause_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (44) ForClause ; XQuery 3.0 EBNF (46) AllowingEmpty")
    internal inner class AllowingEmpty {
        @Test
        @DisplayName("allowing empty")
        fun sllowingEmpty() {
            val expected = loadResource("tests/parser/xquery-3.0/AllowingEmpty.txt")
            val actual = parseResource("tests/parser/xquery-3.0/AllowingEmpty.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'empty' keyword")
        fun missingEmptyKeyword() {
            val expected = loadResource("tests/parser/xquery-3.0/AllowingEmpty_MissingEmptyKeyword.txt")
            val actual = parseResource("tests/parser/xquery-3.0/AllowingEmpty_MissingEmptyKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' keyword")
        fun missingInKeyword() {
            val expected = loadResource("tests/parser/xquery-3.0/AllowingEmpty_ForBinding_MissingInKeyword.txt")
            val actual = parseResource("tests/parser/xquery-3.0/AllowingEmpty_ForBinding_MissingInKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (50) WindowClause")
    internal inner class WindowClause {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (51) TumblingWindowClause")
        internal inner class TumblingWindowClause {
            @Test
            @DisplayName("tumbling window clause")
            fun tumblingWindowClause() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("tumbling window clause; compact whitespace")
            fun tumblingWindowClause_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'window' keyword")
            fun missingWindowKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingWindowKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingWindowKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing '$'")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable name")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword")
            fun missingInKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingInKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingInKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing expression")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing start condition")
            fun missingStartCondition() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingStartCondition.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_MissingStartCondition.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("type declaration")
            fun typeDecl() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: type declaration; missing 'in' keyword")
            fun typeDecl_MissingInKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl_MissingInKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause_TypeDecl_MissingInKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (52) SlidingWindowClause")
        internal inner class SlidingWindowClause {
            @Test
            @DisplayName("sliding window clause")
            fun slidingWindowClause() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("sliding window clause; compact whitespace")
            fun slidingWindowClause_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'window' keyword")
            fun missingWindowKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingWindowKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingWindowKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing '$'")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable name")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword")
            fun missingInKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingInKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingInKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing expression")
            fun testSlidingWindowClause_MissingExpr() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing start condition")
            fun missingStartCondition() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingStartCondition.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingStartCondition.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing end condition")
            fun missingEndCondition() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingEndCondition.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_MissingEndCondition.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("type declaration")
            fun typeDecl() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: type declaration; missing 'in' keyword")
            fun typeDecl_MissingInKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl_MissingInKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SlidingWindowClause_TypeDecl_MissingInKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (53) WindowStartCondition")
        internal inner class WindowStartCondition {
            @Test
            @DisplayName("window start condition")
            fun windowStartCondition() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error condition: missing 'when' keyword")
            fun missingWhenKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowStartCondition_MissingWhenKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowStartCondition_MissingWhenKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error condition: missing expression")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowStartCondition_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowStartCondition_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (54) WindowEndCondition")
        internal inner class WindowEndCondition {
            @Test
            @DisplayName("start and end")
            fun both() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error condition: missing 'when' keyword")
            fun missingWhenKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_MissingWhenKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_MissingWhenKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error condition: missing expression")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("end only")
            fun only() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_Only.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_Only.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error condition: end only; missing 'end' keyword")
            fun only_MissingEndKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowEndCondition_Only_MissingEndKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowEndCondition_Only_MissingEndKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (55) WindowVars")
        internal inner class WindowVars {
            @Test
            @DisplayName("empty")
            fun empty() {
                val expected = loadResource("tests/parser/xquery-3.0/TumblingWindowClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TumblingWindowClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("all variables")
            fun allVars() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowVars_AllVars.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowVars_AllVars.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (55) WindowVars ; XQuery 3.0 EBNF (47) PositionalVar")
        fun positionalVar() {
            val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Position.txt")
            val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Position.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (55) WindowVars ; XQuery 3.0 EBNF (56) CurrentItem")
        internal inner class CurrentItem {
            @Test
            @DisplayName("current item")
            fun currentItem() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Current.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Current.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable name")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Current_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Current_MissingVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (55) WindowVars ; XQuery 3.0 EBNF (57) PreviousItem")
        internal inner class PreviousItem {
            @Test
            @DisplayName("previous item")
            fun previousItem() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Previous.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Previous.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing '$'")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable name")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Previous_MissingVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (55) WindowVars ; XQuery 3.0 EBNF (58) NextItem")
        internal inner class NextItem {
            @Test
            @DisplayName("next item")
            fun nextItem() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Next.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Next.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing '$'")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable name")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarName.txt")
                val actual = parseResource("tests/parser/xquery-3.0/WindowVars_Next_MissingVarName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (59) CountClause")
    internal inner class CountClause {
        @Test
        @DisplayName("count clause")
        fun countClause() {
            val expected = loadResource("tests/parser/xquery-3.0/CountClause.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CountClause.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("count clause; compact whitespace")
        fun countClause_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/CountClause_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CountClause_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing '$'")
        fun missingVariableIndicator() {
            val expected = loadResource("tests/parser/xquery-3.0/CountClause_MissingVariableIndicator.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CountClause_MissingVariableIndicator.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing variable name")
        fun missingVarName() {
            val expected = loadResource("tests/parser/xquery-3.0/CountClause_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CountClause_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (61) GroupByClause ; XQuery 3.0 EBNF (62) GroupingSpecList")
    internal inner class GroupByClause {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (61) GroupByClause ; XQuery 3.0 EBNF (62) GroupingSpecList")
        internal inner class GroupByClause {
            @Test
            @DisplayName("group by clause")
            fun groupByClause() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupByClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("group by clause; compact whitespace")
            fun groupByClause_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupByClause_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupByClause_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'by' keyword")
            fun missingByKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupByClause_MissingByKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupByClause_MissingByKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing GroupingSpecList")
            fun missingGroupingSpecList() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupByClause_MissingGroupingSpecList.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupByClause_MissingGroupingSpecList.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (62) GroupingSpecList")
        internal inner class GroupingSpecList {
            @Test
            @DisplayName("single")
            fun single() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupByClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: multiple; missing GroupingSpec")
            fun multiple_MissingGroupingSpec() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple_MissingGroupingSpec.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpecList_Multiple_MissingGroupingSpec.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (63) GroupingSpec")
        internal inner class GroupingSpec {
            @Test
            @DisplayName("grouping spec")
            fun testGroupingSpec() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupByClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupByClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("value")
            fun value() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("value; compact whitespace")
            fun value_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: value; using '=' instead of ':='")
            fun value_Equal() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value_Equal.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value_Equal.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: value; missing value")
            fun value_MissingValue() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Value_MissingValue.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Value_MissingValue.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("typed value")
            fun typedValue() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("typed value; compact whitespace")
            fun typedValue_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: typed value; using '=' instead of ':='")
            fun typedValue_Equal() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_Equal.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_Equal.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: typed value; missing assignment")
            fun missingAssignment() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingAssignment.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingAssignment.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: typed value; missing value")
            fun typedValue_MissingValue() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingValue.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_TypedValue_MissingValue.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("collation")
            fun collation() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Collation.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Collation.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: collation; missing URI literal")
            fun collation_MissingUriLiteral() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_Collation_MissingUriLiteral.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_Collation_MissingUriLiteral.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("value and collation")
            fun valueAndCollation() {
                val expected = loadResource("tests/parser/xquery-3.0/GroupingSpec_ValueAndCollation.txt")
                val actual = parseResource("tests/parser/xquery-3.0/GroupingSpec_ValueAndCollation.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (59) SwitchExpr ; XQuery 3.0 EBNF (59) SwitchCaseClause")
    internal inner class SwitchExpr {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (59) SwitchExpr")
        internal inner class SwitchExpr {
            @Test
            @DisplayName("switch expression")
            fun switchExpr() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("switch expression; compact whitespace")
            fun switchExpr_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing expression")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing case clause")
            fun missingCaseClause() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingCaseClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingCaseClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'default' keyword")
            fun missingDefaultKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingDefaultKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingDefaultKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' keyword")
            fun missingReturnKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing return expression")
            fun missingReturnExpr() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr_MissingReturnExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (59) SwitchCaseClause")
        internal inner class SwitchCaseClause {
            @Test
            @DisplayName("single clause")
            fun singleClause() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing case operand")
            fun missingCaseOperand() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingCaseOperand.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingCaseOperand.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' keyword")
            fun missingReturnKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing return expression")
            fun missingReturnExpr() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MissingReturnExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple clauses")
            fun multipleClauses() {
                val expected = loadResource("tests/parser/xquery-3.0/SwitchCaseClause_MultipleCases.txt")
                val actual = parseResource("tests/parser/xquery-3.0/SwitchCaseClause_MultipleCases.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (75) CaseClause ; XQuery 3.0 EBNF (76) SequenceTypeUnion")
    internal inner class CaseClause {
        @Test
        @DisplayName("sequence type union")
        fun sequenceTypeUnion() {
            val expected = loadResource("tests/parser/xquery-3.0/SequenceTypeUnion.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("sequence type union; compact whitespace")
        fun sequenceTypeUnion_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/SequenceTypeUnion_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SequenceType")
        fun missingSequenceType() {
            val expected = loadResource("tests/parser/xquery-3.0/SequenceTypeUnion_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SequenceTypeUnion_MissingSequenceType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (78) TryCatchExpr")
    internal inner class TryCatchExpr {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (79) TryClause ; XQuery 3.0 EBNF (80) TryTargetExpr")
        internal inner class TryClause {
            @Test
            @DisplayName("try clause")
            fun tryClause() {
                val expected = loadResource("tests/parser/xquery-3.0/TryClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TryClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xquery-3.0/TryClause_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TryClause_MissingClosingBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (81) CatchClause")
        internal inner class CatchClause {
            @Test
            @DisplayName("catch clause")
            fun catchClause() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("catch clause; compact whitespace")
            fun catchClause_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchClause_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchClause_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing catch error list")
            fun missingCatchErrorList() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchClause_MissingCatchErrorList.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchClause_MissingCatchErrorList.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing opening brace")
            fun missingOpeningBrace() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchClause_MissingOpeningBrace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchClause_MissingOpeningBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchClause_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchClause_MissingClosingBrace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchClause_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchClause_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: catch clause with AxisStep")
            fun catchClauseWithAxisStep() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchClause_AxisStep.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchClause_AxisStep.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: catch clause with AxisStep; multiple")
            fun catchClauseWithAxisStep_multiple() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchClause_AxisStep_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchClause_AxisStep_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (82) CatchErrorList")
        internal inner class CatchErrorList {
            @Test
            @DisplayName("single")
            fun single() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchClause.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchClause.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchErrorList_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchErrorList_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: multiple; missing name test")
            fun multiple_MissingNameTest() {
                val expected = loadResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_MissingNameTest.txt")
                val actual = parseResource("tests/parser/xquery-3.0/CatchErrorList_Multiple_MissingNameTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (86) StringConcatExpr")
    internal inner class StringConcatExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing RangeExpr")
        fun missingRangeExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_MissingRangeExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_MissingRangeExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/StringConcatExpr_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (101) ValidateExpr")
    internal inner class ValidateExpr_XQuery30 {
        @Test
        @DisplayName("type")
        fun type() {
            val expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("type; compact whitespace")
        fun type_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing type name")
        fun missingTypeName() {
            val expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingTypeName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingTypeName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing opening brace")
        fun missingOpeningBrace() {
            val expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingOpeningBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_MissingOpeningBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing expression")
        fun missingExpr() {
            val expected = loadResource("tests/parser/xquery-3.1/ValidateExpr_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/ValidateExpr_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (106) SimpleMapExpr")
    internal inner class SimpleMapExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing PathExpr")
        fun missingPathExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_MissingPathExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_MissingPathExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-3.0/SimpleMapExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SimpleMapExpr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (117) NodeTest ; XQuery 3.0 EBNF (172) KindTest")
    internal inner class NodeTest_XQuery30 {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (177) NamespaceNodeTest")
        internal inner class NamespaceNodeTest {
            @Test
            @DisplayName("namespace node test")
            fun namespaceNodeTest() {
                val expected = loadResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("namespace node test; compact whitespace")
            fun namespaceNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NodeTest_NamespaceNodeTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (117) NodeTest ; XQuery 3.0 EBNF (118) NameTest")
    internal inner class NameTest_XQuery30 {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (201) URIQualifiedName ; XQuery 3.0 EBNF (202) BracedURILiteral")
        internal inner class URIQualifiedName {
            @Test
            @DisplayName("NCName local name")
            fun ncname() {
                val expected = loadResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_NCNameLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_NCNameLocalPart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("keyword local name")
            fun keyword() {
                val expected = loadResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_KeywordLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_KeywordLocalPart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing local name")
            fun missingLocalName() {
                val expected = loadResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_MissingLocalPart.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_MissingLocalPart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: incomplete braced URI literal")
            fun incompleteBracedURILiteral() {
                val expected = loadResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_IncompleteBracedURILiteral.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NameTest_URIQualifiedName_IncompleteBracedURILiteral.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (119) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("BracedURILiteral prefix")
            fun bracedURILiteralPrefix() {
                val expected = loadResource("tests/parser/xquery-3.0/NameTest_Wildcard_BracedURILiteral.txt")
                val actual = parseResource("tests/parser/xquery-3.0/NameTest_Wildcard_BracedURILiteral.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (120) PostfixExpr; XQuery 3.0 EBNF (121) ArgumentList")
    internal inner class PostfixExpr_ArgumentList {
        @Test
        @DisplayName("argument list")
        fun argumentList() {
            val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.txt")
            val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("argument list; compact whitespace")
        fun argumentList_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_ArgumentList_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("mixed")
        fun mixed() {
            val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_Mixed.txt")
            val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_Mixed.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("mixed; compact whitespace")
        fun mixed_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/PostfixExpr_Mixed_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/PostfixExpr_Mixed_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (133) FunctionCall ; XQuery 3.0 EBNF (121) ArgumentList")
    internal inner class FunctionCall_XQuery30 {
        @Test
        @DisplayName("XQuery 3.0 EBNF (135) ArgumentPlaceholder")
        fun argumentPlaceholder() {
            val expected = loadResource("tests/parser/xquery-3.0/ArgumentPlaceholder.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ArgumentPlaceholder.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (156) CompNamespaceConstructor")
    internal inner class CompNamespaceConstructor {
        @Test
        @DisplayName("tag name: NCName")
        fun testCompNamespaceConstructor() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: NCName; compact whitespace")
        fun testCompNamespaceConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing closing brace")
        fun testCompNamespaceConstructor_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression")
        fun testCompNamespaceConstructor_PrefixExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; compact whitespace")
        fun testCompNamespaceConstructor_PrefixExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: expression; missing closing brace")
        fun testCompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingClosingPrefixExprBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("content expression; missing expression")
        fun testCompNamespaceConstructor_PrefixExpr_MissingURIExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingURIExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_PrefixExpr_MissingURIExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("tag name: string literal (error recovery)")
        fun testCompNamespaceConstructor_StringLiteral() {
            val expected = loadResource("tests/parser/xquery-3.0/CompNamespaceConstructor_StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompNamespaceConstructor_StringLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("CompNamespaceConstructor like NameTest followed by a keyword in an expression")
        fun nameTest_CompNamespaceConstructorLike() {
            val expected = loadResource("tests/parser/xquery-3.0/NameTest_CompNamespaceConstructorLike.txt")
            val actual = parseResource("tests/parser/xquery-3.0/NameTest_CompNamespaceConstructorLike.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (164) NamedFunctionRef")
    internal inner class NamedFunctionRef {
        @Test
        @DisplayName("named function reference")
        fun namedFunctionRef() {
            val expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef.txt")
            val actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("named function reference; compact whitespace")
        fun namedFunctionRef_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing arity")
        fun missingArity() {
            val expected = loadResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.txt")
            val actual = parseResource("tests/parser/xquery-3.0/NamedFunctionRef_MissingArity.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (165) InlineFunctionExpr")
    internal inner class InlineFunctionExpr {
        @Test
        @DisplayName("inline function expression")
        fun inlineFunctionExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("inline function expression; compact whitespace")
        fun inlineFunctionExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing opening brace")
        fun missingOpeningBrace() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingOpeningBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingOpeningBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing function body")
        fun missingFunctionBody() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingFunctionBody.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_MissingFunctionBody.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parameter list")
        fun paramList() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ParamList.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ParamList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("return type")
        fun returnType() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("return type; missing SequenceType")
        fun returnType_MissingSequenceType() {
            val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_ReturnType_MissingSequenceType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (27) Annotation")
        internal inner class Annotation {
            @Test
            @DisplayName("single")
            fun single() {
                val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation.txt")
                val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'function' keyword")
            fun missingFunctionKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingFunctionKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing opening parenthesis")
            fun missingOpeningParenthesis() {
                val expected = loadResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingOpeningParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-3.0/InlineFunctionExpr_Annotation_MissingOpeningParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (170) ItemType ; XQuery 3.0 EBNF (172) KindTest")
    internal inner class ItemType_XQuery30 {
        @Test
        @DisplayName("XQuery 3.0 EBNF (177) NamespaceNodeTest")
        fun namespaceNodeTest() {
            val expected = loadResource("tests/parser/xquery-3.0/ItemType_NamespaceNodeTest.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ItemType_NamespaceNodeTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (191) FunctionTest")
    internal inner class FunctionTest {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (27) Annotation")
        internal inner class Annotation {
            @Test
            @DisplayName("single annotation")
            fun singleAnnotation() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single annotation; compact whitespace")
            fun singleAnnotation_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple annotations")
            fun multipleAnnotations() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple annotations; compact whitespace")
            fun multipleAnnotations_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_MultipleAnnotations_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("typed function with annotations")
            fun typedFunctionWithAnnotations() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_TypedFunctionWithAnnotations.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_TypedFunctionWithAnnotations.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'function' keyword")
            fun missingFunctionKeyword() {
                val expected = loadResource("tests/parser/xquery-3.0/FunctionTest_MissingFunctionKeyword.txt")
                val actual = parseResource("tests/parser/xquery-3.0/FunctionTest_MissingFunctionKeyword.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (192) AnyFunctionTest")
        internal inner class AnyFunctionTest {
            @Test
            @DisplayName("any function test")
            fun anyFunctionTest() {
                val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest.txt")
                val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("any function test; compact whitespace")
            fun anyFunctionTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: unexpected return type")
            fun unexpectedReturnType() {
                val expected = loadResource("tests/parser/xquery-3.0/AnyFunctionTest_UnexpectedReturnType.txt")
                val actual = parseResource("tests/parser/xquery-3.0/AnyFunctionTest_UnexpectedReturnType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (193) TypedFunctionTest")
        internal inner class TypedFunctionTest {
            @Test
            @DisplayName("single parameter")
            fun singleParameter() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single parameter; compact whitespace")
            fun singleParameter_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple parameters")
            fun multipleParameters() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple parameters; compact whitespace")
            fun multipleParameters_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: multiple parameters; missing SequenceType")
            fun multipleParameters_MissingSequenceType() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_MissingSequenceType.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_Multiple_MissingSequenceType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple parameters with OccurrenceIndicator")
            fun multipleParametersWithOccurenceIndicator() {
                // This is testing handling of whitespace before parsing the next comma.
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_MultipleWithOccurrenceIndicator.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_MultipleWithOccurrenceIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing return type")
            fun missingReturnType() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_MissingReturnType.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_MissingReturnType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing SequnceType from the return type")
            fun returnType_MissingSequenceType() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_MissingReturnType_MissingSequenceType.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_MissingReturnType_MissingSequenceType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("empty parameter list")
            fun emptyParameterList() {
                val expected = loadResource("tests/parser/xquery-3.0/TypedFunctionTest_EmptyTypeList.txt")
                val actual = parseResource("tests/parser/xquery-3.0/TypedFunctionTest_EmptyTypeList.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (194) ParenthesizedItemType")
    internal inner class ParenthesizedItemType {
        @Test
        @DisplayName("parenthesized item type")
        fun parenthesizedItemType() {
            val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized item type; compact whitespace")
        fun parenthesizedItemType_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing item type")
            fun missingItemType() {
                val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingItemType.txt")
                val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingItemType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("error recovery; item type as sequence type")
        internal inner class SequenceType {
            @Test
            @DisplayName("empty sequence")
            fun emptySequence() {
                val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_EmptySequence.txt")
                val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_EmptySequence.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("occurrence indicator")
            fun occurrenceIndicator() {
                val expected = loadResource("tests/parser/xquery-3.0/ParenthesizedItemType_OccurrenceIndicator.txt")
                val actual = parseResource("tests/parser/xquery-3.0/ParenthesizedItemType_OccurrenceIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (196) EQName")
    internal inner class EQName {
        @Test
        @DisplayName("XQuery 3.0 EBNF (18) DecimalFormatDecl")
        fun decimalFormatDecl() {
            val expected = loadResource("tests/parser/xquery-3.0/DecimalFormatDecl_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/DecimalFormatDecl_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (27) Annotation")
        fun annotation() {
            val expected = loadResource("tests/parser/xquery-3.0/Annotation_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/Annotation_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (28) VarDecl")
        fun varDecl() {
            val expected = loadResource("tests/parser/xquery-3.0/VarDecl_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/VarDecl_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (32) FunctionDecl")
        fun functionDecl() {
            val expected = loadResource("tests/parser/xquery-3.0/FunctionDecl_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/FunctionDecl_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (34) Param")
        fun param() {
            val expected = loadResource("tests/parser/xquery-3.0/Param_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/Param_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (37) OptionDecl")
        fun optionDecl() {
            val expected = loadResource("tests/parser/xquery-3.0/OptionDecl_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/OptionDecl_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (44) ForClause")
        fun forClause() {
            val expected = loadResource("tests/parser/xquery-3.0/ForClause_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ForClause_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (44) ForClause ; XQuery 3.0 EBNF (47) PositionalVar")
        fun positionalVar() {
            val expected = loadResource("tests/parser/xquery-3.0/PositionalVar_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/PositionalVar_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (48) LetClause")
        fun letClause() {
            val expected = loadResource("tests/parser/xquery-3.0/LetClause_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/LetClause_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (70) QuantifiedExpr")
        fun quantifiedExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/QuantifiedExpr_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/QuantifiedExpr_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (74) TypeswitchExpr")
        fun typeswitchExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/TypeswitchExpr_Variable_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/TypeswitchExpr_Variable_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (75) CaseClause")
        fun caseClause() {
            val expected = loadResource("tests/parser/xquery-3.0/CaseClause_Variable_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CaseClause_Variable_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (101) ValidateExpr ; XQuery 3.0 EBNF (190) TypeName")
        fun validateExpr() {
            val expected = loadResource("tests/parser/xquery-3.0/ValidateExpr_Type_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ValidateExpr_Type_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (104) Pragma")
        fun pragma() {
            val expected = loadResource("tests/parser/xquery-3.0/Pragma_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/Pragma_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (111) ForwardStep")
        fun forwardStep() {
            val expected = loadResource("tests/parser/xquery-3.0/ForwardStep_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ForwardStep_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (127) VarRef ; XQuery 3.0 EBNF (128) VarName")
        fun varRef() {
            val expected = loadResource("tests/parser/xquery-3.0/VarRef_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/VarRef_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (133) FunctionCall")
        fun functionCall() {
            val expected = loadResource("tests/parser/xquery-3.0/FunctionCall_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/FunctionCall_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (153) CompElemConstructor")
        fun compElemConstructor() {
            val expected = loadResource("tests/parser/xquery-3.0/CompElemConstructor_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompElemConstructor_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (155) CompAttrConstructor")
        fun compAttrConstructor() {
            val expected = loadResource("tests/parser/xquery-3.0/CompAttrConstructor_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/CompAttrConstructor_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (166) SingleType")
        fun singleType() {
            val expected = loadResource("tests/parser/xquery-3.0/SingleType_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/SingleType_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (167) TypeDeclaration ; XQuery 3.0 EBNF (171) AtomicOrUnionType")
        fun typeDeclaration() {
            val expected = loadResource("tests/parser/xquery-3.0/TypeDeclaration_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/TypeDeclaration_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (179) AttributeTest ; XQuery 3.0 EBNF (187) AttributeName")
        fun attributeTest() {
            val expected = loadResource("tests/parser/xquery-3.0/AttributeTest_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/AttributeTest_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (183) ElementTest ; XQuery 3.0 EBNF (188) ElementName")
        fun elementTest() {
            val expected = loadResource("tests/parser/xquery-3.0/ElementTest_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.0/ElementTest_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (190) TypeName")
        internal inner class TypeName {
            @Test
            @DisplayName("XQuery 3.0 EBNF (179) AttributeTest")
            fun attributeTest() {
                val expected = loadResource("tests/parser/xquery-3.0/AttributeTest_TypeName_EQName.txt")
                val actual = parseResource("tests/parser/xquery-3.0/AttributeTest_TypeName_EQName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.0 EBNF (183) ElementTest")
            fun elementTest() {
                val expected = loadResource("tests/parser/xquery-3.0/ElementTest_TypeName_EQName.txt")
                val actual = parseResource("tests/parser/xquery-3.0/ElementTest_TypeName_EQName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (201) URIQualifiedName ; XQuery 3.0 EBNF (202) BracedURILiteral")
    internal inner class URIQualifiedName {
        @Nested
        @DisplayName("XQuery 3.0 EBNF (201) URIQualifiedName ; XQuery 3.0 EBNF (202) BracedURILiteral")
        internal inner class URIQualifiedName {
            @Test
            @DisplayName("error recovery: wildcard")
            fun wildcard() {
                val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_Wildcard.txt")
                val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_Wildcard.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (202) BracedURILiteral ; XQuery 3.0 EBNF (203) PredefinedEntityRef")
        internal inner class PredefinedEntityRef {
            @Test
            @DisplayName("predefined entity reference")
            fun predefinedEntityRef() {
                val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef.txt")
                val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: incomplete reference")
            fun incompleteRef() {
                val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_IncompleteRef.txt")
                val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_IncompleteRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: empty reference")
            fun emptyRef() {
                val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_EmptyRef.txt")
                val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_PredefinedEntityRef_EmptyRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.0 EBNF (202) BracedURILiteral ; XQuery 3.0 EBNF (211) CharRef")
        internal inner class CharRef {
            @Test
            @DisplayName("character reference")
            fun charRef() {
                val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef.txt")
                val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: incomplete reference")
            fun incompleteRef() {
                val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_IncompleteRef.txt")
                val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_IncompleteRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: empty numeric reference")
            fun emptyNumericRef() {
                val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyNumericRef.txt")
                val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyNumericRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: empty hexadecimal reference")
            fun emptyHexadecimalRef() {
                val expected = loadResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyHexadecimalRef.txt")
                val actual = parseResource("tests/parser/xquery-3.0/BracedURILiteral_CharRef_EmptyHexadecimalRef.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (18) DecimalFormatDecl ; XQuery 3.1 EBNF (19) DFPropertyName")
    fun decimalFormatDecl_XQuery31() {
        val expected = loadResource("tests/parser/xquery-3.1/DecimalFormatDecl_Property_XQuery31.txt")
        val actual = parseResource("tests/parser/xquery-3.1/DecimalFormatDecl_Property_XQuery31.xq")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr")
    internal inner class EnclosedExpr {
        @Nested
        @DisplayName("missing Expr")
        internal inner class MissingExpr {
            @Test
            @DisplayName("XQuery 3.1 EBNF (36) EnclosedExpr")
            fun enclosedExpr() {
                val expected = loadResource("tests/parser/xquery-3.1/EnclosedExpr_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/EnclosedExpr_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (80) EnclosedTryTargetExpr")
            fun enclosedTryTargetExpr() {
                val expected = loadResource("tests/parser/xquery-3.1/TryClause_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/TryClause_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (81) CatchClause")
            fun catchClause() {
                val expected = loadResource("tests/parser/xquery-3.1/CatchClause_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/CatchClause_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (135) OrderedExpr")
            fun orderedExpr() {
                val expected = loadResource("tests/parser/xquery-3.1/OrderedExpr_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/OrderedExpr_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (136) UnorderedExpr")
            fun unorderedExpr() {
                val expected = loadResource("tests/parser/xquery-3.1/UnorderedExpr_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/UnorderedExpr_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (148) CommonContent ; XQuery 3.1 EBNF (144) DirAttributeValue")
            fun dirAttributeValue() {
                val expected = loadResource("tests/parser/xquery-3.1/DirAttributeValue_CommonContent_EnclosedExpr_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/DirAttributeValue_CommonContent_EnclosedExpr_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (148) CommonContent ; XQuery 3.1 EBNF (147) DirElemContent")
            fun dirElemContent() {
                val expected = loadResource("tests/parser/xquery-3.1/DirElemContent_CommonContent_EnclosedExpr_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/DirElemContent_CommonContent_EnclosedExpr_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (156) CompDocConstructor")
            fun compDocConstructor() {
                val expected = loadResource("tests/parser/xquery-3.1/CompDocConstructor_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/CompDocConstructor_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (160) CompNamespaceConstructor ; XQuery 3.1 EBNF (162) EnclosedPrefixExpr")
            fun enclosedPrefixExpr() {
                val expected = loadResource("tests/parser/xquery-3.1/CompNamespaceConstructor_PrefixExpr_MissingPrefixExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_PrefixExpr_MissingPrefixExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (160) CompNamespaceConstructor ; XQuery 3.1 EBNF (163) EnclosedURIExpr")
            fun enclosedUriExpr() {
                val expected = loadResource("tests/parser/xquery-3.1/CompNamespaceConstructor_MissingURIExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/CompNamespaceConstructor_MissingURIExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (164) CompTextConstructor")
            fun compTextConstructor() {
                val expected = loadResource("tests/parser/xquery-3.1/CompTextConstructor_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/CompTextConstructor_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (165) CompCommentConstructor")
            fun compCommentConstructor() {
                val expected = loadResource("tests/parser/xquery-3.1/CompCommentConstructor_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/CompCommentConstructor_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr ; XQuery 3.1 EBNF (127) ArrowFunctionSpecifier")
    internal inner class ArrowExpr {
        @Test
        @DisplayName("arrow function specifier: EQName")
        fun arrowFunctionSpecifier_EQName() {
            val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_EQName.txt")
            val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("arrow function specifier: VarRef")
        fun arrowFunctionSpecifier_VarRef() {
            val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_VarRef.txt")
            val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_VarRef.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("arrow function specifier: ParenthesizedExpr")
        fun arrowFunctionSpecifier_ParenthesizedExpr() {
            val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_ParenthesizedExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_ParenthesizedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ArgumentList")
        fun missingArgumentList() {
            val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_MissingArgumentList.txt")
            val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_MissingArgumentList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing function specifier")
        fun missingFunctionSpecifier() {
            val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_MissingFunctionSpecifier.txt")
            val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_MissingFunctionSpecifier.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple arrows")
        fun multipleArrows() {
            val expected = loadResource("tests/parser/xquery-3.1/ArrowExpr_MultipleArrows.txt")
            val actual = parseResource("tests/parser/xquery-3.1/ArrowExpr_MultipleArrows.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (121) PostfixExpr ; XQuery 3.1 EBNF (125) Lookup ; XQuery 3.1 EBNF (126) KeySpecifier")
    internal inner class PostfixExpr_Lookup {
        @Test
        @DisplayName("key specifier; NCName")
        fun ncname() {
            val expected = loadResource("tests/parser/xquery-3.1/Lookup_NCName.txt")
            val actual = parseResource("tests/parser/xquery-3.1/Lookup_NCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; IntegerLiteral")
        fun integerLiteral() {
            val expected = loadResource("tests/parser/xquery-3.1/Lookup_IntegerLiteral.txt")
            val actual = parseResource("tests/parser/xquery-3.1/Lookup_IntegerLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; ParenthesizedExpr")
        fun parenthesizedExpr() {
            val expected = loadResource("tests/parser/xquery-3.1/Lookup_ParenthesizedExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/Lookup_ParenthesizedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; ParenthesizedExpr; missing VarName")
        fun parenthesizedExpr_missingVarName() {
            val expected = loadResource("tests/parser/xquery-3.1/Lookup_ParenthesizedExpr_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-3.1/Lookup_ParenthesizedExpr_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; ParenthesizedExpr; missing VarName; multiple")
        fun parenthesizedExpr_missingVarName_multiple() {
            val expected = loadResource("tests/parser/xquery-3.1/Lookup_ParenthesizedExpr_MissingVarName_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-3.1/Lookup_ParenthesizedExpr_MissingVarName_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; wildcard")
        fun wildcard() {
            val expected = loadResource("tests/parser/xquery-3.1/Lookup_Wildcard.txt")
            val actual = parseResource("tests/parser/xquery-3.1/Lookup_Wildcard.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/Lookup_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/Lookup_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing KeySpecifier")
        fun missingKeySpecifier() {
            val expected = loadResource("tests/parser/xquery-3.1/Lookup_MissingKeySpecifier.txt")
            val actual = parseResource("tests/parser/xquery-3.1/Lookup_MissingKeySpecifier.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (170) MapConstructor ; XQuery 3.1 EBNF (171) MapConstructorEntry")
    internal inner class MapConstructor {
        @Test
        @DisplayName("empty")
        fun empty() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructor.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty; compact whitespace")
        fun empty_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructor_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("StringLiteral map key expression")
        fun stringLiteral() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("StringLiteral map key expression; compact whitespace")
        fun stringLiteral_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing key-value separator (colon)")
        fun missingSeparator() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingSeparator.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingSeparator.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing map value expression")
        fun missingValueExpr() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_MissingValueExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing map constructor entry after comma")
        fun missingEntry() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_MissingEntry.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_Multiple_MissingEntry.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("NCName map key expression")
        fun ncname() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("NCName map key expression; whitespace after colon")
        fun ncname_WhitespaceAfterColon() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_WhitespaceAfterColon.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_WhitespaceAfterColon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("NCName map key expression; compact whitespace")
        fun ncname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_NCName_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("QName map key expression")
        fun qname_KeyExpr() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_KeyExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_KeyExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("QName map value expression")
        fun qname_ValueExpr() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_ValueExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_ValueExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("QName map key expression; compact whitespace")
        fun qname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_QName_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("VarRef map key expression")
        fun varRef_NCName() {
            val expected = loadResource("tests/parser/xquery-3.1/MapConstructorEntry_VarRef_NCName.txt")
            val actual = parseResource("tests/parser/xquery-3.1/MapConstructorEntry_VarRef_NCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (175) SquareArrayConstructor")
    internal inner class SquareArrayConstructor {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor.txt")
            val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty")
        fun empty() {
            val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; missing expression after comma")
        fun multiple_MissingExpr() {
            val expected = loadResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/SquareArrayConstructor_Multiple_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (176) CurlyArrayConstructor")
    internal inner class CurlyArrayConstructor {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor.txt")
            val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty")
        fun empty() {
            val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; missing expression after comma")
        fun multiple_MissingExpr() {
            val expected = loadResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/CurlyArrayConstructor_Multiple_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (177) StringConstructor")
    internal inner class StringConstructor {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (177) StringConstructor ; XQuery 3.1 EBNF (178) StringConstructorContent")
        internal inner class StringConstructor {
            @Test
            @DisplayName("string constructor")
            fun stringConstructor() {
                val expected = loadResource("tests/parser/xquery-3.1/StringConstructor.txt")
                val actual = parseResource("tests/parser/xquery-3.1/StringConstructor.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing end of string")
            fun missingEndOfString() {
                val expected = loadResource("tests/parser/xquery-3.1/StringConstructor_MissingEndOfString.txt")
                val actual = parseResource("tests/parser/xquery-3.1/StringConstructor_MissingEndOfString.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("empty")
            fun empty() {
                val expected = loadResource("tests/parser/xquery-3.1/StringConstructor_Empty.txt")
                val actual = parseResource("tests/parser/xquery-3.1/StringConstructor_Empty.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 3.1 EBNF (180) StringConstructorInterpolation")
        internal inner class StringConstructorInterpolation {
            @Test
            @DisplayName("string constructor interpolation")
            fun stringConstructorInterpolation() {
                val expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation.txt")
                val actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("string constructor interpolation; compact whitespace")
            fun stringConstructorInterpolation_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing expression")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MissingExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MissingExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple expressions")
            fun multipleExpr() {
                val expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MultipleExpr.txt")
                val actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_MultipleExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("at the start of a string constructor")
            fun atStart() {
                val expected = loadResource("tests/parser/xquery-3.1/StringConstructorInterpolation_AtStart.txt")
                val actual = parseResource("tests/parser/xquery-3.1/StringConstructorInterpolation_AtStart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (181) UnaryLookup ; XQuery 3.1 EBNF (126) KeySpecifier")
    internal inner class UnaryLookup {
        @Test
        @DisplayName("key specifier; NCName")
        fun ncname() {
            val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_NCName.txt")
            val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_NCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; IntegerLiteral")
        fun integerLiteral() {
            val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_IntegerLiteral.txt")
            val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_IntegerLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; ParenthesizedExpr")
        fun parenthesizedExpr() {
            val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_ParenthesizedExpr.txt")
            val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_ParenthesizedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; ParenthesizedExpr; missing VarName")
        fun parenthesizedExpr_missingVarName() {
            val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_ParenthesizedExpr_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_ParenthesizedExpr_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; ParenthesizedExpr; missing VarName; multiple")
        fun parenthesizedExpr_missingVarName_multiple() {
            val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_ParenthesizedExpr_MissingVarName_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_ParenthesizedExpr_MissingVarName_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; wildcard")
        fun wildcard() {
            val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_Wildcard.txt")
            val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_Wildcard.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing KeySpecifier")
        fun missingKeySpecifier() {
            val expected = loadResource("tests/parser/xquery-3.1/UnaryLookup_MissingKeySpecifier.txt")
            val actual = parseResource("tests/parser/xquery-3.1/UnaryLookup_MissingKeySpecifier.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (211) AnyMapTest")
    internal inner class AnyMapTest {
        @Test
        @DisplayName("any map test")
        fun anyMapTest() {
            val expected = loadResource("tests/parser/xquery-3.1/AnyMapTest.txt")
            val actual = parseResource("tests/parser/xquery-3.1/AnyMapTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("any map test; compact whitespace")
        fun anyMapTest_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/AnyMapTest_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/AnyMapTest_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing wildcard")
        fun missingWildcard() {
            val expected = loadResource("tests/parser/xquery-3.1/AnyMapTest_MissingWildcard.txt")
            val actual = parseResource("tests/parser/xquery-3.1/AnyMapTest_MissingWildcard.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-3.1/AnyMapTest_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-3.1/AnyMapTest_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (212) TypedMapTest")
    internal inner class TypedMapTest {
        @Test
        @DisplayName("typed map test")
        fun typedMapTest() {
            val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest.txt")
            val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("typed map test; compact whitespace")
        fun typedMapTest_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing AtomicOrUnionType")
        fun missingAtomicOrUnionType() {
            val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingAtomicOrUnionType.txt")
            val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingAtomicOrUnionType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing comma")
        fun missingComma() {
            val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingComma.txt")
            val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingComma.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SequenceType")
        fun missingSequenceType() {
            val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingSequenceType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-3.1/TypedMapTest_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/TypedMapTest_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (214) AnyArrayTest")
    internal inner class AnyArrayTest {
        @Test
        @DisplayName("any array test")
        fun anyArrayTest() {
            val expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest.txt")
            val actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("any array test; compact whitespace")
        fun anyArrayTest_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing wildcard")
        fun missingWildcard() {
            val expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest_MissingWildcard.txt")
            val actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest_MissingWildcard.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-3.1/AnyArrayTest_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-3.1/AnyArrayTest_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (215) TypedArrayTest")
    internal inner class TypedArrayTest {
        @Test
        @DisplayName("typed array test")
        fun typedArrayTest() {
            val expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest.txt")
            val actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("typed array test; compact whitespace")
        fun typedArrayTest_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SequenceType")
        fun missingSequenceType() {
            val expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest_MissingSequenceType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-3.1/TypedArrayTest_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-3.1/TypedArrayTest_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (25) DefaultNamespaceDecl")
    internal inner class DefaultNamespaceDecl_XQuery40 {
        @Test
        @DisplayName("type")
        fun type() {
            val expected = loadResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type.txt")
            val actual = parseResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("type; compact whitespace")
        fun type_compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: type; missing URI")
        fun type_missingUri() {
            val expected = loadResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type_MissingUri.txt")
            val actual = parseResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type_MissingUri.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: type; missing 'namespace' keyword")
        fun type_MissingNamespaceKeyword() {
            val expected = loadResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type_MissingNamespaceKeyword.txt")
            val actual = parseResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type_MissingNamespaceKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: type; missing 'default' keyword")
        fun type_missingDefaultKeyword() {
            val expected = loadResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type_MissingDefaultKeyword.txt")
            val actual = parseResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type_MissingDefaultKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: type; missing semicolon")
        fun type_missingSemicolon() {
            val expected = loadResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-4.0/DefaultNamespaceDecl_Type_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (43) WithExpr ; XQuery 4.0 ED EBNF (44) NamespaceDeclaration")
    internal inner class WithExpr {
        @Test
        @DisplayName("single NamespaceDeclaration")
        fun singleNamespaceDeclaration() {
            val expected = loadResource("tests/parser/xquery-4.0/WithExpr_SingleNamespaceDeclaration.txt")
            val actual = parseResource("tests/parser/xquery-4.0/WithExpr_SingleNamespaceDeclaration.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single NamespaceDeclaration; compact whitespace")
        fun singleNamespaceDeclaration_compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/WithExpr_SingleNamespaceDeclaration_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/WithExpr_SingleNamespaceDeclaration_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple NamespaceDeclaration")
        fun multipleNamespaceDeclaration() {
            val expected = loadResource("tests/parser/xquery-4.0/WithExpr_MultipleNamespaceDeclaration.txt")
            val actual = parseResource("tests/parser/xquery-4.0/WithExpr_MultipleNamespaceDeclaration.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple NamespaceDeclaration; compact whitespace")
        fun multipleNamespaceDeclaration_compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/WithExpr_MultipleNamespaceDeclaration_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/WithExpr_MultipleNamespaceDeclaration_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing NamespaceDeclaration; first")
        fun missingNamespaceDeclaration_first() {
            val expected = loadResource("tests/parser/xquery-4.0/WithExpr_MissingNamespaceDeclaration.txt")
            val actual = parseResource("tests/parser/xquery-4.0/WithExpr_MissingNamespaceDeclaration.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing NamespaceDeclaration; after comma")
        fun missingNamespaceDeclaration_afterComma() {
            val expected = loadResource("tests/parser/xquery-4.0/WithExpr_MissingNamespaceDeclaration_AfterComma.txt")
            val actual = parseResource("tests/parser/xquery-4.0/WithExpr_MissingNamespaceDeclaration_AfterComma.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing EnclosedExpr")
        fun missingEnclosedExpr() {
            val expected = loadResource("tests/parser/xquery-4.0/WithExpr_MissingEnclosedExpr.txt")
            val actual = parseResource("tests/parser/xquery-4.0/WithExpr_MissingEnclosedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing URILiteral from NamespaceDeclaration")
        fun missingURILiteral() {
            val expected = loadResource("tests/parser/xquery-4.0/WithExpr_NamespaceDeclaration_MissingURILiteral.txt")
            val actual = parseResource("tests/parser/xquery-4.0/WithExpr_NamespaceDeclaration_MissingURILiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (45) TernaryConditionalExpr")
    internal inner class TernaryConditionalExpr {
        @Test
        @DisplayName("ternary conditional")
        fun ternaryConditional() {
            val expected = loadResource("tests/parser/xquery-4.0/TernaryConditionalExpr.txt")
            val actual = parseResource("tests/parser/xquery-4.0/TernaryConditionalExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("ternary conditional; compact whitespace")
        fun ternaryConditional_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/TernaryConditionalExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/TernaryConditionalExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("nested else")
        fun nestedElseExpr() {
            val expected = loadResource("tests/parser/xquery-4.0/TernaryConditionalExpr_NestedElseExpr.txt")
            val actual = parseResource("tests/parser/xquery-4.0/TernaryConditionalExpr_NestedElseExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("nested else; compact whitespace")
        fun nestedElseExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/TernaryConditionalExpr_NestedElseExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/TernaryConditionalExpr_NestedElseExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("nested then")
        fun nestedThenExpr() {
            val expected = loadResource("tests/parser/xquery-4.0/TernaryConditionalExpr_NestedThenExpr.txt")
            val actual = parseResource("tests/parser/xquery-4.0/TernaryConditionalExpr_NestedThenExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("nested then; compact whitespace")
        fun nestedThenExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/TernaryConditionalExpr_NestedThenExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/TernaryConditionalExpr_NestedThenExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing then Expr")
            fun missingThenExpr() {
                val expected = loadResource("tests/parser/xquery-4.0/TernaryConditionalExpr_MissingThenExpr.txt")
                val actual = parseResource("tests/parser/xquery-4.0/TernaryConditionalExpr_MissingThenExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing else operator")
            fun missingElseOperator() {
                val expected = loadResource("tests/parser/xquery-4.0/TernaryConditionalExpr_MissingElseOperator.txt")
                val actual = parseResource("tests/parser/xquery-4.0/TernaryConditionalExpr_MissingElseOperator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing else Expr")
            fun missingElseExpr() {
                val expected = loadResource("tests/parser/xquery-4.0/TernaryConditionalExpr_MissingElseExpr.txt")
                val actual = parseResource("tests/parser/xquery-4.0/TernaryConditionalExpr_MissingElseExpr.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (26) AnnotatedDecl ; XQuery 4.0 ED EBNF (38) ItemTypeDecl")
    internal inner class ItemTypeDecl {
        @Test
        @DisplayName("item type declaration")
        fun itemTypeDecl() {
            val expected = loadResource("tests/parser/xquery-4.0/ItemTypeDecl.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ItemTypeDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("item type declaration; compact whitespace")
        fun itemTypeDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/ItemTypeDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ItemTypeDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("with annotations")
        fun annotations() {
            val expected = loadResource("tests/parser/xquery-4.0/ItemTypeDecl_Annotations.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ItemTypeDecl_Annotations.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing EQName")
        fun missingEQName() {
            val expected = loadResource("tests/parser/xquery-4.0/ItemTypeDecl_MissingEQName.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ItemTypeDecl_MissingEQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'as' keyword")
        fun missingEquals() {
            val expected = loadResource("tests/parser/xquery-4.0/ItemTypeDecl_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ItemTypeDecl_MissingAsKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ItemType")
        fun missingItemType() {
            val expected = loadResource("tests/parser/xquery-4.0/ItemTypeDecl_MissingItemType.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ItemTypeDecl_MissingItemType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: ':=' instead of 'as'")
        fun assignEquals() {
            val expected = loadResource("tests/parser/xquery-4.0/ItemTypeDecl_AssignEquals.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ItemTypeDecl_AssignEquals.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: '=' instead of 'as'")
        fun equals() {
            val expected = loadResource("tests/parser/xquery-4.0/ItemTypeDecl_Equals.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ItemTypeDecl_Equals.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (46) FLWORExpr ; XQuery 4.0 ED EBNF (52) ForMemberClause")
    internal inner class ForClause_XQuery40 {
        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (53) ForMemberBinding")
        internal inner class ForMemberBinding {
            @Test
            @DisplayName("single binding")
            fun singleBinding() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_SingleBinding.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_SingleBinding.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("single binding; compact whitespace")
            fun singleBinding_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_SingleBinding_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_SingleBinding_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple bindings")
            fun multipleBindings() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_MultipleBindings.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_MultipleBindings.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple bindings; compact whitespace")
            fun multipleBindings_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_MultipleBindings_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_MultipleBindings_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("'member' keyword on binding")
            fun memberKeywordOnBinding() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_MemberKeywordOnBinding.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_MemberKeywordOnBinding.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("'member' keyword on binding; compact whitespace")
            fun memberKeywordOnBinding_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_MemberKeywordOnBinding_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_MemberKeywordOnBinding_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing for binding")
            fun missingForBinding() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_MissingForBinding.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_MissingForBinding.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("type declaration")
            fun typeDeclaration() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_TypeDeclaration.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_TypeDeclaration.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("type declaration; compact whitespace")
            fun typeDeclaration_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_TypeDeclaration_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_TypeDeclaration_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("positional var")
            fun positionalVar() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_PositionalVar.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_PositionalVar.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("positional var; compact whitespace")
            fun positionalVar_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_PositionalVar_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_PositionalVar_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: allowing empty")
            fun allowingEmpty() {
                val expected = loadResource("tests/parser/xquery-4.0/ForMemberBinding_AllowingEmpty.txt")
                val actual = parseResource("tests/parser/xquery-4.0/ForMemberBinding_AllowingEmpty.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (96) OtherwiseExpr")
    internal inner class OtherwiseExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-4.0/OtherwiseExpr.txt")
            val actual = parseResource("tests/parser/xquery-4.0/OtherwiseExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing UnionExpr")
        fun missingUnionExpr() {
            val expected = loadResource("tests/parser/xquery-4.0/OtherwiseExpr_MissingUnionExpr.txt")
            val actual = parseResource("tests/parser/xquery-4.0/OtherwiseExpr_MissingUnionExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-4.0/OtherwiseExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-4.0/OtherwiseExpr_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (103) ArrowExpr")
    internal inner class ArrowExpr_XQuery40 {
        @Test
        @DisplayName("mixed targets; fat then thin")
        fun mixedTargets_fatThenThin() {
            val expected = loadResource("tests/parser/xquery-4.0/ArrowExpr_MixedTargets_FatThenThin.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ArrowExpr_MixedTargets_FatThenThin.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("mixed targets; thin then fat")
        fun mixedTargets_thinThenFat() {
            val expected = loadResource("tests/parser/xquery-4.0/ArrowExpr_MixedTargets_ThinThenFat.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ArrowExpr_MixedTargets_ThinThenFat.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: EnclosedExpr in FatArrowTarget")
        fun fatArrowTarget_enclosedExpr() {
            val expected = loadResource("tests/parser/xquery-4.0/FatArrowTarget_EnclosedExpr.txt")
            val actual = parseResource("tests/parser/xquery-4.0/FatArrowTarget_EnclosedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (103) ArrowExpr ; XQuery 4.0 ED EBNF (107) ThinArrowTarget")
    internal inner class ThinArrowTarget {
        @Test
        @DisplayName("XQuery 4.0 ED EBNF (140) ArrowStaticFunction")
        fun arrowStaticFunction() {
            val expected = loadResource("tests/parser/xquery-4.0/ThinArrowTarget_EQName.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ThinArrowTarget_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 4.0 ED EBNF (141) ArrowDynamicFunction ; XQuery 4.0 ED EBNF (145) VarRef")
        fun arrowDynamicFunction_VarRef() {
            val expected = loadResource("tests/parser/xquery-4.0/ThinArrowTarget_VarRef.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ThinArrowTarget_VarRef.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 4.0 ED EBNF (68) ArrowDynamicFunction ; XQuery 4.0 ED EBNF (147) ParenthesizedExpr")
        fun arrowDynamicFunction_ParenthesizedExpr() {
            val expected = loadResource("tests/parser/xquery-4.0/ThinArrowTarget_ParenthesizedExpr.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ThinArrowTarget_ParenthesizedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("XQuery 4.0 ED EBNF (37) EnclosedExpr")
        fun enclosedExpr() {
            val expected = loadResource("tests/parser/xquery-4.0/ThinArrowTarget_EnclosedExpr.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ThinArrowTarget_EnclosedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/ThinArrowTarget_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ThinArrowTarget_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ArgumentList")
        fun missingArgumentList() {
            val expected = loadResource("tests/parser/xquery-4.0/ThinArrowTarget_MissingArgumentList.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ThinArrowTarget_MissingArgumentList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing function specifier")
        fun missingFunctionSpecifier() {
            val expected = loadResource("tests/parser/xquery-4.0/ThinArrowTarget_MissingFunctionSpecifier.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ThinArrowTarget_MissingFunctionSpecifier.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple arrows")
        fun multipleArrows() {
            val expected = loadResource("tests/parser/xquery-4.0/ThinArrowTarget_MultipleArrows.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ThinArrowTarget_MultipleArrows.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (129) PostfixExpr ; XQuery 4.0 ED EBNF (137) Lookup ; XQuery 4.0 ED EBNF (138) KeySpecifier")
    internal inner class PostfixExpr_Lookup_XQuery40 {
        @Test
        @DisplayName("key specifier; StringLiteral")
        fun stringLiteral() {
            val expected = loadResource("tests/parser/xquery-4.0/Lookup_StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-4.0/Lookup_StringLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; VarRef")
        fun varRef() {
            val expected = loadResource("tests/parser/xquery-4.0/Lookup_VarRef.txt")
            val actual = parseResource("tests/parser/xquery-4.0/Lookup_VarRef.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; VarRef; missing VarName")
        fun varRef_missingVarName() {
            val expected = loadResource("tests/parser/xquery-4.0/Lookup_VarRef_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-4.0/Lookup_VarRef_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; VarRef; missing VarName; multiple")
        fun varRef_missingVarName_multiple() {
            val expected = loadResource("tests/parser/xquery-4.0/Lookup_VarRef_MissingVarName_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-4.0/Lookup_VarRef_MissingVarName_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (134) KeywordArguments ; XQuery 4.0 ED EBNF (135) KeywordArgument")
    internal inner class KeywordArgument {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_compactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("keyword NCName")
        fun keywordNCName() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_KeywordNCName.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_KeywordNCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("space before colon")
        fun spaceBeforeColon() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_SpaceBeforeColon.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_SpaceBeforeColon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("space after colon")
        fun spaceAfterColon() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_SpaceAfterColon.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_SpaceAfterColon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("argument before")
        fun argumentBefore() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_ArgumentBefore.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_ArgumentBefore.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("QName-like: is a QName argument")
        fun qnameLike_isQName() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_IsQName.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_IsQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("QName-like: is a QName argument; keyword in local part")
        fun qnameLike_isQName_keywordInLocalPart() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_IsQName_KeywordInLocalPart.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_IsQName_KeywordInLocalPart.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("QName-like: space before colon")
        fun qnameLike_spaceBeforeColon() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_QNameLike_SpaceBeforeColon.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_QNameLike_SpaceBeforeColon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("QName-like: space after colon")
        fun qnameLike_spaceAfterColon() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_QNameLike_SpaceAfterColon.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_QNameLike_SpaceAfterColon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: argument placeholder")
        fun argumentPlaceholder() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_ArgumentPlaceholder.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_ArgumentPlaceholder.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: Argument after KeywordArgument")
        fun argumentAfterKeywordArgument() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_ArgumentAfterKeywordArgument.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_ArgumentAfterKeywordArgument.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: Argument after KeywordArgument and missing Argument")
        fun argumentAfterKeywordArgumentAndMissingArgument() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_ArgumentAfterKeywordArgumentAndMissingArgument.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_ArgumentAfterKeywordArgumentAndMissingArgument.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: ':=' instead of ':'")
        fun assignEqual() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_AssignEqual.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_AssignEqual.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("in PositionalArgumentList")
        fun inPositionalArgumentList() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_InPositionalArgumentList.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_InPositionalArgumentList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ExprSingle")
        fun missingExprSingle() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ExprSingle; space before colon")
        fun missingExprSingle_spaceBeforeColon() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_MissingExpr_SpaceBeforeColon.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_MissingExpr_SpaceBeforeColon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ExprSingle; space after colon")
        fun missingExprSingle_spaceAfterColon() {
            val expected = loadResource("tests/parser/xquery-4.0/KeywordArgument_MissingExpr_SpaceAfterColon.txt")
            val actual = parseResource("tests/parser/xquery-4.0/KeywordArgument_MissingExpr_SpaceAfterColon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (185) InlineFunctionExpr")
    internal inner class InlineFunctionExpr_XQuery40 {
        @Test
        @DisplayName("arrow function expression")
        fun arrowFunctionExpr() {
            val expected = loadResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow.txt")
            val actual = parseResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("arrow function expression; compact whitespace")
        fun arrowFunctionExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing opening brace")
        fun missingOpeningBrace() {
            val expected = loadResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_MissingOpeningBrace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_MissingOpeningBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing function body")
        fun missingFunctionBody() {
            val expected = loadResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_MissingFunctionBody.txt")
            val actual = parseResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_MissingFunctionBody.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("parameter list")
        fun paramList() {
            val expected = loadResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_ParamList.txt")
            val actual = parseResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_ParamList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("return type")
        fun returnType() {
            val expected = loadResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_ReturnType.txt")
            val actual = parseResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_ReturnType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("return type; missing SequenceType")
        fun returnType_MissingSequenceType() {
            val expected = loadResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_ReturnType_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_ReturnType_MissingSequenceType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("arrow function expression; no parameter list")
        fun arrowFunctionExpr_noParamList() {
            val expected = loadResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_NoParamList.txt")
            val actual = parseResource("tests/parser/xquery-4.0/InlineFunctionExpr_Arrow_NoParamList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("inline function expression; no parameter list")
        fun inlineFunctionExpr_noParamList() {
            val expected = loadResource("tests/parser/xquery-4.0/InlineFunctionExpr_NoParamList.txt")
            val actual = parseResource("tests/parser/xquery-4.0/InlineFunctionExpr_NoParamList.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (196) UnaryLookup ; XQuery 4.0 ED EBNF (138) KeySpecifier")
    internal inner class UnaryLookup_XQuery40 {
        @Test
        @DisplayName("key specifier; StringLiteral")
        fun stringLiteral() {
            val expected = loadResource("tests/parser/xquery-4.0/UnaryLookup_StringLiteral.txt")
            val actual = parseResource("tests/parser/xquery-4.0/UnaryLookup_StringLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; VarRef")
        fun varRef() {
            val expected = loadResource("tests/parser/xquery-4.0/UnaryLookup_VarRef.txt")
            val actual = parseResource("tests/parser/xquery-4.0/UnaryLookup_VarRef.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; VarRef; missing VarName")
        fun varRef_missingVarName() {
            val expected = loadResource("tests/parser/xquery-4.0/UnaryLookup_VarRef_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-4.0/UnaryLookup_VarRef_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (202) ItemType")
    internal inner class ItemType_XQuery40 {
        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (233) LocalUnionType")
        internal inner class LocalUnionType {
            @Test
            @DisplayName("one")
            fun one() {
                val expected = loadResource("tests/parser/xquery-4.0/LocalUnionType_One.txt")
                val actual = parseResource("tests/parser/xquery-4.0/LocalUnionType_One.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("one; compact whitespace")
            fun one_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/LocalUnionType_One_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/LocalUnionType_One_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("two")
            fun two() {
                val expected = loadResource("tests/parser/xquery-4.0/LocalUnionType_Two.txt")
                val actual = parseResource("tests/parser/xquery-4.0/LocalUnionType_Two.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("two; compact whitespace")
            fun two_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/LocalUnionType_Two_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/LocalUnionType_Two_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-4.0/LocalUnionType_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-4.0/LocalUnionType_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing first type")
            fun missingFirstType() {
                val expected = loadResource("tests/parser/xquery-4.0/LocalUnionType_MissingFirstType.txt")
                val actual = parseResource("tests/parser/xquery-4.0/LocalUnionType_MissingFirstType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing next type")
            fun missingNextType() {
                val expected = loadResource("tests/parser/xquery-4.0/LocalUnionType_MissingNextType.txt")
                val actual = parseResource("tests/parser/xquery-4.0/LocalUnionType_MissingNextType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple types")
            fun multipleTypes() {
                // This is testing handling of whitespace before parsing the next comma.
                val expected = loadResource("tests/parser/xquery-4.0/LocalUnionType_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-4.0/LocalUnionType_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (234) EnumerationType")
        internal inner class EnumerationType {
            @Test
            @DisplayName("one")
            fun one() {
                val expected = loadResource("tests/parser/xquery-4.0/EnumerationType_One.txt")
                val actual = parseResource("tests/parser/xquery-4.0/EnumerationType_One.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("one; compact whitespace")
            fun one_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/EnumerationType_One_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/EnumerationType_One_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("two")
            fun two() {
                val expected = loadResource("tests/parser/xquery-4.0/EnumerationType_Two.txt")
                val actual = parseResource("tests/parser/xquery-4.0/EnumerationType_Two.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("two; compact whitespace")
            fun two_CompactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/EnumerationType_Two_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/EnumerationType_Two_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-4.0/EnumerationType_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-4.0/EnumerationType_MissingClosingParenthesis.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing first type")
            fun missingFirstType() {
                val expected = loadResource("tests/parser/xquery-4.0/EnumerationType_MissingFirstType.txt")
                val actual = parseResource("tests/parser/xquery-4.0/EnumerationType_MissingFirstType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing next type")
            fun missingNextType() {
                val expected = loadResource("tests/parser/xquery-4.0/EnumerationType_MissingNextType.txt")
                val actual = parseResource("tests/parser/xquery-4.0/EnumerationType_MissingNextType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple types")
            fun multipleTypes() {
                // This is testing handling of whitespace before parsing the next comma.
                val expected = loadResource("tests/parser/xquery-4.0/EnumerationType_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-4.0/EnumerationType_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery 4.0 ED EBNF (227) TypedMapTest ; XQuery 4.0 ED EBNF (233) LocalUnionType")
        fun typedMapTest() {
            val expected = loadResource("tests/parser/xquery-4.0/TypedMapTest_LocalUnionType.txt")
            val actual = parseResource("tests/parser/xquery-4.0/TypedMapTest_LocalUnionType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (198) SingleType ; XQuery 4.0 ED EBNF (233) LocalUnionType")
    internal inner class LocalUnionType_SingleType {
        @Test
        @DisplayName("single type")
        fun singleType() {
            val expected = loadResource("tests/parser/xquery-4.0/SingleType_LocalUnionType.txt")
            val actual = parseResource("tests/parser/xquery-4.0/SingleType_LocalUnionType.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("optional")
        fun optional() {
            val expected = loadResource("tests/parser/xquery-4.0/SingleType_LocalUnionType_Optional.txt")
            val actual = parseResource("tests/parser/xquery-4.0/SingleType_LocalUnionType_Optional.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (212) AttributeTest ; XQuery 4.0 ED EBNF (128) NameTest")
    internal inner class AttributeTest {
        @Test
        @DisplayName("wildcard prefix")
        fun prefix() {
            val expected = loadResource("tests/parser/xquery-4.0/AttributeTest_NameTest_WildcardPrefix.txt")
            val actual = parseResource("tests/parser/xquery-4.0/AttributeTest_NameTest_WildcardPrefix.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("wildcard local name")
        fun localName() {
            val expected = loadResource("tests/parser/xquery-4.0/AttributeTest_NameTest_WildcardLocalName.txt")
            val actual = parseResource("tests/parser/xquery-4.0/AttributeTest_NameTest_WildcardLocalName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("wildcard URIQualifiedName")
        fun uriQualifiedName() {
            val expected = loadResource("tests/parser/xquery-4.0/AttributeTest_NameTest_WildcardURIQualifiedName.txt")
            val actual = parseResource("tests/parser/xquery-4.0/AttributeTest_NameTest_WildcardURIQualifiedName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (215) ElementTest ; XQuery 4.0 ED EBNF (128) NameTest")
    internal inner class ElementTest {
        @Test
        @DisplayName("wildcard prefix")
        fun prefix() {
            val expected = loadResource("tests/parser/xquery-4.0/ElementTest_NameTest_WildcardPrefix.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ElementTest_NameTest_WildcardPrefix.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("wildcard local name")
        fun localName() {
            val expected = loadResource("tests/parser/xquery-4.0/ElementTest_NameTest_WildcardLocalName.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ElementTest_NameTest_WildcardLocalName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("wildcard URIQualifiedName")
        fun uriQualifiedName() {
            val expected = loadResource("tests/parser/xquery-4.0/ElementTest_NameTest_WildcardURIQualifiedName.txt")
            val actual = parseResource("tests/parser/xquery-4.0/ElementTest_NameTest_WildcardURIQualifiedName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 4.0 ED EBNF (122) RecordTest")
    internal inner class RecordTest {
        @Test
        @DisplayName("record test")
        fun recordTest() {
            val expected = loadResource("tests/parser/xquery-4.0/RecordTest.txt")
            val actual = parseResource("tests/parser/xquery-4.0/RecordTest.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("record test; compact whitespace")
        fun recordTest_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-4.0/RecordTest_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-4.0/RecordTest_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-4.0/RecordTest_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-4.0/RecordTest_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (232) ExtensibleFlag")
        internal inner class ExtensibleFlag {
            @Test
            @DisplayName("extensible flag")
            fun extensibleFlag() {
                val expected = loadResource("tests/parser/xquery-4.0/RecordTest_ExtensibleFlag.txt")
                val actual = parseResource("tests/parser/xquery-4.0/RecordTest_ExtensibleFlag.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("extensible flag; compact whitespace")
            fun extensibleFlag_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/RecordTest_ExtensibleFlag_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/RecordTest_ExtensibleFlag_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: field declaration after extensible flag")
            fun fieldDeclarationAfter() {
                val expected = loadResource("tests/parser/xquery-4.0/RecordTest_ExtensibleFlag_FieldDeclarationAfter.txt")
                val actual = parseResource("tests/parser/xquery-4.0/RecordTest_ExtensibleFlag_FieldDeclarationAfter.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: extensible flag at start")
            fun atStart() {
                val expected = loadResource("tests/parser/xquery-4.0/RecordTest_ExtensibleFlag_AtStart.txt")
                val actual = parseResource("tests/parser/xquery-4.0/RecordTest_ExtensibleFlag_AtStart.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (229) FieldDeclaration")
        internal inner class FieldDeclaration {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_NCName.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_NCName.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("StringLiteral")
            fun stringLiteral() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_StringLiteral.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_StringLiteral.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("optional")
            fun optional() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Optional.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Optional.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("optional; compact whitespace")
            fun optional_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Multiple.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Multiple.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Multiple_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("multiple; missing field")
            fun multiple_missingField() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Multiple_MissingField.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Multiple_MissingField.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (229) FieldDeclaration; XQuery 4.0 ED EBNF (200) SequenceType")
        internal inner class FieldDeclaration_SequenceType {
            @Test
            @DisplayName("sequence type")
            fun sequenceType() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_SequenceType.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_SequenceType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing type")
            fun missingType() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_SequenceType_MissingType.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_SequenceType_MissingType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("optional")
            fun optional() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SequenceType.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SequenceType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("optional; compact whitespace")
            fun optional_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SequenceType_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SequenceType_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: sequence type; colon type specifier")
            fun sequenceType_colon() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_SequenceType_Colon.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_SequenceType_Colon.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: optional; colon type specifier")
            fun optional_colon() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SequenceType_Colon.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SequenceType_Colon.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: optional; colon type specifier; compact whitespace")
            fun optional_colon_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SequenceType_Colon_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SequenceType_Colon_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery 4.0 ED EBNF (229) FieldDeclaration; XQuery 4.0 ED EBNF (231) SelfReference")
        internal inner class FieldDeclaration_SelfReference {
            @Test
            @DisplayName("self reference")
            fun selfReference() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_SelfReference.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_SelfReference.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("self reference; compact whitespace")
            fun selfReference_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_SelfReference_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_SelfReference_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("no OccurrenceIndicator")
            fun noOccurrenceIndicator() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_SelfReference_NoOccurrenceIndicator.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_SelfReference_NoOccurrenceIndicator.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("missing type")
            fun missingType() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_SequenceType_MissingType.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_SequenceType_MissingType.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("optional")
            fun optional() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SelfReference.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SelfReference.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("optional; compact whitespace")
            fun optional_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SelfReference_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SelfReference_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: self reference; colon type specifier")
            fun selfReference_colon() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_SelfReference_Colon.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_SelfReference_Colon.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: optional; colon type specifier")
            fun optional_colon() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SelfReference_Colon.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SelfReference_Colon.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: optional; colon type specifier; compact whitespace")
            fun optional_colon_compactWhitespace() {
                val expected = loadResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SelfReference_Colon_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xquery-4.0/FieldDeclaration_Optional_SelfReference_Colon_CompactWhitespace.xq")
                assertThat(actual.toPsiTreeString(), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery Terminal Delimitation")
    internal inner class TerminalDelimitation {
        @Test
        @DisplayName("T=DecimalLiteral U=NCName")
        fun decimalLiteral_NCName() {
            val expected = loadResource("tests/parser/xquery-terminal-delimitation/DecimalLiteral_NCName.txt")
            val actual = parseResource("tests/parser/xquery-terminal-delimitation/DecimalLiteral_NCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("T=DecimalLiteral U=URIQualifiedName")
        fun decimalLiteral_URIQualifiedName() {
            val expected = loadResource("tests/parser/xquery-terminal-delimitation/DecimalLiteral_URIQualifiedName.txt")
            val actual = parseResource("tests/parser/xquery-terminal-delimitation/DecimalLiteral_URIQualifiedName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("T=DoubleLiteral U=NCName")
        fun doubleLiteral_NCName() {
            val expected = loadResource("tests/parser/xquery-terminal-delimitation/DoubleLiteral_NCName.txt")
            val actual = parseResource("tests/parser/xquery-terminal-delimitation/DoubleLiteral_NCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("T=DoubleLiteral U=URIQualifiedName")
        fun doubleLiteral_URIQualifiedName() {
            val expected = loadResource("tests/parser/xquery-terminal-delimitation/DoubleLiteral_URIQualifiedName.txt")
            val actual = parseResource("tests/parser/xquery-terminal-delimitation/DoubleLiteral_URIQualifiedName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("T=IntegerLiteral U=NCName")
        fun integerLiteral_NCName() {
            val expected = loadResource("tests/parser/xquery-terminal-delimitation/IntegerLiteral_NCName.txt")
            val actual = parseResource("tests/parser/xquery-terminal-delimitation/IntegerLiteral_NCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("T=IntegerLiteral U=URIQualifiedName")
        fun integerLiteral_URIQualifiedName() {
            val expected = loadResource("tests/parser/xquery-terminal-delimitation/IntegerLiteral_URIQualifiedName.txt")
            val actual = parseResource("tests/parser/xquery-terminal-delimitation/IntegerLiteral_URIQualifiedName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }
}
