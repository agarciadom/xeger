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

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

/**
 * An object that will generate text from a regular expression. In a way, it's the opposite of a regular expression
 * matcher: an instance of this class will produce text that is guaranteed to match the regular expression passed in.
 */
public class Xeger {

    public static class FailedRandomWalkException extends Exception {
        public FailedRandomWalkException(String message) {
            super(message);
        }
    }

    private final Automaton automaton;
    private Random random;

    /**
     * Constructs a new instance, accepting the regular expression and the randomizer.
     *
     * @param regex  The regular expression. (Not <code>null</code>.)
     * @param random The object that will randomize the way the String is generated. (Not <code>null</code>.)
     * @throws IllegalArgumentException If the regular expression is invalid.
     */
    public Xeger(String regex, Random random) {
        assert regex != null;
        assert random != null;
        this.automaton = new RegExp(regex).toAutomaton();
        this.random = random;
    }

    /**
     * As {@link nl.flotsam.xeger.Xeger#Xeger(String, java.util.Random)}, creating a {@link java.util.Random} instance
     * implicityly.
     */
    public Xeger(String regex) {
        this(regex, new Random());
    }

    /**
     * Generates a random String that is guaranteed to match the regular expression passed to the constructor.
     */
    public String generate() {
        StringBuilder builder = new StringBuilder();
        generate(builder, automaton.getInitialState());
        return builder.toString();
    }

    /**
     * Attempts to generate a random String using a random walk of length between <code>minLength</code> and
     * <code>maxLength</code> steps.
     *
     * A target length will be randomly generated within this range, and a random walk of at least that length
     * will be attempted. The walk will initially avoid states with no outgoing transitions until the target
     * length is reached: from then onwards, it will consider all transitions equally, and stop as soon as an
     * accept state has been reached or the maximum walk length has been exceeded. If the minimum length is not
     * reached or the maximum walk length has been exceeded, a {@link FailedRandomWalkException} exception will
     * be thrown. Callers could catch this exception to try again if desired.
     *
     * @param minLength Minimum length for the range.
     * @param maxLength Maximum length for the range.
     * @throws FailedRandomWalkException The minimum random walk length was not reached, or the maximum random walk
     * length was exceeded.
     */
    public String generate(int minLength, int maxLength) throws FailedRandomWalkException {
        final StringBuilder builder = new StringBuilder();
        int walkLength = 0;
        State state = automaton.getInitialState();

        // First get to the uniformly distributed target length
        final int targetLength = Xeger.getRandomInt(minLength, maxLength, random);
        while (walkLength < targetLength) {
            List<Transition> transitions = state.getSortedTransitions(false);
            if (transitions.size() == 0) {
                if (walkLength >= minLength) {
                    assert state.isAccept();
                    return builder.toString();
                } else {
                    throw new FailedRandomWalkException(String.format(
                            "Reached accept state before minimum length (current = %d < min = %d)",
                            walkLength, minLength));
                }
            }

            // Try to prefer non-final transitions if possible at this first stage
            List<Transition> nonFinalTransitions = transitions.stream()
                    .filter(t -> !t.getDest().getTransitions().isEmpty()).collect(Collectors.toList());
            if (!nonFinalTransitions.isEmpty()) {
                transitions = nonFinalTransitions;
            }

            final int option = Xeger.getRandomInt(0, transitions.size() - 1, random);
            final Transition transition = transitions.get(option);
            appendChoice(builder, transition);
            state = transition.getDest();
            ++walkLength;
        }

        // Now, get to an accept state
        while (!state.isAccept() && walkLength < maxLength) {
            List<Transition> transitions = state.getSortedTransitions(false);
            if (transitions.size() == 0) {
               assert state.isAccept();
               return builder.toString();
            }

            final int option = Xeger.getRandomInt(0, transitions.size() - 1, random);
            final Transition transition = transitions.get(option);
            appendChoice(builder, transition);
            state = transition.getDest();
            ++walkLength;
        }

        if (state.isAccept()) {
            return builder.toString();
        } else {
            throw new FailedRandomWalkException(String.format(
                    "Exceeded maximum walk length (%d) before reaching an accept state: " +
                            "target length was %d (min length = %d)",
                    maxLength, targetLength, minLength));
        }
    }

    private Optional<Transition> appendRandomChoice(StringBuilder builder, State state, int minLength, int walkLength) throws FailedRandomWalkException {
        List<Transition> transitions = state.getSortedTransitions(false);
        if (transitions.size() == 0) {
            if (walkLength >= minLength) {
                return Optional.empty();
            } else {
                throw new FailedRandomWalkException(String.format(
                        "Reached accept state before minimum length (current = %d < min = %d)",
                        walkLength, minLength));
            }
        }

        final int option = Xeger.getRandomInt(0, transitions.size() - 1, random);
        final Transition transition = transitions.get(option);
        appendChoice(builder, transition);
        return Optional.of(transition);
    }

    private void generate(StringBuilder builder, State state) {
        List<Transition> transitions = state.getSortedTransitions(false);
        if (transitions.size() == 0) {
            assert state.isAccept();
            return;
        }
        int nroptions = state.isAccept() ? transitions.size() : transitions.size() - 1;
        int option = Xeger.getRandomInt(0, nroptions, random);
        if (state.isAccept() && option == 0) {          // 0 is considered stop
            return;
        }
        // Moving on to next transition
        Transition transition = transitions.get(option - (state.isAccept() ? 1 : 0));
        appendChoice(builder, transition);
        generate(builder, transition.getDest());
    }

    private void appendChoice(StringBuilder builder, Transition transition) {
        char c = (char) Xeger.getRandomInt(transition.getMin(), transition.getMax(), random);
        builder.append(c);
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Generates a random number within the given bounds.
     *
     * @param min The minimum number (inclusive).
     * @param max The maximum number (inclusive).
     * @param random The object used as the randomizer.
     * @return A random number in the given range.
     */
    static int getRandomInt(int min, int max, Random random) {
        // Use random.nextInt as it guarantees a uniform distribution
        int maxForRandom = max - min + 1;
        return random.nextInt(maxForRandom) + min;
    }
}