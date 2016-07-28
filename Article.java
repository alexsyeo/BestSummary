package bestsummarydevelopment;

public class Article {

	private Sentence[] sentences;
	private int numSentencesInSummary;
	private String text;
	private Sentence[] bestSentences;

	public Article(String s, int numSentences) {
		this.numSentencesInSummary = numSentences;
		WordCounter counter = new WordCounter(s);
		sentences = counter.makeSentences();
		this.text = s;

		for (int i = 0; i < sentences.length; i++) {
			sentences[i].scoreSentence(this);
		}

		// Splits the large string into sentences by punctuation (|\\ is an "or"
		// operator)

		// here, we should take these "sentences" and turn them into Sentences
		// by using the Sentence constructor, feeding in
		// the Strings. This way, the code below will work.
		// NOT EVERY PERIOD WILL SIGNIFY THE END OF A SENTENCE. WE NEED IF
		// STATEMENTS (if it's not Mr., Dr., Mrs., etc.)

		//find best sentences for the summary
		bestSentences = findBestSentences();

		//temp print best sentences
		for (int i = 0; i < bestSentences.length; i++) {
			System.out.println(bestSentences[i]);
		}

	}

	private Sentence[] findBestSentences() {
		Sentence[] top = new Sentence[numSentencesInSummary];

		// initialize top array with blank sentences
		for (int i = 0; i < top.length; i++) {
			top[i] = new Sentence("", null);
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
}
