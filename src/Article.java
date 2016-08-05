package bestsummarydevelopment;
import java.util.Scanner;


public class Article {

	private Sentence[] sentences;
	private int numSentencesInSummary;
	private String text;
	private Sentence[] bestSentences;
	private String url;
	private String title;
	private SentenceGenome weightGenome;
	static Scanner s = new Scanner(System.in);

	//constructor for the Article class
	public Article(String s, int numSentences, SentenceGenome sG) {
		WordCounter counter = new WordCounter(s);
		
		this.numSentencesInSummary = numSentences;
		this.weightGenome = sG;
		this.sentences = counter.makeSentences(sG);
		this.text = s;
		
		
		//goes through the sentences and scores each of them
		for (int i = 0; i < sentences.length; i++) {
			sentences[i].scoreSentence(this);
		}

		//find best sentences for the summary
		bestSentences = findBestSentences();
	}

	
	//goes through the sentences and finds the sentences with the most points
	private Sentence[] findBestSentences() {
		Sentence[] top = new Sentence[numSentencesInSummary];

		// initialize top array with blank sentences
		for (int i = 0; i < top.length; i++) {
			top[i] = new Sentence("", null, null);
			top[i].setPoints(-999999999);
		}

		// get best sentences
		for (int i = 0; i < top.length; i++) {
			for (Sentence sentence : sentences) {
				if (sentence.getPoints() > top[i].getPoints()) {
					boolean isGood = true;
					for (int j = 0; j < top.length; j++) {
						if (top[j].equals(sentence))
							isGood = false;
					}
					if (isGood) {
						top[i] = sentence;
					}
				}
			}
		}

		// sort in order of appearance in article
		for (int i = 0; i < top.length; i++) {
			for (int j = i; j < top.length; j++) {
				if (top[j].getIndexInArticle() < top[i].getIndexInArticle()) {
					Sentence hold = top[i];
					top[i] = top[j];
					top[j] = hold;
				}
			}
		}

		return top;
	}

	public Sentence getSentence(int i) {
		return this.sentences[i];
	}

	public Sentence[] getBestSentences(){
		return bestSentences;
	}
	public String getText() {
		return text;
	}

	public void setText(String s) {
		text = s;
	}

	public String toString() {
		return text;
	}

	public int getNumberOfSentences() {
		return sentences.length;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	//prints the URL of the article, title of the article, and the summary itself
	public String getSummary() {
		String ret = "";
		//print best sentences
		ret += ("---------------------------------------\n");
		ret += ("URL:\t" + url + "\n");
		ret += ("Title:\t" + title + "\n");
		for (int i = 0; i < bestSentences.length; i++) {
			ret += (bestSentences[i] + "\n");
		}
		return ret;
	}
	
	//prints relevant info for each sentence, including its index, number of words and number of points
	public String printInfo() {
		String ret = "";
		//prints the info of each sentence
		for (int i = 0; i < bestSentences.length; i++) {
			ret += (bestSentences[i].getInfo() + "\n");
		}
		ret += "\n" + sentences.length;
		return ret;
	}
	
	//gets user input that rates the summary on a scale from 1 to 10 and returns that rating as a double
	public double setFitnessOfGenome() {
		System.out.println("Please rate the summary (0-10).");
		double rating = s.nextDouble();
		while (rating > 10 || rating < 0) {
			System.out.println("That is not within 0-10. Please rate the summary (0-10).");
			rating = s.nextDouble();
		}
		this.weightGenome.setFitness(rating);
		return rating;
	}
	
	//sets the weights of the SentenceGenome
	public boolean setWeightGenome(SentenceGenome sG){
		this.weightGenome = sG;
		return true;
	}
}
