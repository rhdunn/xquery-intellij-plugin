package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.annotations.NonNls
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import java.nio.charset.StandardCharsets

interface LanguageTestCase : PlatformTestCase {
    val language: Language
}

fun LanguageTestCase.createVirtualFile(@NonNls name: String, text: String): VirtualFile {
    val file = LightVirtualFile(name, language, text)
    file.charset = StandardCharsets.UTF_8
    return file
}

@Suppress("UNCHECKED_CAST")
fun <File : PsiFile> LanguageTestCase.parseText(text: String): File {
    return createVirtualFile("testcase.xqy", text).toPsiFile(project) as File
}

inline fun <reified T> LanguageTestCase.parse(text: String): List<T> {
    return parseText<PsiFile>(text).walkTree().filterIsInstance<T>().toList()
}

inline fun <reified T> LanguageTestCase.parse(vararg text: String): List<T> {
    return parseText<PsiFile>(text.joinToString("\n")).walkTree().filterIsInstance<T>().toList()
}

fun LanguageTestCase.completion(text: String, completionPoint: String = "completion-point"): PsiElement {
    return parse<LeafPsiElement>(text).find { it.text == completionPoint }!!
}
