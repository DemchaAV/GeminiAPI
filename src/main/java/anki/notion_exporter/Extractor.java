package anki.notion_exporter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class Extractor {
    @Getter
    private final String path;
    private Document document;
    private File[] files;

    public Extractor(String path) {
        this.path = path;
        loadFiles();
    }

    private void loadFiles() {
        File folder = new File(path);
        this.files = folder.listFiles();
    }

    public File getFile(int index) {
        return files[index];
    }

    public Document getDocument(int index) throws IOException {
        if (index < 0 || index > files.length) {
            throw new IndexOutOfBoundsException("%s can be from 0 to %d".formatted("index", files.length));
        }
        Document doc = null;
        doc = Jsoup.parse(getFile(index), "UTF-8");
        return doc;
    }

    public String getText(int index) throws IOException {
        String bodyText = null;
        Document doc = null;
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
        for (int i = 0; i < files.length; i++) {
            doc = getDocument(i);
            title = doc.head().text();
            bodyText = doc.body().text();
            page = new Page(title, bodyText);
            pages.add(page);
        }
        return pages;
    }

}

class Test {
    public static void main(String[] args) {


        Extractor extractor = new Extractor("C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\Notion\\notion becupe");
        try {
            System.out.println(extractor.getPages().size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
