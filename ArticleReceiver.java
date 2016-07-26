import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArticleReceiver {

    private static ArrayList<String> newsArticles = new ArrayList<>();
    private static ArrayList<String> newsLinks = new ArrayList<>();

    public ArticleReceiver(int numArticles, String link){
    	receiveNewsArticles(numArticles, link);
    }
    
    private static void receiveNewsArticles(int numArticles, String urlAddress){
        URL rssUrl = null;
        try {
            //gather links
            rssUrl = new URL(urlAddress);
            BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
            String line;
            while((line = in.readLine()) != null && newsLinks.size() <= numArticles){
                if(line.contains("<link>")){
                    int firstPos = line.indexOf("<link>");
                    String temp = line.substring(firstPos);
                    temp = temp.replace("<link>", "");
                    int lastPos = temp.indexOf("</link>");
                    temp = temp.substring(0, lastPos);
                    
                    newsLinks.add(temp);
                }
            }

            in.close();
            
            //remove unnecessary link
            newsLinks.remove(0);
            System.out.println(newsLinks);

            //gather articles
            for (String newsLink : newsLinks) {
            	Document doc = Jsoup.connect(newsLink).get();
            	Elements element = doc.select("section");
            	
            	String article = element.text();
            	
            	newsArticles.add(article);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getArticles(){
        return newsArticles;
    }

    public String getArticle(int i){
        if(newsArticles.size() <= i){
            return "Null pointer exception";
        }else{
            return newsArticles.get(i);
        }
    }
}