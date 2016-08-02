package bestsummarydevelopment;

import java.util.ArrayList;

import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeProcessingException;

public class Main {

	private static ArrayList<Article> articles = new ArrayList<>();

	public static final String TOP_STORIES_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&output=rss";
	public static final String SPORTS_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=s&output=rss";
	public static final String BUSINESS_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=b&output=rss";

	public static final int SUMMARY_SENTENCES = 5;

	public static void main(String[] args) throws BoilerpipeProcessingException, SAXException {
		// Get top stories from Google News

		//NO MORE THAN 10 ARTICLES
		ArticleReceiver receiver1 = new ArticleReceiver(10, TOP_STORIES_URL);
		articles.addAll(receiver1.getArticles());
		
		for (int i = 0; i < articles.size(); i++) {
			System.out.println(articles.get(i).getSummary());
			System.out.println(articles.get(i).printInfo());
		}
	}
}
