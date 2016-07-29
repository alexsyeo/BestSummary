package bestsummarydevelopment;

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

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

public class ArticleReceiver {

	private ArrayList<Article> newsArticles = new ArrayList<>();
	private ArrayList<String> newsLinks = new ArrayList<>();

	public ArticleReceiver(int numArticles, String link) throws BoilerpipeProcessingException, SAXException {
		if (numArticles != 0) {
			receiveNewsArticles(numArticles, link);
		}else{
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
					}else{
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
					String articleA = null;
					
					//BoilerPipe:
					final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(newsLink));
					final TextDocument docB = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
					String articleB = CommonExtractors.ARTICLE_EXTRACTOR.getText(docB);
					
					// get article from different websites
					if (urlAddress.equals(Main.CNN_URL)) {
						Elements element = doc.select("section");
						articleA = element.text();
					}else{
						Elements element = doc.select("p");
						articleA = element.text();
					}
					
					String article = foxMerge(articleA, articleB);
					
					/*System.out.println(articleA + "\n\n\n");
					System.out.println(articleB + "\n\n\n");
					System.out.println(article);*/
					
					
					/*System.out.println(articleB);
					System.out.println("\n=====A:====");
					newsArticles.add(new Article(articleA, Main.SUMMARY_SENTENCES));
					System.out.println("\n=====B:====");
					newsArticles.add(new Article(articleB, Main.SUMMARY_SENTENCES));*/
					
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

	private String foxMerge(String articleA, String articleB) {
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
