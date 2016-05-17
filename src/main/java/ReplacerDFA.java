import java.util.Optional;
import java.util.function.Function;

public class ReplacerDFA {

    public interface Output {
        void write(String s);
        void write(char c);
    }

    public interface Matcher {
        class Result {
            final boolean matches;
            final boolean complete;
            final Optional<String> matchedString;

            public Result(boolean matches, boolean complete, Optional<String> matchedString) {
                this.matches = matches;
                this.complete = complete;
                this.matchedString = matchedString;
            }
        }

        Result process(char c);

        void reset();
    }

    private enum State {
        NO_MATCH,
        MATCH_START,
        MATCH_CONTENT,
        MATCH_END
    }

    private final Matcher startMatcher;
    private final Function<String, Matcher> startStringToEndMatcher;
    private final Function<String, String> endStringToContribution;
    private final Output output;

    private Matcher endMatcher;
    private StringBuilder buffer;

    private State state;

    public ReplacerDFA(
            final Matcher startMatcher,
            final Function<String, Matcher> startStringToEndMatcher,
            final Function<String, String> endStringToContribution,
            final Output output
    ) {
        this.startMatcher = startMatcher;
        this.startStringToEndMatcher = startStringToEndMatcher;
        this.endStringToContribution = endStringToContribution;
        this.output = output;
    }

    public void start() {
        state = State.NO_MATCH;
        reinitializeBuffer();
        this.startMatcher.reset();
    }

    public void process(char c) {
        switch (state) {
            case NO_MATCH:
                noMatch(c);
                break;
            case MATCH_START:
                matchStart(c);
                break;
            case MATCH_CONTENT:
                matchContent(c);
                break;
            case MATCH_END:
                matchEnd(c);
                break;
            default:
                throw new IllegalStateException("state '" + state + "' is unkown!");
        }
    }

    private void noMatch(char c) {
        Matcher.Result matchResult = startMatcher.process(c);
        if (matchResult.matches) {
            state = State.MATCH_START;
            writeToBuffer(c);
        }
        else {
            state = State.NO_MATCH;
            output.write(c);
        }
    }

    private void matchStart(char c) {
        Matcher.Result matchResult = startMatcher.process(c);
        if (matchResult.matches) {
            if (matchResult.complete) {
                state = State.MATCH_CONTENT;
                writeToBuffer(c);
                endMatcher = startStringToEndMatcher.apply( matchResult.matchedString.get() );
            } else {
                state = State.MATCH_START;
                writeToBuffer(c);
            }
        }
        else {
            flushBuffer();
            // process same character again - might match at the beginning of the start string
            noMatch(c);
        }
    }

    private void matchContent(char c) {
        Matcher.Result matchResult = endMatcher.process(c);
        if (matchResult.matches) {
            state = State.MATCH_END;
            writeToBuffer(c);
        }
        else {
            state = State.MATCH_CONTENT;
            writeToBuffer(c);
        }
    }

    private void matchEnd(char c) {
        Matcher.Result matchResult = endMatcher.process(c);
        if (matchResult.matches) {
            if (matchResult.complete) {
                state = State.NO_MATCH;
                String contribution = endStringToContribution.apply( matchResult.matchedString.get() );
                output.write(contribution);
                reinitializeBuffer();
            } else {
                state = State.MATCH_END;
                writeToBuffer(c);
            }
        }
        else {
            // process same character again - might match at the beginning of the end string
            matchContent(c);
        }
    }

    public void finish() {
        state = State.NO_MATCH;
        flushBuffer();
    }

    private void flushBuffer() {
        writeBufferToOutput();
        reinitializeBuffer();
    }

    private void writeToBuffer(char c) {
        buffer.append(c);
    }

    private void writeBufferToOutput() {
        output.write(buffer.toString());
    }

    private void reinitializeBuffer() {
        buffer = new StringBuilder();
    }

}
