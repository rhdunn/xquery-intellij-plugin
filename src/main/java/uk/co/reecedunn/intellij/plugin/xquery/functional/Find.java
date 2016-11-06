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
package uk.co.reecedunn.intellij.plugin.xquery.functional;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Find<A> {
    <B extends A> Option<B> findFirst(Predicate<A> matcher);

    default <B extends A> Option<B> findFirst(Class<B> c) {
        return findFirst(c::isInstance);
    }

    <B extends A> void findAll(Predicate<A> matcher, Consumer<B> consumer);

    default <B extends A> void findAll(Class<B> c, Consumer<B> consumer) {
        findAll(c::isInstance, consumer);
    }
}
