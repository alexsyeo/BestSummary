
package bestsummarydevelopment;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class Sentence {

  private int points;
  private int numWords;
  private int indexInArticle;
  private List<Word> words;
  private static String[] badList = {"cnn", "caption", "photo", "email", "facebook", "twitter", "pinterest", "whatsapp", "linkedin", "related"};

  //constructor, creates a sentence that is split up by spaces
  public Sentence(String s, POSModel model) {
	  words = new ArrayList<Word>();
	  if(model != null){
		POSTaggerME tagger = new POSTaggerME(model);
	    String[] temp = s.split(" ");
	    //array of parts of speech corresponding to each word in the string
	    String[] POS = tagger.tag(temp);
	    for (int i = 0; i < temp.length; i++) {
	      words.add(new Word(temp[i], POS[i]));
	    }
	    this.numWords = temp.length;
	  }
  }
  
//sets the score of the sentence
  public boolean scoreSentence(Article article) {
	  this.points = instancePoints();
	  this.points /= (checkBadWords() + 1);
	  
	  //changes the score based on the location of the sentence within the article
	  this.points /= (article.getLength() / (article.getLength() - this.indexInArticle));
	  if (checkBadList())
		  this.points = 0;
	  return true;
  }
  
  public int instancePoints() {
	  int count = 0;
	    for(int i = 0;i < words.size();i++){
	    	int temp = words.get(i).getInstances()*100;
	    	String posTemp = words.get(i).getPartOfSpeech();
	    	
	      if (posTemp.equals("NNP") || posTemp.equals("NNPS"))
	        temp *= 2;
	      //we use substrings for the rest of the if statements to catch multiple similar parts of speech
	      else if (posTemp.equals("CC") || posTemp.equals("IN") || posTemp.equals("DT") || posTemp.equals("RB"))
	    	temp = 0;
	    
	      count += temp;
	    }
	  return count;
  }
    
  //checks to see if there is a "bad" word in the sentence
	public int checkBadWords() {
	  int badWords = 0;
	  if (words.size() >= 6) {
		  for (int i = 0; i < 6; i++) {
		    if (words.get(i).getPartOfSpeech().equals("WP") || (words.get(i).getPartOfSpeech().length() > 2 && words.get(i).getPartOfSpeech().equals("PRP")))
		      badWords++;
		  }
	  }
	    return badWords;
	}

	//checks to see if there is unnecessary info in the sentence
		public boolean checkBadList() {
			for (int i = 0; i < words.size(); i++) {
				for (int k = 0; k < badList.length; k++) {
					if (words.get(i).equals(badList[k]))
						return true;
				}
			}
			return false;
		}
	
  //getters and setters for number of words in the sentence
  public int getNumWords(){
	  return this.numWords;
  }
  public void setNumWords(int i) {
    this.numWords = i;
  }


  //getters and setters for specific Words
  public Word getWord(int index){
	  return words.get(index);
  }
  public boolean setWord(int index, Word element){
	  words.set(index, element);
	  return true;
  }


  //getters and setters for points
  public int getPoints() {
    return this.points;
  }
  public void setPoints(int i) {
    this.points = i;
  }
  
  public void setIndexInArticle(int i) {
	  this.indexInArticle = i;
  }

  public int getIndexInArticle(){
	  return this.indexInArticle;
  }

  //to-string method
  public String toString() {
    String j = "";
    for (int i = 0; i < words.size(); i++) {
      j += words.get(i) + " ";
    }
    return j;
  }
  public String getInfo() {
	  return "Index:\t" + this.indexInArticle + "NumberOfWords:\t" + this.numWords + "Points:\t" + this.points;
  }
}
