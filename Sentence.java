package bestsummarydevelopment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
package bestsummarydevelopment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class Sentence {

  private int points;
  private int numWords;
  private int indexInArticle;
  private List<Word> words;


  //constructor, creates a sentence that is split up by spaces
  public Sentence(String s) {
    words = new ArrayList<Word>();
    String[] temp = s.split(" ");
    POSTaggerME tagger = setupPOSTagger();
    //array of parts of speech corresponding to each word in the string
    String[] POS = tagger.tag(temp);
    for (int i = 0; i < temp.length; i++) {
      words.add(new Word(temp[i], POS[i]));
    }
    this.numWords = temp.length;
  }

  //sets up the part of speech tagger
  public POSTaggerME setupPOSTagger(){
  	InputStream modelIn = null;
  	POSModel model = null;
  	try {
  	  modelIn = new FileInputStream("/Users/williamadriance/eclipse/workspace/Test/src/bestsummarydevelopment/en-pos-maxent.bin");
  	  model = new POSModel(modelIn);
  	}
  	catch (IOException e) {
  	  // Model loading failed, handle the error
  	  e.printStackTrace();
  	}
  	finally {
  	  if (modelIn != null) {
  	    try {
  	      modelIn.close();
  	    }
  	    catch (IOException e) {
  	    }
  	  }
  	}
  	POSTaggerME tagger = new POSTaggerME(model);

  	return tagger;
  }
  
  //sets the score of the sentence
  public boolean scoreSentence() {
    int count = 0;
    for(int i = 0;i < words.size();i++){
    	int temp = words.get(i).getInstances()*100;
    	String posTemp = words.get(i).getPartOfSpeech();
    	
      if (posTemp.equals("NNP") || posTemp.equals("NNPS"))
        temp *= 2;
      //we use substrings for the rest of the if statements to catch multiple similar parts of speech
      else if (posTemp.equals("CC") || posTemp.equals("IN") || posTemp.equals("DT") || posTemp.substring(0,2).equals("RB"))
    	temp = 0;
    
      count += temp;
    }
    //Can adjust this for longer/shorter sentences
    int ratio = count;
    //We can tweak this for better results
    ratio /= checkBadWords() + 1;
    this.setPoints(ratio);
    return true;
  }
    
  //checks to see if there is a "bad" word in the sentence
	public int checkBadWords() {
	  int badWords = 0;
	  if (words.size() >= 6) {
		  for (int i = 0; i < 6; i++) {
		    if (words.get(i).getPartOfSpeech().substring(0,2).equals("WP") || (words.get(i).getPartOfSpeech().length() > 2 && words.get(i).getPartOfSpeech().substring(0,3).equals("PRP")))
		      badWords++;
		  }
	  }
	    return badWords;
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
}
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class Sentence {

  private int points;
  private int numWords;
  private int indexInArticle;
  private List<Word> words;


  //constructor, creates a sentence that is split up by spaces
  public Sentence(String s) {
    words = new ArrayList<Word>();
    String[] temp = s.split(" ");
    POSTaggerME tagger = setupPOSTagger();
    //array of parts of speech corresponding to each word in the string
    String[] POS = tagger.tag(temp);
    for (int i = 0; i < temp.length; i++) {
      words.add(new Word(temp[i], POS[i]));
    }
    this.numWords = temp.length;
  }

  //sets up the part of speech tagger
  public POSTaggerME setupPOSTagger(){
  	InputStream modelIn = null;
  	POSModel model = null;
  	try {
  	  modelIn = new FileInputStream("/Users/williamadriance/eclipse/workspace/Test/src/bestsummarydevelopment/en-pos-maxent.bin");
  	  model = new POSModel(modelIn);
  	}
  	catch (IOException e) {
  	  // Model loading failed, handle the error
  	  e.printStackTrace();
  	}
  	finally {
  	  if (modelIn != null) {
  	    try {
  	      modelIn.close();
  	    }
  	    catch (IOException e) {
  	    }
  	  }
  	}
  	POSTaggerME tagger = new POSTaggerME(model);

  	return tagger;
  }
  
  //sets the score of the sentence
  public boolean scoreSentence() {
    int count = 0;
    for(int i = 0;i < words.size();i++){
    	int temp = words.get(i).getInstances()*100;
    	String posTemp = words.get(i).getPartOfSpeech();
    	
      if (posTemp.equals("NNP") || posTemp.equals("NNPS"))
        temp *= 2;
      //we use substrings for the rest of the if statements to catch multiple similar parts of speech
      else if (posTemp.equals("CC") || posTemp.equals("IN") || posTemp.equals("DT") || posTemp.substring(0,2).equals("RB"))
    	temp = 0;
    
      count += temp;
    }
    //Can adjust this for longer/shorter sentences
    int ratio = count;
    //We can tweak this for better results
    ratio /= checkBadWords() + 1;
    this.setPoints(ratio);
    return true;
  }
    
  //checks to see if there is a "bad" word in the sentence
	public int checkBadWords() {
	  int badWords = 0;
	  if (words.size() >= 6) {
		  for (int i = 0; i < 6; i++) {
		    if (words.get(i).getPartOfSpeech().substring(0,2).equals("WP") || (words.get(i).getPartOfSpeech().length() > 2 && words.get(i).getPartOfSpeech().substring(0,3).equals("PRP")))
		      badWords++;
		  }
	  }
	    return badWords;
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
}
