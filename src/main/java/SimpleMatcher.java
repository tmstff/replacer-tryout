import java.util.Optional;

public class SimpleMatcher implements ReplacerDFA.Matcher {

    private final char[] stringToMatch;
    private final int lastIndex;

    private int matchedSoFarIndex;

    public SimpleMatcher(char[] stringToMatch) {
        this.stringToMatch = stringToMatch;
        lastIndex = stringToMatch.length - 1;
        reset();
    }

    @Override
    public Result process(char c) {
        matchedSoFarIndex++;
        final boolean matched = stringToMatch[matchedSoFarIndex] == c;

        if (!matched) {
            reset();
        }

        final boolean complete = matchedSoFarIndex == lastIndex;
        if (complete) {
            reset();
        }

        final Optional<String> matchedString = complete
                ? Optional.of(new String(stringToMatch))
                : Optional.empty();

        return new Result(matched, complete, matchedString);
    }

    @Override
    public void reset() {
        matchedSoFarIndex = -1;
    }
}
