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

	public Article(String s, int numSentences, SentenceGenome sG) {
		WordCounter counter = new WordCounter(s);
		
		this.numSentencesInSummary = numSentences;
		this.weightGenome = sG;
		this.sentences = counter.makeSentences(sG);
		this.text = s;
		
		for (int i = 0; i < sentences.length; i++) {
			try{
			sentences[i].scoreSentence(this);
			} catch(Exception e) {}
		}
		// Splits the large string into sentences by punctuation (|\\ is an "or"
		// operator)

		// here, we should take these "sentences" and turn them into Sentences
		// by using the Sentence constructor, feeding in
		// the Strings. This way, the code below will work.
		// NOT EVERY PERIOD WILL SIGNIFY THE END OF A SENTENCE. WE NEED IF
		// STATEMENTS (if it's not Mr., Mrs., etc.)

		//find best sentences for the summary
		bestSentences = findBestSentences();
	}

	private Sentence[] findBestSentences() {
		Sentence[] top = new Sentence[numSentencesInSummary];

		// initialize top array with blank sentences
		for (int i = 0; i < top.length; i++) {
			top[i] = new Sentence("", null, null);
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

	public int getLength() {
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
	public String printInfo() {
		String ret = "";
		//prints the info of each sentence
		for (int i = 0; i < bestSentences.length; i++) {
			ret += (bestSentences[i].getInfo() + "\n");
		}
		ret += "\n";
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
	
	public boolean setWeightGenome(SentenceGenome sG){
		this.weightGenome = sG;
		return true;
	}
}