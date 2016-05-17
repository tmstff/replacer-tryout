import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.LinkedList;

public class ReplacerTest {

    @Test
    public void process() throws Exception {
        try (
            InputStream is = getResourceAsStream("shop_original.html");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
        ) {

            Replacer replacer = new Replacer(contentContributors());

            long before = System.currentTimeMillis();

            replacer.process(is, os);

            long after = System.currentTimeMillis();
            System.out.println("Took " + (after - before) + " ms");

            String actualReplacedShopHtml = IOUtils.toString(os.toByteArray(), "UTF-8");
            String expectedReplacedShopHtml = IOUtils.toString(getResourceAsStream("shop_replaced.html"), "UTF-8");

            Assert.assertEquals(expectedReplacedShopHtml, actualReplacedShopHtml);

        }
    }

    private LinkedList<ContentContributor> contentContributors() {
        LinkedList<ContentContributor> contentContributors = new LinkedList<>();
        contentContributors.add(newHeaderCssContributor());
        contentContributors.add(newHeaderJsContributor());
        contentContributors.add(newHeaderContributor());
        return contentContributors;
    }

    private ContentContributor newHeaderCssContributor() {
        return new ContentContributor() {
            @Override
            public Indicator indicator() {
                return new Indicator(
                        "&lt;!--@@begin-new-header-css-section@@--&gt;",
                        "&lt;!--@@end-new-header-css-section@@--&gt;");
            }

            @Override
            public String contribute() {
                return "NEW_HEADER_CSS";
            }
        };
    }

    private ContentContributor newHeaderJsContributor() {
        return new ContentContributor() {
            @Override
            public Indicator indicator() {
                return new Indicator(
                        "&lt;!--@@begin-new-header-js-section@@--&gt;",
                        "&lt;!--@@end-new-header-js-section@@--&gt;");
            }

            @Override
            public String contribute() {
                return "NEW_HEADER_JS";
            }
        };
    }

    private ContentContributor newHeaderContributor() {
        return new ContentContributor() {
            @Override
            public Indicator indicator() {
                return new Indicator(
                        "&lt;!--@@begin-header@@--&gt;",
                        "&lt;!--@@end-header@@--&gt;");
            }

            @Override
            public String contribute() {
                return "HEADER";
            }
        };
    }

    private InputStream getResourceAsStream(String name) {
        return this.getClass().getResourceAsStream(name);
    }

}