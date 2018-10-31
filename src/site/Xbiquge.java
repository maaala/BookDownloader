package site;

import bean.Book;
import bean.Catalog;
import engine.BaseSite;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.NetUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By zia on 2018/10/31.
 * 新笔趣阁 https://www.xbiquge6.com
 */
public class Xbiquge extends BaseSite {

    private static final String root = "https://www.xbiquge6.com";

    @Override
    public String setSiteName() {
        setEncodeType("utf-8");
        return "新笔趣阁";
    }

    @Override
    public List<Book> search(String bookName) throws Exception {
        String html = NetUtil.getHtml("https://www.xbiquge6.com/search.php?keyword="
                + URLEncoder.encode(bookName, getEncodeType()), getEncodeType());

        Elements items = Jsoup.parse(html).getElementsByClass("result-game-item-detail");
        if (items == null || items.size() <= 0) {
            throw new IOException();
        }

        List<Book> bookList = new ArrayList<>();
        for (Element item : items) {
            String bkName = item.getElementsByTag("a").first().attr("title");
            String url = item.getElementsByTag("a").first().attr("href");
            Elements tags = item.getElementsByClass("result-game-item-info-tag");
            String author = tags.get(0).getElementsByTag("span").get(1).text();
            String lastUpdateTime = tags.get(2).getElementsByTag("span").get(1).text();
            String lastChapterName = tags.get(3).getElementsByTag("a").text();
            bookList.add(new Book(bkName, author, url, "未知", lastUpdateTime, lastChapterName, this));
        }
        return bookList;
    }

    @Override
    public List<Catalog> parseCatalog(String catalogHtml, String url) {
        Element listElement = Jsoup.parse(catalogHtml).getElementById("list");
        List<Catalog> catalogs = new ArrayList<>();
        for (Element a : listElement.getElementsByTag("a")) {
            String bkUrl = root + a.attr("href");
            String name = a.text();
            catalogs.add(new Catalog(name, bkUrl));
        }
        return catalogs;
    }

    @Override
    public List<String> parseContent(String chapterHtml) {
        String html = Jsoup.parse(chapterHtml).getElementById("content").text();
        String lines[] = html.split("<br>|<br/>|<br />");
        List<String> contents = new ArrayList<>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                contents.add(line);
            }
        }
        return contents;
    }
}