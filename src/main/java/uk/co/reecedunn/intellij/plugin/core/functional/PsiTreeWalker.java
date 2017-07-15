/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.functional;

import com.intellij.psi.PsiElement;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class PsiTreeWalker implements Each<PsiElement>, Foldable<PsiElement>, Find<PsiElement> {
    public static PsiElement skipQName(PsiElement element) {
        if (element.getNode().getElementType() == XQueryTokenType.NCNAME ||
            element.getNode().getElementType() == XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            element = element.getNextSibling();

        boolean skippedWhiteSpace = false;
        if (element.getNode().getElementType() == XQueryTokenType.WHITE_SPACE ||
            element.getNode().getElementType() == XQueryTokenType.XML_WHITE_SPACE) {
            element = element.getNextSibling();
            skippedWhiteSpace = true;
        }

        if (element.getNode().getElementType() == XQueryTokenType.QNAME_SEPARATOR ||
            element.getNode().getElementType() == XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR) {
            element = element.getNextSibling();
            if (element.getNode().getElementType() == XQueryTokenType.WHITE_SPACE ||
                element.getNode().getElementType() == XQueryTokenType.XML_WHITE_SPACE)
                element = element.getNextSibling();

            if (element.getNode().getElementType() == XQueryTokenType.NCNAME ||
                element.getNode().getElementType() == XQueryTokenType.XML_ATTRIBUTE_NCNAME)
                element = element.getNextSibling();
        } else if (skippedWhiteSpace)
            element = element.getPrevSibling();
        return element;
    }

    // region Each

    @Override
    public void each(Consumer<PsiElement> consumer) {
        PsiElement element = mElement;
        while (element != null) {
            consumer.accept(element);
            element = mNextElementFunction.apply(element);
        }
    }

    // endregion
    // region Foldable

    @Override
    public <V> V fold(BiFunction<PsiElement, V, V> foldOver, V defaultValue) {
        PsiElement element = mElement;
        while (element != null) {
            defaultValue = foldOver.apply(element, defaultValue);
            element = mNextElementFunction.apply(element);
        }
        return defaultValue;
    }

    // endregion
    // region Find

    @Override
    @SuppressWarnings("unchecked")
    public <B extends PsiElement> Option<B> findFirst(Predicate<PsiElement> matcher) {
        PsiElement element = mElement;
        while (element != null) {
            if (matcher.test(element)) {
                return Option.some((B)element);
            }
            element = mNextElementFunction.apply(element);
        }
        return Option.none();
    }

    @SuppressWarnings("unchecked")
    public <B extends PsiElement> void findAll(Predicate<PsiElement> matcher, Consumer<B> consumer) {
        PsiElement element = mElement;
        while (element != null) {
            if (matcher.test(element)) {
                consumer.accept((B)element);
            }
            element = mNextElementFunction.apply(element);
        }
    }

    // endregion
    // region Value Constructors

    public static PsiTreeWalker ancestors(PsiElement element) {
        return new PsiTreeWalker(element.getParent(), PsiElement::getParent);
    }

    public static PsiTreeWalker descendants(PsiElement element) {
        return new PsiTreeWalker(element.getFirstChild(), PsiElement::getFirstChild);
    }

    public static PsiTreeWalker children(PsiElement element) {
        return new PsiTreeWalker(element.getFirstChild(), PsiElement::getNextSibling);
    }

    public static PsiTreeWalker reversedChildren(PsiElement element) {
        return new PsiTreeWalker(element.getLastChild(), PsiElement::getPrevSibling);
    }

    // endregion
    // region Implementation Details

    private final PsiElement mElement;
    private final Function<PsiElement, PsiElement> mNextElementFunction;

    private PsiTreeWalker(PsiElement element, Function<PsiElement, PsiElement> nextElementFunction) {
        mElement = element;
        mNextElementFunction = nextElementFunction;
    }

    // endregion
}
