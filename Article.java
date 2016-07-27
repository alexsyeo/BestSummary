import java.util.ArrayList;
import java.util.List;


public class Article{

    private List<Sentence> sentences;
    private String text;
    
    public Article(String s){
<<<<<<< HEAD
    	WordCounter counter = new WordCounter(s);
    	Word[] words = counter.countWords();
        sentences = new ArrayList<Sentence>();
=======
        this.text = s;
        sentences = new ArrayList<String>();
>>>>>>> bc2945c0d621b58b97f8e49783d2f6f3f5962524
        //Splits the large string into sentences by punctuation (|\\ is an "or" operator)
        String[] temp = s.split(".|\\!|\\?");
        
        //here, we should take these "sentences" and turn them into Sentences by using the Sentence constructor, feeding in
        //the Strings. This way, the code below will work.
        //NOT EVERY PERIOD WILL SIGNIFY THE END OF A SENTENCE. WE NEED IF STATEMENTS (if it's not Mr., Dr., Mrs., etc.)
        
        
        for(String r:temp){
            sentences.add(new Sentence(r));
        }
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
