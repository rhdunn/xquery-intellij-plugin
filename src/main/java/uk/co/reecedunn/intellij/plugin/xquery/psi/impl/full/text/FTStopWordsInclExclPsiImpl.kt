package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.full.text

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.xquery.ast.full.text.FTStopWordsInclExcl

class FTStopWordsInclExclPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), FTStopWordsInclExcl