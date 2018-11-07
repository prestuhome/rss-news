package ru.prestu.news.services.parsers;

import java.io.IOException;

public class MeduzaPageParser extends AbstractPageParser {

    @Override
    public String parseHtml(String url) throws IOException {
        return from(url).
            mainElement("GeneralMaterial-container").
            ignore("RenderBlocks-tag").
            ignore("Meta-root Meta-simple").
            ignore("Meta-root Meta-center Meta-rich").
            ignore("Meta-root Meta-rich").
            ignore("MaterialNote-root MaterialNote-default").
            ignore("MaterialNote-root MaterialNote-center").
            ignore("Toolbar-root").
            ignore("Toolbar-root Toolbar-center").
            ignore("RelatedBlock-root RelatedBlock-simple").
            ignore("RelatedBlock-root RelatedBlock-rich").
            get();
    }

}
