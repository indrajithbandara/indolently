// Copyright 2016 takahashikzn
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

import java.util.Iterator;

import org.junit.Test;

import static jp.root42.indolently.Iterative.*;
import static org.assertj.core.api.Assertions.*;


/**
 * @author root42 Inc.
 * @version $Id$
 */
public class SIterTest {

    /**
     * test of {@link SIter#filter(java.util.function.Predicate)}
     */
    @Test
    public void filter() {

        assertThat(range(1, 1).filter(x -> x < 0).hasNext()).isFalse();
        assertThat(range(1, 2).filter(x -> x > 0).hasNext()).isTrue();
        assertThat(range(1, 100000).filter(x -> x < 0).hasNext()).isFalse();

        for (final Iterator<Integer> i = range(1, 100000).filter(x -> x > 0); i.hasNext();) {
            assertThat(i.next()).isGreaterThan(0);
        }
    }
}