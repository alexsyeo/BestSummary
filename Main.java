package bestsummarydevelopment;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeProcessingException;

public class Main {

	private static ArrayList<Article> articles = new ArrayList<>();

	public static final String TOP_STORIES_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&output=rss";
	public static final String SPORTS_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=s&output=rss";
	public static final String BUSINESS_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=b&output=rss";

	public static final int SUMMARY_SENTENCES = 2;

	public static void main(String[] args) {
		List<SentenceGenome> sg = new ArrayList<SentenceGenome>();
		sg.add(new SentenceGenome(new double[]{0.9, 0.7, 0.4, 0.2, 0.5, 0.5}));
		sg.add(new SentenceGenome(new double[]{0.84, 0.77, 0.4, 0.3, 0.5, 0.5}));
		sg.add(new SentenceGenome(new double[]{0.9, 0.7, 0.4, 0.2, 0.5, 0.5}));
		sg.add(new SentenceGenome(new double[]{0.84, 0.77, 0.4, 0.3, 0.5, 0.5}));
		sg.add(new SentenceGenome(new double[]{0.9, 0.7, 0.4, 0.2, 0.5, 0.5}));
		sg.add(new SentenceGenome(new double[]{0.84, 0.77, 0.4, 0.3, 0.5, 0.5}));
		sg.add(new SentenceGenome(new double[]{0.9, 0.7, 0.4, 0.2, 0.5, 0.5}));
		sg.add(new SentenceGenome(new double[]{0.84, 0.77, 0.4, 0.3, 0.5, 0.5}));
		sg.add(new SentenceGenome(new double[]{0.9, 0.7, 0.4, 0.2, 0.5, 0.5}));
		sg.add(new SentenceGenome(new double[]{0.84, 0.77, 0.4, 0.3, 0.5, 0.5}));
		
		
		SentenceGenAlg Algorithm = new SentenceGenAlg(sg, 5);
		
		// Get top stories from Google News

		//NO MORE THAN 10 ARTICLES
		ArticleReceiver receiver1 = null;
		
		try {
			receiver1 = new ArticleReceiver(10, TOP_STORIES_URL, Algorithm);
		} catch (BoilerpipeProcessingException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		articles.addAll(receiver1.getArticles());
		
		for (int i=0; i<articles.size();i++){
			System.out.println(articles.get(i).getSummary());
			System.out.println(articles.get(i).printInfo());
			articles.get(i).setFitnessOfGenome();
		}
	}
}
