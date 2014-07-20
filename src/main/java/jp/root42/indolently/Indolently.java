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
package jp.root42.indolently;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;


/**
 * The Java syntax sugar collection for indolent person (like you).
 *
 * @author takahashikzn
 */
@SuppressWarnings("javadoc")
public class Indolently {

    @SuppressWarnings("rawtypes")
    private static final Class<? extends Map> MAP_TYPE = LinkedHashMap.class;

    // non private for subclass
    protected Indolently() {
    }

    /**
     * Express this object is freezable.
     *
     * @param <T> self type
     * @author takahashikzn
     */
    protected interface Freezable<T> {

        /**
         * return freezed new {@link Collections#unmodifiableList(List) List}/{@link Collections#unmodifiableMap(Map)
         * Map}/{@link Collections#unmodifiableSet(Set) Set} instance.
         *
         * @return freezed new instance
         */
        T freeze();
    }

    /**
     * Extended Map class for indolent person.
     *
     * @param <K> key type
     * @param <V> value type
     * @author takahashikzn
     */
    public interface Smap<K, V>
        extends Map<K, V>, Freezable<Map<K, V>> {

        /**
         * put key/value pair and return this instance.
         *
         * @param key key
         * @param value value
         * @return {@code this} instance
         */
        Smap<K, V> push(K key, V value);

        /**
         * put all key/value pairs and return this instance.
         *
         * @param map map
         * @return {@code this} instance
         */
        Smap<K, V> pushAll(Map<? extends K, ? extends V> map);

        /**
         * put key/value pair and return this instance only if value exists.
         * otherwise, do nothing.
         *
         * @param key key
         * @param value nullable value
         * @return {@code this} instance
         */
        Smap<K, V> push(K key, Optional<? extends V> value);

        /**
         * put all key/value pairs and return this instance only if map exists.
         * otherwise, do nothing.
         *
         * @param map nullable map
         * @return {@code this} instance
         */
        Smap<K, V> pushAll(Optional<? extends Map<? extends K, ? extends V>> map);

        /**
         * remove keys and return this instance.
         *
         * @param keys keys to remove
         * @return {@code this} instance
         */
        Smap<K, V> delete(Iterable<? extends K> keys);

        /**
         * an alias of {@link Map#keySet()} and newly constructed (detached) key view.
         *
         * @return keys
         */
        Sset<K> keys();

        /**
         * construct new map which having keys you specify.
         * a key which does not exist is ignored.
         *
         * @return new map
         */
        Smap<K, V> slice(Iterable<? extends K> keys);
    }

    /**
     * common method definition for set/list
     *
     * @param <T> value type
     * @param <SELF> self type
     * @author takahashikzn
     */
    protected interface Scol<T, SELF extends Scol<T, SELF>>
        extends Collection<T> {

        /**
         * add value and return this instance.
         *
         * @param value value
         * @return {@code this} instance
         */
        SELF push(T value);

        /**
         * add all values and return this instance.
         *
         * @param values values
         * @return {@code this} instance
         */
        SELF pushAll(Iterable<? extends T> values);

        /**
         * add value and return this instance only if value exists.
         * otherwise, do nothing.
         *
         * @param value nullable value
         * @return {@code this} instance
         */
        SELF push(Optional<? extends T> value);

        /**
         * add all values and return this instance only if values exists.
         * otherwise, do nothing.
         *
         * @param values nullable values
         * @return {@code this} instance
         */
        SELF pushAll(Optional<? extends Iterable<? extends T>> values);

        /**
         * remove values and return this instance.
         *
         * @param values values to remove
         * @return {@code this} instance
         */
        SELF delete(Iterable<? extends T> values);

        /**
         * get first value.
         *
         * @return first value
         * @throws NoSuchElementException if empty
         */
        T first();

        /**
         * get last value.
         *
         * @return first value
         * @throws NoSuchElementException if empty
         */
        T last();

        /**
         * get rest of this collection.
         *
         * @return collection values except for first value.
         * if this instance is {@link #isEmpty() empty}, return empty collection.
         */
        SELF tail();

        /**
         * internal iterator.
         *
         * @param f function
         * @return {@code this} instance
         */
        default SELF each(final Function<T, ? extends T> f) {

            this.stream().map(f).close();

            @SuppressWarnings("unchecked")
            final SELF self = (SELF) this;

            return self;
        }
    }

