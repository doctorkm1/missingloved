// RssController.java
package com.alkamel.missingloved.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RssController {

    @GetMapping("/rss/news")
    public List<String> getRssHeadlines() {
        List<String> headlines = new ArrayList<>();
        try {
            URL url = new URL("https://www.aljazeera.net/aljazeerarss");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(url.openStream());
            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < Math.min(items.getLength(), 5); i++) {
                Element item = (Element) items.item(i);
                String title = item.getElementsByTagName("title").item(0).getTextContent();
                headlines.add(title);
            }
        } catch (Exception e) {
            headlines.add("تعذر تحميل الأخبار الآن.");
        }
        return headlines;
    }
}
