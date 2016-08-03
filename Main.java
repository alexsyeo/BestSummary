package bestsummarydevelopment;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeProcessingException;

public class Main {

	private static ArrayList<Article> articles = new ArrayList<>();

	public static final String TOP_STORIES_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&output=rss";
	public static final String SPORTS_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=s&output=rss";
	public static final String BUSINESS_URL = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&topic=b&output=rss";
	static Scanner s = new Scanner(System.in);
	private static int summarySentences;

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
			promptNumberOfSummarySentences();
			//creates article receiver, which creates the articles
			receiver1 = new ArticleReceiver(10, TOP_STORIES_URL, Algorithm);
		} catch (BoilerpipeProcessingException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
	
		articles.addAll(receiver1.getArticles());
		
		
		/*
		//goes through all the articles to find the lowest number of sentences in an article
		//NOT IMPLEMENTED NOW
		
		
		low = articles.get(0).getNumberOfSentences();
		
		
		for (int i = 1; i < articles.size(); i++) {
			int sub = articles.get(i).getNumberOfSentences();
			if (sub < low)
				low = sub;
		} */
		
		
	

		
		for (int i=0; i<articles.size();i++){
			System.out.println(articles.get(i).getSummary());
			System.out.println(articles.get(i).printInfo());
			articles.get(i).setFitnessOfGenome();
		}
	}
	
	//gets user input that determines the amount of sentences in the summary
		public static void promptNumberOfSummarySentences() {
			System.out.println("How many sentences do you want in the summary?");
			int response = s.nextInt();
			
			//add something that checks the number of sentences in the article
			while (response < 1 || response > 7) {
				System.out.println("That is not a valid number. Please enter the number of sentences you want in the summary.");
				response = s.nextInt();
			}
			setSummarySentences(response);
		}

		public static int getSummarySentences() {
			return summarySentences;
		}

		public static void setSummarySentences(int summarySentences) {
			Main.summarySentences = summarySentences;
		}


}