    /**
     * Extended {@link List} class for indolent person.
     *
     * @param <T> value type
     * @author takahashikzn
     */
    public interface Slist<T>
        extends Scol<T, Slist<T>>, List<T>, Freezable<List<T>> {

        /**
         * convert this list to {@link Sset}.
         *
         * @return a set of this list.
         */
        Sset<T> set();

        /**
         * insert value at specified index and return this instance.
         *
         * @param idx insertion position.
         * negative value is acceptable. for example, {@code slist.push(-1, "x")} means
         * {@code slist.push(slist.size() - 1, "x")}
         * @param value value
         * @return {@code this} instance
         */
        Slist<T> push(int idx, T value);

        /**
         * insert all values at specified index and return this instance.
         *
         * @param idx insertion position.
         * negative value is acceptable. for example, {@code slist.pushAll(-1, list("x", "y"))} means
         * {@code slist.pushAll(slist.size() - 1,  list("x", "y"))}
         * @param value values
         * @return {@code this} instance
         */
        Slist<T> pushAll(int idx, Iterable<? extends T> values);

        /**
         * insert value at specified index and return this instance only if value exists.
         * otherwise, do nothing.
         *
         * @param idx insertion position.
         * negative value is acceptable. for example, {@code slist.push(-1, "x")} means
         * {@code slist.push(slist.size() - 1, "x")}
         * @param value nullable value
         * @return {@code this} instance
         */
        Slist<T> push(int idx, Optional<? extends T> value);

        /**
         * insert all values at specified index and return this instance only if values exist.
         * otherwise, do nothing.
         *
         * @param idx insertion position.
         * negative value is acceptable. for example, {@code slist.pushAll(-1, list("x", "y"))} means
         * {@code slist.pushAll(slist.size() - 1,  list("x", "y"))}
         * @param value nullable values
         * @return {@code this} instance
         */
        Slist<T> pushAll(int idx, Optional<? extends Iterable<? extends T>> values);

        /**
         * an alias of {@link #subList(int, int)} but newly constructed (detached) view.
         *
         * @return detached sub list
         */
        Slist<T> slice(int from, int to);
    }

    /**
     * Extended {@link Set} class for indolent person.
     *
     * @param <T> value type
     * @author takahashikzn
     */
    public interface Sset<T>
        extends Scol<T, Sset<T>>, Set<T>, Freezable<Set<T>> {

        /**
         * convert this set to {@link Slist}.
         *
         * @return a list of this set.
         */
        Slist<T> list();
    }

    public static <T extends CharSequence> Optional<T> nonEmpty(final T value) {
        return optionalEmpty(value);
    }

    public static <T extends CharSequence> Optional<T> optionalEmpty(final T value) {
        return optional(empty(value) ? null : value);
    }

    public static <T extends CharSequence> Optional<T> nonBlank(final T value) {
        return optionalBlank(value);
    }

    public static <T extends CharSequence> Optional<T> optionalBlank(final T value) {
        return optional(blank(value) ? null : value);
    }

    public static <T> Optional<T> nonNull(final T value) {
        return optional(value);
    }

    public static <T> Optional<T> optional(final T value) {
        return Optional.ofNullable(value);
    }

    public static <T> Slist<T> list(final Optional<? extends T> value) {
        return new SlistImpl<T>().push(value);
    }

    public static <T> Slist<T> list(final Iterable<? extends T> values) {
        return new SlistImpl<T>().pushAll(optional(values));
    }

    @SafeVarargs
    public static <T> Slist<T> list(final T... values) {

        final Slist<T> list = new SlistImpl<>();

        if (values != null) {
            for (final T v : values) {
                list.add(v);
            }
        }

        return list;
    }

    public static Slist<Integer> range(final int from, final int to) {
        return range(from, to, 1);
    }

    public static Slist<Integer> range(final int from, final int to, final int step) {
        if (step <= 0) {
            throw new IllegalArgumentException(String.format("(step = %d) <= 0", step));
        }

        if (from < to) {

            final Slist<Integer> list = list();

            for (int i = from; i <= to; i += Math.abs(step)) {
                list.add(i);
            }

            return list;
        } else {

            final Slist<Integer> list = range(to, from, step);

            Collections.reverse(list);

            return list;
        }
    }

    public static <T> T[] array(final Iterable<? extends T> values) {
        if (empty(values)) {
            throw new IllegalArgumentException("can't infer empty array type");
        }

        // 実際はObject[]であることに注意
        @SuppressWarnings("unchecked")
        final T[] pseudo = (T[]) list(values).toArray();

        return array(pseudo);
    }

