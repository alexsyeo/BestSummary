package com.gigstudios.newssummary;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

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
import org.xml.sax.SAXException;

public class ArticleReceiver {

    private ArrayList<Article> newsArticles = new ArrayList<Article>();
    private ArrayList<String> newsLinks = new ArrayList<String>();
    private ArrayList<String> newsTitles = new ArrayList<String>();
    private Context context;
    private Activity activity;

    public String getSectionLink() {
        return sectionLink;
    }

    public void setSectionLink(String sectionLink) {
        this.sectionLink = sectionLink;
    }

    private String sectionLink;

    public ArticleReceiver(int numArticles, String link, Context context, Activity activity) throws BoilerpipeProcessingException, SAXException {
        this.context = context;
        this.activity = activity;
        this.sectionLink = link;
        if (numArticles != 0) {
            receiveNewsArticles(numArticles, link);
        } else {
            System.out.println("ERROR: numArticles request for " + link + " cannot equal 0.");
        }
    }

    private void receiveNewsArticles(int numArticles, String urlAddress) throws BoilerpipeProcessingException, SAXException {
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

                int linkTagStart = 0, linkTagEnd = 0, titleTagStart = 0, titleTagEnd = 0;
                while (newsLinks.size() < numArticles) {
                    //find start and end of sectionLink tag
                    linkTagStart = fullRss.indexOf("<link>");
                    linkTagEnd = fullRss.indexOf("</link>");
                    titleTagStart = fullRss.indexOf("<title>");
                    titleTagEnd = fullRss.indexOf("</title>");
                    if (linkTagStart != -1 || linkTagEnd != -1) {
                        newsLinks.add(fullRss.substring(linkTagStart + 6, linkTagEnd));
                        newsTitles.add(fullRss.substring(titleTagStart + 7, titleTagEnd));
                        fullRss = fullRss.substring(linkTagEnd + 5);
                    } else {
                        break;
                    }
                }

                //remove 2 news links/titles
                if (!newsLinks.isEmpty()) {
                    newsLinks.remove(0);
                    newsLinks.remove(0);
                    newsTitles.remove(0);
                    newsTitles.remove(0);
                } else {
                    return;
                }

                //find actual website links inside google news links
                int startPos = 0;
                for (int i = 0; i < newsLinks.size(); i++) {
                    startPos = newsLinks.get(i).indexOf("url=") + 4;
                    newsLinks.set(i, newsLinks.get(i).substring(startPos));
                }

                //fix xml format symbols in titles
                for (int i = 0; i < newsTitles.size(); i++) {
                    while (newsTitles.get(i).contains("&apos;") ||
                            newsTitles.get(i).contains("&lt;") ||
                            newsTitles.get(i).contains("&gt;") ||
                            newsTitles.get(i).contains("&amp;") ||
                            newsTitles.get(i).contains("&quot;")) {
                        int index;
                        if (newsTitles.get(i).contains("&apos;")) {
                            index = newsTitles.get(i).indexOf("&apos;");
                            newsTitles.set(i, newsTitles.get(i).substring(0, index) + "\'" + newsTitles.get(i).substring(index + 6));
                        } else if (newsTitles.get(i).contains("&lt;")) {
                            index = newsTitles.get(i).indexOf("&lt;");
                            newsTitles.set(i, newsTitles.get(i).substring(0, index) + "<" + newsTitles.get(i).substring(index + 4));
                        } else if (newsTitles.get(i).contains("&gt;")) {
                            index = newsTitles.get(i).indexOf("&gt;");
                            newsTitles.set(i, newsTitles.get(i).substring(0, index) + ">" + newsTitles.get(i).substring(index + 4));
                        } else if (newsTitles.get(i).contains("&amp;")) {
                            index = newsTitles.get(i).indexOf("&amp;");
                            newsTitles.set(i, newsTitles.get(i).substring(0, index) + "&" + newsTitles.get(i).substring(index + 5));
                        } else if (newsTitles.get(i).contains("&quot;")) {
                            index = newsTitles.get(i).indexOf("&quot;");
                            newsTitles.set(i, newsTitles.get(i).substring(0, index) + "\"" + newsTitles.get(i).substring(index + 6));
                        }
                    }
                }

                //boilerpipe
                for (String newsLink : newsLinks) {
                    gatherArticles(newsLink);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //NO INTERNET
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean gatherArticles(String newsLink) throws IOException, BoilerpipeProcessingException, SAXException {
        //Jsoup:
        Document doc = Jsoup.connect(newsLink).timeout(5000).get();
        Elements element = doc.select("p");
        String articleA = element.text();
        //Boilerpipe:
        final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(newsLink));
        final TextDocument docB = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
        String articleB = CommonExtractors.ARTICLE_EXTRACTOR.getText(docB);
        //Merging two versions:
        String article;
        if (isNYT(newsLink))
            article = mergeNYT(articleA);
        else
            article = merge(articleA, articleB);
        newsArticles.add(new Article(article, MainActivity.SUMMARY_SENTENCES, context, newsTitles.get(0)));
        Article a = newsArticles.get(newsArticles.size() - 1);
        newsTitles.remove(0);
        a.setUrl(newsLink);

        if (sectionLink.equals(MainActivity.sectionUrls[MainActivity.currentSection])) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (newsArticles.get(newsArticles.size() - 1).getNumberOfSentences() > 1 && newsArticles.get(newsArticles.size() - 1).getCharLength() > 200) {
                        MainActivity.addArticleToList(newsArticles.get(newsArticles.size() - 1));
                    }
                }
            });
        }


        return true;
    }

    private String merge(String articleA, String articleB) {
        int maxCount = 0;
        int maxIndex = 0;
        //runs through possible starting indexes of articleB
        for (int i = 0; i < articleB.length(); i++) {
            //counts how many characters are equal
            int count = 0;
            int max = articleA.length();
            if (articleB.length() - i < max)
                max = articleB.length() - i;
            for (int j = 0; j < max; j++) {
                if (articleA.charAt(j) == articleB.charAt(i + j)) {
                    count++;
                    if (count > maxCount) {
                        maxCount = count;
                        maxIndex = i;
                    }
                } else {
                    break;
                }
            }
        }
        return articleB.substring(maxIndex);
    }

    public boolean isNYT(String newsLink) {
        String url = "http://www.nytimes.com";
        return (newsLink.length() >= url.length() && newsLink.substring(0, url.length()).equals(url));
    }

    private String mergeNYT(String articleA) {
        int charCount = 150;
        //Dash thing:
        boolean dashed = false;
        int index = 0;
        for (int i = 0; i < charCount; i++) {
            if (articleA.charAt(i) == '—') {
                index = i;
                articleA = articleA.substring(index + 2);
                dashed = true;
                break;
            }
        }
        if (!dashed) {
            //Removing ads:
            String bad1 = "Advertisement Advertisement ";
            if (articleA.length() >= bad1.length() && articleA.substring(0, bad1.length()).equals(bad1))
                articleA = articleA.substring(bad1.length());
            //Removing authors/date:
            String letters = "abcdefghijklmnopqrstuvwxyz";
            index = 0;
            int count = 0;
            for (int i = 0; i < charCount; i++) {
                boolean isCapital = articleA.charAt(i) == ' ';
                if (!isCapital) {
                    boolean isLetter = false;
                    for (int j = 0; j < letters.length(); j++) {
                        if (Character.toLowerCase(articleA.charAt(i)) == letters.charAt(j))
                            isLetter = true;
                    }
                    if (isLetter)
                        isCapital = Character.toLowerCase(articleA.charAt(i)) != articleA.charAt(i);
                }
                if (isCapital) {
                    count++;
                    if (count > 8 /*Change this var*/)
                        index = i;
                }
                if (!isCapital)
                    count = 0;
            }
            if (index != 0) {
                for (int i = index; i < articleA.length(); i++) {
                    boolean isLetter1 = false;
                    boolean isLetter2 = false;
                    for (int j = 0; j < letters.length(); j++) {
                        if (Character.toLowerCase(articleA.charAt(i)) == letters.charAt(j))
                            isLetter1 = true;
                        if (Character.toLowerCase(articleA.charAt(i + 1)) == letters.charAt(j))
                            isLetter2 = true;
                    }
                    if ((isLetter1 && isLetter2) && (Character.toLowerCase(articleA.charAt(i)) != articleA.charAt(i) && Character.toLowerCase(articleA.charAt(i + 1)) == articleA.charAt(i + 1))) {
                        articleA = articleA.substring(i);
                        break;
                    }

                }
            }
        }
        return articleA;
    }

    private static boolean internetIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
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
