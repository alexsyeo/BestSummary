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
