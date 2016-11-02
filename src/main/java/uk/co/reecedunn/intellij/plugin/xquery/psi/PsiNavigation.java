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
package uk.co.reecedunn.intellij.plugin.xquery.psi;

import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;

public class PsiNavigation {
    // The PsiNavigation constructor is not used, but make sure code coverage reports it as covered.
    @SuppressWarnings("unused")
    private static PsiNavigation INSTANCE = new PsiNavigation();

    @SuppressWarnings("unchecked")
    public static <T> T findDirectDescendantByClass(PsiElement element, Class<T> child) {
        while (element != null && !child.isInstance(element)) {
            element = element.getFirstChild();
        }
        return (T)element;
    }

    @SuppressWarnings("unchecked")
    public static <T> T findChildByClass(PsiElement element, Class<T> child) {
        element = element.getFirstChild();
        while (element != null && !child.isInstance(element)) {
            element = element.getNextSibling();
        }
        return (T)element;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> findChildrenByClass(PsiElement element, Class<T> child) {
        List<T> children = new ArrayList<>();
        element = element.getFirstChild();
        while (element != null) {
            if (child.isInstance(element)) {
                children.add((T)element);
            }
            element = element.getNextSibling();
        }
        return children;
    }
}
