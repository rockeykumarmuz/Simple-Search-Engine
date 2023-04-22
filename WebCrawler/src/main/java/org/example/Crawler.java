package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class Crawler {
    HashSet<String> urlSet;
    int MAX_DEPTH=2;
    Crawler() {
        urlSet= new HashSet<String>();
    }


    public void getPageTextAndLinks(String url, int depth) {
        if(urlSet.contains(url)) {
            return;
        }
        if(depth>=MAX_DEPTH) {
            return;
        }
        if(urlSet.add(url)) {
            System.out.println(url);
        }
        depth++;

        try {
            // we are converting url into java object and timeout is setting 5000ms if timeout then process for next
            Document document = Jsoup.connect(url).timeout(60000).get();

            // here indexers work starts
            Indexer indexer= new Indexer(document, url);

            System.out.println(document.title());

            // anchor tag creates a hyperlink and we are accessing direct link
            Elements availableLinksOnPage = document.select("a[href]");

            // we will iterate over each nodes using for loop
            for (Element currentLink : availableLinksOnPage) {
                // currentLink is an element and and we want string so we use attribut key to convert into string
                getPageTextAndLinks(currentLink.attr("abs:href"), depth);
            }
         }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Crawler crawler= new Crawler();
        crawler.getPageTextAndLinks("https://www.javatpoint.com", 0);
        }
}