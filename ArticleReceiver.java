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

	private ArrayList<Article> newsArticles = new ArrayList<>();
	private ArrayList<String> newsLinks = new ArrayList<>();

	public ArticleReceiver(int numArticles, String link) {
		if (numArticles != 0) {
			receiveNewsArticles(numArticles, link);
		}else{
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
				String line;

				// fix bbc trash urls
				if (urlAddress.equals(Main.BBC_URL)) {
					numArticles++;
				}

				while ((line = in.readLine()) != null && newsLinks.size() <= numArticles) {
					if (line.contains("<link>")) {
						// find links through tags
						int firstPos = line.indexOf("<link>");
						String temp = line.substring(firstPos);
						temp = temp.replace("<link>", "");
						int lastPos = temp.indexOf("</link>");
						temp = temp.substring(0, lastPos);

						newsLinks.add(temp);
					}
				}

				in.close();

				// test if there are links and if there is remove first
				// unnecessary
				// link
				if (!newsLinks.isEmpty()) {
					if (urlAddress.equals(Main.BBC_URL)) {
						newsLinks.remove(0);
						newsLinks.remove(0);
					}else if(urlAddress.equals(Main.CNN_URL) || urlAddress.equals(Main.FOX_URL) || urlAddress.equals(Main.ESPN_URL)){
						newsLinks.remove(0);
					}
				} else {
					System.out.println("ERROR: No Found Articles. Check If You Have Wifi.");
				}

				// gather articles from "section" tag of article using Jsoup
				for (String newsLink : newsLinks) {
					// get webpage
					Document doc = Jsoup.connect(newsLink).get();

					// get article from different websites
					String article = null;
					if (urlAddress.equals(Main.FOX_URL)) {
						Elements element = doc.select("p");
						article = element.text();
					} else if (urlAddress.equals(Main.CNN_URL)) {
						Elements element = doc.select("section");
						article = element.text();
					} else if (urlAddress.equals(Main.BBC_URL)) {
						Elements element = doc.select("p");
						article = element.text();
					}else if(urlAddress.equals(Main.ESPN_URL)){
						Elements element = doc.select("p");
						article = element.text();
					}
					
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
