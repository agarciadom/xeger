/**
 * Copyright 2009 Wilfred Springer
 * Copyright 2012 Jason Pell
 * Copyright 2013 Antonio García-Domínguez
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class XegerTest {

	@Test
	public void shouldGenerateTextCorrectly() {
		String regex = "[ab]{4,6}c";
		Xeger generator = new Xeger(regex);
		for (int i = 0; i < 100; i++) {
			String text = generator.generate();
			assertTrue(text.matches(regex));
		}
	}

	@Test
	public void testRepeatableRegex() {
		for (int x = 0; x < 1000; x++) {
			Xeger generator = new Xeger("[ab]{4,6}c", new Random(1000));
			Xeger generator2 = new Xeger("[ab]{4,6}c", new Random(1000));

			List<String> firstRegexList = generateRegex(generator, 100);
			List<String> secondRegexList = generateRegex(generator2, 100);

			assertListEquals(firstRegexList, secondRegexList);
		}
	}

	@Test
	public void testWalkRange() throws Exception {
		for (int x = 0; x < 100; x++) {
			Xeger generator = new Xeger("[ab]{0,100}c", new Random(1000));
			Xeger generator2 = new Xeger("[ab]{0,100}c", new Random(1000));

			List<String> firstRegexList = generateRegex(generator, 100, 0, 100);
			List<String> secondRegexList = generateRegex(generator2, 100, 0, 100);
			assertListEquals(firstRegexList, secondRegexList);
		}
	}

	private List<String> generateRegex(Xeger generator, int count, int minLength, int maxLength) throws Xeger.FailedRandomWalkException {
		List<String> regexList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			try {
				regexList.add(generator.generate(minLength, maxLength));
			} catch (Xeger.FailedRandomWalkException ex) {
				// add a placeholder for the failed attempt
				regexList.add(null);
			}
		}
		return regexList;
	}

	private <T> void assertListEquals(List<T> firstRegexList, List<T> secondRegexList) {
		assertEquals("size mismatch", firstRegexList.size(), secondRegexList.size());
		for (int i = 0; i < firstRegexList.size(); i++) {
			assertEquals("Index mismatch: " + i, firstRegexList.get(i), secondRegexList.get(i));
		}
	}

	private List<String> generateRegex(Xeger generator, int count) {
		List<String> regexList = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			regexList.add(generator.generate());
		}
		return regexList;
	}
}
