import java.util.ArrayList;

public class Main {
	
	private static List<Article> articles = new ArrayList<>();

	public static void main(String[] args){
		//Get 5 articles from CNN News
		ArticleReceiver receiver = new ArticleReceiver(5 , "http://rss.cnn.com/rss/cnn_topstories.rss");
		articles = receiver.getArticles();
    }
}
