package bestsummarydevelopment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ArticleReceiver {

    private ArrayList<Article> newsArticles = new ArrayList<Article>();
    private ArrayList<String> newsLinks = new ArrayList<String>();

    public ArticleReceiver(int numArticles, String link) {
        if (numArticles != 0) {
            receiveNewsArticles(numArticles, link);
        } else {
            System.out.println("ERROR: numArticles request for " + link + " cannot equal 0.");
        }
    }

    private void receiveNewsArticles(int numArticles, String urlAddress) {
        URL rssUrl = null;
        // if connected to Internet
        if (internetIsAvailable()) {
            try {
                // gather links
                rssUrl = new URL(urlAddress);
                BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
                String fullRss = in.readLine();
                in.close();

                numArticles += 2;

                int linkTagStart = 0, linkTagEnd = 0, actualLink = 0;
                while (newsLinks.size() < numArticles) {
                    //find start and end of link tag
                    linkTagStart = fullRss.indexOf("<link>");
                    linkTagEnd = fullRss.indexOf("</link>");
                    newsLinks.add(fullRss.substring(linkTagStart + 6, linkTagEnd));
                    fullRss = fullRss.substring(linkTagEnd + 5);
                }

                //remove 2 news links
                if (!newsLinks.isEmpty()) {
                    newsLinks.remove(0);
                    newsLinks.remove(0);
                } else {
                    System.out.println("ERROR: No Found Articles. Check If You Have Wifi.");
                }

                //find actual website links inside google news links
                int startPos = 0;
                for (int i = 0; i < newsLinks.size(); i++) {
                    startPos = newsLinks.get(i).indexOf("url=") + 4;
                    newsLinks.set(i, newsLinks.get(i).substring(startPos));
                }

                // gather articles from "section" tag of article using Jsoup
                String article;
                for (String newsLink : newsLinks) {
                    // get webpage
                    Document doc = Jsoup.connect(newsLink).timeout(5000).get();
                    Elements element = doc.select("p");
                    article = element.text();

                    newsArticles.add(new Article(article, Main.SUMMARY_SENTENCES));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("ERROR: No internet connection established.");
            return;
        }
    }

    public ArrayList<Article> getArticles() {
        return newsArticles;
    }

    public Article getArticle(int i) {
        if (newsArticles.size() <= i) {
            return null;
        } else {
            return newsArticles.get(i);
        }
    }

    private static boolean internetIsAvailable() {
        try {
            final URL url = new URL("http://www.cnn.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
}
