package ru.prestu.news.services.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.prestu.news.exceptions.MainElementNotFoundException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class AbstractPageParser implements PageParser {

    protected String url;
    protected Document document;
    protected Element mainElement;
    protected List<String> sourceAttrNames = Collections.singletonList("src");

    private static final String IMAGE_TEMPLATE =
            "<figure >\n" +
            "   <img >\n" +
            "</figure>\n";
    private static final String VIDEO_TEMPLATE =
            "<figure >\n" +
            "   <video autoplay=\"\">\n" +
            "       <source >\n" +
            "   </video>\n" +
            "</figure>\n";

    protected AbstractPageParser from(String url) throws IOException {
        this.url = url;
        if (document != null) {
            clear();
            throw new RuntimeException("Source already specified");
        }
        this.document = Jsoup.connect(url).get();
        return this;
    }

    protected AbstractPageParser mainElement(String className) {
        if (document == null) throw new RuntimeException("Specify source page using method \"from(String url)\"");
        if (mainElement != null) {
            clear();
            throw new RuntimeException("Main element already specified");
        }
        Elements elements = document.getElementsByClass(className);
        if (elements.size() != 1) {
            String urlCopy = url;
            clear();
            throw new MainElementNotFoundException(urlCopy);
        }
        mainElement = document.getElementsByClass(className).get(0);
        return this;
    }

    protected AbstractPageParser ignore(String className) {
        if (mainElement == null) {
            clear();
            throw new RuntimeException("Specify main element using method \"mainElement(String className)\"");
        }
        mainElement.getElementsByClass(className).remove();
        return this;
    }

    public String get() {
        if (mainElement == null) throw new RuntimeException("Specify main element using method \"mainElement(String className)\"");
        changeSources();
        processFigures();
        String result = mainElement.toString();
        clear();
        return result;
    }

    private void changeSources() {
        String absHref = getAbsHref();
        sourceAttrNames.forEach(name -> changeSourcesBySourceAttrName(absHref, name));
    }

    private void processFigures() {
        Elements figures = mainElement.select("figure");
        for (Element figure : figures) {
            Elements images = figure.select("img");
            Elements videos = figure.select("video");
            Elements frames = figure.select("iframe");
            if (images.size() == 1) {
                Element image = images.first();
                String src = image.attr("src");
                Element newFigure = Jsoup.parse(IMAGE_TEMPLATE).selectFirst("figure");
                figure.replaceWith(newFigure);
                figure = newFigure;
                figure.select("img").
                        attr("src", src).
                        attr("alt", "").
                        attr("style", "width: 100%");
                figure.attr("style", "position: relative; display: block; width: 80%");
            } else if (videos.size() == 1) {
                String src = videos.first().select("source").attr("src");
                Element newFigure = Jsoup.parse(VIDEO_TEMPLATE).selectFirst("figure");
                figure.replaceWith(newFigure);
                figure = newFigure;
                figure.select("source").attr("src", src);
                figure.select("video").attr("style", "width: 100%");
                figure.attr("style", "position: relative; display: block; width: 80%");
            } else if (frames.size() == 1) {
                figure.attr("style", "position: relative; display: block");
            } else {
                figure.remove();
            }
        }
    }

    private void changeSourcesBySourceAttrName(String absHref, String sourceAttrName) {
        Elements elementsWithSource = mainElement.select("[" + sourceAttrName + "]");
        for (Element element : elementsWithSource) {
            String oldSrc = element.attr(sourceAttrName);
            String src;
            if (oldSrc.startsWith("/")) src = absHref + element.attr(sourceAttrName);
            else src = element.attr(sourceAttrName);
            element.attr("src", src);
        }
    }

    private String getAbsHref() {
        Element link = document.select("a").first();
        String absHref = link.attr("abs:href");
        return absHref.substring(0, absHref.length() - 1);
    }

    private void clear() {
        url = null;
        mainElement = null;
        document = null;
    }

}