    @SafeVarargs
    public static <T> T[] array(final T... values) {
        if (empty(values)) {
            throw new IllegalArgumentException("can't infer empty array type");
        }

        @SuppressWarnings("unchecked")
        final T[] ary = (T[]) Array.newInstance(values[0].getClass(), values.length);

        for (int i = 0; i < values.length; i++) {

            final T val = values[i];

            try {
                Array.set(ary, i, val);
            } catch (final IllegalArgumentException e) {

                final String msg =
                    String.format("arrayType: %s, valueType: %s", ary.getClass().getName(), (val == null ? "null" : val
                        .getClass().getName()));

                throw new IllegalArgumentException(msg, e);
            }
        }

        return ary;
    }

    @SafeVarargs
    public static <T, V extends T> T[] array(final Class<T> type, final V... values) {

        if (empty(values)) {
            @SuppressWarnings("unchecked")
            final T[] ary = (T[]) Array.newInstance(type, 0);
            return ary;
        }

        @SuppressWarnings("unchecked")
        final T[] ary = (T[]) Array.newInstance(type, values.length);

        for (int i = 0; i < values.length; i++) {
            Array.set(ary, i, values[i]);
        }

        return ary;
    }

    public static Object[] oarray(final Object... values) {
        return values;
    }

    public static char[] parray(final char... values) {
        return values;
    }

    public static int[] parray(final int... values) {
        return values;
    }

    public static long[] parray(final long... values) {
        return values;
    }

    public static float[] parray(final float... values) {
        return values;
    }

    public static byte[] parray(final byte... values) {
        return values;
    }

    public static double[] parray(final double... values) {
        return values;
    }

    public static boolean[] parray(final boolean... values) {
        return values;
    }

    public static <T> Sset<T> set(final Optional<? extends T> value) {
        return new SsetImpl<T>().push(value);
    }

    public static <T> Sset<T> set(final Iterable<? extends T> values) {
        return new SsetImpl<T>().pushAll(optional(values));
    }

    @SafeVarargs
    public static <T> Sset<T> set(final T... values) {

        final Sset<T> set = new SsetImpl<>();

        if (values != null) {
            for (final T v : values) {
                set.add(v);
            }
        }

        return set;
    }

    public static <K, V> Smap<K, V> sort(final Map<K, V> map) {
        return new SmapImpl<>(new TreeMap<>(map));
    }

    public static <T extends Comparable<T>> Sset<T> sort(final Set<? extends T> values) {
        return new SsetImpl<>(new TreeSet<>(values));
    }

    public static <T extends Comparable<T>> Slist<T> sort(final List<? extends T> values) {
        return list(new TreeSet<>(values));
    }

    public static <K, V> Map<K, V> freeze(final Map<? extends K, ? extends V> map) {
        return Collections.unmodifiableMap(map);
    }

    public static <T> Set<T> freeze(final Set<? extends T> values) {
        return Collections.unmodifiableSet(values);
    }

    public static <T> List<T> freeze(final List<? extends T> values) {
        return Collections.unmodifiableList(values);
    }

    public static boolean empty(final Iterable<?> i) {
        if (i == null) {
            return true;
        }

        if (i instanceof Collection) {
            return ((Collection<?>) i).isEmpty();
        } else {
            return i.iterator().hasNext();
        }
    }

    public static boolean empty(final Map<?, ?> map) {
        return (map == null) || map.isEmpty();
    }

    public static boolean empty(final Optional<?> opt) {
        return (opt == null) || !opt.isPresent();
    }

    public static boolean empty(final CharSequence cs) {
        return (cs == null) || (cs.length() == 0);
    }

    public static boolean blank(final CharSequence cs) {
        return empty(cs) || cs.chars().allMatch(c -> Character.isWhitespace(c));
    }

    public static boolean empty(final Object[] ary) {
        return (ary == null) || (ary.length == 0);
    }

    @SafeVarargs
    public static <T> T choose(final T... values) {

        for (final T val : values) {
            if (val != null) {
                return val;
            }
        }

        throw new IllegalArgumentException();
    }

    @SafeVarargs
    public static <T> T choose(final Supplier<T>... values) {

        for (final Supplier<T> o : values) {

            final T val = o.get();

            if (val != null) {
                return val;
            }
        }

        throw new IllegalArgumentException();
    }

    public static <T extends Comparable<T>> T max(final T l, final T r) {
        return (0 <= l.compareTo(r)) ? r : l;
    }

