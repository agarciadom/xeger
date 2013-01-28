/**
 * Copyright 2009 Wilfred Springer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.flotsam.xeger;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import org.hamcrest.Matchers;
import dk.brics.automaton.RegExp;

import java.util.Random;

public class XegerUtilsTest {

    @Test
    public void shouldGenerateRandomNumberCorrectly() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int number = XegerUtils.getRandomInt(3, 7, random);
            assertThat(number, Matchers.greaterThanOrEqualTo(3));
            assertThat(number, Matchers.lessThanOrEqualTo(7));
        }
    }

}
