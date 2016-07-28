package bestsummarydevelopment;

import java.util.ArrayList;

public class Main {

	private static ArrayList<Article> articles = new ArrayList<>();
	public static final String CNN_URL = "http://rss.cnn.com/rss/cnn_topstories.rss";
	public static final String FOX_URL = "http://feeds.foxnews.com/foxnews/most-popular";
	public static final String BBC_URL = "http://feeds.bbci.co.uk/news/rss.xml";
	public static final String ESPN_URL = "http://espn.go.com/espn/rss/news";
	public static final int SUMMARY_SENTENCES = 3;

	public static void main(String[] args) {
		// Get top stories from news sources

		// CNN
		// ArticleReceiver receiver1 = new ArticleReceiver(2, CNN_URL);
		// articles.addAll(receiver1.getArticles());

		// FOX
		//ArticleReceiver receiver2 = new ArticleReceiver(20, FOX_URL);
		//articles.addAll(receiver2.getArticles());

		// BBC
		// ArticleReceiver receiver3 = new ArticleReceiver(1, BBC_URL);
		// articles.addAll(receiver3.getArticles());

		// FOX
		ArticleReceiver receiver4 = new ArticleReceiver(20, ESPN_URL);
		articles.addAll(receiver4.getArticles());

	}
}
