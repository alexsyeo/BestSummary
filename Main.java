import java.util.ArrayList;
import java.util.List;

public class Main {
	
<<<<<<< HEAD
	private static List<Article> articles = new ArrayList<Article>();
=======
	private static List<Article> articles = new ArrayList<>();
>>>>>>> bc2945c0d621b58b97f8e49783d2f6f3f5962524

	public static void main(String[] args){
		//Get 5 articles from CNN News
		ArticleReceiver receiver = new ArticleReceiver(5 , "http://rss.cnn.com/rss/cnn_topstories.rss");
		articles = receiver.getArticles();
		System.out.println(articles.get(0));
    }
}
