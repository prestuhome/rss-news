package ru.prestu.news.services.parsers;

import java.io.IOException;

public interface PageParser {

    String parseHtml(String url) throws IOException;

}
