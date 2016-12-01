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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface Foldable<A> {
    <V> V fold(BiFunction<A, V, V> foldOver, V initialValue);

    @SuppressWarnings("unchecked")
    default <B extends A, V> V foldIf(Predicate<A> matcher, BiFunction<B, V, V> foldOver, V initialValue) {
        return fold((a, b) -> {
            if (matcher.test(a)) {
                return foldOver.apply((B)a, b);
            }
            return b;
        }, initialValue);
    }

    default <B extends A, V> V foldOf(Class<B> c, BiFunction<B, V, V> foldOver, V initialValue) {
        return foldIf(c::isInstance, foldOver, initialValue);
    }

    default int count() {
        return fold((a, value) -> value + 1, 0);
    }

    default int countIf(Predicate<A> matcher) {
        return foldIf(matcher, (a, value) -> value + 1, 0);
    }

    default int countOf(Class c) {
        return foldIf(c::isInstance, (a, value) -> value + 1, 0);
    }

    default List<A> toList() {
        return fold((a, c) -> { c.add(a); return c; }, new ArrayList<A>());
    }

    default List<A> toListIf(Predicate<A> matcher) {
        return foldIf(matcher, (a, c) -> { c.add(a); return c; }, new ArrayList<>());
    }

    default <B extends A> List<B> toListOf(Class<B> c) {
        return foldOf(c, (a, l) -> { l.add(a); return l; }, new ArrayList<>());
    }
}
