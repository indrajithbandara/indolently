// Copyright 2014 takahashikzn
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package jp.root42.indolently.function;

import java.util.Objects;
import java.util.function.Function;


/**
 * @param <T> -
 * @param <U> -
 * @param <V> -
 * @param <R> -
 * @author takahashikzn
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {

    @SuppressWarnings("javadoc")
    R apply(T t, U u, V v);

    /**
     * @param <W> -
     * @param after -
     * @return -
     */
    default <W> TriFunction<T, U, V, W> andThen(final Function<? super R, ? extends W> after) {
        Objects.requireNonNull(after);
        return (t, u, v) -> after.apply(apply(t, u, v));
    }
}