    public static <T extends Comparable<T>> T min(final T l, final T r) {
        return (l.compareTo(r) < 0) ? r : l;
    }

    @SafeVarargs
    public static <T extends Comparable<T>> T max(final T... values) {
        return minmax(true, values);
    }

    @SafeVarargs
    public static <T extends Comparable<T>> T min(final T... values) {
        return minmax(false, values);
    }

    private static <T extends Comparable<T>> T minmax(final boolean max, final T[] values) {

        if (empty(values)) {
            return null;
        }

        T rslt = values[0];

        for (final T value : values) {
            rslt = max ? max(value, rslt) : min(value, rslt);
        }

        return rslt;
    }

    public static Pattern regex(final String ptrn) {
        return Pattern.compile(ptrn);
    }

    public static Class<?> typed(@SuppressWarnings("rawtypes")
    final Class cls) {
        return cls;
    }

    public static Map<?, ?> typed(@SuppressWarnings("rawtypes")
    final Map raw) {
        return raw;
    }

    public static List<?> typed(@SuppressWarnings("rawtypes")
    final List raw) {
        return raw;
    }

    public static Set<?> typed(@SuppressWarnings("rawtypes")
    final Set raw) {
        return raw;
    }

    public static <K, V> Smap<K, V> map(final Map<? extends K, ? extends V> map) {
        return new SmapImpl<K, V>().pushAll(optional(map));
    }

    public static <K, V> Smap<K, V> map() {
        return new SmapImpl<>();
    }

