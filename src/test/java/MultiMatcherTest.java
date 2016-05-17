import org.junit.Test;

import java.util.LinkedList;
import java.util.Optional;

import static org.junit.Assert.*;

public class MultiMatcherTest {

    @Test
    public void process() throws Exception {
        LinkedList<char[]> strings = new LinkedList<>();
        strings.add("ac".toCharArray());
        strings.add("dd".toCharArray());

        MultiMatcher multiMatcher = new MultiMatcher(strings);

        ReplacerDFA.Matcher.Result result = multiMatcher.process('b');
        assertEquals(false, result.complete);
        assertEquals(false, result.matches);
        assertEquals(Optional.empty(), result.matchedString);

        result = multiMatcher.process('a');
        assertEquals(false, result.complete);
        assertEquals(true, result.matches);
        assertEquals(Optional.empty(), result.matchedString);

        result = multiMatcher.process('a');
        assertEquals(false, result.complete);
        assertEquals(false, result.matches);
        assertEquals(Optional.empty(), result.matchedString);

        result = multiMatcher.process('a');
        assertEquals(false, result.complete);
        assertEquals(true, result.matches);
        assertEquals(Optional.empty(), result.matchedString);

        result = multiMatcher.process('c');
        assertEquals(true, result.complete);
        assertEquals(true, result.matches);
        assertEquals(Optional.of("ac"), result.matchedString);

        result = multiMatcher.process('a');
        assertEquals(false, result.complete);
        assertEquals(true, result.matches);
        assertEquals(Optional.empty(), result.matchedString);

        result = multiMatcher.process('b');
        assertEquals(false, result.complete);
        assertEquals(false, result.matches);
        assertEquals(Optional.empty(), result.matchedString);

        result = multiMatcher.process('a');
        assertEquals(false, result.complete);
        assertEquals(true, result.matches);
        assertEquals(Optional.empty(), result.matchedString);
    }

    @Test
    public void processRealExample() throws Exception {
        LinkedList<char[]> strings = new LinkedList<>();
        strings.add("&lt;!--@@begin-new-header-css-section@@--&gt;".toCharArray());

        MultiMatcher multiMatcher = new MultiMatcher(strings);

        ReplacerDFA.Matcher.Result result;

        for (char c : "&lt;!".toCharArray()) {
            result = multiMatcher.process(c);
            assertEquals("char " + c, false, result.complete);
            assertEquals("char " + c, true, result.matches);
            assertEquals("char " + c, Optional.empty(), result.matchedString);
        }

        result = multiMatcher.process('D');
        assertEquals(false, result.complete);
        assertEquals(false, result.matches);
        assertEquals(Optional.empty(), result.matchedString);

        result = multiMatcher.process('O');
        assertEquals(false, result.complete);
        assertEquals(false, result.matches);
        assertEquals(Optional.empty(), result.matchedString);

        for (char c : "&lt;!--@@begin-new-header-css-section@@--&gt".toCharArray()) {
            result = multiMatcher.process(c);
            assertEquals("char " + c, false, result.complete);
            assertEquals("char " + c, true, result.matches);
            assertEquals("char " + c, Optional.empty(), result.matchedString);
        }

        result = multiMatcher.process(';');
        assertEquals(true, result.complete);
        assertEquals(true, result.matches);
        assertEquals(Optional.of("&lt;!--@@begin-new-header-css-section@@--&gt;"), result.matchedString);

        result = multiMatcher.process('b');
        assertEquals(false, result.complete);
        assertEquals(false, result.matches);
        assertEquals(Optional.empty(), result.matchedString);
    }


    @Test
    public void reset() throws Exception {

    }

}