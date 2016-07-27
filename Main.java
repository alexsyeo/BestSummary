import java.util.ArrayList;

public class Main {

	private static ArrayList<String> articles = new ArrayList<>();
	public static final String CNN_URL = "http://rss.cnn.com/rss/cnn_topstories.rss";
	public static final String FOX_URL = "http://feeds.foxnews.com/foxnews/most-popular";
	public static final String BBC_URL = "http://feeds.bbci.co.uk/news/rss.xml";

	public static void main(String[] args) {
		// Get top stories from news sources

		// CNN
		ArticleReceiver receiver1 = new ArticleReceiver(1, CNN_URL);
		articles.addAll(receiver1.getArticles());

		// FOX
		ArticleReceiver receiver2 = new ArticleReceiver(1, FOX_URL);
		articles.addAll(receiver2.getArticles());

		// BBC
		ArticleReceiver receiver3 = new ArticleReceiver(1, BBC_URL);
		articles.addAll(receiver3.getArticles());
	}
}
