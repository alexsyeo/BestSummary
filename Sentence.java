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
  private boolean anaphor;
  private boolean transition;
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
	  modelIn = new FileInputStream("/Users/williamadriance/eclipse/workspace/Testing/src/com/company/en-pos-maxent.bin");
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
  
  public int getNumWords(){
	  return this.numWords;
  }

  //getters and setters for points
  public int getPoints() {
    return this.points;
  }
  public void setPoints(int i) {
    this.points = i;
  }
  
  //getters and setters for specific Words
  public Word getWord(int index){
	  return words.get(index);
  }
  public boolean setWord(int index, Word element){
	  words.set(index, element);
	  return true;
  }


  //getters and setters for anaphors
  public boolean getAnaphor() {
    return this.anaphor;
  }
  public void setAnaphor(boolean i) {
    this.anaphor = i;
  }

  //getters and setters for transitions
  public boolean getTransition() {
    return this.transition;
  }
  public void setTransition(boolean i) {
    this.transition = i;
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
