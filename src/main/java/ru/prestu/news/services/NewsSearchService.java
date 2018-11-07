package ru.prestu.news.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.prestu.news.domain.Item;
import ru.prestu.news.domain.Source;
import ru.prestu.news.exceptions.MainElementNotFoundException;
import ru.prestu.news.repositories.ItemRepository;
import ru.prestu.news.repositories.SourceRepository;
import ru.prestu.news.services.parsers.MeduzaPageParser;
import ru.prestu.news.services.parsers.PageParser;
import ru.prestu.news.services.parsers.TjournalPageParser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NewsSearchService {

    private final Logger logger = LoggerFactory.getLogger(NewsSearchService.class);

    private final ItemRepository itemRepository;
    private final SourceRepository sourceRepository;

    private final DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
    private static final Map<String, PageParser> parsers = new HashMap<>();

    static {
        parsers.put("meduza", new MeduzaPageParser());
        parsers.put("tjournal", new TjournalPageParser());
    }

    public NewsSearchService(ItemRepository itemRepository, SourceRepository sourceRepository) {
        this.itemRepository = itemRepository;
        this.sourceRepository = sourceRepository;
    }

    public void searchNews() {
        logger.info("Start parsing");
        sourceRepository.findAll().forEach(this::parse);
        logger.info("End parsing");
    }

    private void parse(Source source) {
        Item lastItem = itemRepository.findFirstBySourceOrderByPubDateDesc(source);
        Date latest;
        if (lastItem == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            latest = cal.getTime();
        } else {
            latest = lastItem.getPubDate();
            long latestInMillis = latest.getTime();
            long second = 1000;
            latest = new Date(latestInMillis + second);
        }
        Item item;
        try {
            String title = null;
            String description = null;
            String link = null;
            Date pubDate = null;
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new URL(source.getRssUrl()).openStream();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName().getLocalPart();
                    switch (localPart) {
                        case "title":
                            title = getCharacterData(event, eventReader);
                            break;
                        case "description":
                            description = getCharacterData(event, eventReader);
                            break;
                        case "link":
                            link = getCharacterData(event, eventReader);
                            break;
                        case "pubDate":
                            pubDate = format.parse(getCharacterData(event, eventReader));
                            break;
                    }
                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart().equals("item")) {
                        event = eventReader.nextEvent();
                        if (pubDate == null || latest.after(pubDate)) continue;
                        item = new Item();
                        item.setTitle(title);
                        item.setDescription(description);
                        item.setLink(link);
                        item.setPubDate(pubDate);
                        item.setSource(source);
                        try {
                            item.setHtmlContent(parsers.get(source.getDescription()).parseHtml(link));
                        } catch (MainElementNotFoundException e) {
                            logger.info(e.getMessage());
                            item.setHtmlContent("");
                        }
                        itemRepository.save(item);
                    }
                }
            }
        } catch (XMLStreamException | IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        return result;
    }

}
