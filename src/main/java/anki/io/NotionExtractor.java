package anki.io;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NotionExtractor extends ExtractorBase {
    @Getter
    private Document document;

    public NotionExtractor(String path) {
        super(path);
    }


    public Document getDocument(int index) throws IOException {
        if (index < 0 || index > getFiles().length) {
            log.error("{} can be from 0 to {}", "index", getFiles().length);
            throw new IndexOutOfBoundsException("%s can be from 0 to %d".formatted("index", getFiles().length));
        }
        Document doc = null;
        File file = getFile(index);
        doc = Jsoup.parse(file, "UTF-8");
        return doc;
    }

    public String getText(int index) throws IOException {
        String bodyText = null;
        Document doc ;
        doc = getDocument(index);
        bodyText = doc.body().text();
        return bodyText;
    }

    public List<Page> getPages() throws IOException {
        List<Page> pages = new ArrayList<>();
        Document doc = null;
        Page page;
        String title;
        String bodyText;
        for (int i = 0; i < getFiles().length; i++) {
            doc = getDocument(i);
            title = doc.head().text();
            bodyText = doc.body().text();
            page = new Page(title, bodyText);
            pages.add(page);
        }
        return pages;
    }

}