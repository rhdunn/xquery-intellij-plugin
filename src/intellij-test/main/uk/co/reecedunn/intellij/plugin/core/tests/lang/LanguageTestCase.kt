package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.annotations.NonNls
import java.nio.charset.StandardCharsets

interface LanguageTestCase {
    val language: Language

    fun createVirtualFile(@NonNls name: String, text: String): VirtualFile {
        val file = LightVirtualFile(name, language, text)
        file.charset = StandardCharsets.UTF_8
        return file
    }
}