import java.util.ArrayList;

public class WordCounter {
    String article;
    Sentence[] sentences;

    public WordCounter(String a) {
        article = a;
    }
    public Sentence[] makeSentences() {
    	int mark = 0;
    	ArrayList<Sentence> sentenceList = new ArrayList<Sentence>();
    	for (int i = 0; i < article.length(); i++) {
			if (article.charAt(i) == '.' || article.charAt(i) == '!' || article.charAt(i) == '?') {
				Sentence sentence = new Sentence(article.substring(mark,i + 1));
				sentenceList.add(sentence);
				mark = i + 2;
				//assuming there will always be spaces at the beginning of each sentence. we should change this later
			}
		}
    	sentences = new Sentence[sentenceList.size()];
    	for (int i = 0; i < sentenceList.size(); i++) {
    		sentences[i] = sentenceList.get(i);
    	}
    	return sentences;
    }
    
    public void setWordNumbers() {
    	for (int i = 0; i < sentences.length; i++) {
    		//Make sure we make the getNumWords() method for Sentence!
    		for (int j = 0; j < sentences.getNumWords(); j++) {
    			int count = 0;
    			for (int k = 0; k < sentences.length; k++) {
    				for (int l = 0; l < sentences.getNumWords(); l++) {
    					//Make sure we make the getWord(int i) method for Sentence!
    					//Make sure we make the equals(Word) method for Word!
    					if (sentences[i].getWord(j).equals(sentences[k].getWord(l)))
    						count++;
    				}
    			}
    			sentences[i].getWord(j).setInstances(count);
    		}
    	}
    }
}
