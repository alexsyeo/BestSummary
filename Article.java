package bestsummarydevelopment;

import java.util.ArrayList;
import java.util.List;


public class Article{

    private Sentence[] sentences;
    private String text;
    
    public Article(String s){
    	WordCounter counter = new WordCounter(s);
        sentences = counter.makeSentences();
        this.text = s;

        //Splits the large string into sentences by punctuation (|\\ is an "or" operator)
        
        //here, we should take these "sentences" and turn them into Sentences by using the Sentence constructor, feeding in
        //the Strings. This way, the code below will work.
        //NOT EVERY PERIOD WILL SIGNIFY THE END OF A SENTENCE. WE NEED IF STATEMENTS (if it's not Mr., Dr., Mrs., etc.)
        
    
    }
    
    public Sentence getSentence(int i){
    	return this.sentences[i];
    }
    
    public String getText(){
        return text;
    }
    public void setText(String s){
        text = s;
    }
    
    public String toString(){
        return text;
    }
}
