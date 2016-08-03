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
  private String text;
  private static String[] badList = {"cnn", "caption", "photo", "images", "email", "espn", "facebook", "twitter", "pinterest", "whatsapp", "linkedin", "related"};
  
  //list of different weights. NOUN, PROPER NOUN, PRESENT TENSE VERB, OTHER VERBS, ADJECTIVE, QUOTATION
  private double[] weightList;
  
  
  //constructor, creates a sentence that is split up by spaces
  public Sentence(String s, POSModel model, SentenceGenome sg) {
	  if(sg != null)
		  this.weightList = sg.getWeights();
	  else
		  this.weightList = null;
	  text = s;
	    String OK = "abcdefghijklmnopqrstuvwxyz' ";
	    for (int i = 0; i < s.length(); i++) {
	        boolean isOK = false;
	        for (int j = 0; j < OK.length(); j++) {
	            if (Character.toLowerCase(s.charAt(i)) == OK.charAt(j))
	                isOK = true;
	            if (isOK)
	                break;
	        }
	        if (!isOK) {
	            s = s.substring(0,i) + s.substring(i+1);
	            i--;
	        }
	    }
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
	  if(this.weightList != null){
		  this.points = instancePoints();
	  
		  //changes the score based on the location of the sentence within the article
		  this.points /= (article.getNumberOfSentences() / (article.getNumberOfSentences() - this.indexInArticle));
		  if (this.checkBadList() || this.checkBadWords() || this.checkFirstWord())
			  this.points = 0;
	  }
	  return true;
  }
  
  public int instancePoints() {
	  double count = 0;
	    for(int i = 0;i < words.size();i++){
	    	double temp = words.get(i).getInstances()*100;
	    	String posTemp = words.get(i).getPartOfSpeech();
	   
	    	//multiplies by certain weights depending on the part of speech (accesses the weightList array)
	    	
	    	//nouns
	    	if (posTemp.equals("NN") || posTemp.equals("NNS"))
	    		temp *= this.weightList[0];
	    	//proper nouns
	    	if (posTemp.equals("NNP") || posTemp.equals("NNPS"))
	    		temp *= this.weightList[1];
	    	//present tense verbs
	    	if (posTemp.equals("VBP") || posTemp.equals("VBZ"))
	    		temp *= this.weightList[2];
	    	//other types of verbs
	    	if (posTemp.equals("VB") || posTemp.equals("VBD") || posTemp.equals("VBG") || posTemp.equals("VBN"))
	    		temp *= this.weightList[3];
	    	//adjectives
	    	if (posTemp.equals("JJ") || posTemp.equals("JJR") || posTemp.equals("JJS"))
	    		temp *= this.weightList[4];
	    	//quotations
	    	if (this.containsString("\""))
	    		count *= this.weightList[5];
	    	
	    	//sets the word equal to zero if the word is a coordinating conjunction, subordinating conjunction, preposition, determiner, or adverb
	    	else if (posTemp.equals("CC") || posTemp.equals("IN") || posTemp.equals("DT") || posTemp.equals("RB"))
	    		temp = 0;
	    
	      count += temp;
	    }
	  return (int)count;
  }
    
  //checks to see if there is a "bad" word in the sentence
	public boolean checkBadWords() {
	  if (words.size() >= 6) {
		  for (int i = 0; i < 6; i++) {
			  String sub = words.get(i).getPartOfSpeech();
			  //checks to see if the first six words in a sentence contain a pronoun or a personal pronoun
			  if (sub.equals("WP") || (sub.length() > 2 && (sub.equals("WP$") || sub.equals("PRP"))))
				  return true;
		  }
	  }
	  return false;
	}
	
	//checks to see if there is unnecessary info in the sentence
	public boolean checkBadList() {
		//goes through badList
		for (int i = 0; i < words.size(); i++) {
			for (int k = 0; k < badList.length; k++) {
				if (words.get(i).toString().toLowerCase().equals(badList[k]))
					return true;
			}
		}
		return false;
	}
	//checks to see if the first word in the sentence is a conjunction
	public boolean checkFirstWord() {
		String sub = words.get(0).getPartOfSpeech();
		if (sub.equals("CC") || sub.equals("IN") || sub.equals("WRB") || sub.equals("RB") || words.get(0).toString().toLowerCase().equals("read"))
			return true;
		return false;
	}
	
	public boolean containsString(String s){
		return this.toString().contains(s);
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
    return text;
  }
  public String getInfo() {
	  return "Index:\t" + this.indexInArticle + "\tNumberOfWords:\t" + this.numWords + "\tPoints:\t" + this.points;
  }
}
