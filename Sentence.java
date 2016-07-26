import java.util.ArrayList;
import java.util.List;

public class Sentence {

  private int points;
  private int numKeyWords;
  private int numWords;
  private boolean anaphor;
  private boolean transition;
  private List<String> words;


  //constructor, creates a sentence that is split up by spaces
  public Sentence(String s) {
    words = new ArrayList<String>();
    String[] temp = s.split(" ");
    for (int i = 0; i < temp.length; i++) {
      words.add(temp[i]);
    }
  }


  //getters and setters for points
  public int getPoints() {
    return this.points;
  }
  public void setPoints(int i) {
    this.points = i;
  }


  //getters and setters for keywords
  public int getNumKeyWords() {
    return this.numKeyWords;
  }
  public void setNumKeyWords(int i) {
    this.numKeyWords = i;
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
