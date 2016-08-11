package bestsummarydevelopment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
		
		//gets info from file
		BufferedReader reader = null;
		try {
			//creates file reader
			reader = new BufferedReader(new FileReader(new File("data/data.txt")));
			
			//stores all the text info in an ArrayList
			List<String> initialPopAsString = new ArrayList<String>();
			String input = "";
			while((input = reader.readLine()) != null){
					initialPopAsString.add(input);
			}
			//adds the info of the file to the genetic algorithm
			for(int i=0;i<initialPopAsString.size();i++){
				String[] temp1 = initialPopAsString.get(i).split(" ");
				
				double[] temp2 = new double[temp1.length-1];
				for(int j=0; j<temp2.length;j++){
					temp2[j] = Double.parseDouble(temp1[j]);
				}
				//currently there is an unused value in temp2, but it doesn't cause an error
				sg.add(new SentenceGenome(temp2.clone(), Double.parseDouble(temp1[temp1.length-1])));
			}
			
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		
		
		SentenceGenAlg Algorithm = new SentenceGenAlg(sg, 10);
		
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
		
		
		//write info to file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(new File("data/data.txt")));
			
			String toWrite = "";
			List<SentenceGenome> infoToWrite = Algorithm.getPop();
			System.out.println("INFO to write:" + infoToWrite);
			for(int i=0;i<infoToWrite.size();i++){
				for(int j=0;j<infoToWrite.get(i).getWeights().length; j++){
					toWrite += infoToWrite.get(i).getWeights()[j] + " ";
				}
				toWrite += infoToWrite.get(i).getFitness() + "\n";
			}
			
			writer.write(toWrite);
			
			writer.close();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	//gets user input that determines the amount of sentences in the summary
		public static void promptNumberOfSummarySentences() {
			System.out.println("How many sentences do you want in the summaries?");
			int response = s.nextInt();
			
			//add something that checks the number of sentences in the article
			while (response < 1 || response > 7) { //currently there will be an error if article is less than 7 sentences
				System.out.println("That is not a valid number. Please enter the number of sentences you want in the summaries.");
				response = s.nextInt();
			}
			
			System.out.println("Please wait about 10 seconds...");
			setSummarySentences(response);
		}

		public static int getSummarySentences() {
			return summarySentences;
		}

		public static void setSummarySentences(int summarySentences) {
			Main.summarySentences = summarySentences;
		}
}
