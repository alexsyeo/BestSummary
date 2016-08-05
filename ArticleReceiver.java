package com.gigstudios.newssummary;

import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

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
    private ArrayList<String> newsTitles = new ArrayList<String>();
    private Context context;

    public ArticleReceiver(int numArticles, String link, Context context){
        this.context = context;
        if (numArticles != 0) {


            //temporary / bad
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);




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

                int linkTagStart = 0, linkTagEnd = 0, titleTagStart = 0, titleTagEnd = 0;
                while (newsLinks.size() < numArticles) {
                    //find start and end of link tag
                    linkTagStart = fullRss.indexOf("<link>");
                    linkTagEnd = fullRss.indexOf("</link>");
                    titleTagStart = fullRss.indexOf("<title>");
                    titleTagEnd = fullRss.indexOf("</title>");
                    newsLinks.add(fullRss.substring(linkTagStart + 6, linkTagEnd));
                    newsTitles.add(fullRss.substring(titleTagStart + 7, titleTagEnd));
                    fullRss = fullRss.substring(linkTagEnd + 5);
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

                System.out.println(newsTitles);
                System.out.println(newsLinks);

                // gather articles from "section" tag of article using Jsoup
                for (int i = 0; i < newsTitles.size(); i++) {
                    // get webpage
                    if(!newsTitles.get(i).isEmpty()) {
                        Document doc = Jsoup.connect(newsLinks.get(i)).timeout(5000).get();
                        Elements element = doc.select("p");
                        String article = element.text();

                        newsArticles.add(new Article(article, MainActivity.SUMMARY_SENTENCES, context));

                        Article a = newsArticles.get(newsArticles.size() - 1);
                        a.setTitle(newsTitles.get(i));
                        a.setUrl(newsLinks.get(i));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    private String merge(String articleA, String articleB) {
    	int maxCount = 0;
		int maxIndex = 0;
		//runs through possible starting indexes of articleB
		for (int i = 0; i < articleB.length(); i++) {
			//counts how many characters are equal
			int count = 0;
			int max = articleA.length();
			if (articleB.length()-i < max)
				max = articleB.length()-i;
			for (int j = 0; j < max; j++) {
				if (articleA.charAt(j) == articleB.charAt(i+j)) {
					count++;
					if (count > maxCount) {
						maxCount = count;
						maxIndex = i;
					}
				}
				else {
					break;
				}
			}
		}
		return articleB.substring(maxIndex);
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
