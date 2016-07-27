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

	private static ArrayList<String> newsArticles = new ArrayList<>();
	private static ArrayList<String> newsLinks = new ArrayList<>();

	public ArticleReceiver(int numArticles, String link) {
		receiveNewsArticles(numArticles, link);
	}

	private static void receiveNewsArticles(int numArticles, String urlAddress) {
		URL rssUrl = null;
		// if connected to Internet
		if (internetIsAvailable()) {
			try {
				// gather links
				rssUrl = new URL(urlAddress);
				BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
				String line;
				while ((line = in.readLine()) != null && newsLinks.size() <= numArticles) {
					if (line.contains("<link>")) {
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
					newsLinks.remove(0);
				} else {
					System.out.println("ERROR: No Found Articles. Check If You Have Wifi.");
				}

				// gather articles from "section" tag of article using Jsoup
				for (String newsLink : newsLinks) {
					Document doc = Jsoup.connect(newsLink).get();
					Elements element = doc.select("section");

					String article = element.text();

					newsArticles.add(article);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("ERROR: No internet connection established.");
			return;
		}
	}

	public ArrayList<String> getArticles() {
		return newsArticles;
	}

	public String getArticle(int i) {
		if (newsArticles.size() <= i) {
			return "Null pointer exception";
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

