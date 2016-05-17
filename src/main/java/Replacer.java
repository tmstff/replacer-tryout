import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Replacer {

    private final ReplacerDFA.Matcher startMatcher;

    private final Map<String, Supplier<ReplacerDFA.Matcher>> startStringToEndMatcher;

    private final Map<String, Supplier<String>> endStringToContribution;

    public Replacer(final List<ContentContributor> contentContributors) {

        List<char[]> startStings = contentContributors.stream()
                .map(cc -> cc.indicator().getStart())
                .collect(Collectors.toList());
        startMatcher = new MultiMatcher(startStings);

        startStringToEndMatcher = contentContributors.stream()
                .collect(Collectors.toMap(
                    cc -> new String(cc.indicator().getStart()),
                    cc -> ( () -> new SimpleMatcher(cc.indicator().getEnd()))
                ));

        endStringToContribution = contentContributors.stream()
                .collect(Collectors.toMap(
                    cc -> new String(cc.indicator().getEnd()),
                    cc -> ( () -> cc.contribute() )
                ));
    }

    public void process(final InputStream is, final OutputStream os) {
        try (
            BufferedInputStream bis = new BufferedInputStream(is);
            InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
            BufferedOutputStream bos = new BufferedOutputStream(os);
            OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8");
        ) {

            ReplacerDFA replacerDFA = new ReplacerDFA(
                    startMatcher,
                    start -> startStringToEndMatcher.get(start).get(),
                    end -> endStringToContribution.get(end).get(),
                    createOutput(osw));
            replacerDFA.start();
            char[] cb = new char[512];
            int length;
            while ((length = isr.read(cb)) != -1) {
                for (int i = 0; i < length; i++) {
                    replacerDFA.process(cb[i]);
                }
                osw.flush();
            }
            replacerDFA.finish();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private ReplacerDFA.Output createOutput(OutputStreamWriter osw) {
        return new ReplacerDFA.Output() {
            @Override
            public void write(String s) {
                try {
                    osw.write(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void write(char c) {
                try {
                    osw.write(c);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