    public static <K, V> Smap<K, V> map(@SuppressWarnings("rawtypes")
    final Class<? extends Map> clazz, final K key, final V val) {

        try {
            return new SmapImpl<K, V>(clazz.newInstance()).push(key, val);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <K, V> Smap<K, V> map(@SuppressWarnings("rawtypes")
    final Class<? extends Map> clazz, final K key, final Optional<? extends V> val) {

        try {
            return new SmapImpl<K, V>(clazz.newInstance()).push(key, val);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <K, V> Smap<K, V> map(final K key, final V val) {

        @SuppressWarnings("unchecked")
        final Smap<K, V> map = (Smap<K, V>) map().push(key, val);

        return map;
    }

    public static <K, V> Smap<K, V> map(final K key, final Optional<? extends V> val) {

        @SuppressWarnings("unchecked")
        final Smap<K, V> map = (Smap<K, V>) map().push(key, val);

        return map;
    }

    // CHECKSTYLE:OFF

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1) {
        return map(MAP_TYPE, k0, v0).push(k1, v1);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21, final K k22, final V v22) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21).push(k22, v22);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21, final K k22, final V v22, final K k23, final V v23) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21).push(k22, v22).push(k23, v23);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21, final K k22, final V v22, final K k23, final V v23,
        final K k24, final V v24) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21).push(k22, v22).push(k23, v23).push(k24, v24);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21, final K k22, final V v22, final K k23, final V v23,
        final K k24, final V v24, final K k25, final V v25) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21).push(k22, v22).push(k23, v23).push(k24, v24).push(k25, v25);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21, final K k22, final V v22, final K k23, final V v23,
        final K k24, final V v24, final K k25, final V v25, final K k26, final V v26) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21).push(k22, v22).push(k23, v23).push(k24, v24).push(k25, v25).push(k26, v26);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21, final K k22, final V v22, final K k23, final V v23,
        final K k24, final V v24, final K k25, final V v25, final K k26, final V v26, final K k27, final V v27) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21).push(k22, v22).push(k23, v23).push(k24, v24).push(k25, v25).push(k26, v26).push(k27, v27);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21, final K k22, final V v22, final K k23, final V v23,
        final K k24, final V v24, final K k25, final V v25, final K k26, final V v26, final K k27, final V v27,
        final K k28, final V v28) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21).push(k22, v22).push(k23, v23).push(k24, v24).push(k25, v25).push(k26, v26).push(k27, v27)
            .push(k28, v28);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21, final K k22, final V v22, final K k23, final V v23,
        final K k24, final V v24, final K k25, final V v25, final K k26, final V v26, final K k27, final V v27,
        final K k28, final V v28, final K k29, final V v29) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21).push(k22, v22).push(k23, v23).push(k24, v24).push(k25, v25).push(k26, v26).push(k27, v27)
            .push(k28, v28).push(k29, v29);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21, final K k22, final V v22, final K k23, final V v23,
        final K k24, final V v24, final K k25, final V v25, final K k26, final V v26, final K k27, final V v27,
        final K k28, final V v28, final K k29, final V v29, final K k30, final V v30) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21).push(k22, v22).push(k23, v23).push(k24, v24).push(k25, v25).push(k26, v26).push(k27, v27)
            .push(k28, v28).push(k29, v29).push(k30, v30);
    }

    public static <K, V> Smap<K, V> map(final K k0, final V v0, final K k1, final V v1, final K k2, final V v2,
        final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7,
        final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11,
        final K k12, final V v12, final K k13, final V v13, final K k14, final V v14, final K k15, final V v15,
        final K k16, final V v16, final K k17, final V v17, final K k18, final V v18, final K k19, final V v19,
        final K k20, final V v20, final K k21, final V v21, final K k22, final V v22, final K k23, final V v23,
        final K k24, final V v24, final K k25, final V v25, final K k26, final V v26, final K k27, final V v27,
        final K k28, final V v28, final K k29, final V v29, final K k30, final V v30, final K k31, final V v31) {
        return map(MAP_TYPE, k0, v0).push(k1, v1).push(k2, v2).push(k3, v3).push(k4, v4).push(k5, v5).push(k6, v6)
            .push(k7, v7).push(k8, v8).push(k9, v9).push(k10, v10).push(k11, v11).push(k12, v12).push(k13, v13)
            .push(k14, v14).push(k15, v15).push(k16, v16).push(k17, v17).push(k18, v18).push(k19, v19).push(k20, v20)
            .push(k21, v21).push(k22, v22).push(k23, v23).push(k24, v24).push(k25, v25).push(k26, v26).push(k27, v27)
            .push(k28, v28).push(k29, v29).push(k30, v30).push(k31, v31);
    }

    // CHECKSTYLE:OFF

    private static final class SmapImpl<K, V>
        extends AbstractMap<K, V>
        implements Smap<K, V>, Serializable {

        private static final long serialVersionUID = 8705188807596442213L;

        private Map<K, V> store;

        SmapImpl() {
            this(new HashMap<>());
        }

        SmapImpl(final Map<K, V> store) {
            this.store = store;
        }

        @Override
        public Smap<K, V> slice(final Iterable<? extends K> keys) {

            final Smap<K, V> map = Indolently.map();

            for (final K key : keys) {
                map.put(key, this.get(key));
            }

            return map;
        }

        @Override
        public Sset<K> keys() {
            return new SsetImpl<>(this.keySet());
        }

        @Override
        public V put(final K key, final V value) {
            return this.store.put(key, value);
        }

        @Override
        public SmapImpl<K, V> push(final K key, final V value) {
            this.put(key, value);
            return this;
        }

        @Override
        public SmapImpl<K, V> pushAll(final Map<? extends K, ? extends V> map) {
            this.putAll(map);
            return this;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return this.store.entrySet();
        }

        @Override
        public Map<K, V> freeze() {
            return Indolently.freeze(this);
        }

        @Override
        public Smap<K, V> push(final K key, final Optional<? extends V> value) {
            return Indolently.empty(value) ? this : this.push(key, value.get());
        }

        @Override
        public Smap<K, V> pushAll(final Optional<? extends Map<? extends K, ? extends V>> map) {
            return Indolently.empty(map) ? this : this.pushAll(map.get());
        }

        @Override
        public Smap<K, V> delete(final Iterable<? extends K> keys) {
            for (final K key : keys) {
                this.remove(key);
            }
            return this;
        }

        @Override
        public String toString() {
            return this.store.toString();
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode() ^ this.store.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            return (this == o) || this.store.equals(o);
        }
    }

    private static final class SlistImpl<T>
        extends AbstractList<T>
        implements Slist<T>, Serializable {

        private static final long serialVersionUID = 8705188807596442213L;

        private final List<T> store;

        SlistImpl() {
            this(new ArrayList<>());
        }

        SlistImpl(final List<T> store) {
            this.store = store;
        }

        @Override
        public Sset<T> set() {
            return new SsetImpl<>(new LinkedHashSet<>(this));
        }

        @Override
        public T set(final int i, final T val) {
            return this.store.set(this.idx(i), val);
        }

        @Override
        public void add(final int i, final T val) {
            this.store.add(this.idx(i), val);
        }

        @Override
        public T remove(final int i) {
            return this.store.remove(this.idx(i));
        }

        @Override
        public Slist<T> push(final T value) {
            this.store.add(value);
            return this;
        }

        @Override
        public Slist<T> pushAll(final Iterable<? extends T> values) {
            for (final T val : values) {
                this.store.add(val);
            }
            return this;
        }

        @Override
        public Slist<T> push(final Optional<? extends T> value) {
            return Indolently.empty(value) ? this : this.push(value.get());
        }

        @Override
        public Slist<T> pushAll(final Optional<? extends Iterable<? extends T>> values) {
            return Indolently.empty(values) ? this : this.pushAll(values.get());
        }

        @Override
        public Slist<T> push(final int idx, final T value) {
            this.add(idx, value);
            return this;
        }

        @Override
        public Slist<T> pushAll(final int idx, final Iterable<? extends T> values) {
            for (final T val : values) {
                this.add(idx, val);
            }
            return this;
        }

        @Override
        public Slist<T> push(final int idx, final Optional<? extends T> value) {
            return Indolently.empty(value) ? this : this.push(idx, value.get());
        }

        @Override
        public Slist<T> pushAll(final int idx, final Optional<? extends Iterable<? extends T>> values) {
            return Indolently.empty(values) ? this : this.pushAll(idx, values.get());
        }

        @Override
        public List<T> freeze() {
            return Indolently.freeze(this);
        }

        @Override
        public T get(final int i) {
            return this.store.get(this.idx(i));
        }

        private int idx(final int i) {
            return (i < 0) ? this.size() + i : i;
        }

        @Override
        public List<T> subList(final int from, final int to) {
            return this.store.subList(this.idx(from), this.idx(to));
        }

        @Override
        public int size() {
            return this.store.size();
        }

        @Override
        public T first() {
            return this.get(0);
        }

        @Override
        public Slist<T> tail() {
            return (this.size() <= 1) ? Indolently.list() : Indolently.list(this.subList(1, -1));
        }

        @Override
        public T last() {
            return this.get(-1);
        }

        @Override
        public Slist<T> delete(final Iterable<? extends T> values) {
            this.removeAll(Indolently.list(values));
            return this;
        }

        @Override
        public Slist<T> slice(final int from, final int to) {
            return Indolently.list(this.subList(from, to));
        }

        @Override
        public Slist<T> each(final Function<T, ? extends T> f) {
            this.stream().map(f).close();
            return this;
        }

        @Override
        public String toString() {
            return this.store.toString();
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode() ^ this.store.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            return (this == o) || this.store.equals(o);
        }
    }

    private static final class SsetImpl<T>
        extends AbstractSet<T>
        implements Sset<T>, Serializable {

        private static final long serialVersionUID = 8705188807596442213L;

        private final Set<T> store;

        SsetImpl() {
            this(new HashSet<>());
        }

        SsetImpl(final Set<T> store) {
            this.store = store;
        }

        @Override
        public Slist<T> list() {
            return new SlistImpl<T>().pushAll(this);
        }

        @Override
        public boolean add(final T element) {
            return this.store.add(element);
        }

        @Override
        public Sset<T> push(final T value) {
            this.store.add(value);
            return this;
        }

        @Override
        public Sset<T> pushAll(final Iterable<? extends T> values) {
            for (final T val : values) {
                this.store.add(val);
            }
            return this;
        }

        @Override
        public Sset<T> push(final Optional<? extends T> value) {
            return Indolently.empty(value) ? this : this.push(value.get());
        }

        @Override
        public Sset<T> pushAll(final Optional<? extends Iterable<? extends T>> values) {
            return Indolently.empty(values) ? this : this.pushAll(values.get());
        }

        @Override
        public Set<T> freeze() {
            return Indolently.freeze(this);
        }

        @Override
        public int size() {
            return this.store.size();
        }

        @Override
        public Iterator<T> iterator() {
            return this.store.iterator();
        }

        @Override
        public T first() {
            return this.iterator().next();
        }

        @Override
        public Sset<T> tail() {
            return (this.size() <= 1) ? Indolently.set() : Indolently.set(Indolently.list(this).subList(1,
                this.size() - 1));
        }

        @Override
        public T last() {

            for (final Iterator<T> i = this.iterator(); i.hasNext();) {
                if (!i.hasNext()) {
                    return i.next();
                }
            }

            throw new NoSuchElementException();
        }

        @Override
        public Sset<T> delete(final Iterable<? extends T> values) {
            this.removeAll(Indolently.list(values));
            return this;
        }

        @Override
        public String toString() {
            return this.store.toString();
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode() ^ this.store.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            return (this == o) || this.store.equals(o);
        }
    }
}
