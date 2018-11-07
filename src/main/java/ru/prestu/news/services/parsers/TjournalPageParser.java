package ru.prestu.news.services.parsers;

import java.io.IOException;
import java.util.Arrays;

public class TjournalPageParser extends AbstractPageParser {

    public TjournalPageParser() {
        sourceAttrNames = Arrays.asList("src", "air-image-src");
    }

    @Override
    public String parseHtml(String url) throws IOException {
        return from(url).
            mainElement("l-wide_container l-wide_container--white").
            ignore("entry_header__info l-mb-15").
            ignore("entry_content__shares l-clear").
            ignore("entry_footer entry_footer--full l-clear").
            ignore("l-hidden entry_data").
            ignore("propaganda").
            ignore("relap_propaganda").
            ignore("entry_related_entries").
            get();
    }

}
