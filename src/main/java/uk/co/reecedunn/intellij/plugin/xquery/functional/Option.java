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

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A value that may (<em>some</em>) or may not (<em>none</em>) exist.
 *
 * This option type is defined to support Category Theory semantics. In particular,
 * <em>some</em> may be used to create an optional value that is null. This is
 * important in order to preserve the composability semantics needed for Option
 * to be a category.
 *
 * @param <A> The type of the value being stored in the option.
 */
public abstract class Option<A> {
    public <B> Option<B> map(Function<? super A, ? extends B> f) {
        return isDefined() ? some(f.apply(get())) : none();
    }

    public <B> Option<B> flatMap(Function<? super A, ? extends Option<? extends B>> f) {
        if (isDefined()) {
            Option<? extends B> ret = f.apply(get());
            return ret.isDefined() ? some(ret.get()) : none();
        }
        return none();
    }

    // region Option Interface

    /**
     * Checks whether the option is defined.
     *
     * @return true if the option contains a value (<em>some</em>), false otherwise.
     */
    public abstract boolean isDefined();

    /**
     * Returns the value stored within the option.
     *
     * @throws NoSuchElementException If the value is not defined (i.e. is <em>none</em>).
     * @return The value held by the option.
     */
    public abstract A get();

    /**
     * Returns the value stored within the option.
     *
     * @param defaultValue The value to use if the option is not defined.
     * @return The value held by the option if defined, otherwise the default value.
     */
    public abstract A getOrElse(final A defaultValue);

    /**
     * Provides a computed value to return if the option is not defined.
     *
     * @param supplier Compute the value to use if undefined.
     * @return The computed value.
     */
    public abstract Option<A> orElse(Supplier<A> supplier);

    // endregion
    // region Value Constructors

    /**
     * Create an optional value from a Java value, where null denotes a missing
     * (<em>none</em>) value.
     *
     * @param value The value to store in the option.
     * @param <A> The type of the value being stored.
     * @return <em>none</em> if the value is null, <em>some</em> containing the
     *         value otherwise.
     */
    public static <A> Option<A> of(final A value) {
        return (value == null) ? Option.<A>none() : some(value);
    }

    /**
     * Create an option denoting the absence of a value.
     *
     * @param <A> The type of the option.
     * @return An option representing no value.
     */
    @SuppressWarnings("unchecked")
    public static <A> Option<A> none() {
        return (Option<A>)None.NONE;
    }

    /**
     * Create an option denoting the presence of a value (which may be null).
     *
     * This can be used to create an option that has a defined value of null,
     * as well as creating an option with a non-null value. This is to support
     * using it in things like Map.get(key) to differentiate from an entry with
     * that key not existing (<em>none</em>) and an entry that contains a null
     * value (<em>some(null)</em>).
     *
     * @param <A> The type of the option.
     * @return An option containing that specified value.
     */
    public static <A> Option<A> some(final A value) {
        return new Some<>(value);
    }

    // endregion
    // region Implementation Details

    private Option() {
    }

    private static final class None extends Option<Object> {
        private static final Option<Object> NONE = new None();

        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public Object get() {
            throw new NoSuchElementException();
        }

        @Override
        public Object getOrElse(Object defaultValue) {
            return defaultValue;
        }

        @Override
        public Option<Object> orElse(Supplier<Object> supplier) {
            return Option.some(supplier.get());
        }
    }

    private static final class Some<A> extends Option<A> {
        private final A mValue;

        private Some(final A value) {
            mValue = value;
        }

        @Override
        public boolean isDefined() {
            return true;
        }

        @Override
        public A get() {
            return mValue;
        }

        @Override
        public A getOrElse(A defaultValue) {
            return mValue;
        }

        @Override
        public Option<A> orElse(Supplier<A> supplier) {
            return this;
        }
    }

    // endregion
}
