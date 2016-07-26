public class Article{

    private List<Sentence> sentences;
    private String text;
    
    public Article(String s){
        sentences = new ArrayList<String>();
        //Splits the large string into sentences by punctuation (|\\ is an "or" operator)
        String[] temp = s.split(".|\\!|\\?")
        for(String r:temp){
            sentences.add(new Sentence(r));
        }
    }
    
    public String getText(){
        return text;
    }
    public String setText(String s){
        text = s;
    }
    
    public String toString(){
        return text;
    }
}